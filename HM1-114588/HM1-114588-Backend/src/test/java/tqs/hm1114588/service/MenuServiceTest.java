package tqs.hm1114588.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import tqs.hm1114588.model.restaurant.Dish;
import tqs.hm1114588.model.restaurant.DishType;
import tqs.hm1114588.model.restaurant.Menu;
import tqs.hm1114588.model.restaurant.Restaurant;
import tqs.hm1114588.repository.DishRepository;
import tqs.hm1114588.repository.MenuRepository;
import tqs.hm1114588.repository.RestaurantRepository;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;
    
    @Mock
    private DishRepository dishRepository;
    
    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private MenuService menuService;

    private Menu menu;
    private Restaurant restaurant;
    private Dish dish1;
    private Dish dish2;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setCapacity(50);
        
        dish1 = new Dish();
        dish1.setId(1L);
        dish1.setName("Dish 1");
        dish1.setDescription("Description 1");
        dish1.setType(DishType.Carne);
        dish1.setIsAvailable(true);
        
        dish2 = new Dish();
        dish2.setId(2L);
        dish2.setName("Dish 2");
        dish2.setDescription("Description 2");
        dish2.setType(DishType.Sobremesa);
        dish2.setIsAvailable(true);
        
        menu = new Menu();
        menu.setId(1L);
        menu.setName("Test Menu");
        menu.setDescription("Test Description");
        menu.setRestaurant(restaurant);
        menu.setIsAvailable(true);
        menu.addDish(dish1);
    }

    @Test
    void testFindAll() {
        // Arrange
        when(menuRepository.findAll()).thenReturn(Collections.singletonList(menu));

        // Act
        List<Menu> result = menuService.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals(menu.getId(), result.get(0).getId());
        verify(menuRepository).findAll();
    }

    @Test
    void testFindById() {
        // Arrange
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));

        // Act
        Optional<Menu> result = menuService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(menu.getId(), result.get().getId());
        verify(menuRepository).findById(1L);
    }

    @Test
    void testFindByRestaurantId() {
        // Arrange
        when(menuRepository.findByRestaurantId(1L)).thenReturn(Collections.singletonList(menu));

        // Act
        List<Menu> result = menuService.findByRestaurantId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getRestaurant().getId());
        verify(menuRepository).findByRestaurantId(1L);
    }
    
    @Test
    void testFindAvailable() {
        // Arrange
        when(menuRepository.findByIsAvailableTrue()).thenReturn(Collections.singletonList(menu));

        // Act
        List<Menu> result = menuService.findAvailable();

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsAvailable());
        verify(menuRepository).findByIsAvailableTrue();
    }
    
    @Test
    void testFindAvailableByRestaurant() {
        // Arrange
        when(menuRepository.findByRestaurantIdAndIsAvailableTrue(1L)).thenReturn(Collections.singletonList(menu));

        // Act
        List<Menu> result = menuService.findAvailableByRestaurant(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getRestaurant().getId());
        assertTrue(result.get(0).getIsAvailable());
        verify(menuRepository).findByRestaurantIdAndIsAvailableTrue(1L);
    }
    
    @Test
    void testCreateMenu() {
        // Arrange
        Set<Long> dishIds = new HashSet<>();
        dishIds.add(1L);
        dishIds.add(2L);
        
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(dishRepository.findAllById(dishIds)).thenReturn(List.of(dish1, dish2));
        when(menuRepository.save(any(Menu.class))).thenAnswer(invocation -> {
            Menu savedMenu = invocation.getArgument(0);
            savedMenu.setId(2L);
            return savedMenu;
        });

        // Act
        Menu result = menuService.createMenu(
                1L, 
                "New Menu", 
                "New Description", 
                dishIds
        );

        // Assert
        assertEquals(2L, result.getId());
        assertEquals("New Menu", result.getName());
        assertEquals("New Description", result.getDescription());
        assertEquals(restaurant, result.getRestaurant());
        assertTrue(result.getIsAvailable());
        verify(restaurantRepository).findById(1L);
        verify(dishRepository).findAllById(dishIds);
        verify(menuRepository, org.mockito.Mockito.times(2)).save(any(Menu.class));
    }
    
    @Test
    void testCreateMenu_RestaurantNotFound() {
        // Arrange
        Set<Long> dishIds = new HashSet<>();
        dishIds.add(1L);
        
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            menuService.createMenu(
                    99L, 
                    "New Menu", 
                    "New Description", 
                    dishIds
            );
        });
        
        assertEquals("Restaurant not found", exception.getMessage());
    }
    
    @Test
    void testUpdateAvailability() {
        // Arrange
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
        when(menuRepository.save(any(Menu.class))).thenReturn(menu);

        // Act
        Optional<Menu> result = menuService.updateAvailability(1L, false);

        // Assert
        assertTrue(result.isPresent());
        assertFalse(result.get().getIsAvailable());
        verify(menuRepository).findById(1L);
        verify(menuRepository).save(menu);
    }
    
    @Test
    void testAddDishToMenu() {
        // Arrange
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
        when(dishRepository.findById(2L)).thenReturn(Optional.of(dish2));
        when(menuRepository.save(any(Menu.class))).thenReturn(menu);

        // Act
        Optional<Menu> result = menuService.addDishToMenu(1L, 2L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(2, result.get().getDishes().size());
        verify(menuRepository).findById(1L);
        verify(dishRepository).findById(2L);
        verify(menuRepository).save(menu);
    }
    
    @Test
    void testRemoveDishFromMenu() {
        // Arrange
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
        when(dishRepository.findById(1L)).thenReturn(Optional.of(dish1));
        when(menuRepository.save(any(Menu.class))).thenReturn(menu);

        // Act
        Optional<Menu> result = menuService.removeDishFromMenu(1L, 1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(0, result.get().getDishes().size());
        verify(menuRepository).findById(1L);
        verify(dishRepository).findById(1L);
        verify(menuRepository).save(menu);
    }

    @Test
    void testSave() {
        // Arrange
        when(menuRepository.save(menu)).thenReturn(menu);

        // Act
        Menu result = menuService.save(menu);

        // Assert
        assertEquals(menu.getId(), result.getId());
        verify(menuRepository).save(menu);
    }
} 