package tqs.hm1114588.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import tqs.hm1114588.model.Location;
import tqs.hm1114588.repository.LocationRepository;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationService locationService;

    private Location location;

    @BeforeEach
    void setUp() {
        // Set up test data
        location = new Location();
        location.setId(1L);
        location.setName("Lisbon");
        location.setLatitude(38.736946);
        location.setLongitude(-9.142685);
    }

    @Test
    void testFindAll() {
        // Arrange
        List<Location> locations = Arrays.asList(location);
        when(locationRepository.findAll()).thenReturn(locations);

        // Act
        List<Location> result = locationService.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals(location.getId(), result.get(0).getId());
        verify(locationRepository).findAll();
    }

    @Test
    void testFindById_WhenExists() {
        // Arrange
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        // Act
        Optional<Location> result = locationService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(location.getId(), result.get().getId());
        verify(locationRepository).findById(1L);
    }

    @Test
    void testFindById_WhenNotExists() {
        // Arrange
        when(locationRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Location> result = locationService.findById(99L);

        // Assert
        assertFalse(result.isPresent());
        verify(locationRepository).findById(99L);
    }

    @Test
    void testFindByName() {
        // Arrange
        when(locationRepository.findByName("Lisbon")).thenReturn(Optional.of(location));

        // Act
        Optional<Location> result = locationService.findByName("Lisbon");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Lisbon", result.get().getName());
        verify(locationRepository).findByName("Lisbon");
    }

    @Test
    void testFindByLatitudeAndLongitude() {
        // Arrange
        double latitude = 38.736946;
        double longitude = -9.142685;
        when(locationRepository.findByLatitudeAndLongitude(latitude, longitude))
            .thenReturn(Optional.of(location));

        // Act
        Optional<Location> result = locationService.findByLatitudeAndLongitude(latitude, longitude);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(latitude, result.get().getLatitude());
        assertEquals(longitude, result.get().getLongitude());
        verify(locationRepository).findByLatitudeAndLongitude(latitude, longitude);
    }

    @Test
    void testCreate() {
        // Arrange
        when(locationRepository.save(any(Location.class))).thenAnswer(invocation -> {
            Location savedLocation = invocation.getArgument(0);
            savedLocation.setId(1L);
            return savedLocation;
        });

        // Act
        Location result = locationService.create("Porto", 41.1579, -8.6291);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Porto", result.getName());
        assertEquals(41.1579, result.getLatitude());
        assertEquals(-8.6291, result.getLongitude());
        verify(locationRepository).save(any(Location.class));
    }

    @Test
    void testSave() {
        // Arrange
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        // Act
        Location result = locationService.save(location);

        // Assert
        assertNotNull(result);
        assertEquals(location.getId(), result.getId());
        verify(locationRepository).save(location);
    }

    @Test
    void testDeleteById() {
        // Arrange
        doNothing().when(locationRepository).deleteById(1L);

        // Act
        locationService.deleteById(1L);

        // Assert
        verify(locationRepository).deleteById(1L);
    }

    @Test
    void testFindOrCreate_WhenExists() {
        // Arrange
        when(locationRepository.findByName("Lisbon")).thenReturn(Optional.of(location));

        // Act
        Location result = locationService.findOrCreate("Lisbon", 38.736946, -9.142685);

        // Assert
        assertNotNull(result);
        assertEquals(location.getId(), result.getId());
        verify(locationRepository).findByName("Lisbon");
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    void testFindOrCreate_WhenNotExists() {
        // Arrange
        when(locationRepository.findByName("Porto")).thenReturn(Optional.empty());
        when(locationRepository.save(any(Location.class))).thenAnswer(invocation -> {
            Location savedLocation = invocation.getArgument(0);
            savedLocation.setId(2L);
            return savedLocation;
        });

        // Act
        Location result = locationService.findOrCreate("Porto", 41.1579, -8.6291);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Porto", result.getName());
        assertEquals(41.1579, result.getLatitude());
        assertEquals(-8.6291, result.getLongitude());
        verify(locationRepository).findByName("Porto");
        verify(locationRepository).save(any(Location.class));
    }
} 