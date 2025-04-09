package tqs.hm1114588.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import tqs.hm1114588.model.restaurant.Restaurant;
import tqs.hm1114588.model.restaurant.Meal;
import tqs.hm1114588.model.restaurant.MealType;
import tqs.hm1114588.service.RestaurantService;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration
public class RestaurantSteps {

    @Autowired
    private RestaurantService restaurantService;

    private Restaurant restaurant;
    private Exception exception;
    private Meal mealService;

    @Given("the restaurant {string} exists with capacity {int}")
    public void the_restaurant_exists_with_capacity(String name, Integer capacity) {
        restaurant = restaurantService.createRestaurant(name, capacity);
        assertNotNull(restaurant);
    }

    @When("I create a restaurant with name {string} and capacity {int}")
    public void i_create_a_restaurant_with_name_and_capacity(String name, Integer capacity) {
        try {
            restaurant = restaurantService.createRestaurant(name, capacity);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("the restaurant should be created successfully")
    public void the_restaurant_should_be_created_successfully() {
        assertNotNull(restaurant);
        assertNull(exception);
    }

    @Then("the restaurant should have a capacity of {int}")
    public void the_restaurant_should_have_a_capacity_of(Integer capacity) {
        assertEquals(capacity, restaurant.getCapacity());
    }

    @When("I update the capacity to {int}")
    public void i_update_the_capacity_to(Integer capacity) {
        try {
            restaurant.setCapacity(capacity);
            restaurant = restaurantService.save(restaurant);
        } catch (Exception e) {
            exception = e;
        }
    }

    @When("I delete the restaurant")
    public void i_delete_the_restaurant() {
        try {
            Long id = restaurant.getId();
            restaurantService.deleteById(id);
            restaurant = null;
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("the restaurant should no longer exist")
    public void the_restaurant_should_no_longer_exist() {
        assertNull(restaurant);
        assertNull(exception);
    }

    @Given("the restaurant has a dinner service from {string} to {string} on {string}")
    public void the_restaurant_has_a_dinner_service_from_to_on(String startTime, String endTime, String day) {
        // Create a meal service for the restaurant
        Meal dinner = new Meal();
        dinner.setMealType(MealType.DINNER);
        dinner.setDayOfWeek(DayOfWeek.valueOf(day));
        dinner.setStartTime(LocalTime.parse(startTime));
        dinner.setEndTime(LocalTime.parse(endTime));
        dinner.setRestaurant(restaurant);
        dinner.setActive(true);
        
        restaurant.addMeal(dinner);
        restaurant = restaurantService.save(restaurant);
    }

    @When("I add a meal service with the following details:")
    public void i_add_a_meal_service_with_the_following_details(DataTable dataTable) {
        Map<String, String> details = dataTable.asMap();
        try {
            mealService = new Meal();
            mealService.setMealType(MealType.valueOf(details.get("Meal Type")));
            mealService.setDayOfWeek(DayOfWeek.valueOf(details.get("Day")));
            mealService.setStartTime(LocalTime.parse(details.get("Start Time")));
            mealService.setEndTime(LocalTime.parse(details.get("End Time")));
            mealService.setRestaurant(restaurant);
            mealService.setActive(true);
            
            restaurant.addMeal(mealService);
            restaurant = restaurantService.save(restaurant);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("the restaurant should have a {string} service on {string}")
    public void the_restaurant_should_have_a_service_on(String mealType, String day) {
        boolean hasService = restaurant.getMeals().stream()
            .anyMatch(meal -> 
                meal.getMealType() == MealType.valueOf(mealType) && 
                meal.getDayOfWeek() == DayOfWeek.valueOf(day)
            );
        assertTrue(hasService);
    }

    @Then("the service hours should be from {string} to {string}")
    public void the_service_hours_should_be_from_to(String startTime, String endTime) {
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        
        assertEquals(start, mealService.getStartTime());
        assertEquals(end, mealService.getEndTime());
    }
} 