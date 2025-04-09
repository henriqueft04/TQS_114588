package tqs.hm1114588.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * Find location by latitude and longitude
     * @param latitude Latitude
     * @param longitude Longitude
     * @return Location if found
     */
    public Optional<Location> findByLatitudeAndLongitude(Double latitude, Double longitude) {
        return locationRepository.findByLatitudeAndLongitude(latitude, longitude);
    }

    /**
     * Create a new location 
     * @param name Location name
     * @param latitude Latitude
     * @param longitude Longitude
     * @return Created location
     */
    @Transactional
    public Location create(String name, Double latitude, Double longitude) {
        Location location = new Location();
        location.setName(name);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return locationRepository.save(location);
    }

    /**
     * Create or update a location
     * @param location Location to save
     * @return Saved location
     */
    @Transactional
    public Location save(Location location) {
        return locationRepository.save(location);
    }

    /**
     * Delete a location by ID
     * @param id Location ID
     */
    @Transactional
    public void deleteById(Long id) {
        locationRepository.deleteById(id);
    }

    /**
     * Find or create a location
     * @param name Location name
     * @param latitude Latitude
     * @param longitude Longitude
     * @return Existing or created location
     */
    public Location findOrCreate(String name, Double latitude, Double longitude) {
        return locationRepository.findByName(name)
            .orElseGet(() -> create(name, latitude, longitude));
    }
} 