package tqs.hm1114588.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tqs.hm1114588.model.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
    Optional<Location> findByName(String name);
    
    Optional<Location> findByNameAndCountry(String name, String country);
    
    Optional<Location> findByLatitudeAndLongitude(Double latitude, Double longitude);
} 