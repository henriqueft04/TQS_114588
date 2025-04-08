package tqs.hm1114588.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import tqs.hm1114588.model.Location;
import tqs.hm1114588.model.WeatherData;
import tqs.hm1114588.repository.LocationRepository;
import tqs.hm1114588.repository.WeatherDataRepository;

@Service
public class WeatherAPIService {

    @Value("${weather.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final LocationRepository locationRepository;
    private final WeatherDataRepository weatherDataRepository;
    private final ObjectMapper objectMapper;
    private final WeatherDataService weatherDataService;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public WeatherAPIService(RestTemplate restTemplate, 
                           LocationRepository locationRepository,
                           WeatherDataRepository weatherDataRepository,
                           ObjectMapper objectMapper,
                           WeatherDataService weatherDataService) {
        this.restTemplate = restTemplate;
        this.locationRepository = locationRepository;
        this.weatherDataRepository = weatherDataRepository;
        this.objectMapper = objectMapper;
        this.weatherDataService = weatherDataService;
    }

    /**
     * Get weather forecast for a location
     * @param location Location
     * @param date Forecast date
     * @return Weather data if found
     */
    public Optional<WeatherData> getForecast(Location location, LocalDate date) {
        try {
            JsonNode response = restTemplate.getForObject(apiUrl, JsonNode.class);
            
            if (response != null) {
                JsonNode root = objectMapper.readTree(response.traverse());
                return findAndParseWeatherData(root, location, date);
            }
        } catch (Exception e) {
            // Log error and return empty
        }

        return Optional.empty();
    }

    /**
     * Find and parse weather data from GeoJSON response
     * @param root JSON root node
     * @param location Location
     * @param date Forecast date
     * @return Weather data if found
     */
    private Optional<WeatherData> findAndParseWeatherData(JsonNode root, Location location, LocalDate date) {
        JsonNode features = root.path("features");
        
        for (JsonNode feature : features) {
            JsonNode properties = feature.path("properties");
            String stationName = properties.path("localEstacao").asText();
            
            if (stationName.equals(location.getName())) {
                return Optional.of(parseWeatherData(feature, location, date));
            }
        }
        
        return Optional.empty();
    }

    /**
     * Parse weather data from GeoJSON feature
     * @param feature GeoJSON feature
     * @param location Location
     * @param date Forecast date
     * @return Weather data
     */
    private WeatherData parseWeatherData(JsonNode feature, Location location, LocalDate date) {
        JsonNode properties = feature.path("properties");
        
        double temperature = properties.path("temperatura").asDouble();
        double humidity = properties.path("humidade").asDouble();
        double windSpeedKm = properties.path("intensidadeVentoKM").asDouble();
        int windDirectionId = properties.path("idDireccVento").asInt();
        double precipitation = properties.path("precAcumulada").asDouble();
        double pressure = properties.path("pressao").asDouble();
        double radiation = properties.path("radiacao").asDouble();
        String stationId = properties.path("idEstacao").asText();
        
        // Parse timestamp
        String timeStr = properties.path("time").asText();
        LocalDateTime timestamp = LocalDateTime.parse(timeStr, TIME_FORMATTER);

        return weatherDataService.create(
            location, temperature, humidity, null, windSpeedKm, 
            null, windDirectionId, precipitation, pressure, 
            radiation, stationId, timestamp, date
        );
    }
} 