package tqs.hm1114588.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tqs.hm1114588.model.restaurant.Restaurant;
import tqs.hm1114588.service.RestaurantService;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    /**
     * Get all restaurants
     * @return List of restaurants
     */
    @GetMapping
    public List<Restaurant> getAllRestaurants() {
        return restaurantService.findAll();
    }

    /**
     * Get restaurant by ID
     * @param id Restaurant ID
     * @return Restaurant if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long id) {
        return restaurantService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get restaurant by name
     * @param name Restaurant name
     * @return Restaurant if found
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<Restaurant> getRestaurantByName(@PathVariable String name) {
        return restaurantService.findByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new restaurant
     * @param restaurant Restaurant data
     * @return Created restaurant
     */
    @PostMapping
    public Restaurant createRestaurant(@RequestBody Restaurant restaurant) {
        return restaurantService.save(restaurant);
    }

    /**
     * Update a restaurant
     * @param id Restaurant ID
     * @param restaurant Restaurant data
     * @return Updated restaurant if found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(
            @PathVariable Long id,
            @RequestBody Restaurant restaurant) {
        return restaurantService.findById(id)
                .map(existingRestaurant -> {
                    existingRestaurant.setName(restaurant.getName());
                    existingRestaurant.setLocation(restaurant.getLocation());
                    existingRestaurant.setCapacity(restaurant.getCapacity());
                    existingRestaurant.setAvailableMenus(restaurant.getAvailableMenus());
                    return restaurantService.save(existingRestaurant);
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a restaurant
     * @param id Restaurant ID
     * @return No content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        return restaurantService.findById(id)
                .map(restaurant -> {
                    restaurantService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 