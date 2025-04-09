package tqs.hm1114588.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tqs.hm1114588.model.restaurant.Dish;
import tqs.hm1114588.model.restaurant.DishType;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
    
    /**
     * Find dishes by type
     * @param type Dish type
     * @return List of dishes
     */
    List<Dish> findByType(DishType type);
    
    /**
     * Find available dishes
     * @return List of available dishes
     */
    List<Dish> findByIsAvailableTrue();
    
    /**
     * Find dishes by name containing
     * @param name Name pattern
     * @return List of dishes
     */
    List<Dish> findByNameContainingIgnoreCase(String name);
} 