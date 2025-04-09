package tqs.hm1114588.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tqs.hm1114588.model.StaffRole;
import tqs.hm1114588.model.User;
import tqs.hm1114588.service.UserService;

@RestController
@RequestMapping("/api/staff")
public class StaffController {
    
    @Autowired
    private UserService userService;
    
    /**
     * Get all staff members
     * @return List of staff members
     */
    @GetMapping
    public List<User> getAllStaff() {
        return userService.findByRole("STAFF");
    }
    
    /**
     * Get staff by restaurant
     * @param restaurantId Restaurant ID
     * @return List of staff members
     */
    @GetMapping("/restaurant/{restaurantId}")
    public List<User> getStaffByRestaurant(@PathVariable Long restaurantId) {
        return userService.findStaffByRestaurant(restaurantId);
    }
    
    /**
     * Get staff by role
     * @param role Staff role
     * @return List of staff members
     */
    @GetMapping("/role/{role}")
    public List<User> getStaffByRole(@PathVariable StaffRole role) {
        return userService.findStaffByRole(role);
    }
    
    /**
     * Create a new staff member
     * @param request Staff information
     * @return Created staff member
     */
    @PostMapping
    public ResponseEntity<User> createStaff(@RequestBody Map<String, Object> request) {
        String email = (String) request.get("email");
        String name = (String) request.get("name");
        StaffRole staffRole = StaffRole.valueOf((String) request.get("staffRole"));
        Long restaurantId = Long.valueOf(request.get("restaurantId").toString());
        
        User user = userService.createStaffMember(email, name, staffRole, restaurantId);
        return ResponseEntity.ok(user);
    }
    
    /**
     * Update staff role
     * @param id Staff ID
     * @param request Update information
     * @return Updated staff member
     */
    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateStaffRole(@PathVariable Long id, @RequestBody Map<String, String> request) {
        StaffRole staffRole = StaffRole.valueOf(request.get("staffRole"));
        
        return userService.updateStaffRole(id, staffRole)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Transfer staff to another restaurant
     * @param id Staff ID
     * @param request Transfer information
     * @return Updated staff member
     */
    @PutMapping("/{id}/transfer")
    public ResponseEntity<?> transferStaff(@PathVariable Long id, @RequestBody Map<String, Long> request) {
        Long restaurantId = request.get("restaurantId");
        
        return userService.transferStaff(id, restaurantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Delete staff
     * @param id Staff ID
     * @return Success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
} 