package tqs.hm1114588.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tqs.hm1114588.model.Location;
import tqs.hm1114588.model.restaurant.Reservation;
import tqs.hm1114588.model.restaurant.ReservationStatus;
import tqs.hm1114588.model.restaurant.Restaurant;
import tqs.hm1114588.repository.LocationRepository;
import tqs.hm1114588.repository.ReservationRepository;
import tqs.hm1114588.repository.RestaurantRepository;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;
    
    @Autowired
    private LocationRepository locationRepository;
    
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
    public Optional<Restaurant> findByName(String name) {
        return restaurantRepository.findByName(name);
    }

    /**
     * Find restaurants by location
     * @param locationId Location ID
     * @return List of restaurants in the specified location
     */
    @Cacheable(value = "restaurantsByLocation", key = "#locationId")
    public List<Restaurant> findByLocationId(Long locationId) {
        return restaurantRepository.findByLocationId(locationId);
    }

    /**
     * Save restaurant
     * @param restaurant Restaurant to save
     * @return Saved restaurant
     */
    @Transactional
    @CachePut(value = "restaurant", key = "#result.id")
    @CacheEvict(value = {"restaurants", "restaurantsByLocation"}, allEntries = true)
    public Restaurant save(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    /**
     * Create a new restaurant
     * @param name Restaurant name
     * @param description Restaurant description
     * @param capacity Restaurant capacity
     * @param operatingHours Restaurant operating hours
     * @param contactInfo Restaurant contact information
     * @param locationId Location ID
     * @return Created restaurant
     */
    @Transactional
    @CachePut(value = "restaurant", key = "#result.id")
    @CacheEvict(value = {"restaurants", "restaurantsByLocation"}, allEntries = true)
    public Restaurant createRestaurant(String name, String description, int capacity, String operatingHours, String contactInfo, Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Location not found"));
        
        Restaurant restaurant = new Restaurant();
        restaurant.setName(name);
        restaurant.setDescription(description);
        restaurant.setCapacity(capacity);
        restaurant.setOperatingHours(operatingHours);
        restaurant.setContactInfo(contactInfo);
        restaurant.setLocation(location);
        
        return restaurantRepository.save(restaurant);
    }

    /**
     * Delete restaurant by ID
     * @param id Restaurant ID
     */
    @Transactional
    @CacheEvict(value = {"restaurant", "restaurants", "restaurantsByLocation"}, allEntries = true)
    public void deleteById(Long id) {
        restaurantRepository.deleteById(id);
    }
    
    /**
     * Get available capacity for a restaurant at a specific time
     * @param id Restaurant ID
     * @param startTime Start time
     * @param endTime End time
     * @return Available capacity
     */
    public Integer getAvailableCapacity(Long id, LocalDateTime startTime, LocalDateTime endTime) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        
        List<Reservation> activeReservations = reservationRepository.findByRestaurantIdAndReservationTimeBetweenAndStatusIn(
            id,
            startTime,
            endTime,
            Arrays.asList(ReservationStatus.CONFIRMED, ReservationStatus.CHECKED_IN)
        );
        
        int occupiedCapacity = activeReservations.stream()
                .mapToInt(Reservation::getPartySize)
                .sum();
        
        return Math.max(0, restaurant.getCapacity() - occupiedCapacity);
    }
    
    /**
     * Check if a restaurant has capacity for a reservation
     * @param id Restaurant ID
     * @param reservationTime Reservation time
     * @param partySize Party size
     * @return True if capacity is available
     */
    public boolean hasCapacityForReservation(Long id, LocalDateTime reservationTime, Integer partySize) {
        // Get available capacity for a 2-hour window
        Integer availableCapacity = getAvailableCapacity(
            id,
            reservationTime.minusHours(1),
            reservationTime.plusHours(1)
        );
        
        return availableCapacity >= partySize;
    }
    
    /**
     * Update restaurant capacity
     * @param id Restaurant ID
     * @param capacity New capacity
     * @return Updated restaurant
     */
    @Transactional
    public Optional<Restaurant> updateCapacity(Long id, Integer capacity) {
        return restaurantRepository.findById(id)
                .map(restaurant -> {
                    restaurant.setCapacity(capacity);
                    return restaurantRepository.save(restaurant);
                });
    }
    
    /**
     * Update available menus for a restaurant
     * @param id Restaurant ID
     * @param availableMenus New available menus count
     * @return Updated restaurant
     */
    @Transactional
    public Optional<Restaurant> updateAvailableMenus(Long id, Integer availableMenus) {
        // This is just a stub method - there's no availableMenus field in Restaurant
        // We're just returning the existing restaurant without modification
        return restaurantRepository.findById(id);
    }
    
    /**
     * Check if a restaurant has enough menus for a reservation
     * @param id Restaurant ID
     * @param menusRequired Number of menus required
     * @return True if enough menus are available
     */
    public boolean hasEnoughMenus(Long id, Integer menusRequired) {
        // Since there's no availableMenus field in the Restaurant class,
        // we'll assume there are always enough menus available
        return true;
    }
} 