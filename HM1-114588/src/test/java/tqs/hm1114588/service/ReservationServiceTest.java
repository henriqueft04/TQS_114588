package tqs.hm1114588.service;

import java.time.LocalDateTime;
import java.util.Arrays;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import tqs.hm1114588.model.restaurant.Reservation;
import tqs.hm1114588.model.restaurant.ReservationStatus;
import tqs.hm1114588.model.restaurant.Restaurant;
import tqs.hm1114588.repository.ReservationRepository;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private ReservationService reservationService;

    private Reservation reservation;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
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
    void testFindAll() {
        // Arrange
        List<Reservation> reservations = Arrays.asList(reservation);
        when(reservationRepository.findAll()).thenReturn(reservations);

        // Act
        List<Reservation> result = reservationService.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals(reservation.getId(), result.get(0).getId());
        verify(reservationRepository).findAll();
    }

    @Test
    void testFindById_WhenExists() {
        // Arrange
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // Act
        Optional<Reservation> result = reservationService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(reservation.getId(), result.get().getId());
        verify(reservationRepository).findById(1L);
    }

    @Test
    void testFindById_WhenNotExists() {
        // Arrange
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Reservation> result = reservationService.findById(99L);

        // Assert
        assertFalse(result.isPresent());
        verify(reservationRepository).findById(99L);
    }

    @Test
    void testFindByToken() {
        // Arrange
        when(reservationRepository.findByToken("test-token-123")).thenReturn(Optional.of(reservation));

        // Act
        Optional<Reservation> result = reservationService.findByToken("test-token-123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("test-token-123", result.get().getToken());
        verify(reservationRepository).findByToken("test-token-123");
    }

    @Test
    void testFindByRestaurantId() {
        // Arrange
        List<Reservation> reservations = Arrays.asList(reservation);
        when(reservationRepository.findByRestaurantId(1L)).thenReturn(reservations);

        // Act
        List<Reservation> result = reservationService.findByRestaurantId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getRestaurant().getId());
        verify(reservationRepository).findByRestaurantId(1L);
    }

    @Test
    void testCreateReservation() {
        // Arrange
        LocalDateTime reservationTime = LocalDateTime.now().plusDays(1);
        when(restaurantService.findById(1L)).thenReturn(Optional.of(restaurant));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation savedReservation = invocation.getArgument(0);
            savedReservation.setId(1L);
            return savedReservation;
        });

        // Act
        Reservation result = reservationService.createReservation(
                1L, "John Doe", "john@example.com", "1234567890",
                4, reservationTime, "Dinner", "Special requests",
                false, 4);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getCustomerName());
        assertEquals("john@example.com", result.getCustomerEmail());
        assertEquals(ReservationStatus.PENDING, result.getStatus());
        assertNotNull(result.getToken());
        verify(restaurantService).findById(1L);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void testCreateReservation_RestaurantNotFound() {
        // Arrange
        LocalDateTime reservationTime = LocalDateTime.now().plusDays(1);
        when(restaurantService.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.createReservation(
                    99L, "John Doe", "john@example.com", "1234567890",
                    4, reservationTime, "Dinner", "Special requests",
                    false, 4);
        });

        assertEquals("Restaurant not found", exception.getMessage());
        verify(restaurantService).findById(99L);
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void testCancelReservation() {
        // Arrange
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // Act
        Optional<Reservation> result = reservationService.cancelReservation(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ReservationStatus.CANCELLED, result.get().getStatus());
        verify(reservationRepository).findById(1L);
        verify(reservationRepository).save(reservation);
    }

    @Test
    void testConfirmReservationById() {
        // Arrange
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // Act
        Optional<Reservation> result = reservationService.confirmReservation(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ReservationStatus.CONFIRMED, result.get().getStatus());
        verify(reservationRepository).findById(1L);
        verify(reservationRepository).save(reservation);
    }

    @Test
    void testConfirmReservationByToken() {
        // Arrange
        when(reservationRepository.findByToken("test-token-123")).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // Act
        Optional<Reservation> result = reservationService.confirmReservation("test-token-123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ReservationStatus.CONFIRMED, result.get().getStatus());
        verify(reservationRepository).findByToken("test-token-123");
        verify(reservationRepository).save(reservation);
    }

    @Test
    void testCheckInReservation_WhenConfirmed() {
        // Arrange
        reservation.setStatus(ReservationStatus.CONFIRMED);
        when(reservationRepository.findByToken("test-token-123")).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // Act
        Optional<Reservation> result = reservationService.checkInReservation("test-token-123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ReservationStatus.CHECKED_IN, result.get().getStatus());
        verify(reservationRepository).findByToken("test-token-123");
        verify(reservationRepository).save(reservation);
    }

    @Test
    void testCheckInReservation_WhenNotConfirmed() {
        // Arrange - reservation is already in PENDING status from setUp
        when(reservationRepository.findByToken("test-token-123")).thenReturn(Optional.of(reservation));

        // Act
        Optional<Reservation> result = reservationService.checkInReservation("test-token-123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ReservationStatus.PENDING, result.get().getStatus()); // Status should not change
        verify(reservationRepository).findByToken("test-token-123");
        verify(reservationRepository, never()).save(reservation);
    }
} 