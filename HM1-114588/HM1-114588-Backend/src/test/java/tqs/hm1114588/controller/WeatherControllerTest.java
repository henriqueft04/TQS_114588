package tqs.hm1114588.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import tqs.hm1114588.model.Location;
import tqs.hm1114588.model.WeatherData;
import tqs.hm1114588.service.LocationService;
import tqs.hm1114588.service.OpenWeatherService;
import tqs.hm1114588.service.WeatherDataService;

@WebMvcTest(WeatherController.class)
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OpenWeatherService openWeatherService;

    @MockBean
    private WeatherDataService weatherDataService;

    @MockBean
    private LocationService locationService;

    private Location location;
    private WeatherData weatherData;
    private ObjectMapper objectMapper;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        
        testDate = LocalDate.of(2025, 4, 15);
        
        location = new Location();
        location.setId(1L);
        location.setName("Aveiro");
        location.setLatitude(40.64427);
        location.setLongitude(-8.64554);
        
        // Create WeatherData using reflection to set values for testing
        LocalDateTime now = LocalDateTime.now();
        weatherData = new WeatherData(
            location,          // location
            22.5,              // temperature
            65.0,              // humidity
            10.5,              // windSpeedKm
            2,                 // windDirectionId
            0.0,               // precipitation
            1013.0,            // pressure
            75.0,              // radiation
            "AVR01",           // stationId
            now,               // timestamp
            testDate           // forecastDate
        );
        weatherData.setId(1L);
    }

    @Test
    void testGetForecast_WhenLocationAndForecastExist() throws Exception {
        // Arrange
        when(locationService.findById(1L)).thenReturn(Optional.of(location));
        when(openWeatherService.getForecast(location, testDate)).thenReturn(Optional.of(weatherData));

        // Act & Assert
        mockMvc.perform(get("/api/weather/forecast")
                .param("locationId", "1")
                .param("date", testDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.temperature", is(22.5)))
                .andExpect(jsonPath("$.humidity", is(65.0)));
        
        verify(locationService).findById(1L);
        verify(openWeatherService).getForecast(location, testDate);
    }

    @Test
    void testGetForecast_WhenLocationNotFound() throws Exception {
        // Arrange
        when(locationService.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/weather/forecast")
                .param("locationId", "99")
                .param("date", testDate.toString()))
                .andExpect(status().isNotFound());
        
        verify(locationService).findById(99L);
    }

    @Test
    void testGetForecast_WhenForecastNotFound() throws Exception {
        // Arrange
        when(locationService.findById(1L)).thenReturn(Optional.of(location));
        when(openWeatherService.getForecast(location, testDate)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/weather/forecast")
                .param("locationId", "1")
                .param("date", testDate.toString()))
                .andExpect(status().isNotFound());
        
        verify(locationService).findById(1L);
        verify(openWeatherService).getForecast(location, testDate);
    }

    @Test
    void testGetWeatherByLocation_WhenLocationExists() throws Exception {
        // Arrange
        List<WeatherData> weatherDataList = Collections.singletonList(weatherData);
        when(locationService.findById(1L)).thenReturn(Optional.of(location));
        when(weatherDataService.findByLocation(location)).thenReturn(weatherDataList);

        // Act & Assert
        mockMvc.perform(get("/api/weather/location/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].temperature", is(22.5)));
        
        verify(locationService).findById(1L);
        verify(weatherDataService).findByLocation(location);
    }

    @Test
    void testGetWeatherByLocation_WhenLocationNotFound() throws Exception {
        // Arrange
        when(locationService.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/weather/location/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        
        verify(locationService).findById(99L);
    }

    @Test
    void testGetWeatherByLocationAndDateRange_WhenLocationExists() throws Exception {
        // Arrange
        List<WeatherData> weatherDataList = Arrays.asList(weatherData);
        when(locationService.findById(1L)).thenReturn(Optional.of(location));
        when(weatherDataService.findByLocationAndDateRange(location, testDate)).thenReturn(weatherDataList);

        // Act & Assert
        mockMvc.perform(get("/api/weather/location/1/range")
                .param("startDate", testDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].temperature", is(22.5)));
        
        verify(locationService).findById(1L);
        verify(weatherDataService).findByLocationAndDateRange(location, testDate);
    }

    @Test
    void testGetWeatherByLocationAndDateRange_WhenLocationNotFound() throws Exception {
        // Arrange
        when(locationService.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/weather/location/99/range")
                .param("startDate", testDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        
        verify(locationService).findById(99L);
    }

    @Test
    void testUpdateWeatherData_WhenExists() throws Exception {
        // Arrange
        when(weatherDataService.update(
                1L, 25.0, 70.0, 15.0, 3, 0.5
        )).thenReturn(Optional.of(weatherData));

        // Setup the updated weatherData to be returned
        weatherData.setTemperature(25.0);
        weatherData.setHumidity(70.0);
        weatherData.setWindSpeedKm(15.0);
        weatherData.setWindDirectionId(3);
        weatherData.setPrecipitation(0.5);

        // Act & Assert
        mockMvc.perform(put("/api/weather/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(weatherData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.temperature", is(25.0)))
                .andExpect(jsonPath("$.humidity", is(70.0)))
                .andExpect(jsonPath("$.windSpeedKm", is(15.0)))
                .andExpect(jsonPath("$.windDirectionId", is(3)))
                .andExpect(jsonPath("$.precipitation", is(0.5)));
        
        verify(weatherDataService).update(1L, 25.0, 70.0, 15.0, 3, 0.5);
    }

    @Test
    void testUpdateWeatherData_WhenNotExists() throws Exception {
        // Arrange
        when(weatherDataService.update(
                99L, 25.0, 70.0, 15.0, 3, 0.5
        )).thenReturn(Optional.empty());

        // Setup test data
        weatherData.setId(99L);
        weatherData.setTemperature(25.0);
        weatherData.setHumidity(70.0);
        weatherData.setWindSpeedKm(15.0);
        weatherData.setWindDirectionId(3);
        weatherData.setPrecipitation(0.5);

        // Act & Assert
        mockMvc.perform(put("/api/weather/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(weatherData)))
                .andExpect(status().isNotFound());
        
        verify(weatherDataService).update(99L, 25.0, 70.0, 15.0, 3, 0.5);
    }

    @Test
    void testDeleteWeatherData_WhenExists() throws Exception {
        // Arrange
        when(weatherDataService.findById(1L)).thenReturn(Optional.of(weatherData));
        doNothing().when(weatherDataService).deleteById(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/weather/1"))
                .andExpect(status().isNoContent());
        
        verify(weatherDataService).findById(1L);
        verify(weatherDataService).deleteById(1L);
    }

    @Test
    void testDeleteWeatherData_WhenNotExists() throws Exception {
        // Arrange
        when(weatherDataService.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/api/weather/99"))
                .andExpect(status().isNotFound());
        
        verify(weatherDataService).findById(99L);
    }
} 