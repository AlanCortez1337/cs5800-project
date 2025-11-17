package com.alancortez.project.controller;
import com.alancortez.project.model.User;
import com.alancortez.project.service.UserService;
import com.alancortez.project.utils.USER_ROLE;
import com.alancortez.project.utils.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserFactory userFactory;
    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        this.userFactory = UserFactory.getInstance();
        testUser1 = userFactory.createUser("LebronJames23", "password", USER_ROLE.ADMIN);
        testUser2 = userFactory.createUser("LukaDoncic77", "password", USER_ROLE.STAFF);

    }

    // GET /api/user - Get All Users Tests
    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        List<User> users = Arrays.asList(testUser1, testUser2);
        when(userService.getAllUsers()).thenReturn(users);

        List<User> result = userController.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("LebronJames23", result.get(0).getUsername());
        assertEquals("LukaDoncic77", result.get(1).getUsername());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getAllUsers_ShouldReturnEmptyList_WhenNoUsers() {
        when(userService.getAllUsers()).thenReturn(Arrays.asList());

        List<User> result = userController.getAllUsers();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(userService, times(1)).getAllUsers();
    }

    // GET /api/user/{id} - Get User By ID Tests
    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        when(userService.getUserById(1L)).thenReturn(testUser1);

        ResponseEntity<User> response = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("LebronJames23", response.getBody().getUsername());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void getUserById_ShouldReturnNotFound_WhenUserDoesNotExist() {
        when(userService.getUserById(999L)).thenReturn(null);

        ResponseEntity<User> response = userController.getUserById(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).getUserById(999L);
    }

    // GET /api/user/{userName} - Get User By Username Tests
    @Test
    void getUserByUsername_ShouldReturnUser_WhenUserExists() {
        when(userService.getUserByUsername("LebronJames23")).thenReturn(testUser1);

        ResponseEntity<User> response = userController.getUserByUsername("LebronJames23");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("LebronJames23", response.getBody().getUsername());
        verify(userService, times(1)).getUserByUsername("LebronJames23");
    }

    @Test
    void getUserByUsername_ShouldReturnNotFound_WhenUserDoesNotExist() {
        when(userService.getUserByUsername("NonExistentUser")).thenReturn(null);

        ResponseEntity<User> response = userController.getUserByUsername("NonExistentUser");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).getUserByUsername("NonExistentUser");
    }

    // GET /api/user/staff/{id} - Get User By Staff ID Tests
    @Test
    void getUserByStaffID_ShouldReturnUser_WhenStaffExists() {
        when(userService.getUserByStaffID(2L)).thenReturn(testUser2);

        ResponseEntity<User> response = userController.getUserByStaffID(2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("LukaDoncic77", response.getBody().getUsername());
        assertEquals(USER_ROLE.STAFF, response.getBody().getRole());
        verify(userService, times(1)).getUserByStaffID(2L);
    }

    @Test
    void getUserByStaffID_ShouldReturnNotFound_WhenStaffDoesNotExist() {
        when(userService.getUserByStaffID(999L)).thenReturn(null);

        ResponseEntity<User> response = userController.getUserByStaffID(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).getUserByStaffID(999L);
    }

    // GET /api/user/admin/{id} - Get User By Admin ID Tests
    @Test
    void getUserByAdminID_ShouldReturnUser_WhenAdminExists() {
        when(userService.getUserByAdminID(1L)).thenReturn(testUser1);

        ResponseEntity<User> response = userController.getUserByAdminID(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("LebronJames23", response.getBody().getUsername());
        assertEquals(USER_ROLE.ADMIN, response.getBody().getRole());
        verify(userService, times(1)).getUserByAdminID(1L);
    }

    @Test
    void getUserByAdminID_ShouldReturnNotFound_WhenAdminDoesNotExist() {
        when(userService.getUserByAdminID(999L)).thenReturn(null);

        ResponseEntity<User> response = userController.getUserByAdminID(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).getUserByAdminID(999L);
    }

    // POST /api/user - Create User Tests
    @Test
    void createUser_ShouldReturnCreatedUser() {
        User newUser = userFactory.createUser("KobeBryant24", "password", USER_ROLE.STAFF);
        when(userService.createUser(any(User.class))).thenReturn(newUser);

        User result = userController.createUser("KobeBryant24", "password", USER_ROLE.STAFF);

        assertNotNull(result);
        assertEquals("KobeBryant24", result.getUsername());
        assertEquals(USER_ROLE.STAFF, result.getRole());
        verify(userService, times(1)).createUser(any(User.class));
    }

    // PUT /api/user/{id} - Update User Tests
    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenUserExists() {
        User updatedDetails = userFactory.createUser("UpdatedUsername", "newPassword", USER_ROLE.ADMIN);
        when(userService.getUserById(1L)).thenReturn(testUser1);
        when(userService.createUser(any(User.class))).thenReturn(testUser1);

        ResponseEntity<User> response = userController.updateUser(1L, updatedDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void updateUser_ShouldReturnNotFound_WhenUserDoesNotExist() {
        User updatedDetails = userFactory.createUser("UpdatedUsername", "newPassword", USER_ROLE.ADMIN);
        when(userService.getUserById(999L)).thenReturn(null);

        ResponseEntity<User> response = userController.updateUser(999L, updatedDetails);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).getUserById(999L);
        verify(userService, never()).createUser(any(User.class));
    }

    // DELETE /api/user/{id} - Delete User Tests
    @Test
    void deleteUser_ShouldReturnNoContent() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void deleteUser_ShouldReturnNoContent_EvenWhenUserDoesNotExist() {
        doNothing().when(userService).deleteUser(999L);

        ResponseEntity<Void> response = userController.deleteUser(999L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).deleteUser(999L);
    }
}