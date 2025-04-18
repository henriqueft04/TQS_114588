package tqs.hm1114588.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tqs.hm1114588.model.restaurant.Menu;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    
    /**
     * Find menus by meal ID
     * @param mealId Meal ID
     * @return List of menus
     */
    List<Menu> findByMealId(Long mealId);
    
    /**
     * Find available menus
     * @return List of available menus
     */
    List<Menu> findByIsAvailableTrue();
    
    /**
     * Find available menus by meal
     * @param mealId Meal ID
     * @return List of available menus
     */
    List<Menu> findByMealIdAndIsAvailableTrue(Long mealId);
    
    /**
     * Find menus by restaurant ID
     * @param restaurantId Restaurant ID
     * @return List of menus
     */
    @Query("SELECT m FROM Menu m WHERE m.meal.restaurant.id = :restaurantId")
    List<Menu> findByRestaurantId(@Param("restaurantId") Long restaurantId);
    
    /**
     * Find available menus by restaurant ID
     * @param restaurantId Restaurant ID
     * @return List of available menus
     */
    @Query("SELECT m FROM Menu m WHERE m.meal.restaurant.id = :restaurantId AND m.isAvailable = true")
    List<Menu> findByRestaurantIdAndIsAvailableTrue(@Param("restaurantId") Long restaurantId);
} 