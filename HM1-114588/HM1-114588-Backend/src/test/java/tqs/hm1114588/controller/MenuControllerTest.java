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
    void testCreateMenu_Success() throws Exception {
        when(menuService.createMenu(anyLong(), any(Menu.class))).thenReturn(menu);

        mockMvc.perform(post("/api/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"mealId\": 1, \"name\": \"New Menu\", \"description\": \"A new test menu\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Menu")));
                
        verify(menuService).createMenu(anyLong(), any(Menu.class));
    }
    
    @Test
    void testCreateMenu_BadRequest() throws Exception {
        when(menuService.createMenu(anyLong(), any(Menu.class)))
            .thenThrow(new IllegalArgumentException("Invalid request"));

        mockMvc.perform(post("/api/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"mealId\": 1, \"name\": \"New Menu\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testDeleteMenu() throws Exception {
        doNothing().when(menuService).deleteById(1L);

        mockMvc.perform(delete("/api/menus/1"))
                .andExpect(status().isNoContent());
                
        verify(menuService).deleteById(1L);
    }
} 