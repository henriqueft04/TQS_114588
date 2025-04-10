package tqs.hm1114588.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tqs.hm1114588.model.restaurant.Meal;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {

    List<Meal> findByScheduleId(Long scheduleId);
    
    /**
     * Find meals by restaurant ID
     * @param restaurantId Restaurant ID
     * @return List of meals
     */
    List<Meal> findByRestaurantId(Long restaurantId);
} 