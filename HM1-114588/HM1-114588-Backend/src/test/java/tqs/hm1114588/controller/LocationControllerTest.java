package tqs.hm1114588.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
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
import tqs.hm1114588.service.LocationService;

@WebMvcTest(LocationController.class)
class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationService locationService;

    private Location location;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        
        location = new Location();
        location.setId(1L);
        location.setName("Aveiro");
        location.setLatitude(40.64427);
        location.setLongitude(-8.64554);
    }

    @Test
    void testGetAllLocations() throws Exception {
        // Arrange
        when(locationService.findAll()).thenReturn(Collections.singletonList(location));

        // Act & Assert
        mockMvc.perform(get("/api/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Aveiro")))
                .andExpect(jsonPath("$[0].latitude", is(40.64427)))
                .andExpect(jsonPath("$[0].longitude", is(-8.64554)));
        
        verify(locationService).findAll();
    }

    @Test
    void testGetLocationById_WhenExists() throws Exception {
        // Arrange
        when(locationService.findById(1L)).thenReturn(Optional.of(location));

        // Act & Assert
        mockMvc.perform(get("/api/locations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Aveiro")))
                .andExpect(jsonPath("$.latitude", is(40.64427)))
                .andExpect(jsonPath("$.longitude", is(-8.64554)));
        
        verify(locationService).findById(1L);
    }

    @Test
    void testGetLocationById_WhenNotExists() throws Exception {
        // Arrange
        when(locationService.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/locations/99"))
                .andExpect(status().isNotFound());
        
        verify(locationService).findById(99L);
    }

    @Test
    void testGetLocationByName_WhenExists() throws Exception {
        // Arrange
        when(locationService.findByName("Aveiro")).thenReturn(Optional.of(location));

        // Act & Assert
        mockMvc.perform(get("/api/locations/name/Aveiro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Aveiro")))
                .andExpect(jsonPath("$.latitude", is(40.64427)))
                .andExpect(jsonPath("$.longitude", is(-8.64554)));
        
        verify(locationService).findByName("Aveiro");
    }

    @Test
    void testGetLocationByName_WhenNotExists() throws Exception {
        // Arrange
        when(locationService.findByName("Unknown")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/locations/name/Unknown"))
                .andExpect(status().isNotFound());
        
        verify(locationService).findByName("Unknown");
    }

    @Test
    void testCreateLocation() throws Exception {
        // Arrange
        when(locationService.create(anyString(), anyDouble(), anyDouble())).thenReturn(location);

        // Act & Assert
        mockMvc.perform(post("/api/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(location)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Aveiro")))
                .andExpect(jsonPath("$.latitude", is(40.64427)))
                .andExpect(jsonPath("$.longitude", is(-8.64554)));
        
        verify(locationService).create("Aveiro", 40.64427, -8.64554);
    }

    @Test
    void testUpdateLocation_WhenExists() throws Exception {
        // Arrange
        Location updatedLocation = new Location();
        updatedLocation.setName("Updated Location");
        updatedLocation.setLatitude(41.0);
        updatedLocation.setLongitude(-9.0);
        
        when(locationService.findById(1L)).thenReturn(Optional.of(location));
        when(locationService.save(any(Location.class))).thenReturn(location);

        // Act & Assert
        mockMvc.perform(put("/api/locations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedLocation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
        
        verify(locationService).findById(1L);
        verify(locationService).save(any(Location.class));
    }

    @Test
    void testUpdateLocation_WhenNotExists() throws Exception {
        // Arrange
        Location updatedLocation = new Location();
        updatedLocation.setName("Updated Location");
        updatedLocation.setLatitude(41.0);
        updatedLocation.setLongitude(-9.0);
        
        when(locationService.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/locations/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedLocation)))
                .andExpect(status().isNotFound());
        
        verify(locationService).findById(99L);
        verify(locationService, never()).save(any(Location.class));
    }

    @Test
    void testDeleteLocation_WhenExists() throws Exception {
        // Arrange
        when(locationService.findById(1L)).thenReturn(Optional.of(location));
        doNothing().when(locationService).deleteById(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/locations/1"))
                .andExpect(status().isNoContent());
        
        verify(locationService).findById(1L);
        verify(locationService).deleteById(1L);
    }

    @Test
    void testDeleteLocation_WhenNotExists() throws Exception {
        // Arrange
        when(locationService.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/api/locations/99"))
                .andExpect(status().isNotFound());
        
        verify(locationService).findById(99L);
        verify(locationService, never()).deleteById(anyLong());
    }
} 