package tqs.hm1114588.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import tqs.hm1114588.model.StaffRole;
import tqs.hm1114588.model.User;
import tqs.hm1114588.model.restaurant.Restaurant;
import tqs.hm1114588.repository.RestaurantRepository;
import tqs.hm1114588.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setRole("CUSTOMER");
    }

    @Test
    void testFindAll() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        // Act
        List<User> result = userService.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals(user.getId(), result.get(0).getId());
        verify(userRepository).findAll();
    }

    @Test
    void testFindById() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user.getId(), result.get().getId());
        verify(userRepository).findById(1L);
    }

    @Test
    void testFindByEmail() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findByEmail("test@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user.getEmail(), result.get().getEmail());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void testFindByRole() {
        // Arrange
        when(userRepository.findByRole("CUSTOMER")).thenReturn(Collections.singletonList(user));

        // Act
        List<User> result = userService.findByRole("CUSTOMER");

        // Assert
        assertEquals(1, result.size());
        assertEquals("CUSTOMER", result.get(0).getRole());
        verify(userRepository).findByRole("CUSTOMER");
    }

    @Test
    void testSave() {
        // Arrange
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User result = userService.save(user);

        // Assert
        assertEquals(user.getId(), result.getId());
        verify(userRepository).save(user);
    }

    @Test
    void testCreateUser() {
        // Arrange
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        // Act
        User result = userService.createUser("test@example.com", "Test User", "CUSTOMER");

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
        assertEquals("CUSTOMER", result.getRole());
        assertEquals("ACTIVE", result.getStatus());
        assertNotNull(result.getPassword());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void testFindStaffByRestaurant() {
        // Arrange
        User staffUser = new User();
        staffUser.setId(2L);
        staffUser.setRole("STAFF");
        staffUser.setRestaurant(restaurant);
        
        when(userRepository.findByRoleAndRestaurantId("STAFF", 1L))
            .thenReturn(Collections.singletonList(staffUser));

        // Act
        List<User> result = userService.findStaffByRestaurant(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("STAFF", result.get(0).getRole());
        assertEquals(1L, result.get(0).getRestaurant().getId());
        verify(userRepository).findByRoleAndRestaurantId("STAFF", 1L);
    }
    
    @Test
    void testFindStaffByRole() {
        // Arrange
        User staffUser = new User();
        staffUser.setId(2L);
        staffUser.setRole("STAFF");
        staffUser.setStaffRole(StaffRole.MANAGER);
        
        when(userRepository.findByStaffRole(StaffRole.MANAGER))
            .thenReturn(Collections.singletonList(staffUser));

        // Act
        List<User> result = userService.findStaffByRole(StaffRole.MANAGER);

        // Assert
        assertEquals(1, result.size());
        assertEquals(StaffRole.MANAGER, result.get(0).getStaffRole());
        verify(userRepository).findByStaffRole(StaffRole.MANAGER);
    }
    
    @Test
    void testCreateStaffMember() {
        // Arrange
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2L);
            return savedUser;
        });

        // Act
        User result = userService.createStaffMember("staff@example.com", "Staff User", StaffRole.CHEF, 1L);

        // Assert
        assertEquals(2L, result.getId());
        assertEquals("staff@example.com", result.getEmail());
        assertEquals("Staff User", result.getName());
        assertEquals("STAFF", result.getRole());
        assertEquals(StaffRole.CHEF, result.getStaffRole());
        assertEquals(restaurant, result.getRestaurant());
        assertEquals("ACTIVE", result.getStatus());
        assertNotNull(result.getPassword());
        assertNotNull(result.getEmployeeId());
        assertTrue(result.getEmployeeId().startsWith("EMP-"));
        verify(restaurantRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateStaffRole() {
        // Arrange
        User staffUser = new User();
        staffUser.setId(2L);
        staffUser.setRole("STAFF");
        staffUser.setStaffRole(StaffRole.SERVER);
        
        when(userRepository.findById(2L)).thenReturn(Optional.of(staffUser));
        when(userRepository.save(any(User.class))).thenReturn(staffUser);

        // Act
        Optional<User> result = userService.updateStaffRole(2L, StaffRole.CHEF);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(StaffRole.CHEF, result.get().getStaffRole());
        verify(userRepository).findById(2L);
        verify(userRepository).save(staffUser);
    }
    
    @Test
    void testTransferStaff() {
        // Arrange
        User staffUser = new User();
        staffUser.setId(2L);
        staffUser.setRole("STAFF");
        staffUser.setRestaurant(restaurant);
        
        Restaurant newRestaurant = new Restaurant();
        newRestaurant.setId(2L);
        newRestaurant.setName("New Restaurant");
        
        when(userRepository.findById(2L)).thenReturn(Optional.of(staffUser));
        when(restaurantRepository.findById(2L)).thenReturn(Optional.of(newRestaurant));
        when(userRepository.save(any(User.class))).thenReturn(staffUser);

        // Act
        Optional<User> result = userService.transferStaff(2L, 2L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(2L, result.get().getRestaurant().getId());
        assertEquals("New Restaurant", result.get().getRestaurant().getName());
        verify(userRepository).findById(2L);
        verify(restaurantRepository).findById(2L);
        verify(userRepository).save(staffUser);
    }
} 