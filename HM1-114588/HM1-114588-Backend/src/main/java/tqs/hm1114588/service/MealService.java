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
import tqs.hm1114588.model.restaurant.Menu;
import tqs.hm1114588.repository.MealRepository;
import tqs.hm1114588.repository.MenuRepository;

@Service
public class MealService {

    @Autowired
    private MealRepository mealRepository;
    
    @Autowired
    private MenuRepository menuRepository;

    /**
     * Find all meals
     * @return List of all meals
     */
    @Cacheable(value = "meals")
    public List<Meal> findAll() {
        return mealRepository.findAll();
    }

    /**
     * Find meal by ID
     * @param id Meal ID
     * @return Meal if found
     */
    @Cacheable(value = "meal", key = "#id")
    public Optional<Meal> findById(Long id) {
        return mealRepository.findById(id);
    }

    /**
     * Find meals by schedule
     * @param scheduleId Schedule ID
     * @return List of meals for the specified schedule
     */
    @Cacheable(value = "mealsBySchedule", key = "#scheduleId")
    public List<Meal> findBySchedule(Long scheduleId) {
        return mealRepository.findByScheduleId(scheduleId);
    }

    /**
     * Save meal
     * @param meal Meal to save
     * @return Saved meal
     */
    @Transactional
    @CachePut(value = "meal", key = "#result.id")
    @CacheEvict(value = {"meals", "mealsBySchedule"}, allEntries = true)
    public Meal save(Meal meal) {
        return mealRepository.save(meal);
    }

    /**
     * Create a new meal for a schedule
     * @param scheduleId Schedule ID
     * @param meal Meal to create
     * @return Created meal
     */
    @Transactional
    @CachePut(value = "meal", key = "#result.id")
    @CacheEvict(value = {"meals", "mealsBySchedule"}, allEntries = true)
    public Meal createMeal(Long scheduleId, Meal meal) {
        Schedule schedule = new Schedule();
        schedule.setId(scheduleId);
        meal.setSchedule(schedule);
        return mealRepository.save(meal);
    }
    
    /**
     * Add a menu to a meal
     * @param mealId Meal ID
     * @param menu Menu to add
     * @return Updated meal
     */
    @Transactional
    @CachePut(value = "meal", key = "#mealId")
    @CacheEvict(value = {"meals", "mealsBySchedule"}, allEntries = true)
    public Optional<Meal> addMenu(Long mealId, Menu menu) {
        return mealRepository.findById(mealId)
                .map(meal -> {
                    menu.setMeal(meal);
                    menuRepository.save(menu);
                    return mealRepository.save(meal);
                });
    }

    /**
     * Delete meal by ID
     * @param id Meal ID
     */
    @Transactional
    @CacheEvict(value = {"meal", "meals", "mealsBySchedule"}, allEntries = true)
    public void deleteById(Long id) {
        mealRepository.deleteById(id);
    }
} 