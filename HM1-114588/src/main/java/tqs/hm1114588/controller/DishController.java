package tqs.hm1114588.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import tqs.hm1114588.model.restaurant.Dish;
import tqs.hm1114588.model.restaurant.DishType;
import tqs.hm1114588.service.DishService;

@RestController
@RequestMapping("/api/dishes")
public class DishController {
    
    @Autowired
    private DishService dishService;
    
    /**
     * Get all dishes
     * @return List of dishes
     */
    @GetMapping
    public List<Dish> getAllDishes() {
        return dishService.findAll();
    }
    
    /**
     * Get dish by ID
     * @param id Dish ID
     * @return Dish if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Dish> getDish(@PathVariable Long id) {
        return dishService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get dishes by type
     * @param type Dish type
     * @return List of dishes
     */
    @GetMapping("/type/{type}")
    public List<Dish> getDishesByType(@PathVariable DishType type) {
        return dishService.findByType(type);
    }
    
    /**
     * Get available dishes
     * @return List of available dishes
     */
    @GetMapping("/available")
    public List<Dish> getAvailableDishes() {
        return dishService.findAvailable();
    }
    
    /**
     * Search dishes by name
     * @param query Search query
     * @return List of matching dishes
     */
    @GetMapping("/search")
    public List<Dish> searchDishes(@RequestParam String query) {
        return dishService.findByNameContaining(query);
    }
    
    /**
     * Create a new dish
     * @param request Dish information
     * @return Created dish
     */
    @PostMapping
    public ResponseEntity<Dish> createDish(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String description = request.get("description");
        DishType type = DishType.valueOf(request.get("type"));
        
        Dish dish = dishService.createDish(name, description, type);
        return ResponseEntity.ok(dish);
    }
    
    /**
     * Update dish availability
     * @param id Dish ID
     * @param request Update information
     * @return Updated dish
     */
    @PutMapping("/{id}/availability")
    public ResponseEntity<?> updateAvailability(@PathVariable Long id, @RequestBody Map<String, Boolean> request) {
        Boolean isAvailable = request.get("isAvailable");
        
        return dishService.updateAvailability(id, isAvailable)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Delete dish
     * @param id Dish ID
     * @return Success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDish(@PathVariable Long id) {
        dishService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
} 