package tqs.hm1114588.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import tqs.hm1114588.model.Location;
import tqs.hm1114588.model.restaurant.Reservation;
import tqs.hm1114588.model.restaurant.ReservationStatus;
import tqs.hm1114588.model.restaurant.Restaurant;
import tqs.hm1114588.repository.LocationRepository;
import tqs.hm1114588.repository.ReservationRepository;
import tqs.hm1114588.repository.RestaurantRepository;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;
    
    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    private Restaurant restaurant;
    private Location location;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        // Set up test data
        location = new Location();
        location.setId(1L);
        location.setName("Test City");
        
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setDescription("Test Description");
        restaurant.setCapacity(50);
        restaurant.setOperatingHours("10:00-22:00");
        restaurant.setContactInfo("123456789");
        restaurant.setLocation(location);
        
        now = LocalDateTime.now();
    }

    @Test
    void testFindAll() {
        // Arrange
        List<Restaurant> restaurants = Collections.singletonList(restaurant);
        when(restaurantRepository.findAll()).thenReturn(restaurants);

        // Act
        List<Restaurant> result = restaurantService.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals(restaurant.getId(), result.get(0).getId());
        verify(restaurantRepository).findAll();
    }

    @Test
    void testFindById_WhenExists() {
        // Arrange
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        // Act
        Optional<Restaurant> result = restaurantService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(restaurant.getId(), result.get().getId());
        verify(restaurantRepository).findById(1L);
    }

    @Test
    void testFindById_WhenNotExists() {
        // Arrange
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Restaurant> result = restaurantService.findById(99L);

        // Assert
        assertFalse(result.isPresent());
        verify(restaurantRepository).findById(99L);
    }

    @Test
    void testFindByName() {
        // Arrange
        when(restaurantRepository.findByName("Test Restaurant")).thenReturn(Optional.of(restaurant));

        // Act
        Optional<Restaurant> result = restaurantService.findByName("Test Restaurant");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Restaurant", result.get().getName());
        verify(restaurantRepository).findByName("Test Restaurant");
    }

    @Test
    void testSave() {
        // Arrange
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        // Act
        Restaurant result = restaurantService.save(restaurant);

        // Assert
        assertNotNull(result);
        assertEquals(restaurant.getId(), result.getId());
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void testCreateRestaurant() {
        // Arrange
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(invocation -> {
            Restaurant savedRestaurant = invocation.getArgument(0);
            savedRestaurant.setId(1L);
            return savedRestaurant;
        });

        // Act
        Restaurant result = restaurantService.createRestaurant(
            "New Restaurant", 
            "New Description", 
            100, 
            "10:00-22:00", 
            "123456789", 
            1L
        );

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Restaurant", result.getName());
        assertEquals("New Description", result.getDescription());
        assertEquals(100, result.getCapacity());
        assertEquals("10:00-22:00", result.getOperatingHours());
        assertEquals("123456789", result.getContactInfo());
        assertEquals(location, result.getLocation());
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    void testDeleteById() {
        // Arrange
        doNothing().when(restaurantRepository).deleteById(1L);

        // Act
        restaurantService.deleteById(1L);

        // Assert
        verify(restaurantRepository).deleteById(1L);
    }

    @Test
    void testGetAvailableCapacity() {
        // Arrange
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        
        // Create two active reservations
        Reservation reservation1 = new Reservation();
        reservation1.setPartySize(10);
        reservation1.setStatus(ReservationStatus.CONFIRMED);
        
        Reservation reservation2 = new Reservation();
        reservation2.setPartySize(15);
        reservation2.setStatus(ReservationStatus.CHECKED_IN);
        
        List<Reservation> activeReservations = Arrays.asList(reservation1, reservation2);
        
        when(reservationRepository.findByRestaurantIdAndReservationTimeBetweenAndStatusIn(
            eq(1L), 
            any(LocalDateTime.class), 
            any(LocalDateTime.class), 
            anyList()
        )).thenReturn(activeReservations);

        // Act
        Integer availableCapacity = restaurantService.getAvailableCapacity(
            1L, 
            now.minusHours(1), 
            now.plusHours(1)
        );

        // Assert
        // Total capacity is 50, occupied is 10 + 15 = 25, so available should be 25
        assertEquals(25, availableCapacity);
        verify(restaurantRepository).findById(1L);
        verify(reservationRepository).findByRestaurantIdAndReservationTimeBetweenAndStatusIn(
            eq(1L), 
            any(LocalDateTime.class), 
            any(LocalDateTime.class), 
            anyList()
        );
    }
    
    @Test
    void testGetAvailableCapacity_NoAvailability() {
        // Arrange
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        
        // Create reservations that exceed capacity
        Reservation reservation1 = new Reservation();
        reservation1.setPartySize(30);
        reservation1.setStatus(ReservationStatus.CONFIRMED);
        
        Reservation reservation2 = new Reservation();
        reservation2.setPartySize(25);
        reservation2.setStatus(ReservationStatus.CHECKED_IN);
        
        List<Reservation> activeReservations = Arrays.asList(reservation1, reservation2);
        
        when(reservationRepository.findByRestaurantIdAndReservationTimeBetweenAndStatusIn(
            eq(1L), 
            any(LocalDateTime.class), 
            any(LocalDateTime.class), 
            anyList()
        )).thenReturn(activeReservations);

        // Act
        Integer availableCapacity = restaurantService.getAvailableCapacity(
            1L, 
            now.minusHours(1), 
            now.plusHours(1)
        );

        // Assert
        // Total capacity is 50, occupied is 30 + 25 = 55, so available should be 0
        assertEquals(0, availableCapacity);
    }
    
    @Test
    void testGetAvailableCapacity_RestaurantNotFound() {
        // Arrange
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            restaurantService.getAvailableCapacity(
                99L, 
                now.minusHours(1), 
                now.plusHours(1)
            );
        });
        
        assertEquals("Restaurant not found", exception.getMessage());
    }
    
    @Test
    void testUpdateCapacity() {
        // Arrange
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        // Act
        Optional<Restaurant> result = restaurantService.updateCapacity(1L, 60);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(60, result.get().getCapacity());
        verify(restaurantRepository).findById(1L);
        verify(restaurantRepository).save(restaurant);
    }
    
    @Test
    void testHasCapacityForReservation_HasCapacity() {
        // Arrange
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        
        Reservation reservation1 = new Reservation();
        reservation1.setPartySize(10);
        reservation1.setStatus(ReservationStatus.CONFIRMED);
        
        List<Reservation> activeReservations = Collections.singletonList(reservation1);
        
        when(reservationRepository.findByRestaurantIdAndReservationTimeBetweenAndStatusIn(
            eq(1L), 
            any(LocalDateTime.class), 
            any(LocalDateTime.class), 
            anyList()
        )).thenReturn(activeReservations);

        // Act
        boolean hasCapacity = restaurantService.hasCapacityForReservation(1L, now, 20);

        // Assert
        // Available capacity is 40, party size is 20, so should return true
        assertTrue(hasCapacity);
    }
    
    @Test
    void testHasCapacityForReservation_NoCapacity() {
        // Arrange
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        
        Reservation reservation1 = new Reservation();
        reservation1.setPartySize(40);
        reservation1.setStatus(ReservationStatus.CONFIRMED);
        
        List<Reservation> activeReservations = Collections.singletonList(reservation1);
        
        when(reservationRepository.findByRestaurantIdAndReservationTimeBetweenAndStatusIn(
            eq(1L), 
            any(LocalDateTime.class), 
            any(LocalDateTime.class), 
            anyList()
        )).thenReturn(activeReservations);

        // Act
        boolean hasCapacity = restaurantService.hasCapacityForReservation(1L, now, 20);

        // Assert
        // Available capacity is 10, party size is 20, so should return false
        assertFalse(hasCapacity);
    }
    
    @Test
    void testHasEnoughMenus() {
        // Act
        boolean hasEnough = restaurantService.hasEnoughMenus(1L, 30);

        // Assert
        // Since hasEnoughMenus always returns true, it should be true
        assertTrue(hasEnough);
        
        // Also test with a larger number
        boolean hasEnoughLarger = restaurantService.hasEnoughMenus(1L, 50);
        assertTrue(hasEnoughLarger);
    }

    @Test
    public void testFindByLocationId() {
        // Arrange
        List<Restaurant> restaurants = new ArrayList<>();
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setName("Restaurant 1");
        restaurant1.setDescription("Description 1");
        restaurant1.setCapacity(100);
        restaurant1.setOperatingHours("10:00-22:00");
        restaurant1.setContactInfo("123456789");
        restaurant1.setLocation(new Location());
        
        Restaurant restaurant2 = new Restaurant();
        restaurant2.setName("Restaurant 2");
        restaurant2.setDescription("Description 2");
        restaurant2.setCapacity(80);
        restaurant2.setOperatingHours("11:00-23:00");
        restaurant2.setContactInfo("987654321");
        restaurant2.setLocation(new Location());
        
        restaurants.add(restaurant1);
        restaurants.add(restaurant2);

        when(restaurantRepository.findByLocationId(1L)).thenReturn(restaurants);

        // Act
        List<Restaurant> result = restaurantService.findByLocationId(1L);

        // Assert
        assertEquals(2, result.size());
        assertEquals("Restaurant 1", result.get(0).getName());
        assertEquals("Restaurant 2", result.get(1).getName());
    }

    @Test
    public void testCreateRestaurantLocationNotFound() {
        // Arrange
        when(locationRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            restaurantService.createRestaurant("New Restaurant", "New Description", 120, "12:00-00:00", "111222333", 1L);
        });
    }
} 