package tqs.hm1114588.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tqs.hm1114588.service.OpenWeatherService;
import tqs.hm1114588.service.RedisService;

@RestController
@RequestMapping("/api/cache")
public class CacheController {

    @Autowired
    private RedisService redisService;
    
    @Autowired
    private OpenWeatherService openWeatherService;

    /**
     * Get all cache keys
     * @return List of all cache keys
     */
    @GetMapping("/keys")
    public ResponseEntity<Set<Object>> getAllKeys() {
        Set<Object> keys = redisService.sMembers("*");
        return ResponseEntity.ok(keys);
    }

    /**
     * Get cache statistics
     * @return Cache statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCacheStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Get general cache statistics
            stats.put("totalKeys", redisService.sMembers("*").size());
            stats.put("weatherDataKeys", redisService.sMembers("weatherData*").size());
            stats.put("locationKeys", redisService.sMembers("location*").size());
            
            // Get weather API cache statistics
            stats.put("weatherApiRequests", openWeatherService.getTotalRequests());
            stats.put("weatherApiHits", openWeatherService.getCacheHits());
            stats.put("weatherApiMisses", openWeatherService.getCacheMisses());
            
            // Calculate hit ratio (avoid division by zero)
            double hitRatio = 0;
            if (openWeatherService.getTotalRequests() > 0) {
                hitRatio = (double) openWeatherService.getCacheHits() / openWeatherService.getTotalRequests();
            }
            stats.put("weatherApiHitRatio", hitRatio);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            // Log the error
            System.err.println("Error getting cache statistics: " + e.getMessage());
            
            // Return a simple response with error information
            Map<String, Object> errorStats = new HashMap<>();
            errorStats.put("error", "Failed to retrieve cache statistics");
            errorStats.put("reason", e.getMessage());
            errorStats.put("weatherApiRequests", 0);
            errorStats.put("weatherApiHits", 0);
            errorStats.put("weatherApiMisses", 0);
            errorStats.put("weatherApiHitRatio", 0);
            
            return ResponseEntity.ok(errorStats);
        }
    }

    /**
     * Clear all caches
     * @return Status message
     */
    @DeleteMapping
    public ResponseEntity<Map<String, String>> clearAllCaches() {
        redisService.clearAllCaches();
        openWeatherService.resetCacheStatistics();
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "All caches cleared successfully");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Clear a specific cache
     * @param cacheName Cache name
     * @return Status message
     */
    @DeleteMapping("/{cacheName}")
    public ResponseEntity<Map<String, String>> clearCache(@PathVariable String cacheName) {
        Set<Object> keys = redisService.sMembers(cacheName + "*");
        for (Object key : keys) {
            redisService.delete(key.toString());
        }
        
        // Reset weather API cache statistics if clearing weather cache
        if (cacheName.equals("weather")) {
            openWeatherService.resetCacheStatistics();
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Cache '" + cacheName + "' cleared successfully");
        
        return ResponseEntity.ok(response);
    }
} 