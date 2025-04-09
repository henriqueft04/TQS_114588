package tqs.hm1114588.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tqs.hm1114588.model.restaurant.Reservation;
import tqs.hm1114588.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    /**
     * Get all reservations
     * @return List of reservations
     */
    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationService.findAll();
    }

    /**
     * Get reservation by ID
     * @param id Reservation ID
     * @return Reservation if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        return reservationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get reservation by token
     * @param token Reservation token
     * @return Reservation if found
     */
    @GetMapping("/token/{token}")
    public ResponseEntity<Reservation> getReservationByToken(@PathVariable String token) {
        return reservationService.findByToken(token)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get reservations by restaurant ID
     * @param restaurantId Restaurant ID
     * @return List of reservations
     */
    @GetMapping("/restaurant/{restaurantId}")
    public List<Reservation> getReservationsByRestaurant(@PathVariable Long restaurantId) {
        return reservationService.findByRestaurantId(restaurantId);
    }

    /**
     * Get reservations by date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of reservations
     */
    @GetMapping("/date-range")
    public List<Reservation> getReservationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return reservationService.findByDateRange(startDate, endDate);
    }

    /**
     * Create a new reservation
     * @param reservation Reservation data
     * @return Created reservation
     */
    @PostMapping
    public Reservation createReservation(@RequestBody Reservation reservation) {
        return reservationService.createReservation(
                reservation.getRestaurant().getId(),
                reservation.getCustomerName(),
                reservation.getCustomerEmail(),
                reservation.getCustomerPhone(),
                reservation.getPartySize(),
                reservation.getReservationTime(),
                reservation.getMealType(),
                reservation.getSpecialRequests(),
                reservation.getIsGroupReservation(),
                reservation.getMenusRequired()
        );
    }

    /**
     * Update a reservation
     * @param id Reservation ID
     * @param reservation Reservation data
     * @return Updated reservation if found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable Long id,
            @RequestBody Reservation reservation) {
        return reservationService.findById(id)
                .map(existingReservation -> {
                    existingReservation.setCustomerName(reservation.getCustomerName());
                    existingReservation.setCustomerEmail(reservation.getCustomerEmail());
                    existingReservation.setCustomerPhone(reservation.getCustomerPhone());
                    existingReservation.setPartySize(reservation.getPartySize());
                    existingReservation.setReservationTime(reservation.getReservationTime());
                    existingReservation.setMealType(reservation.getMealType());
                    existingReservation.setSpecialRequests(reservation.getSpecialRequests());
                    return reservationService.save(existingReservation);
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cancel a reservation
     * @param id Reservation ID
     * @return Updated reservation if found
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Reservation> cancelReservation(@PathVariable Long id) {
        return reservationService.cancelReservation(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Confirm a reservation
     * @param id Reservation ID
     * @return Updated reservation if found
     */
    @PutMapping("/{id}/confirm")
    public ResponseEntity<Reservation> confirmReservation(@PathVariable Long id) {
        return reservationService.confirmReservation(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Check-in a reservation
     * @param token Reservation token
     * @return Updated reservation if found
     */
    @PutMapping("/check-in/{token}")
    public ResponseEntity<Reservation> checkInReservation(@PathVariable String token) {
        return reservationService.checkInReservation(token)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a reservation
     * @param id Reservation ID
     * @return No content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        return reservationService.findById(id)
                .map(reservation -> {
                    reservationService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 