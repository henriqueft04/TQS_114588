package tqs.hm1114588.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import tqs.hm1114588.model.Location;
import tqs.hm1114588.model.WeatherData;
import tqs.hm1114588.model.openweather.Daily;
import tqs.hm1114588.model.openweather.OpenWeatherResponse;

@Service
public class OpenWeatherService {

    @Value("${openweather.api.url}")
    private String apiUrl;
    
    @Value("${openweather.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final WeatherDataService weatherDataService;
    
    // Cache statistics
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong cacheMisses = new AtomicLong(0);

    public OpenWeatherService(RestTemplate restTemplate, WeatherDataService weatherDataService) {
        this.restTemplate = restTemplate;
        this.weatherDataService = weatherDataService;
    }

    /**
     * Get weather forecast for a location on a specific date
     * @param location Location
     * @param date Forecast date
     * @return Weather data if found
     */
    public Optional<WeatherData> getForecast(Location location, LocalDate date) {
        totalRequests.incrementAndGet();
        
        // Check if we have cached data
        Optional<WeatherData> cachedData = weatherDataService.findByLocationAndDate(location, date);
        if (cachedData.isPresent()) {
            cacheHits.incrementAndGet();
            return cachedData;
        }
        
        cacheMisses.incrementAndGet();
        
        try {
            String url = buildApiUrl(location);
            OpenWeatherResponse response = restTemplate.getForObject(url, OpenWeatherResponse.class);
            
            if (response != null) {
                // Find the daily forecast for the requested date
                Optional<Daily> dailyForecast = findDailyForecastForDate(response, date);
                
                if (dailyForecast.isPresent()) {
                    return Optional.of(createWeatherDataFromDailyForecast(
                        location, dailyForecast.get(), date));
                }
            }
            return Optional.empty();
        } catch (Exception e) {
            // Log the error with details
            System.err.println("Error fetching weather forecast: " + e.getMessage());
            System.err.println("Location: " + location.getName() + " (lat: " + location.getLatitude() + ", lon: " + location.getLongitude() + ")");
            System.err.println("Date: " + date);
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch weather forecast", e);
        }
    }
    
    /**
     * Build the API URL with location and API key
     * @param location Location
     * @return API URL
     */
    private String buildApiUrl(Location location) {
        return UriComponentsBuilder.fromHttpUrl(apiUrl)
            .queryParam("lat", location.getLatitude())
            .queryParam("lon", location.getLongitude())
            .queryParam("exclude", "minutely")
            .queryParam("appid", apiKey)
            .build()
            .toUriString();
    }
    
    /**
     * Find the daily forecast for a specific date
     * @param response OpenWeatherMap response
     * @param date Date to find forecast for
     * @return Daily forecast if found
     */
    private Optional<Daily> findDailyForecastForDate(OpenWeatherResponse response, LocalDate date) {
        if (response.getDaily() == null || response.getDaily().isEmpty()) {
            return Optional.empty();
        }
        
        LocalDate today = LocalDate.now();
        int daysFromToday = (int) (date.toEpochDay() - today.toEpochDay());
        
        // Check if the requested date is within the available forecast range (typically 7 days)
        if (daysFromToday >= 0 && daysFromToday < response.getDaily().size()) {
            return Optional.of(response.getDaily().get(daysFromToday));
        }
        
        return Optional.empty();
    }
    
    /**
     * Create a WeatherData object from a Daily forecast
     * @param location Location
     * @param daily Daily forecast
     * @param date Forecast date
     * @return WeatherData
     */
    private WeatherData createWeatherDataFromDailyForecast(Location location, Daily daily, LocalDate date) {
        // Convert Unix timestamp to LocalDateTime
        LocalDateTime timestamp = Instant.ofEpochSecond(daily.getDt())
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
            
        // Get weather data from daily forecast
        double temperature = daily.getTemp().getDay() - 273.15; // Convert from Kelvin to Celsius
        double humidity = daily.getHumidity();
        double windSpeedKm = daily.getWindSpeed() * 3.6; // Convert from m/s to km/h
        int windDirectionId = daily.getWindDeg();
        double precipitation = daily.getRain(); // Rain in mm
        double pressure = daily.getPressure();
        
        // Create and return weather data
        return weatherDataService.create(
            location, temperature, humidity, null, windSpeedKm, 
            null, windDirectionId, precipitation, pressure, 
            null, null, timestamp, date
        );
    }
    
    /**
     * Get total number of requests
     * @return Total number of requests
     */
    public long getTotalRequests() {
        return totalRequests.get();
    }
    
    /**
     * Get number of cache hits
     * @return Number of cache hits
     */
    public long getCacheHits() {
        return cacheHits.get();
    }
    
    /**
     * Get number of cache misses
     * @return Number of cache misses
     */
    public long getCacheMisses() {
        return cacheMisses.get();
    }
    
    /**
     * Reset cache statistics
     */
    public void resetCacheStatistics() {
        totalRequests.set(0);
        cacheHits.set(0);
        cacheMisses.set(0);
    }
} 