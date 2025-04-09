package tqs.hm1114588.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import tqs.hm1114588.model.restaurant.Menu;
import tqs.hm1114588.model.restaurant.Restaurant;
import tqs.hm1114588.repository.MenuRepository;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private MenuService menuService;

    private Menu menu;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        // Set up test data
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setCapacity(50);
        restaurant.setAvailableMenus(40);

        menu = new Menu();
        menu.setId(1L);
        menu.setName("Test Menu");
        menu.setDescription("Test Description");
        menu.setPrice(new BigDecimal("19.99"));
        menu.setIsAvailable(true);
        menu.setRestaurant(restaurant);
    }

    @Test
    void testFindAll() {
        // Arrange
        List<Menu> menus = Arrays.asList(menu);
        when(menuRepository.findAll()).thenReturn(menus);

        // Act
        List<Menu> result = menuService.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals(menu.getId(), result.get(0).getId());
        verify(menuRepository).findAll();
    }

    @Test
    void testFindById_WhenExists() {
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
    void testFindById_WhenNotExists() {
        // Arrange
        when(menuRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Menu> result = menuService.findById(99L);

        // Assert
        assertFalse(result.isPresent());
        verify(menuRepository).findById(99L);
    }

    @Test
    void testFindByRestaurantId() {
        // Arrange
        List<Menu> menus = Arrays.asList(menu);
        when(menuRepository.findByRestaurantId(1L)).thenReturn(menus);

        // Act
        List<Menu> result = menuService.findByRestaurantId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getRestaurant().getId());
        verify(menuRepository).findByRestaurantId(1L);
    }

    @Test
    void testSave() {
        // Arrange
        when(menuRepository.save(any(Menu.class))).thenReturn(menu);

        // Act
        Menu result = menuService.save(menu);

        // Assert
        assertNotNull(result);
        assertEquals(menu.getId(), result.getId());
        verify(menuRepository).save(menu);
    }

    @Test
    void testDeleteById() {
        // Arrange
        doNothing().when(menuRepository).deleteById(1L);

        // Act
        menuService.deleteById(1L);

        // Assert
        verify(menuRepository).deleteById(1L);
    }
} 