package tqs.hm1114588.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;

    /**
     * Find all restaurants
     * @return List of all restaurants
     */
    @Cacheable(value = "restaurants")
    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    /**
     * Find restaurant by ID
     * @param id Restaurant ID
     * @return Restaurant if found
     */
    @Cacheable(value = "restaurant", key = "#id")
    public Optional<Restaurant> findById(Long id) {
        return restaurantRepository.findById(id);
    }

    /**
     * Find restaurant by name
     * @param name Restaurant name
     * @return Restaurant if found
     */
    @Cacheable(value = "restaurantByName", key = "#name")
    public Optional<Restaurant> findByName(String name) {
        return restaurantRepository.findByName(name);
    }

    /**
     * Save restaurant
     * @param restaurant Restaurant to save
     * @return Saved restaurant
     */
    @Transactional
    @CachePut(value = "restaurant", key = "#result.id")
    @CacheEvict(value = {"restaurants"}, allEntries = true)
    public Restaurant save(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    /**
     * Create a new restaurant
     * @param name Restaurant name
     * @param capacity Restaurant capacity
     * @return Created restaurant
     */
    @Transactional
    @CachePut(value = "restaurant", key = "#result.id")
    @CacheEvict(value = {"restaurants"}, allEntries = true)
    public Restaurant createRestaurant(String name, Integer capacity) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(name);
        restaurant.setCapacity(capacity);
        return restaurantRepository.save(restaurant);
    }

    /**
     * Delete restaurant by ID
     * @param id Restaurant ID
     */
    @Transactional
    @CacheEvict(value = {"restaurant", "restaurants"}, allEntries = true)
    public void deleteById(Long id) {
        restaurantRepository.deleteById(id);
    }
    
    /**
     * Calculate available capacity for a specific time window
     * @param restaurantId Restaurant ID
     * @param startTime Start time
     * @param endTime End time
     * @return Available capacity
     */
    @Cacheable(value = "restaurantAvailableCapacity", key = "#restaurantId + '_' + #startTime + '_' + #endTime")
    public Integer getAvailableCapacity(Long restaurantId, LocalDateTime startTime, LocalDateTime endTime) {
        Optional<Restaurant> restaurantOpt = restaurantRepository.findById(restaurantId);
        
        if (restaurantOpt.isEmpty()) {
            throw new IllegalArgumentException("Restaurant not found");
        }
        
        Restaurant restaurant = restaurantOpt.get();
        Integer totalCapacity = restaurant.getCapacity();
        
        // Find active reservations for this time period
        List<Reservation> activeReservations = reservationRepository.findByRestaurantIdAndReservationTimeBetweenAndStatusIn(
            restaurantId, 
            startTime, 
            endTime, 
            List.of(ReservationStatus.CONFIRMED, ReservationStatus.CHECKED_IN)
        );
        
        // Calculate occupied capacity
        Integer occupiedCapacity = activeReservations.stream()
            .mapToInt(Reservation::getPartySize)
            .sum();
        
        // Return available capacity
        return Math.max(0, totalCapacity - occupiedCapacity);
    }
    
    /**
     * Update restaurant capacity
     * @param id Restaurant ID
     * @param capacity New capacity
     * @return Updated restaurant if found
     */
    @Transactional
    @CachePut(value = "restaurant", key = "#id")
    @CacheEvict(value = {"restaurants", "restaurantAvailableCapacity"}, allEntries = true)
    public Optional<Restaurant> updateCapacity(Long id, Integer capacity) {
        return restaurantRepository.findById(id)
                .map(restaurant -> {
                    restaurant.setCapacity(capacity);
                    return restaurantRepository.save(restaurant);
                });
    }
    
    /**
     * Update restaurant available menus
     * @param id Restaurant ID
     * @param availableMenus New available menus count
     * @return Updated restaurant if found
     */
    @Transactional
    @CachePut(value = "restaurant", key = "#id")
    @CacheEvict(value = {"restaurants"}, allEntries = true)
    public Optional<Restaurant> updateAvailableMenus(Long id, Integer availableMenus) {
        return restaurantRepository.findById(id)
                .map(restaurant -> {
                    restaurant.setAvailableMenus(availableMenus);
                    return restaurantRepository.save(restaurant);
                });
    }
    
    /**
     * Check if restaurant has enough capacity for a reservation
     * @param restaurantId Restaurant ID
     * @param reservationTime Reservation time
     * @param partySize Party size
     * @return True if restaurant has enough capacity
     */
    public boolean hasCapacityForReservation(Long restaurantId, LocalDateTime reservationTime, Integer partySize) {
        // Define a 2-hour window for capacity check (typical dining duration)
        LocalDateTime windowStart = reservationTime.minusMinutes(30);
        LocalDateTime windowEnd = reservationTime.plusHours(2);
        
        Integer availableCapacity = getAvailableCapacity(restaurantId, windowStart, windowEnd);
        
        return availableCapacity >= partySize;
    }
    
    /**
     * Check if restaurant has enough menus for a reservation
     * @param restaurantId Restaurant ID
     * @param requiredMenus Number of menus required
     * @return True if restaurant has enough menus
     */
    public boolean hasEnoughMenus(Long restaurantId, Integer requiredMenus) {
        return restaurantRepository.findById(restaurantId)
                .map(restaurant -> restaurant.getAvailableMenus() >= requiredMenus)
                .orElse(false);
    }
} 