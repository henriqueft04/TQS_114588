package tqs.hm1114588.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<List<Menu>> getAllMenus() {
        List<Menu> menus = menuService.findAll();
        return ResponseEntity.ok(menus);
    }

    /**
     * Get menu by ID
     * @param id Menu ID
     * @return Menu if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Menu> getMenuById(@PathVariable Long id) {
        Optional<Menu> menu = menuService.findById(id);
        return menu.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get menus by meal
     * @param mealId Meal ID
     * @return List of menus
     */
    @GetMapping("/meal/{mealId}")
    public ResponseEntity<List<Menu>> getMenusByMeal(@PathVariable Long mealId) {
        List<Menu> menus = menuService.findByMeal(mealId);
        return ResponseEntity.ok(menus);
    }

    /**
     * Get menus by restaurant
     * @param restaurantId Restaurant ID
     * @return List of menus
     */
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Menu>> getMenusByRestaurant(@PathVariable Long restaurantId) {
        List<Menu> menus = menuService.findByRestaurant(restaurantId);
        return ResponseEntity.ok(menus);
    }

    /**
     * Get available menus by restaurant
     * @param restaurantId Restaurant ID
     * @return List of available menus
     */
    @GetMapping("/restaurant/{restaurantId}/available")
    public ResponseEntity<List<Menu>> getAvailableMenusByRestaurant(@PathVariable Long restaurantId) {
        List<Menu> menus = menuService.findAvailableByRestaurant(restaurantId);
        return ResponseEntity.ok(menus);
    }

    /**
     * Create a new menu
     * @param requestBody Request containing meal ID and menu information
     * @return Created menu
     */
    @PostMapping
    public ResponseEntity<Menu> createMenu(@RequestBody Map<String, Object> requestBody) {
        try {
            Long mealId = Long.valueOf(requestBody.get("mealId").toString());
            
            Menu menu = new Menu();
            menu.setName((String) requestBody.get("name"));
            menu.setDescription((String) requestBody.get("description"));
            
            Menu savedMenu = menuService.createMenu(mealId, menu);
            return ResponseEntity.ok(savedMenu);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid request: " + e.getMessage());
        }
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
    
    /**
     * Exception handler for IllegalArgumentException
     * @param e Exception
     * @return Error response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }
} 