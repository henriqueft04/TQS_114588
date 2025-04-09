package tqs.hm1114588.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import tqs.hm1114588.model.restaurant.Meal;
import tqs.hm1114588.model.restaurant.Restaurant;

@Entity
@Table(name = "schedules")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    private String name; // e.g., "Regular Schedule", "Holiday Schedule"

    @Column(nullable = false)
    private boolean isActive;

    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Meal> meals = new HashSet<>();

    // Default constructor
    public Schedule() {
        this.isActive = true;
    }

    // Helper methods for managing meals
    public void addMeal(Meal meal) {
        meals.add(meal);
        meal.setSchedule(this);
    }

    public void removeMeal(Meal meal) {
        meals.remove(meal);
        meal.setSchedule(null);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Set<Meal> getMeals() {
        return meals;
    }

    public void setMeals(Set<Meal> meals) {
        this.meals = meals;
    }
} 