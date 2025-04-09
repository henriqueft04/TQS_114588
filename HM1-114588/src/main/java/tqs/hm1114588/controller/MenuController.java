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
    public ResponseEntity<Menu> getMenuById(@PathVariable Long id) {
        return menuService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get menus by restaurant ID
     * @param restaurantId Restaurant ID
     * @return List of menus
     */
    @GetMapping("/restaurant/{restaurantId}")
    public List<Menu> getMenusByRestaurant(@PathVariable Long restaurantId) {
        return menuService.findByRestaurantId(restaurantId);
    }

    /**
     * Create a new menu
     * @param menu Menu data
     * @return Created menu
     */
    @PostMapping
    public Menu createMenu(@RequestBody Menu menu) {
        return menuService.save(menu);
    }

    /**
     * Update a menu
     * @param id Menu ID
     * @param menu Menu data
     * @return Updated menu if found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Menu> updateMenu(
            @PathVariable Long id,
            @RequestBody Menu menu) {
        return menuService.findById(id)
                .map(existingMenu -> {
                    existingMenu.setName(menu.getName());
                    existingMenu.setDescription(menu.getDescription());
                    existingMenu.setPrice(menu.getPrice());
                    existingMenu.setIsAvailable(menu.getIsAvailable());
                    return menuService.save(existingMenu);
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a menu
     * @param id Menu ID
     * @return No content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        return menuService.findById(id)
                .map(menu -> {
                    menuService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 