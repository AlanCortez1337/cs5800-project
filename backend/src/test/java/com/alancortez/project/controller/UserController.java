package com.alancortez.project.controller;

import com.alancortez.project.model.User;
import com.alancortez.project.service.UserService;
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

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser1 = new User("John Doe", "john@example.com");
        testUser1.setId(1L);
        testUser1.setCreatedAt(LocalDateTime.now());

        testUser2 = new User("Jane Smith", "jane@example.com");
        testUser2.setId(2L);
        testUser2.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateUser_Success() throws Exception {
        User newUser = new User("John Doe", "john@example.com");

        when(userService.createUser(any(User.class))).thenReturn(testUser1);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void testCreateUser_DuplicateEmail() throws Exception {
        User newUser = new User("John Doe", "john@example.com");

        when(userService.createUser(any(User.class)))
                .thenThrow(new IllegalArgumentException("Email already exists"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void testGetAllUsers_Success() throws Exception {
        List<User> users = Arrays.asList(testUser1, testUser2);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"))
                .andExpect(jsonPath("$[1].email").value("jane@example.com"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetAllUsers_EmptyList() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetUserById_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser1));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(999L);
    }

    @Test
    void testGetUserByEmail_Success() throws Exception {
        when(userService.getUserByEmail("john@example.com"))
                .thenReturn(Optional.of(testUser1));

        mockMvc.perform(get("/api/users/email/john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService, times(1)).getUserByEmail("john@example.com");
    }

    @Test
    void testGetUserByEmail_NotFound() throws Exception {
        when(userService.getUserByEmail("notfound@example.com"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/email/notfound@example.com"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserByEmail("notfound@example.com");
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        User updatedUser = new User("John Updated", "johnupdated@example.com");
        updatedUser.setId(1L);
        updatedUser.setCreatedAt(testUser1.getCreatedAt());

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.email").value("johnupdated@example.com"));

        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    void testUpdateUser_NotFound() throws Exception {
        User updatedUser = new User("John Updated", "johnupdated@example.com");

        when(userService.updateUser(eq(999L), any(User.class)))
                .thenThrow(new IllegalArgumentException("User not found"));

        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateUser(eq(999L), any(User.class));
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("User not found"))
                .when(userService).deleteUser(999L);

        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUser(999L);
    }
}