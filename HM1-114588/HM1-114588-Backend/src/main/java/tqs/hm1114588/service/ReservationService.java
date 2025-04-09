package tqs.hm1114588.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tqs.hm1114588.model.restaurant.Reservation;
import tqs.hm1114588.model.restaurant.ReservationStatus;
import tqs.hm1114588.model.restaurant.Restaurant;
import tqs.hm1114588.repository.ReservationRepository;
import tqs.hm1114588.repository.RestaurantRepository;

@Service
public class ReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantService restaurantService;

    /**
     * Find all reservations
     * @return List of all reservations
     */
    @Cacheable(value = "reservations")
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    /**
     * Find reservation by ID
     * @param id Reservation ID
     * @return Reservation if found
     */
    @Cacheable(value = "reservation", key = "#id")
    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    /**
     * Find reservation by token
     * @param token Reservation token
     * @return Reservation if found
     */
    @Cacheable(value = "reservationByToken", key = "#token")
    public Optional<Reservation> findByToken(String token) {
        return reservationRepository.findByToken(token);
    }

    /**
     * Find reservations by restaurant ID
     * @param restaurantId Restaurant ID
     * @return List of reservations
     */
    @Cacheable(value = "reservationsByRestaurant", key = "#restaurantId")
    public List<Reservation> findByRestaurantId(Long restaurantId) {
        return reservationRepository.findByRestaurantId(restaurantId);
    }

    /**
     * Find reservations by date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of reservations
     */
    @Cacheable(value = "reservationsByDateRange", key = "#startDate + '_' + #endDate")
    public List<Reservation> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return reservationRepository.findByReservationTimeBetween(startDate, endDate);
    }

    /**
     * Save reservation
     * @param reservation Reservation to save
     * @return Saved reservation
     */
    @Transactional
    @CachePut(value = "reservation", key = "#result.id")
    @CacheEvict(value = {"reservations", "reservationsByRestaurant", "reservationsByDateRange"}, allEntries = true)
    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    /**
     * Create a new reservation
     * @param restaurantId Restaurant ID
     * @param customerName Customer name
     * @param customerEmail Customer email
     * @param customerPhone Customer phone
     * @param partySize Party size
     * @param reservationTime Reservation time
     * @param mealType Meal type
     * @param specialRequests Special requests
     * @param isGroupReservation Is group reservation
     * @param menusRequired Menus required
     * @return Created reservation
     */
    @Transactional
    @CachePut(value = "reservation", key = "#result.id")
    @CacheEvict(value = {"reservations", "reservationsByRestaurant", "reservationsByDateRange"}, allEntries = true)
    public Reservation createReservation(
            Long restaurantId,
            String customerName,
            String customerEmail,
            String customerPhone,
            Integer partySize,
            LocalDateTime reservationTime,
            String mealType,
            String specialRequests,
            Boolean isGroupReservation,
            Integer menusRequired) {
        
        Restaurant restaurant = restaurantService.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        
        // Check if restaurant has enough capacity
        if (!restaurantService.hasCapacityForReservation(restaurantId, reservationTime, partySize)) {
            throw new IllegalStateException("Restaurant does not have enough capacity for this reservation");
        }
        
        // Check if restaurant has enough menus
        if (!restaurantService.hasEnoughMenus(restaurantId, menusRequired)) {
            throw new IllegalStateException("Restaurant does not have enough menus for this reservation");
        }
        
        Reservation reservation = new Reservation();
        reservation.setRestaurant(restaurant);
        reservation.setCustomerName(customerName);
        reservation.setCustomerEmail(customerEmail);
        reservation.setCustomerPhone(customerPhone);
        reservation.setPartySize(partySize);
        reservation.setReservationTime(reservationTime);
        reservation.setMealType(mealType);
        reservation.setSpecialRequests(specialRequests);
        reservation.setIsGroupReservation(isGroupReservation);
        reservation.setMenusRequired(menusRequired);
        reservation.setToken(UUID.randomUUID().toString());
        reservation.setStatus(ReservationStatus.PENDING);
        
        logger.info("Created reservation: {}", reservation);
        
        return reservationRepository.save(reservation);
    }

    /**
     * Cancel a reservation
     * @param id Reservation ID
     * @return Updated reservation if found
     */
    @Transactional
    @CachePut(value = "reservation", key = "#id")
    @CacheEvict(value = {"reservations", "reservationsByRestaurant", "reservationsByDateRange"}, allEntries = true)
    public Optional<Reservation> cancelReservation(Long id) {
        return reservationRepository.findById(id)
                .map(reservation -> {
                    reservation.setStatus(ReservationStatus.CANCELLED);
                    logger.info("Cancelled reservation: {}", reservation);
                    return reservationRepository.save(reservation);
                });
    }

    /**
     * Confirm a reservation
     * @param id Reservation ID
     * @return Updated reservation if found
     */
    @Transactional
    @CachePut(value = "reservation", key = "#id")
    @CacheEvict(value = {"reservations", "reservationsByRestaurant", "reservationsByDateRange"}, allEntries = true)
    public Optional<Reservation> confirmReservation(Long id) {
        return reservationRepository.findById(id)
                .map(reservation -> {
                    reservation.setStatus(ReservationStatus.CONFIRMED);
                    return reservationRepository.save(reservation);
                });
    }

    /**
     * Confirm a reservation by token
     * @param token Reservation token
     * @return Updated reservation if found
     */
    @Transactional
    @CacheEvict(value = {"reservations", "reservationsByRestaurant", "reservationsByDateRange", "reservationByToken"}, allEntries = true)
    public Optional<Reservation> confirmReservation(String token) {
        return reservationRepository.findByToken(token)
                .map(reservation -> {
                    reservation.setStatus(ReservationStatus.CONFIRMED);
                    return reservationRepository.save(reservation);
                });
    }

    /**
     * Check-in a reservation
     * @param id Reservation ID
     * @return Updated reservation if found
     */
    @Transactional
    @CachePut(value = "reservation", key = "#id")
    @CacheEvict(value = {"reservations", "reservationsByRestaurant", "reservationsByDateRange"}, allEntries = true)
    public Optional<Reservation> checkInReservation(Long id) {
        return reservationRepository.findById(id)
                .filter(reservation -> reservation.getStatus() == ReservationStatus.CONFIRMED)
                .map(reservation -> {
                    reservation.setStatus(ReservationStatus.CHECKED_IN);
                    logger.info("Checked in reservation: {}", reservation);
                    return reservationRepository.save(reservation);
                });
    }

    /**
     * Delete reservation by ID
     * @param id Reservation ID
     */
    @Transactional
    @CacheEvict(value = {"reservation", "reservations", "reservationsByRestaurant", "reservationsByDateRange", "reservationByToken"}, allEntries = true)
    public void deleteById(Long id) {
        reservationRepository.deleteById(id);
    }

    /**
     * Mark a reservation as completed (used)
     * @param token Reservation token
     * @return Updated reservation if found and verified
     */
    @Transactional
    @CachePut(value = "reservation", key = "#result.id")
    @CacheEvict(value = {"reservations", "reservationsByRestaurant", "reservationsByDateRange", "reservationByToken"}, allEntries = true)
    public Optional<Reservation> verifyReservation(String token) {
        return reservationRepository.findByToken(token)
                .map(reservation -> {
                    if (reservation.getStatus() == ReservationStatus.CHECKED_IN) {
                        reservation.setStatus(ReservationStatus.COMPLETED);
                        return reservationRepository.save(reservation);
                    }
                    return reservation;
                });
    }
} 