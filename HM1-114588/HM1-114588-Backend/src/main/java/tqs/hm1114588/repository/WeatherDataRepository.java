package tqs.hm1114588.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tqs.hm1114588.model.Location;
import tqs.hm1114588.model.WeatherData;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    
    List<WeatherData> findByLocation(Location location);
    
    List<WeatherData> findByLocationAndForecastDateGreaterThanEqual(Location location, LocalDate date);
    
    Optional<WeatherData> findByLocationAndForecastDate(Location location, LocalDate date);
    
    List<WeatherData> findByForecastDate(LocalDate date);
    
    void deleteByLocationAndForecastDate(Location location, LocalDate date);
} 