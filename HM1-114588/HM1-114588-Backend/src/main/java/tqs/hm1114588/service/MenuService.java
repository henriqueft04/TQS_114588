package tqs.hm1114588.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tqs.hm1114588.model.restaurant.Dish;
import tqs.hm1114588.model.restaurant.Menu;
import tqs.hm1114588.model.restaurant.Restaurant;
import tqs.hm1114588.repository.DishRepository;
import tqs.hm1114588.repository.MenuRepository;
import tqs.hm1114588.repository.RestaurantRepository;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;
    
    @Autowired
    private DishRepository dishRepository;
    
    @Autowired
    private RestaurantRepository restaurantRepository;

    /**
     * Find all menus
     * @return List of all menus
     */
    @Cacheable(value = "menus")
    public List<Menu> findAll() {
        return menuRepository.findAll();
    }

    /**
     * Find menu by ID
     * @param id Menu ID
     * @return Menu if found
     */
    @Cacheable(value = "menu", key = "#id")
    public Optional<Menu> findById(Long id) {
        return menuRepository.findById(id);
    }

    /**
     * Find menus by restaurant ID
     * @param restaurantId Restaurant ID
     * @return List of menus
     */
    @Cacheable(value = "menusByRestaurant", key = "#restaurantId")
    public List<Menu> findByRestaurantId(Long restaurantId) {
        return menuRepository.findByRestaurantId(restaurantId);
    }
    
    /**
     * Find available menus
     * @return List of available menus
     */
    @Cacheable(value = "availableMenus")
    public List<Menu> findAvailable() {
        return menuRepository.findByIsAvailableTrue();
    }
    
    /**
     * Find available menus by restaurant
     * @param restaurantId Restaurant ID
     * @return List of available menus
     */
    @Cacheable(value = "availableMenusByRestaurant", key = "#restaurantId")
    public List<Menu> findAvailableByRestaurant(Long restaurantId) {
        return menuRepository.findByRestaurantIdAndIsAvailableTrue(restaurantId);
    }

    /**
     * Create a new menu
     * @param restaurantId Restaurant ID
     * @param name Menu name
     * @param description Menu description
     * @param price Menu price
     * @param dishIds Dish IDs to include
     * @return Created menu
     */
    @Transactional
    @CachePut(value = "menu", key = "#result.id")
    @CacheEvict(value = {"menus", "menusByRestaurant", "availableMenus", "availableMenusByRestaurant"}, allEntries = true)
    public Menu createMenu(Long restaurantId, String name, String description, BigDecimal price, Set<Long> dishIds) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        
        List<Dish> dishes = dishRepository.findAllById(dishIds);
        if (dishes.isEmpty() || dishes.size() != dishIds.size()) {
            throw new IllegalArgumentException("One or more dishes not found");
        }
        
        Menu menu = new Menu();
        menu.setName(name);
        menu.setDescription(description);
        menu.setPrice(price);
        menu.setRestaurant(restaurant);
        menu.setIsAvailable(true);
        
        Menu savedMenu = menuRepository.save(menu);
        
        // Add dishes to menu
        for (Dish dish : dishes) {
            savedMenu.addDish(dish);
        }
        
        return menuRepository.save(savedMenu);
    }
    
    /**
     * Update menu availability
     * @param id Menu ID
     * @param isAvailable Availability status
     * @return Updated menu if found
     */
    @Transactional
    @CachePut(value = "menu", key = "#id")
    @CacheEvict(value = {"menus", "menusByRestaurant", "availableMenus", "availableMenusByRestaurant"}, allEntries = true)
    public Optional<Menu> updateAvailability(Long id, Boolean isAvailable) {
        return menuRepository.findById(id)
                .map(menu -> {
                    menu.setIsAvailable(isAvailable);
                    return menuRepository.save(menu);
                });
    }
    
    /**
     * Add dish to menu
     * @param menuId Menu ID
     * @param dishId Dish ID
     * @return Updated menu if found
     */
    @Transactional
    @CachePut(value = "menu", key = "#menuId")
    @CacheEvict(value = {"menus", "menusByRestaurant", "availableMenus", "availableMenusByRestaurant"}, allEntries = true)
    public Optional<Menu> addDishToMenu(Long menuId, Long dishId) {
        Optional<Menu> menuOpt = menuRepository.findById(menuId);
        Optional<Dish> dishOpt = dishRepository.findById(dishId);
        
        if (menuOpt.isPresent() && dishOpt.isPresent()) {
            Menu menu = menuOpt.get();
            Dish dish = dishOpt.get();
            
            menu.addDish(dish);
            return Optional.of(menuRepository.save(menu));
        }
        
        return Optional.empty();
    }
    
    /**
     * Remove dish from menu
     * @param menuId Menu ID
     * @param dishId Dish ID
     * @return Updated menu if found
     */
    @Transactional
    @CachePut(value = "menu", key = "#menuId")
    @CacheEvict(value = {"menus", "menusByRestaurant", "availableMenus", "availableMenusByRestaurant"}, allEntries = true)
    public Optional<Menu> removeDishFromMenu(Long menuId, Long dishId) {
        Optional<Menu> menuOpt = menuRepository.findById(menuId);
        Optional<Dish> dishOpt = dishRepository.findById(dishId);
        
        if (menuOpt.isPresent() && dishOpt.isPresent()) {
            Menu menu = menuOpt.get();
            Dish dish = dishOpt.get();
            
            menu.removeDish(dish);
            return Optional.of(menuRepository.save(menu));
        }
        
        return Optional.empty();
    }

    /**
     * Save menu
     * @param menu Menu to save
     * @return Saved menu
     */
    @Transactional
    @CachePut(value = "menu", key = "#result.id")
    @CacheEvict(value = {"menus", "menusByRestaurant", "availableMenus", "availableMenusByRestaurant"}, allEntries = true)
    public Menu save(Menu menu) {
        return menuRepository.save(menu);
    }

    /**
     * Delete menu by ID
     * @param id Menu ID
     */
    @Transactional
    @CacheEvict(value = {"menu", "menus", "menusByRestaurant", "availableMenus", "availableMenusByRestaurant"}, allEntries = true)
    public void deleteById(Long id) {
        menuRepository.deleteById(id);
    }
} 