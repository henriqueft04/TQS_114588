package tqs.hm1114588.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void testCreateMenu() {
        // Arrange
        Menu newMenu = new Menu();
        newMenu.setName("New Menu");
        newMenu.setDescription("New Description");
        newMenu.setIsAvailable(true);
        
        when(menuRepository.save(any(Menu.class))).thenReturn(menu);

        // Act
        Menu result = menuService.createMenu(1L, newMenu);

        // Assert
        assertEquals(menu.getId(), result.getId());
        verify(menuRepository).save(any(Menu.class));
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
    
    @Test
    void testDeleteById() {
        // Arrange
        
        // Act
        menuService.deleteById(1L);
        
        // Assert
        verify(menuRepository).deleteById(1L);
    }
} 