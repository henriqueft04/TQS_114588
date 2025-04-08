package tqs.hm1114588.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tqs.hm1114588.model.Location;
import tqs.hm1114588.model.WeatherData;
import tqs.hm1114588.repository.WeatherDataRepository;

@Service
public class WeatherDataService {

    @Autowired
    private WeatherDataRepository weatherDataRepository;

    @Autowired
    private LocationService locationService;

    /**
     * Find all weather data
     * @return List of all weather data
     */
    @Cacheable(value = "weatherDataList")
    public List<WeatherData> findAll() {
        return weatherDataRepository.findAll();
    }

    /**
     * Find weather data by ID
     * @param id Weather data ID
     * @return Weather data if found
     */
    @Cacheable(value = "weatherData", key = "#id")
    public Optional<WeatherData> findById(Long id) {
        return weatherDataRepository.findById(id);
    }

    /**
     * Find weather data by location
     * @param location Location
     * @return List of weather data for the location
     */
    @Cacheable(value = "weatherDataByLocation", key = "#location.id")
    public List<WeatherData> findByLocation(Location location) {
        return weatherDataRepository.findByLocation(location);
    }

    /**
     * Find weather data by location and forecast date
     * @param location Location
     * @param date Forecast date
     * @return Weather data if found
     */
    @Cacheable(value = "weatherDataByLocationAndDate", key = "#location.id + '_' + #date")
    public Optional<WeatherData> findByLocationAndDate(Location location, LocalDate date) {
        return weatherDataRepository.findByLocationAndForecastDate(location, date);
    }

    /**
     * Find weather data by location and forecast date range
     * @param location Location
     * @param startDate Start date
     * @return List of weather data in the date range
     */
    @Cacheable(value = "weatherDataByLocationAndDateRange", key = "#location.id + '_' + #startDate")
    public List<WeatherData> findByLocationAndDateRange(Location location, LocalDate startDate) {
        return weatherDataRepository.findByLocationAndForecastDateGreaterThanEqual(location, startDate);
    }

    /**
     * Save weather data
     * @param weatherData Weather data to save
     * @return Saved weather data
     */
    @Transactional
    @CachePut(value = "weatherData", key = "#result.id")
    @CacheEvict(value = {"weatherDataList", "weatherDataByLocation", "weatherDataByLocationAndDate", "weatherDataByLocationAndDateRange", "weatherDataByDate"}, allEntries = true)
    public WeatherData save(WeatherData weatherData) {
        if (weatherData.getTimestamp() == null) {
            weatherData.setTimestamp(LocalDateTime.now());
        }
        return weatherDataRepository.save(weatherData);
    }

    /**
     * Create weather data
     * @param location Location
     * @param temperature Temperature
     * @param humidity Humidity
     * @param windSpeed Wind speed
     * @param windSpeedKm Wind speed in km
     * @param windDirection Wind direction
     * @param windDirectionId Wind direction ID
     * @param precipitation Precipitation
     * @param pressure Pressure
     * @param radiation Radiation
     * @param stationId Station ID
     * @param timestamp Timestamp
     * @param forecastDate Forecast date
     * @return Created weather data
     */
    @Transactional
    @CachePut(value = "weatherData", key = "#result.id")
    @CacheEvict(value = {"weatherDataList", "weatherDataByLocation", "weatherDataByLocationAndDate", "weatherDataByLocationAndDateRange", "weatherDataByDate"}, allEntries = true)
    public WeatherData create(Location location, Double temperature, Double humidity, 
                            Double windSpeed, Double windSpeedKm, String windDirection, 
                            Integer windDirectionId, Double precipitation, Double pressure, 
                            Double radiation, String stationId, LocalDateTime timestamp, 
                            LocalDate forecastDate) {
        WeatherData weatherData = new WeatherData(
            location, temperature, humidity, windSpeedKm, windDirectionId,
            precipitation, pressure, radiation, stationId, timestamp, forecastDate
        );
        
        return weatherDataRepository.save(weatherData);
    }

    /**
     * Update weather data
     * @param id Weather data ID
     * @param temperature Temperature
     * @param humidity Humidity
     * @param windSpeed Wind speed
     * @param windDirection Wind direction
     * @param precipitation Precipitation
     * @param weatherCondition Weather condition
     * @return Updated weather data
     */
    @Transactional
    @Caching(
        put = { @CachePut(value = "weatherData", key = "#id") },
        evict = {
            @CacheEvict(value = {"weatherDataList", "weatherDataByLocation", "weatherDataByLocationAndDate", "weatherDataByLocationAndDateRange", "weatherDataByDate"}, allEntries = true)
        }
    )
    public Optional<WeatherData> update(Long id, Double temperature, Double humidity, 
                                      Double windSpeedKm, Integer windDirectionId, Double precipitation) {
        return weatherDataRepository.findById(id)
            .map(weatherData -> {
                weatherData.setTemperature(temperature);
                weatherData.setHumidity(humidity);
                weatherData.setWindSpeedKm(windSpeedKm);
                weatherData.setWindDirectionId(windDirectionId);
                weatherData.setPrecipitation(precipitation);
                weatherData.setTimestamp(LocalDateTime.now());
                return weatherDataRepository.save(weatherData);
            });
    }

    /**
     * Delete weather data by ID
     * @param id Weather data ID
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "weatherData", key = "#id"),
        @CacheEvict(value = {"weatherDataList", "weatherDataByLocation", "weatherDataByLocationAndDate", "weatherDataByLocationAndDateRange", "weatherDataByDate"}, allEntries = true)
    })
    public void deleteById(Long id) {
        weatherDataRepository.deleteById(id);
    }

    /**
     * Delete weather data by location and forecast date
     * @param location Location
     * @param date Forecast date
     */
    @Transactional
    @CacheEvict(value = {"weatherDataList", "weatherDataByLocation", "weatherDataByLocationAndDate", "weatherDataByLocationAndDateRange", "weatherDataByDate"}, allEntries = true)
    public void deleteByLocationAndDate(Location location, LocalDate date) {
        weatherDataRepository.deleteByLocationAndForecastDate(location, date);
    }

    @Cacheable(value = "weatherDataByDate", key = "#date")
    public List<WeatherData> findByDate(LocalDate date) {
        return weatherDataRepository.findByForecastDate(date);
    }
} 