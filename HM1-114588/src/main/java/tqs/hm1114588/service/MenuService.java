package tqs.hm1114588.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tqs.hm1114588.model.Menu;
import tqs.hm1114588.repository.MenuRepository;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

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
     * Save menu
     * @param menu Menu to save
     * @return Saved menu
     */
    @Transactional
    @CachePut(value = "menu", key = "#result.id")
    @CacheEvict(value = {"menus", "menusByRestaurant"}, allEntries = true)
    public Menu save(Menu menu) {
        return menuRepository.save(menu);
    }

    /**
     * Delete menu by ID
     * @param id Menu ID
     */
    @Transactional
    @CacheEvict(value = {"menu", "menus", "menusByRestaurant"}, allEntries = true)
    public void deleteById(Long id) {
        menuRepository.deleteById(id);
    }
} 