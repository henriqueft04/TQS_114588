package tqs.hm1114588.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import tqs.hm1114588.model.Location;
import tqs.hm1114588.model.WeatherData;
import tqs.hm1114588.model.openweather.Current;
import tqs.hm1114588.model.openweather.Daily;
import tqs.hm1114588.model.openweather.FeelsLike;
import tqs.hm1114588.model.openweather.OpenWeatherResponse;
import tqs.hm1114588.model.openweather.Temp;
import tqs.hm1114588.model.openweather.Weather;

@ExtendWith(MockitoExtension.class)
class OpenWeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;
    
    @Mock
    private WeatherDataService weatherDataService;
    
    @InjectMocks
    private OpenWeatherService openWeatherService;
    
    private Location location;
    private LocalDate date;
    private WeatherData weatherData;
    private OpenWeatherResponse openWeatherResponse;
    
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(openWeatherService, "apiUrl", "https://api.openweathermap.org/data/3.0/onecall");
        ReflectionTestUtils.setField(openWeatherService, "apiKey", "test-api-key");
        
        // Create test location
        location = new Location();
        location.setId(1L);
        location.setName("Aveiro");
        location.setLatitude(40.6443);
        location.setLongitude(-8.6455);
        
        // Create test date (today)
        date = LocalDate.now();
        
        // Create test weather data - using parameterized constructor instead of default constructor
        LocalDateTime now = LocalDateTime.now();
        weatherData = new WeatherData(
            location,          // location
            25.0,              // temperature
            50.0,              // humidity
            10.0,              // windSpeedKm
            180,               // windDirectionId
            0.0,               // precipitation
            1013.0,            // pressure
            null,              // radiation
            null,              // stationId
            now,               // timestamp
            date               // forecastDate
        );
        weatherData.setId(1L);
        
        // Create OpenWeatherMap API response
        openWeatherResponse = createOpenWeatherResponse();
    }
    
    @Test
    void testGetForecastFromCache() {
        // Arrange
        when(weatherDataService.findByLocationAndDate(location, date)).thenReturn(Optional.of(weatherData));
        
        // Act
        Optional<WeatherData> result = openWeatherService.getForecast(location, date);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(weatherData, result.get());
        assertEquals(1, openWeatherService.getTotalRequests());
        assertEquals(1, openWeatherService.getCacheHits());
        assertEquals(0, openWeatherService.getCacheMisses());
        
        // Verify that the API was not called
        verify(restTemplate, times(0)).getForObject(anyString(), eq(OpenWeatherResponse.class));
    }
    
    @Test
    void testGetForecastFromApi() {
        // Arrange
        when(weatherDataService.findByLocationAndDate(location, date)).thenReturn(Optional.empty());
        when(restTemplate.getForObject(anyString(), eq(OpenWeatherResponse.class))).thenReturn(openWeatherResponse);
        when(weatherDataService.create(
            eq(location), anyDouble(), anyDouble(), any(), anyDouble(), 
            any(), anyInt(), anyDouble(), anyDouble(), 
            any(), any(), any(), eq(date)
        )).thenReturn(weatherData);
        
        // Act
        Optional<WeatherData> result = openWeatherService.getForecast(location, date);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(weatherData, result.get());
        assertEquals(1, openWeatherService.getTotalRequests());
        assertEquals(0, openWeatherService.getCacheHits());
        assertEquals(1, openWeatherService.getCacheMisses());
        
        // Verify API was called
        verify(restTemplate).getForObject(anyString(), eq(OpenWeatherResponse.class));
    }
    
    @Test
    void testGetForecastApiFailure() {
        // Arrange
        when(weatherDataService.findByLocationAndDate(location, date)).thenReturn(Optional.empty());
        when(restTemplate.getForObject(anyString(), eq(OpenWeatherResponse.class))).thenReturn(null);
        
        // Act
        Optional<WeatherData> result = openWeatherService.getForecast(location, date);
        
        // Assert
        assertFalse(result.isPresent());
        assertEquals(1, openWeatherService.getTotalRequests());
        assertEquals(0, openWeatherService.getCacheHits());
        assertEquals(1, openWeatherService.getCacheMisses());
    }
    
    @Test
    void testGetForecastDateOutOfRange() {
        // Arrange
        LocalDate futureDate = LocalDate.now().plusDays(10); // Beyond forecast range
        when(weatherDataService.findByLocationAndDate(location, futureDate)).thenReturn(Optional.empty());
        when(restTemplate.getForObject(anyString(), eq(OpenWeatherResponse.class))).thenReturn(openWeatherResponse);
        
        // Act
        Optional<WeatherData> result = openWeatherService.getForecast(location, futureDate);
        
        // Assert
        assertFalse(result.isPresent());
        assertEquals(1, openWeatherService.getTotalRequests());
        assertEquals(0, openWeatherService.getCacheHits());
        assertEquals(1, openWeatherService.getCacheMisses());
    }
    
    @Test
    void testResetCacheStatistics() {
        // Arrange
        openWeatherService.getForecast(location, date); // Increment counters
        
        // Act
        openWeatherService.resetCacheStatistics();
        
        // Assert
        assertEquals(0, openWeatherService.getTotalRequests());
        assertEquals(0, openWeatherService.getCacheHits());
        assertEquals(0, openWeatherService.getCacheMisses());
    }
    
    private OpenWeatherResponse createOpenWeatherResponse() {
        OpenWeatherResponse response = new OpenWeatherResponse();
        response.setLat(40.6443);
        response.setLon(-8.6455);
        response.setTimezone("Europe/Lisbon");
        response.setTimezoneOffset(3600);
        
        // Create current weather
        Current current = new Current();
        current.setDt(System.currentTimeMillis() / 1000);
        current.setTemp(298.15); // 25°C in Kelvin
        current.setHumidity(50);
        current.setPressure(1013);
        current.setWindSpeed(2.78); // 10 km/h in m/s
        current.setWindDeg(180);
        
        // Create weather condition
        Weather weather = new Weather();
        weather.setId(800);
        weather.setMain("Clear");
        weather.setDescription("Clear sky");
        weather.setIcon("01d");
        List<Weather> weatherList = new ArrayList<>();
        weatherList.add(weather);
        current.setWeather(weatherList);
        
        response.setCurrent(current);
        
        // Create daily forecasts
        List<Daily> dailyList = new ArrayList<>();
        
        // Today's forecast
        Daily today = new Daily();
        today.setDt(System.currentTimeMillis() / 1000);
        
        Temp temp = new Temp();
        temp.setDay(298.15); // 25°C in Kelvin
        temp.setMin(288.15);
        temp.setMax(303.15);
        today.setTemp(temp);
        
        FeelsLike feelsLike = new FeelsLike();
        feelsLike.setDay(298.15);
        today.setFeelsLike(feelsLike);
        
        today.setHumidity(50);
        today.setPressure(1013);
        today.setWindSpeed(2.78); // m/s
        today.setWindDeg(180);
        today.setRain(0.0);
        today.setWeather(weatherList);
        
        dailyList.add(today);
        
        // Add some more days
        for (int i = 1; i < 7; i++) {
            Daily day = new Daily();
            day.setDt(System.currentTimeMillis() / 1000 + (i * 86400));
            
            Temp dayTemp = new Temp();
            dayTemp.setDay(298.15 - i);
            dayTemp.setMin(288.15 - i);
            dayTemp.setMax(303.15 - i);
            day.setTemp(dayTemp);
            
            FeelsLike dayFeels = new FeelsLike();
            dayFeels.setDay(298.15 - i);
            day.setFeelsLike(dayFeels);
            
            day.setHumidity(50 + i);
            day.setPressure(1013 - i);
            day.setWindSpeed(2.78 + (i * 0.5));
            day.setWindDeg(180 + (i * 10));
            day.setRain(i * 0.5);
            day.setWeather(weatherList);
            
            dailyList.add(day);
        }
        
        response.setDaily(dailyList);
        
        return response;
    }
} 