package tqs.hm1114588.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import tqs.hm1114588.model.StaffRole;
import tqs.hm1114588.model.User;
import tqs.hm1114588.model.restaurant.Restaurant;
import tqs.hm1114588.service.UserService;

@WebMvcTest(StaffController.class)
class StaffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User staffUser;
    private Restaurant restaurant;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        
        staffUser = new User();
        staffUser.setId(1L);
        staffUser.setName("John Staff");
        staffUser.setEmail("staff@example.com");
        staffUser.setRole("STAFF");
        staffUser.setStaffRole(StaffRole.CHEF);
        staffUser.setRestaurant(restaurant);
    }

    @Test
    void testGetAllStaff() throws Exception {
        when(userService.findByRole("STAFF")).thenReturn(Collections.singletonList(staffUser));

        mockMvc.perform(get("/api/staff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("John Staff")))
                .andExpect(jsonPath("$[0].email", is("staff@example.com")));
                
        verify(userService).findByRole("STAFF");
    }

    @Test
    void testGetStaffByRestaurant() throws Exception {
        when(userService.findStaffByRestaurant(1L)).thenReturn(Collections.singletonList(staffUser));

        mockMvc.perform(get("/api/staff/restaurant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("John Staff")));
                
        verify(userService).findStaffByRestaurant(1L);
    }

    @Test
    void testGetStaffByRole() throws Exception {
        when(userService.findStaffByRole(StaffRole.CHEF)).thenReturn(Collections.singletonList(staffUser));

        mockMvc.perform(get("/api/staff/role/CHEF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("John Staff")));
                
        verify(userService).findStaffByRole(StaffRole.CHEF);
    }

    @Test
    void testCreateStaff() throws Exception {
        String requestBody = """
            {
                "email": "newstaff@example.com",
                "name": "New Staff",
                "staffRole": "SERVER",
                "restaurantId": 1
            }
            """;
            
        when(userService.createStaffMember(
            eq("newstaff@example.com"), 
            eq("New Staff"), 
            eq(StaffRole.SERVER), 
            eq(1L)
        )).thenReturn(staffUser);

        mockMvc.perform(post("/api/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Staff")));
                
        verify(userService).createStaffMember(
            eq("newstaff@example.com"), 
            eq("New Staff"), 
            eq(StaffRole.SERVER), 
            eq(1L)
        );
    }

    @Test
    void testUpdateStaffRole_Success() throws Exception {
        String requestBody = """
            {
                "staffRole": "MANAGER"
            }
            """;
            
        when(userService.updateStaffRole(1L, StaffRole.MANAGER)).thenReturn(Optional.of(staffUser));

        mockMvc.perform(put("/api/staff/1/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Staff")));
                
        verify(userService).updateStaffRole(1L, StaffRole.MANAGER);
    }

    @Test
    void testUpdateStaffRole_NotFound() throws Exception {
        String requestBody = """
            {
                "staffRole": "MANAGER"
            }
            """;
            
        when(userService.updateStaffRole(99L, StaffRole.MANAGER)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/staff/99/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound());
                
        verify(userService).updateStaffRole(99L, StaffRole.MANAGER);
    }

    @Test
    void testTransferStaff_Success() throws Exception {
        String requestBody = """
            {
                "restaurantId": 2
            }
            """;
            
        when(userService.transferStaff(1L, 2L)).thenReturn(Optional.of(staffUser));

        mockMvc.perform(put("/api/staff/1/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Staff")));
                
        verify(userService).transferStaff(1L, 2L);
    }

    @Test
    void testTransferStaff_NotFound() throws Exception {
        String requestBody = """
            {
                "restaurantId": 2
            }
            """;
            
        when(userService.transferStaff(99L, 2L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/staff/99/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound());
                
        verify(userService).transferStaff(99L, 2L);
    }

    @Test
    void testDeleteStaff() throws Exception {
        doNothing().when(userService).deleteById(1L);

        mockMvc.perform(delete("/api/staff/1"))
                .andExpect(status().isNoContent());
                
        verify(userService).deleteById(1L);
    }
} 