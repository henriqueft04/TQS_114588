package tqs.hm1114588.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tqs.hm1114588.model.User;
import tqs.hm1114588.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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
     * Save user
     * @param user User to save
     * @return Saved user
     */
    @Transactional
    @CachePut(value = "user", key = "#result.id")
    @CacheEvict(value = {"users", "userByEmail", "usersByRole"}, allEntries = true)
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
        return userRepository.save(user);
    }

    /**
     * Delete user by ID
     * @param id User ID
     */
    @Transactional
    @CacheEvict(value = {"user", "users", "userByEmail", "usersByRole"}, allEntries = true)
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
} 