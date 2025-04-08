package tqs.hm1114588.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tqs.hm1114588.model.WeatherData;
import tqs.hm1114588.service.LocationService;
import tqs.hm1114588.service.WeatherAPIService;
import tqs.hm1114588.service.WeatherDataService;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherAPIService weatherAPIService;

    @Autowired
    private WeatherDataService weatherDataService;

    @Autowired
    private LocationService locationService;

    /**
     * Get weather forecast for a location
     * @param locationId Location ID
     * @param date Forecast date
     * @return Weather data if found
     */
    @GetMapping("/forecast")
    public ResponseEntity<WeatherData> getForecast(
            @RequestParam Long locationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        return locationService.findById(locationId)
                .map(location -> weatherAPIService.getForecast(location, date)
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build()))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get weather data for a location
     * @param locationId Location ID
     * @return List of weather data
     */
    @GetMapping("/location/{locationId}")
    public List<WeatherData> getWeatherByLocation(@PathVariable Long locationId) {
        return locationService.findById(locationId)
                .map(weatherDataService::findByLocation)
                .orElse(List.of());
    }

    /**
     * Get weather data for a location and date range
     * @param locationId Location ID
     * @param startDate Start date
     * @return List of weather data
     */
    @GetMapping("/location/{locationId}/range")
    public List<WeatherData> getWeatherByLocationAndDateRange(
            @PathVariable Long locationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        
        return locationService.findById(locationId)
                .map(location -> weatherDataService.findByLocationAndDateRange(location, startDate))
                .orElse(List.of());
    }

    /**
     * Update weather data
     * @param id Weather data ID
     * @param weatherData Weather data
     * @return Updated weather data if found
     */
    @PutMapping("/{id}")
    public ResponseEntity<WeatherData> updateWeatherData(
            @PathVariable Long id,
            @RequestBody WeatherData weatherData) {
        
        return weatherDataService.update(
                id,
                weatherData.getTemperature(),
                weatherData.getHumidity(),
                weatherData.getWindSpeedKm(),
                weatherData.getWindDirectionId(),
                weatherData.getPrecipitation()
        )
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete weather data
     * @param id Weather data ID
     * @return No content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWeatherData(@PathVariable Long id) {
        return weatherDataService.findById(id)
                .map(weatherData -> {
                    weatherDataService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 