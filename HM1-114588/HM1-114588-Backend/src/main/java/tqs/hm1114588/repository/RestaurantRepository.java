package tqs.hm1114588.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tqs.hm1114588.model.restaurant.Restaurant;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    
    /**
     * Find restaurant by name
     * @param name Restaurant name
     * @return Restaurant if found
     */
    Optional<Restaurant> findByName(String name);
} 