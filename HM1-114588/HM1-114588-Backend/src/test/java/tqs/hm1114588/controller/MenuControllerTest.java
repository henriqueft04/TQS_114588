package tqs.hm1114588.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import tqs.hm1114588.model.restaurant.Dish;
import tqs.hm1114588.model.restaurant.Menu;
import tqs.hm1114588.model.restaurant.Restaurant;
import tqs.hm1114588.service.MenuService;

@WebMvcTest(MenuController.class)
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuService menuService;

    private Menu menu;
    private Restaurant restaurant;
    private Dish dish;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        
        dish = new Dish();
        dish.setId(1L);
        dish.setName("Test Dish");
        
        Set<Dish> dishes = new HashSet<>();
        dishes.add(dish);
        
        menu = new Menu();
        menu.setId(1L);
        menu.setName("Test Menu");
        menu.setDescription("Menu for tests");
        menu.setPrice(new BigDecimal("19.99"));
        menu.setRestaurant(restaurant);
        menu.setDishes(dishes);
    }

    @Test
    void testGetAllMenus() throws Exception {
        when(menuService.findAll()).thenReturn(Collections.singletonList(menu));

        mockMvc.perform(get("/api/menus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Menu")))
                .andExpect(jsonPath("$[0].description", is("Menu for tests")));
                
        verify(menuService).findAll();
    }

    @Test
    void testGetMenu_WhenExists() throws Exception {
        when(menuService.findById(1L)).thenReturn(Optional.of(menu));

        mockMvc.perform(get("/api/menus/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Menu")))
                .andExpect(jsonPath("$.description", is("Menu for tests")));
                
        verify(menuService).findById(1L);
    }

    @Test
    void testGetMenu_WhenNotExists() throws Exception {
        when(menuService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/menus/99"))
                .andExpect(status().isNotFound());
                
        verify(menuService).findById(99L);
    }

    @Test
    void testGetMenusByRestaurant() throws Exception {
        when(menuService.findByRestaurantId(1L)).thenReturn(Collections.singletonList(menu));

        mockMvc.perform(get("/api/menus/restaurant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Menu")));
                
        verify(menuService).findByRestaurantId(1L);
    }

    @Test
    void testGetAvailableMenus() throws Exception {
        when(menuService.findAvailable()).thenReturn(Collections.singletonList(menu));

        mockMvc.perform(get("/api/menus/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Menu")));
                
        verify(menuService).findAvailable();
    }

    @Test
    void testGetAvailableMenusByRestaurant() throws Exception {
        when(menuService.findAvailableByRestaurant(1L)).thenReturn(Collections.singletonList(menu));

        mockMvc.perform(get("/api/menus/restaurant/1/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Menu")));
                
        verify(menuService).findAvailableByRestaurant(1L);
    }

    @Test
    void testCreateMenu_Success() throws Exception {
        // Create request body
        Map<String, Object> requestBody = Map.of(
            "restaurantId", 1L,
            "name", "New Menu",
            "description", "A new test menu",
            "price", "29.99",
            "dishIds", Arrays.asList(1, 2, 3)
        );
        
        when(menuService.createMenu(
            eq(1L), 
            eq("New Menu"), 
            eq("A new test menu"), 
            eq(new BigDecimal("29.99")),
            any()
        )).thenReturn(menu);

        mockMvc.perform(post("/api/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Menu")));
                
        verify(menuService).createMenu(
            eq(1L), 
            eq("New Menu"), 
            eq("A new test menu"), 
            eq(new BigDecimal("29.99")),
            any()
        );
    }
    
    @Test
    void testCreateMenu_BadRequest() throws Exception {
        // Create request body with invalid data
        Map<String, Object> requestBody = Map.of(
            "restaurantId", 1L,
            "name", "New Menu",
            "price", "invalid-price",
            "dishIds", Arrays.asList(1, 2, 3)
        );
        
        when(menuService.createMenu(
            any(), 
            any(), 
            any(), 
            any(),
            any()
        )).thenThrow(new IllegalArgumentException("Invalid price"));

        mockMvc.perform(post("/api/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testUpdateAvailability() throws Exception {
        when(menuService.updateAvailability(1L, true)).thenReturn(Optional.of(menu));

        mockMvc.perform(put("/api/menus/1/availability")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"isAvailable\": true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Menu")));
                
        verify(menuService).updateAvailability(1L, true);
    }

    @Test
    void testAddDishToMenu_Success() throws Exception {
        when(menuService.addDishToMenu(1L, 2L)).thenReturn(Optional.of(menu));

        mockMvc.perform(put("/api/menus/1/dishes/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"dishId\": 2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Menu")));
                
        verify(menuService).addDishToMenu(1L, 2L);
    }

    @Test
    void testAddDishToMenu_NotFound() throws Exception {
        when(menuService.addDishToMenu(99L, 2L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/menus/99/dishes/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"dishId\": 2}"))
                .andExpect(status().isNotFound());
                
        verify(menuService).addDishToMenu(99L, 2L);
    }

    @Test
    void testRemoveDishFromMenu_Success() throws Exception {
        when(menuService.removeDishFromMenu(1L, 1L)).thenReturn(Optional.of(menu));

        mockMvc.perform(put("/api/menus/1/dishes/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"dishId\": 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Menu")));
                
        verify(menuService).removeDishFromMenu(1L, 1L);
    }

    @Test
    void testRemoveDishFromMenu_NotFound() throws Exception {
        when(menuService.removeDishFromMenu(99L, 1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/menus/99/dishes/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"dishId\": 1}"))
                .andExpect(status().isNotFound());
                
        verify(menuService).removeDishFromMenu(99L, 1L);
    }

    @Test
    void testDeleteMenu() throws Exception {
        doNothing().when(menuService).deleteById(1L);

        mockMvc.perform(delete("/api/menus/1"))
                .andExpect(status().isNoContent());
                
        verify(menuService).deleteById(1L);
    }
} 