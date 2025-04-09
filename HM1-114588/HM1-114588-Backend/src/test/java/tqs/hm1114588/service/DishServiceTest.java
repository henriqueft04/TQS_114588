package tqs.hm1114588.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import tqs.hm1114588.repository.DishRepository;

@ExtendWith(MockitoExtension.class)
class DishServiceTest {

    @Mock
    private DishRepository dishRepository;

    @InjectMocks
    private DishService dishService;

    private Dish dish;

    @BeforeEach
    void setUp() {
        dish = new Dish();
        dish.setId(1L);
        dish.setName("Test Dish");
        dish.setDescription("Test Description");
        dish.setType(DishType.Carne);
        dish.setIsAvailable(true);
        dish.setPrice(new BigDecimal("9.99"));
    }

    @Test
    void testFindAll() {
        // Arrange
        when(dishRepository.findAll()).thenReturn(Collections.singletonList(dish));

        // Act
        List<Dish> result = dishService.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals(dish.getId(), result.get(0).getId());
        verify(dishRepository).findAll();
    }

    @Test
    void testFindById() {
        // Arrange
        when(dishRepository.findById(1L)).thenReturn(Optional.of(dish));

        // Act
        Optional<Dish> result = dishService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(dish.getId(), result.get().getId());
        verify(dishRepository).findById(1L);
    }

    @Test
    void testFindByType() {
        // Arrange
        when(dishRepository.findByType(DishType.Carne)).thenReturn(Collections.singletonList(dish));

        // Act
        List<Dish> result = dishService.findByType(DishType.Carne);

        // Assert
        assertEquals(1, result.size());
        assertEquals(DishType.Carne, result.get(0).getType());
        verify(dishRepository).findByType(DishType.Carne);
    }

    @Test
    void testFindAvailable() {
        // Arrange
        when(dishRepository.findByIsAvailableTrue()).thenReturn(Collections.singletonList(dish));

        // Act
        List<Dish> result = dishService.findAvailable();

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsAvailable());
        verify(dishRepository).findByIsAvailableTrue();
    }

    @Test
    void testFindByNameContaining() {
        // Arrange
        when(dishRepository.findByNameContainingIgnoreCase("Dish")).thenReturn(Collections.singletonList(dish));

        // Act
        List<Dish> result = dishService.findByNameContaining("Dish");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Dish", result.get(0).getName());
        verify(dishRepository).findByNameContainingIgnoreCase("Dish");
    }

    @Test
    void testCreateDish() {
        // Arrange
        when(dishRepository.save(any(Dish.class))).thenAnswer(invocation -> {
            Dish savedDish = invocation.getArgument(0);
            savedDish.setId(1L);
            return savedDish;
        });

        // Act
        Dish result = dishService.createDish("New Dish", "New Description", DishType.Sobremesa, 12.99);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("New Dish", result.getName());
        assertEquals("New Description", result.getDescription());
        assertEquals(DishType.Sobremesa, result.getType());
        assertEquals(new BigDecimal("12.99"), result.getPrice());
        assertTrue(result.getIsAvailable());
        verify(dishRepository).save(any(Dish.class));
    }

    @Test
    void testUpdateAvailability() {
        // Arrange
        when(dishRepository.findById(1L)).thenReturn(Optional.of(dish));
        when(dishRepository.save(any(Dish.class))).thenReturn(dish);

        // Act
        Optional<Dish> result = dishService.updateAvailability(1L, false);

        // Assert
        assertTrue(result.isPresent());
        assertFalse(result.get().getIsAvailable());
        verify(dishRepository).findById(1L);
        verify(dishRepository).save(dish);
    }

    @Test
    void testSave() {
        // Arrange
        when(dishRepository.save(dish)).thenReturn(dish);

        // Act
        Dish result = dishService.save(dish);

        // Assert
        assertEquals(dish.getId(), result.getId());
        verify(dishRepository).save(dish);
    }
} 