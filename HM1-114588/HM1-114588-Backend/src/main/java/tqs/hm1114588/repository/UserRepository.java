package tqs.hm1114588.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tqs.hm1114588.model.StaffRole;
import tqs.hm1114588.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email
     * @param email User email
     * @return User if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find users by role
     * @param role User role
     * @return List of users with the specified role
     */
    List<User> findByRole(String role);
    
    /**
     * Find staff members by restaurant
     * @param role User role (STAFF)
     * @param restaurantId Restaurant ID
     * @return List of staff members
     */
    List<User> findByRoleAndRestaurantId(String role, Long restaurantId);
    
    /**
     * Find staff members by role
     * @param staffRole Staff role
     * @return List of staff members
     */
    List<User> findByStaffRole(StaffRole staffRole);
} 