package tqs.hm1114588.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tqs.hm1114588.model.Location;
import tqs.hm1114588.service.LocationService;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    /**
     * Get all locations
     * @return List of locations
     */
    @GetMapping
    public List<Location> getAllLocations() {
        return locationService.findAll();
    }

    /**
     * Get location by ID
     * @param id Location ID
     * @return Location if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable Long id) {
        return locationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get location by name
     * @param name Location name
     * @return Location if found
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<Location> getLocationByName(@PathVariable String name) {
        return locationService.findByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get location by name and country
     * @param name Location name
     * @param country Country name
     * @return Location if found
     */
    @GetMapping("/search")
    public ResponseEntity<Location> getLocationByNameAndCountry(
            @RequestParam String name,
            @RequestParam String country) {
        return locationService.findByNameAndCountry(name, country)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new location
     * @param location Location data
     * @return Created location
     */
    @PostMapping
    public Location createLocation(@RequestBody Location location) {
        return locationService.create(
                location.getName(),
                location.getLatitude(),
                location.getLongitude(),
                location.getCountry()
        );
    }

    /**
     * Update a location
     * @param id Location ID
     * @param location Location data
     * @return Updated location if found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Location> updateLocation(
            @PathVariable Long id,
            @RequestBody Location location) {
        return locationService.findById(id)
                .map(existingLocation -> {
                    existingLocation.setName(location.getName());
                    existingLocation.setLatitude(location.getLatitude());
                    existingLocation.setLongitude(location.getLongitude());
                    existingLocation.setCountry(location.getCountry());
                    return locationService.save(existingLocation);
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a location
     * @param id Location ID
     * @return No content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        return locationService.findById(id)
                .map(location -> {
                    locationService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 