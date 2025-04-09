package tqs.hm1114588.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tqs.hm1114588.model.restaurant.Restaurant;
import tqs.hm1114588.repository.RestaurantRepository;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    /**
     * Find all restaurants
     * @return List of all restaurants
     */
    @Cacheable(value = "restaurants")
    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    /**
     * Find restaurant by ID
     * @param id Restaurant ID
     * @return Restaurant if found
     */
    @Cacheable(value = "restaurant", key = "#id")
    public Optional<Restaurant> findById(Long id) {
        return restaurantRepository.findById(id);
    }

    /**
     * Find restaurant by name
     * @param name Restaurant name
     * @return Restaurant if found
     */
    @Cacheable(value = "restaurantByName", key = "#name")
    public Optional<Restaurant> findByName(String name) {
        return restaurantRepository.findByName(name);
    }

    /**
     * Save restaurant
     * @param restaurant Restaurant to save
     * @return Saved restaurant
     */
    @Transactional
    @CachePut(value = "restaurant", key = "#result.id")
    @CacheEvict(value = {"restaurants", "restaurantByName"}, allEntries = true)
    public Restaurant save(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    /**
     * Create a new restaurant
     * @param name Restaurant name
     * @param capacity Restaurant capacity
     * @return Created restaurant
     */
    @Transactional
    @CachePut(value = "restaurant", key = "#result.id")
    @CacheEvict(value = {"restaurants", "restaurantByName"}, allEntries = true)
    public Restaurant createRestaurant(String name, Integer capacity) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(name);
        restaurant.setCapacity(capacity);
        return restaurantRepository.save(restaurant);
    }

    /**
     * Delete restaurant by ID
     * @param id Restaurant ID
     */
    @Transactional
    @CacheEvict(value = {"restaurant", "restaurants", "restaurantByName"}, allEntries = true)
    public void deleteById(Long id) {
        restaurantRepository.deleteById(id);
    }
} 