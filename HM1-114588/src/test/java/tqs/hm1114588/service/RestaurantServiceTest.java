package tqs.hm1114588.service;

import java.util.Arrays;
import java.util.Collections;
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

import tqs.hm1114588.model.restaurant.Restaurant;
import tqs.hm1114588.repository.RestaurantRepository;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        // Set up test data
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setCapacity(50);
        restaurant.setAvailableMenus(40);
    }

    @Test
    void testFindAll() {
        // Arrange
        List<Restaurant> restaurants = Collections.singletonList(restaurant);
        when(restaurantRepository.findAll()).thenReturn(restaurants);

        // Act
        List<Restaurant> result = restaurantService.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals(restaurant.getId(), result.get(0).getId());
        verify(restaurantRepository).findAll();
    }

    @Test
    void testFindById_WhenExists() {
        // Arrange
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        // Act
        Optional<Restaurant> result = restaurantService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(restaurant.getId(), result.get().getId());
        verify(restaurantRepository).findById(1L);
    }

    @Test
    void testFindById_WhenNotExists() {
        // Arrange
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Restaurant> result = restaurantService.findById(99L);

        // Assert
        assertFalse(result.isPresent());
        verify(restaurantRepository).findById(99L);
    }

    @Test
    void testFindByName() {
        // Arrange
        when(restaurantRepository.findByName("Test Restaurant")).thenReturn(Optional.of(restaurant));

        // Act
        Optional<Restaurant> result = restaurantService.findByName("Test Restaurant");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Restaurant", result.get().getName());
        verify(restaurantRepository).findByName("Test Restaurant");
    }

    @Test
    void testSave() {
        // Arrange
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        // Act
        Restaurant result = restaurantService.save(restaurant);

        // Assert
        assertNotNull(result);
        assertEquals(restaurant.getId(), result.getId());
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void testCreateRestaurant() {
        // Arrange
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(invocation -> {
            Restaurant savedRestaurant = invocation.getArgument(0);
            savedRestaurant.setId(1L);
            return savedRestaurant;
        });

        // Act
        Restaurant result = restaurantService.createRestaurant("New Restaurant", 100);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Restaurant", result.getName());
        assertEquals(100, result.getCapacity());
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    void testDeleteById() {
        // Arrange
        doNothing().when(restaurantRepository).deleteById(1L);

        // Act
        restaurantService.deleteById(1L);

        // Assert
        verify(restaurantRepository).deleteById(1L);
    }
} 