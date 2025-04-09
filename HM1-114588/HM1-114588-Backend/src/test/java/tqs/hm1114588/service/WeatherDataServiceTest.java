package tqs.hm1114588.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import tqs.hm1114588.model.Location;
import tqs.hm1114588.model.WeatherData;
import tqs.hm1114588.repository.WeatherDataRepository;

@ExtendWith(MockitoExtension.class)
class WeatherDataServiceTest {

    @Mock
    private WeatherDataRepository weatherDataRepository;

    @Mock
    private LocationService locationService;

    @InjectMocks
    private WeatherDataService weatherDataService;

    private Location location;
    private WeatherData weatherData;
    private LocalDate testDate;
    private LocalDateTime testTimestamp;

    @BeforeEach
    void setUp() {
        testDate = LocalDate.of(2025, 4, 10);
        testTimestamp = LocalDateTime.of(2025, 4, 9, 12, 0);
        
        location = new Location();
        location.setId(1L);
        location.setName("Lisbon");
        location.setLatitude(38.7223);
        location.setLongitude(-9.1393);
        
        weatherData = createWeatherData(
            location, 
            22.5, 
            65.0, 
            15.0, 
            1, 
            0.0, 
            1013.0, 
            75.0, 
            "LISBON-01", 
            testTimestamp, 
            testDate
        );
        weatherData.setId(1L);
    }
    
    // Helper method to create WeatherData instances for testing
    private WeatherData createWeatherData(
        Location location, Double temperature, Double humidity, 
        Double windSpeedKm, Integer windDirectionId, Double precipitation,
        Double pressure, Double radiation, String stationId, 
        LocalDateTime timestamp, LocalDate forecastDate) {
            
        WeatherData data = new WeatherData(
            location, temperature, humidity, windSpeedKm, windDirectionId,
            precipitation, pressure, radiation, stationId, timestamp, forecastDate
        );
        return data;
    }

    @Test
    void testFindAll() {
        // Arrange
        List<WeatherData> weatherDataList = Arrays.asList(weatherData);
        when(weatherDataRepository.findAll()).thenReturn(weatherDataList);

        // Act
        List<WeatherData> result = weatherDataService.findAll();

        // Assert
        assertThat(result).isEqualTo(weatherDataList);
        verify(weatherDataRepository).findAll();
    }

    @Test
    void testFindById_WhenExists() {
        // Arrange
        when(weatherDataRepository.findById(1L)).thenReturn(Optional.of(weatherData));

        // Act
        Optional<WeatherData> result = weatherDataService.findById(1L);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.get()).isEqualTo(weatherData);
        verify(weatherDataRepository).findById(1L);
    }

    @Test
    void testFindById_WhenNotExists() {
        // Arrange
        when(weatherDataRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<WeatherData> result = weatherDataService.findById(99L);

        // Assert
        assertThat(result).isEmpty();
        verify(weatherDataRepository).findById(99L);
    }

    @Test
    void testFindByLocation() {
        // Arrange
        List<WeatherData> weatherDataList = Arrays.asList(weatherData);
        when(weatherDataRepository.findByLocation(location)).thenReturn(weatherDataList);

        // Act
        List<WeatherData> result = weatherDataService.findByLocation(location);

        // Assert
        assertThat(result).isEqualTo(weatherDataList);
        verify(weatherDataRepository).findByLocation(location);
    }

    @Test
    void testFindByLocationAndDate_WhenExists() {
        // Arrange
        when(weatherDataRepository.findByLocationAndForecastDate(location, testDate))
            .thenReturn(Optional.of(weatherData));

        // Act
        Optional<WeatherData> result = weatherDataService.findByLocationAndDate(location, testDate);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.get()).isEqualTo(weatherData);
        verify(weatherDataRepository).findByLocationAndForecastDate(location, testDate);
    }

    @Test
    void testFindByLocationAndDate_WhenNotExists() {
        // Arrange
        LocalDate otherDate = LocalDate.of(2025, 4, 15);
        when(weatherDataRepository.findByLocationAndForecastDate(location, otherDate))
            .thenReturn(Optional.empty());

        // Act
        Optional<WeatherData> result = weatherDataService.findByLocationAndDate(location, otherDate);

        // Assert
        assertThat(result).isEmpty();
        verify(weatherDataRepository).findByLocationAndForecastDate(location, otherDate);
    }

    @Test
    void testFindByLocationAndDateRange() {
        // Arrange
        List<WeatherData> weatherDataList = Arrays.asList(weatherData);
        when(weatherDataRepository.findByLocationAndForecastDateGreaterThanEqual(location, testDate))
            .thenReturn(weatherDataList);

        // Act
        List<WeatherData> result = weatherDataService.findByLocationAndDateRange(location, testDate);

        // Assert
        assertThat(result).isEqualTo(weatherDataList);
        verify(weatherDataRepository).findByLocationAndForecastDateGreaterThanEqual(location, testDate);
    }

    @Test
    void testSave() {
        // Arrange
        when(weatherDataRepository.save(weatherData)).thenReturn(weatherData);

        // Act
        WeatherData result = weatherDataService.save(weatherData);

        // Assert
        assertThat(result).isEqualTo(weatherData);
        verify(weatherDataRepository).save(weatherData);
    }

    @Test
    void testSaveWithTimestampUpdate() {
        // Arrange
        WeatherData newData = createWeatherData(
            location, 
            25.0, 
            60.0, 
            10.0, 
            1, 
            0.0, 
            1012.0, 
            70.0, 
            "LISBON-01", 
            null, // timestamp should be set by service
            testDate
        );
        
        when(weatherDataRepository.save(any(WeatherData.class))).thenAnswer(invocation -> {
            WeatherData savedData = invocation.getArgument(0);
            savedData.setId(2L);
            return savedData;
        });

        // Act
        WeatherData result = weatherDataService.save(newData);

        // Assert
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getTimestamp()).isNotNull();
        verify(weatherDataRepository).save(any(WeatherData.class));
    }

    @Test
    void testCreate() {
        // Arrange
        when(weatherDataRepository.save(any(WeatherData.class))).thenReturn(weatherData);

        // Act
        WeatherData result = weatherDataService.create(
            location, 22.5, 65.0, 12.0, 15.0, "North", 1, 0.0, 1013.0, 75.0,
            "LISBON-01", testTimestamp, testDate
        );

        // Assert
        assertThat(result).isEqualTo(weatherData);
        verify(weatherDataRepository).save(any(WeatherData.class));
    }

    @Test
    void testUpdate_WhenExists() {
        // Arrange
        when(weatherDataRepository.findById(1L)).thenReturn(Optional.of(weatherData));
        when(weatherDataRepository.save(any(WeatherData.class))).thenReturn(weatherData);

        // Act
        Optional<WeatherData> result = weatherDataService.update(
            1L, 25.0, 70.0, 20.0, 2, 1.0
        );

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.get().getTemperature()).isEqualTo(25.0);
        assertThat(result.get().getHumidity()).isEqualTo(70.0);
        assertThat(result.get().getWindSpeedKm()).isEqualTo(20.0);
        assertThat(result.get().getWindDirectionId()).isEqualTo(2);
        assertThat(result.get().getPrecipitation()).isEqualTo(1.0);
        verify(weatherDataRepository).findById(1L);
        verify(weatherDataRepository).save(any(WeatherData.class));
    }

    @Test
    void testUpdate_WhenNotExists() {
        // Arrange
        when(weatherDataRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<WeatherData> result = weatherDataService.update(
            99L, 25.0, 70.0, 20.0, 2, 1.0
        );

        // Assert
        assertThat(result).isEmpty();
        verify(weatherDataRepository).findById(99L);
        verify(weatherDataRepository, times(0)).save(any(WeatherData.class));
    }

    @Test
    void testDeleteById() {
        // Arrange
        doNothing().when(weatherDataRepository).deleteById(1L);

        // Act
        weatherDataService.deleteById(1L);

        // Assert
        verify(weatherDataRepository).deleteById(1L);
    }

    @Test
    void testDeleteByLocationAndDate() {
        // Arrange
        doNothing().when(weatherDataRepository).deleteByLocationAndForecastDate(location, testDate);

        // Act
        weatherDataService.deleteByLocationAndDate(location, testDate);

        // Assert
        verify(weatherDataRepository).deleteByLocationAndForecastDate(location, testDate);
    }

    @Test
    void testFindByDate() {
        // Arrange
        List<WeatherData> weatherDataList = Arrays.asList(weatherData);
        when(weatherDataRepository.findByForecastDate(testDate)).thenReturn(weatherDataList);

        // Act
        List<WeatherData> result = weatherDataService.findByDate(testDate);

        // Assert
        assertThat(result).isEqualTo(weatherDataList);
        verify(weatherDataRepository).findByForecastDate(testDate);
    }
} 