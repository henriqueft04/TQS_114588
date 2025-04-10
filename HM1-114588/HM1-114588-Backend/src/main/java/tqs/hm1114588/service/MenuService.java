package tqs.hm1114588.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tqs.hm1114588.model.restaurant.Meal;
import tqs.hm1114588.model.restaurant.Menu;
import tqs.hm1114588.repository.DishRepository;
import tqs.hm1114588.repository.MenuRepository;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;
    
    @Autowired
    private DishRepository dishRepository;

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
     * Find menus by meal
     * @param mealId Meal ID
     * @return List of menus for the specified meal
     */
    @Cacheable(value = "menusByMeal", key = "#mealId")
    public List<Menu> findByMeal(Long mealId) {
        return menuRepository.findByMealId(mealId);
    }

    /**
     * Find menus by restaurant
     * @param restaurantId Restaurant ID
     * @return List of menus for the specified restaurant
     */
    @Cacheable(value = "menusByRestaurant", key = "#restaurantId")
    public List<Menu> findByRestaurant(Long restaurantId) {
        return menuRepository.findByRestaurantId(restaurantId);
    }

    /**
     * Find available menus by restaurant
     * @param restaurantId Restaurant ID
     * @return List of available menus for the specified restaurant
     */
    @Cacheable(value = "availableMenusByRestaurant", key = "#restaurantId")
    public List<Menu> findAvailableByRestaurant(Long restaurantId) {
        return menuRepository.findByRestaurantIdAndIsAvailableTrue(restaurantId);
    }

    /**
     * Save menu
     * @param menu Menu to save
     * @return Saved menu
     */
    @Transactional
    @CachePut(value = "menu", key = "#result.id")
    @CacheEvict(value = {"menus", "menusByMeal"}, allEntries = true)
    public Menu save(Menu menu) {
        return menuRepository.save(menu);
    }

    /**
     * Create a new menu for a meal
     * @param mealId Meal ID
     * @param menu Menu to create
     * @return Created menu
     */
    @Transactional
    @CachePut(value = "menu", key = "#result.id")
    @CacheEvict(value = {"menus", "menusByMeal"}, allEntries = true)
    public Menu createMenu(Long mealId, Menu menu) {
        Meal meal = new Meal();
        meal.setId(mealId);
        menu.setMeal(meal);
        return menuRepository.save(menu);
    }

    /**
     * Delete menu by ID
     * @param id Menu ID
     */
    @Transactional
    @CacheEvict(value = {"menu", "menus", "menusByMeal"}, allEntries = true)
    public void deleteById(Long id) {
        menuRepository.deleteById(id);
    }
} 