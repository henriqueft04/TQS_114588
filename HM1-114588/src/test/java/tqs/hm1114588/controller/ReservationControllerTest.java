package tqs.hm1114588.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import tqs.hm1114588.model.restaurant.Reservation;
import tqs.hm1114588.model.restaurant.ReservationStatus;
import tqs.hm1114588.model.restaurant.Restaurant;
import tqs.hm1114588.service.ReservationService;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    private Reservation reservation;
    private Restaurant restaurant;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Set up Object Mapper with Java Time Module for LocalDateTime serialization
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // Set up test data
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setCapacity(50);
        restaurant.setAvailableMenus(40);

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
    }

    @Test
    void testGetAllReservations() throws Exception {
        // Arrange
        when(reservationService.findAll()).thenReturn(Arrays.asList(reservation));

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
        when(reservationService.findByRestaurantId(1L)).thenReturn(Arrays.asList(reservation));

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
        when(reservationService.createReservation(
                eq(1L),
                eq("John Doe"),
                eq("john@example.com"),
                eq("1234567890"),
                eq(4),
                any(LocalDateTime.class),
                eq("Dinner"),
                eq(null),  // specialRequests is null in our test setup
                eq(false), // isGroupReservation is false in our test setup
                eq(4)      // menusRequired is 4 in our test setup
        )).thenReturn(reservation);

        // Act & Assert
        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservation)))
                .andExpect(status().isOk());

        verify(reservationService).createReservation(
                eq(1L),
                eq("John Doe"),
                eq("john@example.com"),
                eq("1234567890"),
                eq(4),
                any(LocalDateTime.class),
                eq("Dinner"),
                eq(null),  // specialRequests is null in our test setup
                eq(false), // isGroupReservation is false in our test setup
                eq(4)      // menusRequired is 4 in our test setup
        );
    }

    @Test
    void testUpdateReservation_WhenExists() throws Exception {
        // Arrange
        when(reservationService.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationService.save(any())).thenReturn(reservation);

        // Update some fields
        Reservation updatedReservation = new Reservation();
        updatedReservation.setCustomerName("Jane Doe");
        updatedReservation.setPartySize(6);

        // Act & Assert
        mockMvc.perform(put("/api/reservations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedReservation)))
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
        when(reservationService.checkInReservation("test-token-123")).thenReturn(Optional.of(reservation));

        // Act & Assert
        mockMvc.perform(put("/api/reservations/check-in/test-token-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CHECKED_IN")));

        verify(reservationService).checkInReservation("test-token-123");
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