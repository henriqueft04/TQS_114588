package tqs.hm1114588.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import tqs.hm1114588.model.restaurant.Dish;
import tqs.hm1114588.model.restaurant.DishType;
import tqs.hm1114588.service.DishService;

@WebMvcTest(DishController.class)
class DishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DishService dishService;

    private Dish dish;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        
        dish = new Dish();
        dish.setId(1L);
        dish.setName("Test Dish");
        dish.setDescription("A delicious test dish");
        dish.setType(DishType.Peixe);
        dish.setIsAvailable(true);
    }

    @Test
    void testGetAllDishes() throws Exception {
        // Arrange
        when(dishService.findAll()).thenReturn(Collections.singletonList(dish));

        // Act & Assert
        mockMvc.perform(get("/api/dishes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Dish")))
                .andExpect(jsonPath("$[0].description", is("A delicious test dish")))
                .andExpect(jsonPath("$[0].type", is("Peixe")))
                .andExpect(jsonPath("$[0].isAvailable", is(true)));
        
        verify(dishService).findAll();
    }

    @Test
    void testGetDish_WhenExists() throws Exception {
        // Arrange
        when(dishService.findById(1L)).thenReturn(Optional.of(dish));

        // Act & Assert
        mockMvc.perform(get("/api/dishes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Dish")))
                .andExpect(jsonPath("$.description", is("A delicious test dish")))
                .andExpect(jsonPath("$.type", is("Peixe")))
                .andExpect(jsonPath("$.isAvailable", is(true)));
        
        verify(dishService).findById(1L);
    }

    @Test
    void testGetDish_WhenNotExists() throws Exception {
        // Arrange
        when(dishService.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/dishes/99"))
                .andExpect(status().isNotFound());
        
        verify(dishService).findById(99L);
    }

    @Test
    void testGetDishesByType() throws Exception {
        // Arrange
        when(dishService.findByType(DishType.Peixe)).thenReturn(Collections.singletonList(dish));

        // Act & Assert
        mockMvc.perform(get("/api/dishes/type/Peixe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Dish")))
                .andExpect(jsonPath("$[0].type", is("Peixe")));
        
        verify(dishService).findByType(DishType.Peixe);
    }

    @Test
    void testGetAvailableDishes() throws Exception {
        // Arrange
        when(dishService.findAvailable()).thenReturn(Collections.singletonList(dish));

        // Act & Assert
        mockMvc.perform(get("/api/dishes/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Dish")))
                .andExpect(jsonPath("$[0].isAvailable", is(true)));
        
        verify(dishService).findAvailable();
    }

    @Test
    void testSearchDishes() throws Exception {
        // Arrange
        when(dishService.findByNameContaining("Test")).thenReturn(Collections.singletonList(dish));

        // Act & Assert
        mockMvc.perform(get("/api/dishes/search")
                .param("query", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Dish")));
        
        verify(dishService).findByNameContaining("Test");
    }

    @Test
    void testCreateDish() throws Exception {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("name", "New Dish");
        request.put("description", "A new delicious dish");
        request.put("type", "Peixe");

        when(dishService.createDish(anyString(), anyString(), any(DishType.class))).thenReturn(dish);

        // Act & Assert
        mockMvc.perform(post("/api/dishes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Dish")));
        
        verify(dishService).createDish("New Dish", "A new delicious dish", DishType.Peixe);
    }

    @Test
    void testUpdateAvailability_WhenExists() throws Exception {
        // Arrange
        Map<String, Boolean> request = new HashMap<>();
        request.put("isAvailable", false);

        when(dishService.updateAvailability(1L, false)).thenReturn(Optional.of(dish));

        // Act & Assert
        mockMvc.perform(put("/api/dishes/1/availability")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Dish")));
        
        verify(dishService).updateAvailability(1L, false);
    }

    @Test
    void testUpdateAvailability_WhenNotExists() throws Exception {
        // Arrange
        Map<String, Boolean> request = new HashMap<>();
        request.put("isAvailable", false);

        when(dishService.updateAvailability(99L, false)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/dishes/99/availability")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        
        verify(dishService).updateAvailability(99L, false);
    }

    @Test
    void testDeleteDish() throws Exception {
        // Arrange
        doNothing().when(dishService).deleteById(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/dishes/1"))
                .andExpect(status().isNoContent());
        
        verify(dishService).deleteById(1L);
    }
} 