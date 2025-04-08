package tqs.hm1114588.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tqs.hm1114588.model.Location;
import tqs.hm1114588.repository.LocationRepository;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    /**
     * Find all locations
     * @return List of all locations
     */
    public List<Location> findAll() {
        return locationRepository.findAll();
    }

    /**
     * Find location by ID
     * @param id Location ID
     * @return Location if found
     */
    public Optional<Location> findById(Long id) {
        return locationRepository.findById(id);
    }

    /**
     * Find location by name
     * @param name Location name
     * @return Location if found
     */
    public Optional<Location> findByName(String name) {
        return locationRepository.findByName(name);
    }

    /**
     * Find location by name and country
     * @param name Location name
     * @param country Country name
     * @return Location if found
     */
    public Optional<Location> findByNameAndCountry(String name, String country) {
        return locationRepository.findByNameAndCountry(name, country);
    }

    /**
     * Find location by latitude and longitude
     * @param latitude Latitude
     * @param longitude Longitude
     * @return Location if found
     */
    public Optional<Location> findByLatitudeAndLongitude(Double latitude, Double longitude) {
        return locationRepository.findByLatitudeAndLongitude(latitude, longitude);
    }

    /**
     * Create or update a location
     * @param location Location to save
     * @return Saved location
     */
    public Location save(Location location) {
        if (location.getCreatedAt() == null) {
            location.setCreatedAt(LocalDateTime.now());
        }
        return locationRepository.save(location);
    }

    /**
     * Create a new location 
     * @param name Location name
     * @param latitude Latitude
     * @param longitude Longitude
     * @param country Country name
     * @return Created location
     */
    public Location create(String name, Double latitude, Double longitude, String country) {
        Location location = new Location(name, latitude, longitude, country);
        return locationRepository.save(location);
    }

    /**
     * Delete a location by ID
     * @param id Location ID
     */
    public void deleteById(Long id) {
        locationRepository.deleteById(id);
    }

    /**
     * Find or create a location
     * @param name Location name
     * @param latitude Latitude
     * @param longitude Longitude
     * @param country Country name
     * @return Existing or created location
     */
    public Location findOrCreate(String name, Double latitude, Double longitude, String country) {
        return locationRepository.findByNameAndCountry(name, country)
            .orElseGet(() -> create(name, latitude, longitude, country));
    }
} 