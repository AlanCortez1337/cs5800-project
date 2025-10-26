package com.alancortez.project.controller;

import com.alancortez.project.model.Staff;
import com.alancortez.project.service.StaffService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(StaffController.class)
class StaffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StaffService staffService;

    private Staff testUser1;
    private Staff testUser2;

    @BeforeEach
    void setUp() {
        testUser1 = new Staff("johnDoeStaff", "password123");
        testUser1.setId(1L);
        testUser1.setCreatedAt(LocalDateTime.now());

        testUser2 = new Staff("janeSmithStaff", "cookies546");
        testUser2.setId(2L);
        testUser2.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateStaff_Success() throws Exception {
        Staff newUser = new Staff("johnDoeStaff", "password123");

        when(staffService.createStaff(any(Staff.class))).thenReturn(testUser1);

        mockMvc.perform(post("/api/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userName").value("johnDoeStaff"))
                .andExpect(jsonPath("$.password").value("password123"));

        verify(staffService, times(1)).createStaff(any(Staff.class));
    }

    @Test
    void testGetAllStaff_Success() throws Exception {
        List<Staff> users = Arrays.asList(testUser1, testUser2);

        when(staffService.getAllStaff()).thenReturn(users);

        mockMvc.perform(get("/api/staff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userName").value("johnDoeStaff"))
                .andExpect(jsonPath("$[0].password").value("password123"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].userName").value("janeSmithStaff"))
                .andExpect(jsonPath("$[1].password").value("cookies546"));

        verify(staffService, times(1)).getAllStaff();
    }

    @Test
    void testGetAllStaff_EmptyList() throws Exception {
        when(staffService.getAllStaff()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/staff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(staffService, times(1)).getAllStaff();
    }

    @Test
    void testGetStaffById_Success() throws Exception {
        when(staffService.getStaffById(1L)).thenReturn(Optional.of(testUser1));

        mockMvc.perform(get("/api/staff/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userName").value("johnDoeStaff"))
                .andExpect(jsonPath("$.password").value("password123"));

        verify(staffService, times(1)).getStaffById(1L);
    }

    @Test
    void testGetStaffById_NotFound() throws Exception {
        when(staffService.getUserById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(staffService, times(1)).getUserById(999L);
    }

    @Test
    void testUpdateStaff_Success() throws Exception {
        Staff updatedUser = new Staff("johnDoeStaffUpdated", "password12323");
        updatedUser.setId(1L);
        updatedUser.setCreatedAt(testUser1.getCreatedAt());

        when(staffService.updateStaff(eq(1L), any(Staff.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/staff/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userName").value("johnDoeStaffUpdated"))
                .andExpect(jsonPath("$.password").value("password12323"));

        verify(staffService, times(1)).updateStaff(eq(1L), any(Staff.class));
    }

    @Test
    void testUpdateStaff_NotFound() throws Exception {
        Staff updatedUser = new Staff("johnDoeStaffUpdated", "password12323");

        when(staffService.updateUser(eq(999L), any(Staff.class)))
                .thenThrow(new IllegalArgumentException("User not found"));

        mockMvc.perform(put("/api/staff/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isNotFound());

        verify(staffService, times(1)).updateStaff(eq(999L), any(Staff.class));
    }

    @Test
    void testDeleteStaff_Success() throws Exception {
        doNothing().when(staffService).deleteStaff(1L);

        mockMvc.perform(delete("/api/staff/1"))
                .andExpect(status().isNoContent());

        verify(staffService, times(1)).deleteStaff(1L);
    }

    @Test
    void testDeleteStaff_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Staff not found"))
                .when(staffService).deleteStaff(999L);

        mockMvc.perform(delete("/api/staff/999"))
                .andExpect(status().isNotFound());

        verify(staffService, times(1)).deleteStaff(999L);
    }
}