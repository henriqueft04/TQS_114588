package tqs.hm1114588.model;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import tqs.hm1114588.model.restaurant.Reservation;
import tqs.hm1114588.model.restaurant.Restaurant;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "role", nullable = false)
    private String role; // CUSTOMER, STAFF, ADMIN

    @Enumerated(EnumType.STRING)
    @Column(name = "staff_role")
    private StaffRole staffRole;

    @Column()
    private String phone;

    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
    
    @Column(name = "hire_date")
    private LocalDateTime hireDate;
    
    @Column(name = "employee_id")
    private String employeeId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reservation> activeReservations;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reservation> totalReservations;

    // Default constructor
    public User() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructor with fields
    public User(String email, String name, String role) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    // Staff constructor
    public User(String email, String name, String role, StaffRole staffRole, Restaurant restaurant) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.staffRole = staffRole;
        this.restaurant = restaurant;
        this.createdAt = LocalDateTime.now();
        this.hireDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    public StaffRole getStaffRole() {
        return staffRole;
    }
    
    public void setStaffRole(StaffRole staffRole) {
        this.staffRole = staffRole;
    }
    
    public Restaurant getRestaurant() {
        return restaurant;
    }
    
    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
    
    public LocalDateTime getHireDate() {
        return hireDate;
    }
    
    public void setHireDate(LocalDateTime hireDate) {
        this.hireDate = hireDate;
    }
    
    public String getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}