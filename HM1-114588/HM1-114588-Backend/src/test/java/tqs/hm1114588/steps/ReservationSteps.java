package tqs.hm1114588.steps;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import tqs.hm1114588.model.Location;
import tqs.hm1114588.model.restaurant.Meal;
import tqs.hm1114588.model.restaurant.Reservation;
import tqs.hm1114588.model.restaurant.ReservationStatus;
import tqs.hm1114588.model.restaurant.Restaurant;
import tqs.hm1114588.service.ReservationService;
import tqs.hm1114588.service.RestaurantService;

@SpringBootTest
@ContextConfiguration
public class ReservationSteps {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RestaurantService restaurantService;

    private Restaurant restaurant;
    private Reservation reservation;
    private Exception exception;
    private String token;

    @Given("the restaurant {string} exists with capacity {int}")
    public void the_restaurant_exists_with_capacity(String name, int capacity) {
        Location location = new Location();
        location.setName("Test Location");
        location.setLatitude(40.7128);
        location.setLongitude(-74.0060);
        
        restaurant = restaurantService.createRestaurant(
            name, 
            "A test restaurant", 
            capacity, 
            "10:00-22:00", 
            "123456789", 
            1L
        );
    }

    @Given("the restaurant is open from {string} to {string}")
    public void the_restaurant_is_open_from_to(String openTime, String closeTime) {
        Meal dinner = new Meal();
        dinner.setMealType("DINNER");
        dinner.setStartTime(LocalTime.parse(openTime));
        dinner.setEndTime(LocalTime.parse(closeTime));
        dinner.setRestaurant(restaurant);
        
        restaurant.addMeal(dinner);
        restaurantService.save(restaurant);
    }

    @When("I make a reservation with the following details:")
    public void i_make_a_reservation_with_the_following_details(DataTable dataTable) {
        Map<String, String> details = dataTable.asMap();
        try {
            reservation = reservationService.createReservation(
                restaurant.getId(),
                details.get("Customer Name"),
                details.get("Customer Email"),
                details.get("Customer Phone"),
                Integer.parseInt(details.get("Party Size")),
                LocalTime.parse(details.get("Reservation Time")).atDate(java.time.LocalDate.now()),
                details.get("Meal Type"),
                null,
                false,
                Integer.parseInt(details.get("Party Size"))
            );
            token = reservation.getToken();
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("the reservation should be created successfully")
    public void the_reservation_should_be_created_successfully() {
        assertNotNull(reservation);
        assertNull(exception);
    }

    @Then("I should receive a confirmation token")
    public void i_should_receive_a_confirmation_token() {
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Then("the reservation status should be {string}")
    public void the_reservation_status_should_be(String status) {
        assertEquals(status, reservation.getStatus().name());
    }

    @Then("the reservation should fail")
    public void the_reservation_should_fail() {
        assertNull(reservation);
        assertNotNull(exception);
    }

    @Then("I should receive an error message about invalid party size")
    public void i_should_receive_an_error_message_about_invalid_party_size() {
        assertTrue(exception.getMessage().contains("party size"));
    }

    @Given("a reservation exists for {string} with token {string}")
    public void a_reservation_exists_for_with_token(String customerName, String token) {
        reservation = reservationService.createReservation(
            restaurant.getId(),
            customerName,
            "test@example.com",
            "1234567890",
            4,
            LocalTime.now().atDate(java.time.LocalDate.now()),
            "Dinner",
            null,
            false,
            4
        );
        reservation.setToken(token);
        reservationService.save(reservation);
    }

    @When("I confirm the reservation with token {string}")
    public void i_confirm_the_reservation_with_token(String token) {
        try {
            Optional<Reservation> result = reservationService.confirmReservation(Long.parseLong(token));
            result.ifPresent(value -> reservation = value);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("the confirmation should fail")
    public void the_confirmation_should_fail() {
        assertNotNull(exception);
    }

    @Then("I should receive an error message about invalid token")
    public void i_should_receive_an_error_message_about_invalid_token() {
        assertTrue(exception.getMessage().contains("token"));
    }

    @Then("the confirmation time should be recorded")
    public void the_confirmation_time_should_be_recorded() {
        assertNotNull(reservation.getUpdatedAt());
    }

    @Then("the reservation should be marked as a group reservation")
    public void the_reservation_should_be_marked_as_a_group_reservation() {
        assertTrue(reservation.getIsGroupReservation());
    }

    @Given("{int} seats are already reserved for {string} on {string}")
    public void seats_are_already_reserved_for_on(Integer seats, String time, String day) {
        // Create a dummy reservation with the specified number of seats
        LocalTime reservationTime = LocalTime.parse(time);
        LocalDateTime dateTime = reservationTime.atDate(java.time.LocalDate.now()
            .with(java.time.DayOfWeek.valueOf(day)));
        
        reservation = reservationService.createReservation(
            restaurant.getId(),
            "Dummy Customer",
            "dummy@example.com",
            "1234567890",
            seats,
            dateTime,
            "Dinner",
            null,
            seats >= 8,
            seats
        );
        
        assertNotNull(reservation);
    }
    
    @Then("I should receive an error message about insufficient capacity")
    public void i_should_receive_an_error_message_about_insufficient_capacity() {
        assertTrue(exception.getMessage().contains("capacity") || 
                   exception.getMessage().contains("available seats"));
    }
    
    @Given("a confirmed reservation exists for {string} with token {string}")
    public void a_confirmed_reservation_exists_for_with_token(String customerName, String token) {
        // First create a pending reservation
        reservation = reservationService.createReservation(
            restaurant.getId(),
            customerName,
            "test@example.com",
            "1234567890",
            4,
            LocalTime.now().atDate(java.time.LocalDate.now()),
            "Dinner",
            null,
            false,
            4
        );
        
        // Set the token and update the status to confirmed
        reservation.setToken(token);
        reservation.setStatus(ReservationStatus.CONFIRMED);
        
        reservationService.save(reservation);
    }
    
    @Given("a checked-in reservation exists for {string} with token {string}")
    public void a_checked_in_reservation_exists_for_with_token(String customerName, String token) {
        // First create a confirmed reservation
        a_confirmed_reservation_exists_for_with_token(customerName, token);
        
        // Then check it in
        reservation.setStatus(ReservationStatus.CHECKED_IN);
        reservationService.save(reservation);
    }
    
    @When("the customer checks in with token {string}")
    public void the_customer_checks_in_with_token(String token) {
        try {
            Optional<Reservation> result = reservationService.checkInReservation(token);
            result.ifPresent(value -> reservation = value);
        } catch (Exception e) {
            exception = e;
        }
    }
    
    @When("I mark the reservation as {string}")
    public void i_mark_the_reservation_as(String status) {
        try {
            reservation.setStatus(ReservationStatus.valueOf(status));
            reservation = reservationService.save(reservation);
        } catch (Exception e) {
            exception = e;
        }
    }
    
    @Given("the reservation time has passed")
    public void the_reservation_time_has_passed() {
        // Set reservation time to 2 hours ago
        LocalDateTime pastTime = LocalDateTime.now().minusHours(2);
        reservation.setReservationTime(pastTime);
        reservationService.save(reservation);
    }
} 