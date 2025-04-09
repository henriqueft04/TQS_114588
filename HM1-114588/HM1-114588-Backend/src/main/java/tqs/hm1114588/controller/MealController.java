package tqs.hm1114588.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tqs.hm1114588.model.restaurant.Meal;
import tqs.hm1114588.service.MealService;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    @Autowired
    private MealService mealService;

    @GetMapping
    public ResponseEntity<List<Meal>> getAllMeals() {
        List<Meal> meals = mealService.findAll();
        return ResponseEntity.ok(meals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Meal> getMealById(@PathVariable Long id) {
        Optional<Meal> meal = mealService.findById(id);
        return meal.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<List<Meal>> getMealsBySchedule(@PathVariable Long scheduleId) {
        List<Meal> meals = mealService.findBySchedule(scheduleId);
        return ResponseEntity.ok(meals);
    }

    @PostMapping
    public ResponseEntity<Meal> createMeal(@RequestBody Meal meal) {
        Meal savedMeal = mealService.save(meal);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMeal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long id) {
        mealService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
} 