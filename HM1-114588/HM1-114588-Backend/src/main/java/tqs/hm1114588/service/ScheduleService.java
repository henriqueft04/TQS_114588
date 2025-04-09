package tqs.hm1114588.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tqs.hm1114588.model.Schedule;
import tqs.hm1114588.model.restaurant.Meal;
import tqs.hm1114588.model.restaurant.Restaurant;
import tqs.hm1114588.repository.MealRepository;
import tqs.hm1114588.repository.RestaurantRepository;
import tqs.hm1114588.repository.ScheduleRepository;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;
    
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    @Autowired
    private MealRepository mealRepository;

    /**
     * Find all schedules
     * @return List of all schedules
     */
    @Cacheable(value = "schedules")
    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    /**
     * Find schedule by ID
     * @param id Schedule ID
     * @return Schedule if found
     */
    @Cacheable(value = "schedule", key = "#id")
    public Optional<Schedule> findById(Long id) {
        return scheduleRepository.findById(id);
    }

    /**
     * Find schedules by restaurant
     * @param restaurantId Restaurant ID
     * @return List of schedules for the specified restaurant
     */
    @Cacheable(value = "schedulesByRestaurant", key = "#restaurantId")
    public List<Schedule> findByRestaurant(Long restaurantId) {
        return scheduleRepository.findByRestaurantId(restaurantId);
    }

    /**
     * Save schedule
     * @param schedule Schedule to save
     * @return Saved schedule
     */
    @Transactional
    @CachePut(value = "schedule", key = "#result.id")
    @CacheEvict(value = {"schedules", "schedulesByRestaurant"}, allEntries = true)
    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    /**
     * Create a new schedule for a restaurant
     * @param restaurantId Restaurant ID
     * @param schedule Schedule to create
     * @return Created schedule
     */
    @Transactional
    @CachePut(value = "schedule", key = "#result.id")
    @CacheEvict(value = {"schedules", "schedulesByRestaurant"}, allEntries = true)
    public Schedule createSchedule(Long restaurantId, Schedule schedule) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        
        schedule.setRestaurant(restaurant);
        return scheduleRepository.save(schedule);
    }
    
    /**
     * Add a meal to a schedule
     * @param scheduleId Schedule ID
     * @param meal Meal to add
     * @return Updated schedule
     */
    @Transactional
    @CachePut(value = "schedule", key = "#scheduleId")
    @CacheEvict(value = {"schedules", "schedulesByRestaurant"}, allEntries = true)
    public Optional<Schedule> addMeal(Long scheduleId, Meal meal) {
        return scheduleRepository.findById(scheduleId)
                .map(schedule -> {
                    meal.setSchedule(schedule);
                    mealRepository.save(meal);
                    return scheduleRepository.save(schedule);
                });
    }

    /**
     * Delete schedule by ID
     * @param id Schedule ID
     */
    @Transactional
    @CacheEvict(value = {"schedule", "schedules", "schedulesByRestaurant"}, allEntries = true)
    public void deleteById(Long id) {
        scheduleRepository.deleteById(id);
    }
} 