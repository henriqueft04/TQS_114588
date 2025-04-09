package tqs.hm1114588.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tqs.hm1114588.model.Location;
import tqs.hm1114588.model.restaurant.Restaurant;
import tqs.hm1114588.service.LocationService;
import tqs.hm1114588.service.RestaurantService;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;
    
    @Autowired
    private LocationService locationService;

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
     * Get available capacity for a restaurant at a specific time
     * @param id Restaurant ID
     * @param startTime Start time
     * @param endTime End time
     * @return Available capacity
     */
    @GetMapping("/{id}/available-capacity")
    public ResponseEntity<Integer> getAvailableCapacity(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            Integer availableCapacity = restaurantService.getAvailableCapacity(id, startTime, endTime);
            return ResponseEntity.ok(availableCapacity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Check if a restaurant has capacity for a reservation
     * @param id Restaurant ID
     * @param request Request parameters
     * @return Boolean indicating if capacity is available
     */
    @PostMapping("/{id}/check-capacity")
    public ResponseEntity<?> checkCapacity(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            LocalDateTime reservationTime = LocalDateTime.parse((String) request.get("reservationTime"));
            Integer partySize = Integer.valueOf(request.get("partySize").toString());
            
            boolean hasCapacity = restaurantService.hasCapacityForReservation(id, reservationTime, partySize);
            
            return ResponseEntity.ok(Map.of(
                "hasCapacity", hasCapacity
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
    
    /**
     * Update restaurant capacity
     * @param id Restaurant ID
     * @param request Capacity update data
     * @return Updated restaurant
     */
    @PutMapping("/{id}/capacity")
    public ResponseEntity<?> updateCapacity(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer capacity = request.get("capacity");
        
        return restaurantService.updateCapacity(id, capacity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update available menus count
     * @param id Restaurant ID
     * @param request Menus update data
     * @return Updated restaurant
     */
    @PutMapping("/{id}/menus")
    public ResponseEntity<?> updateAvailableMenus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer availableMenus = request.get("availableMenus");
        
        return restaurantService.updateAvailableMenus(id, availableMenus)
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
        // Save the location first if it exists
        if (restaurant.getLocation() != null) {
            Location savedLocation = locationService.save(restaurant.getLocation());
            restaurant.setLocation(savedLocation);
        }
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
                    
                    // Handle location update
                    if (restaurant.getLocation() != null) {
                        Location savedLocation = locationService.save(restaurant.getLocation());
                        existingRestaurant.setLocation(savedLocation);
                    }
                    
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