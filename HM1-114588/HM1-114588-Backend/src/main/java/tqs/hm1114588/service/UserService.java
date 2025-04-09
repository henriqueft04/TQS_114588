package tqs.hm1114588.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tqs.hm1114588.model.StaffRole;
import tqs.hm1114588.model.User;
import tqs.hm1114588.model.restaurant.Restaurant;
import tqs.hm1114588.repository.RestaurantRepository;
import tqs.hm1114588.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RestaurantRepository restaurantRepository;

    /**
     * Find all users
     * @return List of all users
     */
    @Cacheable(value = "users")
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Find user by ID
     * @param id User ID
     * @return User if found
     */
    @Cacheable(value = "user", key = "#id")
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Find user by email
     * @param email User email
     * @return User if found
     */
    @Cacheable(value = "userByEmail", key = "#email")
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Find users by role
     * @param role User role
     * @return List of users with the specified role
     */
    @Cacheable(value = "usersByRole", key = "#role")
    public List<User> findByRole(String role) {
        return userRepository.findByRole(role);
    }
    
    /**
     * Find staff members by restaurant
     * @param restaurantId Restaurant ID
     * @return List of staff members
     */
    @Cacheable(value = "staffByRestaurant", key = "#restaurantId")
    public List<User> findStaffByRestaurant(Long restaurantId) {
        return userRepository.findByRoleAndRestaurantId("STAFF", restaurantId);
    }
    
    /**
     * Find staff members by role
     * @param staffRole Staff role
     * @return List of staff members
     */
    @Cacheable(value = "staffByRole", key = "#staffRole")
    public List<User> findStaffByRole(StaffRole staffRole) {
        return userRepository.findByStaffRole(staffRole);
    }

    /**
     * Save user
     * @param user User to save
     * @return Saved user
     */
    @Transactional
    @CachePut(value = "user", key = "#result.id")
    @CacheEvict(value = {"users", "userByEmail", "usersByRole", "staffByRestaurant", "staffByRole"}, allEntries = true)
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * Create a new user
     * @param email User email
     * @param name User name
     * @param role User role
     * @return Created user
     */
    @Transactional
    @CachePut(value = "user", key = "#result.id")
    @CacheEvict(value = {"users", "userByEmail", "usersByRole"}, allEntries = true)
    public User createUser(String email, String name, String role) {
        User user = new User(email, name, role);
        user.setStatus("ACTIVE");
        user.setPassword(UUID.randomUUID().toString()); // Temporary password
        return userRepository.save(user);
    }
    
    /**
     * Create a new staff member
     * @param email Staff email
     * @param name Staff name
     * @param staffRole Staff role
     * @param restaurantId Restaurant ID
     * @return Created staff member
     */
    @Transactional
    @CachePut(value = "user", key = "#result.id")
    @CacheEvict(value = {"users", "userByEmail", "usersByRole", "staffByRestaurant", "staffByRole"}, allEntries = true)
    public User createStaffMember(String email, String name, StaffRole staffRole, Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        
        User user = new User(email, name, "STAFF", staffRole, restaurant);
        user.setStatus("ACTIVE");
        user.setPassword(UUID.randomUUID().toString()); // Temporary password
        user.setEmployeeId("EMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return userRepository.save(user);
    }
    
    /**
     * Update staff role
     * @param userId User ID
     * @param staffRole New staff role
     * @return Updated user
     */
    @Transactional
    @CachePut(value = "user", key = "#userId")
    @CacheEvict(value = {"users", "userByEmail", "usersByRole", "staffByRestaurant", "staffByRole"}, allEntries = true)
    public Optional<User> updateStaffRole(Long userId, StaffRole staffRole) {
        return userRepository.findById(userId)
                .map(user -> {
                    if ("STAFF".equals(user.getRole())) {
                        user.setStaffRole(staffRole);
                        return userRepository.save(user);
                    }
                    return user;
                });
    }
    
    /**
     * Transfer staff to another restaurant
     * @param userId User ID
     * @param restaurantId New restaurant ID
     * @return Updated user
     */
    @Transactional
    @CachePut(value = "user", key = "#userId")
    @CacheEvict(value = {"users", "userByEmail", "usersByRole", "staffByRestaurant", "staffByRole"}, allEntries = true)
    public Optional<User> transferStaff(Long userId, Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
                
        return userRepository.findById(userId)
                .map(user -> {
                    if ("STAFF".equals(user.getRole())) {
                        user.setRestaurant(restaurant);
                        return userRepository.save(user);
                    }
                    return user;
                });
    }

    /**
     * Delete user by ID
     * @param id User ID
     */
    @Transactional
    @CacheEvict(value = {"user", "users", "userByEmail", "usersByRole", "staffByRestaurant", "staffByRole"}, allEntries = true)
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
} 