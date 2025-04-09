package tqs.hm1114588.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import tqs.hm1114588.model.restaurant.Menu;
import tqs.hm1114588.service.MenuService;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * Get all menus
     * @return List of menus
     */
    @GetMapping
    public List<Menu> getAllMenus() {
        return menuService.findAll();
    }

    /**
     * Get menu by ID
     * @param id Menu ID
     * @return Menu if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Menu> getMenu(@PathVariable Long id) {
        return menuService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get menus by restaurant
     * @param restaurantId Restaurant ID
     * @return List of menus
     */
    @GetMapping("/restaurant/{restaurantId}")
    public List<Menu> getMenusByRestaurant(@PathVariable Long restaurantId) {
        return menuService.findByRestaurantId(restaurantId);
    }
    
    /**
     * Get available menus
     * @return List of available menus
     */
    @GetMapping("/available")
    public List<Menu> getAvailableMenus() {
        return menuService.findAvailable();
    }
    
    /**
     * Get available menus by restaurant
     * @param restaurantId Restaurant ID
     * @return List of available menus
     */
    @GetMapping("/restaurant/{restaurantId}/available")
    public List<Menu> getAvailableMenusByRestaurant(@PathVariable Long restaurantId) {
        return menuService.findAvailableByRestaurant(restaurantId);
    }

    /**
     * Create a new menu
     * @param request Menu information
     * @return Created menu
     */
    @PostMapping
    public ResponseEntity<?> createMenu(@RequestBody Map<String, Object> request) {
        try {
            Long restaurantId = Long.valueOf(request.get("restaurantId").toString());
            String name = (String) request.get("name");
            String description = (String) request.get("description");
            
            @SuppressWarnings("unchecked")
            List<Integer> dishIdsList = (List<Integer>) request.get("dishIds");
            Set<Long> dishIds = new HashSet<>();
            
            for (Integer id : dishIdsList) {
                dishIds.add(id.longValue());
            }
            
            Menu menu = menuService.createMenu(restaurantId, name, description, dishIds);
            return ResponseEntity.ok(menu);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update menu availability
     * @param id Menu ID
     * @param request Update information
     * @return Updated menu
     */
    @PutMapping("/{id}/availability")
    public ResponseEntity<?> updateAvailability(@PathVariable Long id, @RequestBody Map<String, Boolean> request) {
        Boolean isAvailable = request.get("isAvailable");
        
        return menuService.updateAvailability(id, isAvailable)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Add dish to menu
     * @param id Menu ID
     * @param request Dish information
     * @return Updated menu
     */
    @PutMapping("/{id}/dishes/add")
    public ResponseEntity<?> addDishToMenu(@PathVariable Long id, @RequestBody Map<String, Long> request) {
        Long dishId = request.get("dishId");
        
        return menuService.addDishToMenu(id, dishId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Remove dish from menu
     * @param id Menu ID
     * @param request Dish information
     * @return Updated menu
     */
    @PutMapping("/{id}/dishes/remove")
    public ResponseEntity<?> removeDishFromMenu(@PathVariable Long id, @RequestBody Map<String, Long> request) {
        Long dishId = request.get("dishId");
        
        return menuService.removeDishFromMenu(id, dishId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete menu
     * @param id Menu ID
     * @return Success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
} 