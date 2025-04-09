package tqs.hm1114588.controller;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import tqs.hm1114588.model.restaurant.Reservation;
import tqs.hm1114588.model.restaurant.ReservationStatus;
import tqs.hm1114588.model.restaurant.Restaurant;
import tqs.hm1114588.service.LocationService;
import tqs.hm1114588.service.ReservationService;
import tqs.hm1114588.service.RestaurantService;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;
    
    @MockitoBean
    private RestaurantService restaurantService;
    
    @MockitoBean
    private LocationService locationService;

    private Reservation reservation;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Set up Object Mapper with Java Time Module for LocalDateTime serialization
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // Set up location
        tqs.hm1114588.model.Location location = new tqs.hm1114588.model.Location();
        location.setId(1L);
        location.setName("Test Location");
        location.setLatitude(40.7128);
        location.setLongitude(-74.0060);
        
        // Set up test data
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setCapacity(50);
        restaurant.setLocation(location);

        // Set up reservation
        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setRestaurant(restaurant);
        reservation.setCustomerName("John Doe");
        reservation.setCustomerEmail("john@example.com");
        reservation.setCustomerPhone("1234567890");
        reservation.setPartySize(4);
        reservation.setReservationTime(LocalDateTime.now().plusDays(1));
        reservation.setMealType("Dinner");
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setToken("test-token-123");
        
        // Set up mocks
        when(locationService.findById(any())).thenReturn(Optional.of(location));
        when(restaurantService.findById(any())).thenReturn(Optional.of(restaurant));
    }

    @Test
    void testGetAllReservations() throws Exception {
        // Arrange
        when(reservationService.findAll()).thenReturn(Collections.singletonList(reservation));

        // Act & Assert
        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].customerName", is("John Doe")));

        verify(reservationService).findAll();
    }

    @Test
    void testGetReservationById_WhenExists() throws Exception {
        // Arrange
        when(reservationService.findById(1L)).thenReturn(Optional.of(reservation));

        // Act & Assert
        mockMvc.perform(get("/api/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.customerName", is("John Doe")));

        verify(reservationService).findById(1L);
    }

    @Test
    void testGetReservationById_WhenNotExists() throws Exception {
        // Arrange
        when(reservationService.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/reservations/99"))
                .andExpect(status().isNotFound());

        verify(reservationService).findById(99L);
    }

    @Test
    void testGetReservationByToken() throws Exception {
        // Arrange
        when(reservationService.findByToken("test-token-123")).thenReturn(Optional.of(reservation));

        // Act & Assert
        mockMvc.perform(get("/api/reservations/token/test-token-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.token", is("test-token-123")));

        verify(reservationService).findByToken("test-token-123");
    }

    @Test
    void testGetReservationsByRestaurant() throws Exception {
        // Arrange
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        reservation.setRestaurant(restaurant);
        
        when(reservationService.findByRestaurantId(1L))
                .thenReturn(Collections.singletonList(reservation));

        // Act & Assert
        mockMvc.perform(get("/api/reservations/restaurant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].restaurant.id", is(1)));

        verify(reservationService).findByRestaurantId(1L);
    }

    @Test
    void testCreateReservation() throws Exception {
        // Arrange
        // Create a simpler request instead of using a full Reservation object
        String requestBody = String.format("""
            {
                "restaurant": {"id": 1},
                "customerName": "John Doe",
                "customerEmail": "john@example.com",
                "customerPhone": "1234567890",
                "partySize": 4,
                "reservationTime": "%s",
                "mealType": "Dinner",
                "isGroupReservation": false,
                "menusRequired": 4
            }
            """, LocalDateTime.now().plusDays(1).toString());

        when(reservationService.createReservation(
                eq(1L),
                eq("John Doe"),
                eq("john@example.com"),
                eq("1234567890"),
                eq(4),
                any(LocalDateTime.class),
                eq("Dinner"),
                eq(null),
                eq(false),
                eq(4)
        )).thenReturn(reservation);

        // Act & Assert
        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.customerName", is("John Doe")));

        verify(reservationService).createReservation(
                any(Long.class),
                eq("John Doe"),
                eq("john@example.com"),
                eq("1234567890"),
                eq(4),
                any(LocalDateTime.class),
                eq("Dinner"),
                any(),
                eq(false),
                eq(4)
        );
    }

    @Test
    void testUpdateReservation_WhenExists() throws Exception {
        // Arrange
        when(reservationService.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationService.save(any())).thenReturn(reservation);

        // Create a simplified request body with only the fields that need to be updated
        String requestBody = """
            {
                "customerName": "Jane Doe",
                "customerEmail": "jane@example.com",
                "customerPhone": "9876543210",
                "partySize": 6,
                "mealType": "Dinner",
                "specialRequests": "Special request"
            }
            """;

        // Act & Assert
        mockMvc.perform(put("/api/reservations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());

        verify(reservationService).findById(1L);
        verify(reservationService).save(any());
    }

    @Test
    void testCancelReservation() throws Exception {
        // Arrange
        reservation.setStatus(ReservationStatus.CANCELLED);
        when(reservationService.cancelReservation(1L)).thenReturn(Optional.of(reservation));

        // Act & Assert
        mockMvc.perform(put("/api/reservations/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELLED")));

        verify(reservationService).cancelReservation(1L);
    }

    @Test
    void testConfirmReservation() throws Exception {
        // Arrange
        reservation.setStatus(ReservationStatus.CONFIRMED);
        when(reservationService.confirmReservation(1L)).thenReturn(Optional.of(reservation));

        // Act & Assert
        mockMvc.perform(put("/api/reservations/1/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CONFIRMED")));

        verify(reservationService).confirmReservation(1L);
    }

    @Test
    void testCheckInReservation() throws Exception {
        // Arrange
        reservation.setStatus(ReservationStatus.CHECKED_IN);
        when(reservationService.checkInReservation(123L)).thenReturn(Optional.of(reservation));

        // Act & Assert
        mockMvc.perform(put("/api/reservations/check-in/test-token-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CHECKED_IN")));

        verify(reservationService).checkInReservation(123L);
    }

    @Test
    void testDeleteReservation() throws Exception {
        // Arrange
        when(reservationService.findById(1L)).thenReturn(Optional.of(reservation));
        doNothing().when(reservationService).deleteById(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/reservations/1"))
                .andExpect(status().isNoContent());

        verify(reservationService).findById(1L);
        verify(reservationService).deleteById(1L);
    }
} 