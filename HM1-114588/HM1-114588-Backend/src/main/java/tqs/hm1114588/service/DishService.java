package tqs.hm1114588.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tqs.hm1114588.model.restaurant.Dish;
import tqs.hm1114588.model.restaurant.DishType;
import tqs.hm1114588.repository.DishRepository;

@Service
public class DishService {

    @Autowired
    private DishRepository dishRepository;

    /**
     * Find all dishes
     * @return List of all dishes
     */
    @Cacheable(value = "dishes")
    public List<Dish> findAll() {
        return dishRepository.findAll();
    }

    /**
     * Find dish by ID
     * @param id Dish ID
     * @return Dish if found
     */
    @Cacheable(value = "dish", key = "#id")
    public Optional<Dish> findById(Long id) {
        return dishRepository.findById(id);
    }

    /**
     * Find dishes by type
     * @param type Dish type
     * @return List of dishes
     */
    @Cacheable(value = "dishesByType", key = "#type")
    public List<Dish> findByType(DishType type) {
        return dishRepository.findByType(type);
    }

    /**
     * Find available dishes
     * @return List of available dishes
     */
    @Cacheable(value = "availableDishes")
    public List<Dish> findAvailable() {
        return dishRepository.findByIsAvailableTrue();
    }

    /**
     * Find dishes by name containing
     * @param name Name pattern
     * @return List of dishes
     */
    @Cacheable(value = "dishesByName", key = "#name")
    public List<Dish> findByNameContaining(String name) {
        return dishRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Create a new dish
     * @param name Dish name
     * @param description Dish description
     * @param type Dish type
     * @param price Dish price
     * @return Created dish
     */
    @Transactional
    @CachePut(value = "dish", key = "#result.id")
    @CacheEvict(value = {"dishes", "dishesByType", "availableDishes", "dishesByName"}, allEntries = true)
    public Dish createDish(String name, String description, DishType type, double price) {
        Dish dish = new Dish();
        dish.setName(name);
        dish.setDescription(description);
        dish.setType(type);
        dish.setIsAvailable(true);
        dish.setPrice(BigDecimal.valueOf(price));
        
        return dishRepository.save(dish);
    }

    /**
     * Update dish availability
     * @param id Dish ID
     * @param isAvailable Availability status
     * @return Updated dish if found
     */
    @Transactional
    @CachePut(value = "dish", key = "#id")
    @CacheEvict(value = {"dishes", "dishesByType", "availableDishes", "dishesByName"}, allEntries = true)
    public Optional<Dish> updateAvailability(Long id, Boolean isAvailable) {
        return dishRepository.findById(id)
                .map(dish -> {
                    dish.setIsAvailable(isAvailable);
                    return dishRepository.save(dish);
                });
    }

    /**
     * Save dish
     * @param dish Dish to save
     * @return Saved dish
     */
    @Transactional
    @CachePut(value = "dish", key = "#result.id")
    @CacheEvict(value = {"dishes", "dishesByType", "availableDishes", "dishesByName"}, allEntries = true)
    public Dish save(Dish dish) {
        return dishRepository.save(dish);
    }

    /**
     * Delete dish by ID
     * @param id Dish ID
     */
    @Transactional
    @CacheEvict(value = {"dish", "dishes", "dishesByType", "availableDishes", "dishesByName"}, allEntries = true)
    public void deleteById(Long id) {
        dishRepository.deleteById(id);
    }
} 