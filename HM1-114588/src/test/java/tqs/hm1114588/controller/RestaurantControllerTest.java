package tqs.hm1114588.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import tqs.hm1114588.model.restaurant.Restaurant;
import tqs.hm1114588.service.RestaurantService;

@WebMvcTest(RestaurantController.class)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantService restaurantService;

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
        restaurant.setAvailableMenus(40);
    }

    @Test
    void testGetAllRestaurants() throws Exception {
        // Arrange
        when(restaurantService.findAll()).thenReturn(Arrays.asList(restaurant));

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
                .andExpect(status().isOk())
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
        when(restaurantService.findById(1L)).thenReturn(Optional.of(restaurant));
        doNothing().when(restaurantService).deleteById(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/restaurants/1"))
                .andExpect(status().isNoContent());
        
        verify(restaurantService).findById(1L);
        verify(restaurantService).deleteById(1L);
    }

    @Test
    void testDeleteRestaurant_WhenNotExists() throws Exception {
        // Arrange
        when(restaurantService.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/api/restaurants/99"))
                .andExpect(status().isNotFound());
        
        verify(restaurantService).findById(99L);
        verify(restaurantService, never()).deleteById(anyLong());
    }
} 