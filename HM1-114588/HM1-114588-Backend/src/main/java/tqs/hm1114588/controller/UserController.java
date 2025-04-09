package tqs.hm1114588.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tqs.hm1114588.model.User;
import tqs.hm1114588.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        String role = credentials.get("role");
        
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body("Email and password are required");
        }

        // Simple authentication logic
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // In a real application, use password encryption
            if (password.equals(user.getPassword())) {
                // Checking role if provided
                if (role != null && !role.equals(user.getRole())) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid role");
                }
                
                Map<String, Object> response = new HashMap<>();
                response.put("id", user.getId());
                response.put("email", user.getEmail());
                response.put("name", user.getName());
                response.put("role", user.getRole());
                
                return ResponseEntity.ok(response);
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> userData) {
        String email = userData.get("email");
        String name = userData.get("name");
        String password = userData.get("password");
        String role = userData.getOrDefault("role", "CUSTOMER");
        
        if (email == null || password == null || name == null) {
            return ResponseEntity.badRequest().body("Email, name, and password are required");
        }

        // Check if user already exists
        Optional<User> existingUser = userService.findByEmail(email);
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User with this email already exists");
        }

        // Create new user
        User user = new User(email, name, role);
        user.setPassword(password);
        user.setStatus("ACTIVE");
        
        User savedUser = userService.save(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", savedUser.getId());
        response.put("email", savedUser.getEmail());
        response.put("name", savedUser.getName());
        response.put("role", savedUser.getRole());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser() {
        // In a real application, you would get the user from the session/token
        // For now, we'll return a sample user
        Map<String, Object> response = new HashMap<>();
        response.put("id", 1);
        response.put("email", "user@example.com");
        response.put("name", "John Doe");
        response.put("role", "CUSTOMER");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // In a real application, you would invalidate the session/token
        return ResponseEntity.ok().body("Logged out successfully");
    }
} 