package tqs.hm1114588.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import tqs.hm1114588.model.Location;
import tqs.hm1114588.model.WeatherData;

@ExtendWith(MockitoExtension.class)
class WeatherAPIServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private WeatherDataService weatherDataService;

    @InjectMocks
    private WeatherAPIService weatherAPIService;

    private Location location;
    private WeatherData weatherData;
    private LocalDate date;
    private JsonNode mockResponse;

    @BeforeEach
    void setUp() {
        // Set up test data
        location = new Location();
        location.setId(1L);
        location.setName("Lisbon");
        location.setLatitude(38.736946);
        location.setLongitude(-9.142685);

        date = LocalDate.now();
        LocalDateTime timestamp = LocalDateTime.now();

        weatherData = new WeatherData(
            location,
            25.5,
            65.0,
            10.0,
            1,
            0.0,
            1013.2,
            850.0,
            "LISBOA",
            timestamp,
            date
        );

        // Set API URL using reflection since it's normally set by @Value
        ReflectionTestUtils.setField(weatherAPIService, "apiUrl", "http://test-api.com/weather");

        // Create mock JSON response
        ObjectMapper realObjectMapper = new ObjectMapper();
        ObjectNode mockRoot = realObjectMapper.createObjectNode();
        ObjectNode feature = realObjectMapper.createObjectNode();
        ObjectNode properties = realObjectMapper.createObjectNode();
        
        properties.put("localEstacao", "Lisbon");
        properties.put("temperatura", 25.5);
        properties.put("humidade", 65.0);
        properties.put("intensidadeVentoKM", 10.0);
        properties.put("idDireccVento", 1);
        properties.put("precAcumulada", 0.0);
        properties.put("pressao", 1013.2);
        properties.put("radiacao", 850.0);
        properties.put("idEstacao", "LISBOA");
        properties.put("time", "2023-01-01T12:00:00");
        
        feature.set("properties", properties);
        mockRoot.withArray("features").add(feature);
        
        mockResponse = mockRoot;
    }

    @Test
    void testGetForecast_CacheHit() {
        // Arrange
        when(weatherDataService.findByLocationAndDate(location, date))
            .thenReturn(Optional.of(weatherData));

        // Act
        Optional<WeatherData> result = weatherAPIService.getForecast(location, date);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(weatherData, result.get());
        verify(weatherDataService).findByLocationAndDate(location, date);
        verify(restTemplate, never()).getForObject(anyString(), any());
        
        // Verify cache statistics
        assertEquals(1, weatherAPIService.getTotalRequests());
        assertEquals(1, weatherAPIService.getCacheHits());
        assertEquals(0, weatherAPIService.getCacheMisses());
    }

    @Test
    void testGetForecast_CacheMiss() throws Exception {
        // Arrange
        when(weatherDataService.findByLocationAndDate(location, date))
            .thenReturn(Optional.empty());
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class)))
            .thenReturn(mockResponse);
            
        // Configure objectMapper to return the mockResponse when traverse() is called
        JsonNode mockTraverseResult = mockResponse;
        when(objectMapper.readTree(any(com.fasterxml.jackson.core.JsonParser.class))).thenReturn(mockTraverseResult);
            
        when(weatherDataService.create(
                any(), anyDouble(), anyDouble(), any(), 
                anyDouble(), any(), anyInt(), anyDouble(), 
                anyDouble(), anyDouble(), anyString(), any(), any()))
            .thenReturn(weatherData);

        // Act
        Optional<WeatherData> result = weatherAPIService.getForecast(location, date);

        // Assert
        assertTrue(result.isPresent());
        verify(weatherDataService).findByLocationAndDate(location, date);
        verify(restTemplate).getForObject(anyString(), eq(JsonNode.class));
        
        // Verify cache statistics
        assertEquals(1, weatherAPIService.getTotalRequests());
        assertEquals(0, weatherAPIService.getCacheHits());
        assertEquals(1, weatherAPIService.getCacheMisses());
    }

    @Test
    void testGetForecast_ApiError() {
        // Arrange
        when(weatherDataService.findByLocationAndDate(location, date))
            .thenReturn(Optional.empty());
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class)))
            .thenThrow(new RuntimeException("API Error"));

        // Act
        Optional<WeatherData> result = weatherAPIService.getForecast(location, date);

        // Assert
        assertFalse(result.isPresent());
        verify(weatherDataService).findByLocationAndDate(location, date);
        verify(restTemplate).getForObject(anyString(), eq(JsonNode.class));
        
        // Verify cache statistics
        assertEquals(1, weatherAPIService.getTotalRequests());
        assertEquals(0, weatherAPIService.getCacheHits());
        assertEquals(1, weatherAPIService.getCacheMisses());
    }

    @Test
    void testResetCacheStatistics() {
        // Instead of trying to modify final fields, we'll test functionality
        // by calling the method and verifying the counts return to 0
        
        // Setup: Make sure we get non-zero values first to verify reset works
        when(weatherDataService.findByLocationAndDate(location, date))
            .thenReturn(Optional.of(weatherData));
            
        // Call the service to increment counters
        weatherAPIService.getForecast(location, date);
        
        // Now reset statistics
        weatherAPIService.resetCacheStatistics();
        
        // Verify they're now 0
        assertEquals(0, weatherAPIService.getTotalRequests());
        assertEquals(0, weatherAPIService.getCacheHits());
        assertEquals(0, weatherAPIService.getCacheMisses());
    }
} 