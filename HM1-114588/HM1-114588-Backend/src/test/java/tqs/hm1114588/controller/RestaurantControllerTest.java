package tqs.hm1114588.controller;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import tqs.hm1114588.model.restaurant.Restaurant;
import tqs.hm1114588.service.LocationService;
import tqs.hm1114588.service.RestaurantService;

@WebMvcTest(RestaurantController.class)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestaurantService restaurantService;
    
    @MockitoBean
    private LocationService locationService;

    private Restaurant restaurant;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setCapacity(50);
        
        // Create and set a location since it's required by the model
        tqs.hm1114588.model.Location location = new tqs.hm1114588.model.Location();
        location.setId(1L);
        location.setName("Test Location");
        location.setLatitude(40.7128);
        location.setLongitude(-74.0060);
        restaurant.setLocation(location);

        // Mock location service
        when(locationService.findById(anyLong())).thenReturn(Optional.of(location));
        when(locationService.save(any(tqs.hm1114588.model.Location.class))).thenReturn(location);
    }

    @Test
    void testGetAllRestaurants() throws Exception {
        // Arrange
        when(restaurantService.findAll()).thenReturn(Collections.singletonList(restaurant));

        // Act & Assert
        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Restaurant")))
                .andExpect(jsonPath("$[0].capacity", is(50)));
        
        verify(restaurantService).findAll();
    }

    @Test
    void testGetRestaurantById_WhenExists() throws Exception {
        // Arrange
        when(restaurantService.findById(1L)).thenReturn(Optional.of(restaurant));

        // Act & Assert
        mockMvc.perform(get("/api/restaurants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Restaurant")))
                .andExpect(jsonPath("$.capacity", is(50)));
        
        verify(restaurantService).findById(1L);
    }

    @Test
    void testGetRestaurantById_WhenNotExists() throws Exception {
        // Arrange
        when(restaurantService.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/restaurants/99"))
                .andExpect(status().isNotFound());
        
        verify(restaurantService).findById(99L);
    }

    @Test
    void testGetRestaurantByName() throws Exception {
        // Arrange
        when(restaurantService.findByName("Test Restaurant")).thenReturn(Optional.of(restaurant));

        // Act & Assert
        mockMvc.perform(get("/api/restaurants/name/Test Restaurant"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Restaurant")));
        
        verify(restaurantService).findByName("Test Restaurant");
    }

    @Test
    void testCreateRestaurant() throws Exception {
        // Arrange
        when(restaurantService.save(any())).thenReturn(restaurant);

        // Act & Assert
        mockMvc.perform(post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurant)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Restaurant")));
        
        verify(restaurantService).save(any());
    }

    @Test
    void testUpdateRestaurant_WhenExists() throws Exception {
        // Arrange
        Restaurant updatedRestaurant = new Restaurant();
        updatedRestaurant.setName("Updated Restaurant");
        updatedRestaurant.setCapacity(100);
        
        when(restaurantService.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantService.save(any())).thenReturn(restaurant);

        // Act & Assert
        mockMvc.perform(put("/api/restaurants/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRestaurant)))
                .andExpect(status().isOk());
        
        verify(restaurantService).findById(1L);
        verify(restaurantService).save(any());
    }

    @Test
    void testUpdateRestaurant_WhenNotExists() throws Exception {
        // Arrange
        Restaurant updatedRestaurant = new Restaurant();
        updatedRestaurant.setName("Updated Restaurant");
        updatedRestaurant.setCapacity(100);
        
        when(restaurantService.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/restaurants/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRestaurant)))
                .andExpect(status().isNotFound());
        
        verify(restaurantService).findById(99L);
        verify(restaurantService, never()).save(any());
    }

    @Test
    void testDeleteRestaurant_WhenExists() throws Exception {
        // Arrange
        doNothing().when(restaurantService).deleteById(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/restaurants/1"))
                .andExpect(status().isNoContent());
        
        verify(restaurantService).deleteById(1L);
    }

    @Test
    void testDeleteRestaurant_WhenNotExists() throws Exception {
        // Arrange
        doNothing().when(restaurantService).deleteById(99L);

        // Act & Assert
        mockMvc.perform(delete("/api/restaurants/99"))
                .andExpect(status().isNoContent());
        
        verify(restaurantService).deleteById(99L);
    }

    @Test
    void testGetAvailableCapacity() throws Exception {
        when(restaurantService.getAvailableCapacity(
            eq(1L), 
            any(), 
            any()
        )).thenReturn(30);

        mockMvc.perform(get("/api/restaurants/1/available-capacity")
                .param("startTime", "2025-04-10T18:00:00")
                .param("endTime", "2025-04-10T20:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(30)));
        
        verify(restaurantService).getAvailableCapacity(eq(1L), any(), any());
    }
    
    @Test
    void testGetAvailableCapacity_NotFound() throws Exception {
        when(restaurantService.getAvailableCapacity(
            eq(99L), 
            any(), 
            any()
        )).thenThrow(new IllegalArgumentException("Restaurant not found"));

        mockMvc.perform(get("/api/restaurants/99/available-capacity")
                .param("startTime", "2025-04-10T18:00:00")
                .param("endTime", "2025-04-10T20:00:00"))
                .andExpect(status().isNotFound());
        
        verify(restaurantService).getAvailableCapacity(eq(99L), any(), any());
    }
    
    @Test
    void testCheckCapacity_HasCapacity() throws Exception {
        when(restaurantService.hasCapacityForReservation(
            eq(1L), 
            any(), 
            eq(4)
        )).thenReturn(true);

        String requestBody = """
            {
                "reservationTime": "2025-04-10T18:00:00",
                "partySize": 4
            }
            """;

        mockMvc.perform(post("/api/restaurants/1/check-capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasCapacity", is(true)));
        
        verify(restaurantService).hasCapacityForReservation(eq(1L), any(), eq(4));
    }
    
    @Test
    void testCheckCapacity_NoCapacity() throws Exception {
        when(restaurantService.hasCapacityForReservation(
            eq(1L), 
            any(), 
            eq(10)
        )).thenReturn(false);

        String requestBody = """
            {
                "reservationTime": "2025-04-10T18:00:00",
                "partySize": 10
            }
            """;

        mockMvc.perform(post("/api/restaurants/1/check-capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasCapacity", is(false)));
        
        verify(restaurantService).hasCapacityForReservation(eq(1L), any(), eq(10));
    }
    
    @Test
    void testCheckCapacity_BadRequest() throws Exception {
        String requestBody = """
            {
                "reservationTime": "invalid-date",
                "partySize": 4
            }
            """;

        mockMvc.perform(post("/api/restaurants/1/check-capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
    
    @Test
    void testUpdateCapacity_Success() throws Exception {
        when(restaurantService.updateCapacity(1L, 100)).thenReturn(Optional.of(restaurant));

        String requestBody = """
            {
                "capacity": 100
            }
            """;

        mockMvc.perform(put("/api/restaurants/1/capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Restaurant")));
        
        verify(restaurantService).updateCapacity(1L, 100);
    }
    
    @Test
    void testUpdateCapacity_NotFound() throws Exception {
        when(restaurantService.updateCapacity(99L, 100)).thenReturn(Optional.empty());

        String requestBody = """
            {
                "capacity": 100
            }
            """;

        mockMvc.perform(put("/api/restaurants/99/capacity")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound());
        
        verify(restaurantService).updateCapacity(99L, 100);
    }
    
    @Test
    void testUpdateAvailableMenus_Success() throws Exception {
        when(restaurantService.updateAvailableMenus(eq(1L), eq(60))).thenReturn(Optional.of(restaurant));

        mockMvc.perform(put("/api/restaurants/1/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"availableMenus\": 60}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Restaurant")));
        
        verify(restaurantService).updateAvailableMenus(eq(1L), eq(60));
    }
    
    @Test
    void testUpdateAvailableMenus_NotFound() throws Exception {
        when(restaurantService.updateAvailableMenus(99L, 60)).thenReturn(Optional.empty());

        String requestBody = """
            {
                "availableMenus": 60
            }
            """;

        mockMvc.perform(put("/api/restaurants/99/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound());
        
        verify(restaurantService).updateAvailableMenus(99L, 60);
    }
} 