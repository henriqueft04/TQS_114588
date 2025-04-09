package tqs.hm1114588.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import tqs.hm1114588.service.OpenWeatherService;
import tqs.hm1114588.service.RedisService;

@WebMvcTest(CacheController.class)
class CacheControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedisService redisService;

    @MockBean
    private OpenWeatherService openWeatherService;

    private Set<Object> cacheKeys;

    @BeforeEach
    void setUp() {
        cacheKeys = new HashSet<>();
        cacheKeys.add("weatherData::1");
        cacheKeys.add("weatherData::2");
        cacheKeys.add("location::1");
    }

    @Test
    void testGetAllKeys() throws Exception {
        // Arrange
        when(redisService.sMembers("*")).thenReturn(cacheKeys);

        // Act & Assert
        mockMvc.perform(get("/api/cache/keys")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").exists())
                .andExpect(jsonPath("$[2]").exists());
        
        verify(redisService).sMembers("*");
    }

    @Test
    void testGetCacheStatistics() throws Exception {
        // Arrange
        when(redisService.sMembers("*")).thenReturn(cacheKeys);
        when(redisService.sMembers("weatherData*")).thenReturn(Set.of("weatherData::1", "weatherData::2"));
        when(redisService.sMembers("location*")).thenReturn(Set.of("location::1"));
        
        when(openWeatherService.getTotalRequests()).thenReturn(100L);
        when(openWeatherService.getCacheHits()).thenReturn(80L);
        when(openWeatherService.getCacheMisses()).thenReturn(20L);

        // Act & Assert
        mockMvc.perform(get("/api/cache/stats")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalKeys", is(3)))
                .andExpect(jsonPath("$.weatherDataKeys", is(2)))
                .andExpect(jsonPath("$.locationKeys", is(1)))
                .andExpect(jsonPath("$.weatherApiRequests", is(100)))
                .andExpect(jsonPath("$.weatherApiHits", is(80)))
                .andExpect(jsonPath("$.weatherApiMisses", is(20)))
                .andExpect(jsonPath("$.weatherApiHitRatio", is(0.8)));
        
        verify(redisService).sMembers("*");
        verify(redisService).sMembers("weatherData*");
        verify(redisService).sMembers("location*");
        verify(openWeatherService, times(3)).getTotalRequests();
        verify(openWeatherService, times(2)).getCacheHits();
        verify(openWeatherService, times(1)).getCacheMisses();
    }

    @Test
    void testClearAllCaches() throws Exception {
        // Arrange
        doNothing().when(redisService).clearAllCaches();
        doNothing().when(openWeatherService).resetCacheStatistics();

        // Act & Assert
        mockMvc.perform(delete("/api/cache")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", is("All caches cleared successfully")));
        
        verify(redisService).clearAllCaches();
        verify(openWeatherService).resetCacheStatistics();
    }

    @Test
    void testClearSpecificCache_Weather() throws Exception {
        // Arrange
        Set<Object> weatherKeys = Set.of("weather::1", "weather::2");
        when(redisService.sMembers("weather*")).thenReturn(weatherKeys);
        when(redisService.delete(anyString())).thenReturn(true);
        doNothing().when(openWeatherService).resetCacheStatistics();

        // Act & Assert
        mockMvc.perform(delete("/api/cache/weather")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", is("Cache 'weather' cleared successfully")));
        
        verify(redisService).sMembers("weather*");
        verify(redisService).delete("weather::1");
        verify(redisService).delete("weather::2");
        verify(openWeatherService).resetCacheStatistics();
    }

    @Test
    void testClearSpecificCache_Location() throws Exception {
        // Arrange
        Set<Object> locationKeys = Set.of("location::1", "location::2");
        when(redisService.sMembers("location*")).thenReturn(locationKeys);
        when(redisService.delete(anyString())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/cache/location")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", is("Cache 'location' cleared successfully")));
        
        verify(redisService).sMembers("location*");
        verify(redisService).delete("location::1");
        verify(redisService).delete("location::2");
    }
} 