package tqs.hm1114588.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tqs.hm1114588.model.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    /**
     * Find reservation by token
     * @param token Reservation token
     * @return Reservation if found
     */
    Optional<Reservation> findByToken(String token);
    
    /**
     * Find reservations by restaurant ID
     * @param restaurantId Restaurant ID
     * @return List of reservations
     */
    List<Reservation> findByRestaurantId(Long restaurantId);
    
    /**
     * Find reservations by date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of reservations
     */
    List<Reservation> findByReservationTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
} 