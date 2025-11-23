package com.alancortez.project.controller;
import com.alancortez.project.model.Admin;
import com.alancortez.project.model.Staff;
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
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserFactory userFactory;
    private Admin testUser1;
    private Staff testUser2;

    @BeforeEach
    void setUp() {
        this.userFactory = UserFactory.getInstance();
        testUser1 = (Admin) userFactory.createUser("LebronJames23", "password", USER_ROLE.ADMIN);
        testUser2 = (Staff) userFactory.createUser("LukaDoncic77", "password", USER_ROLE.STAFF);

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
        when(userService.getUserById(1)).thenReturn(testUser1);

        ResponseEntity<User> response = userController.getUserById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("LebronJames23", response.getBody().getUsername());
        verify(userService, times(1)).getUserById(1);
    }

    @Test
    void getUserById_ShouldReturnNotFound_WhenUserDoesNotExist() {
        when(userService.getUserById(999)).thenReturn(null);

        ResponseEntity<User> response = userController.getUserById(999);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).getUserById(999);
    }

    // GET /api/user/{userName} - Get User By Username Tests
    @Test
    void getUserByUsername_ShouldReturnUser_WhenUserExists() {
        when(userService.getUserByUserName("LebronJames23")).thenReturn(testUser1);

        ResponseEntity<User> response = userController.getUserByUserName("LebronJames23");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("LebronJames23", response.getBody().getUsername());
        verify(userService, times(1)).getUserByUserName("LebronJames23");
    }

    @Test
    void getUserByUsername_ShouldReturnNotFound_WhenUserDoesNotExist() {
        when(userService.getUserByUserName("NonExistentUser")).thenReturn(null);

        ResponseEntity<User> response = userController.getUserByUserName("NonExistentUser");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).getUserByUserName("NonExistentUser");
    }

    // GET /api/user/staff/{id} - Get User By Staff ID Tests
    @Test
    void getUserByStaffID_ShouldReturnUser_WhenStaffExists() {
        when(userService.getUserByStaffID(testUser2.getStaffID())).thenReturn(testUser2);

        ResponseEntity<User> response = userController.getUserByStaffID(testUser2.getStaffID());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("LukaDoncic77", response.getBody().getUsername());
        assertEquals(USER_ROLE.STAFF, response.getBody().getRole());
        verify(userService, times(1)).getUserByStaffID(testUser2.getStaffID());
    }

    @Test
    void getUserByStaffID_ShouldReturnNotFound_WhenStaffDoesNotExist() {
        when(userService.getUserByStaffID("12312312312312312312")).thenReturn(null);

        ResponseEntity<User> response = userController.getUserByStaffID("12312312312312312312");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).getUserByStaffID("12312312312312312312");
    }

    // GET /api/user/admin/{id} - Get User By Admin ID Tests
    @Test
    void getUserByAdminID_ShouldReturnUser_WhenAdminExists() {
        when(userService.getUserByAdminID(testUser1.getAdminID())).thenReturn(testUser1);

        ResponseEntity<User> response = userController.getUserByAdminID(testUser1.getAdminID());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("LebronJames23", response.getBody().getUsername());
        assertEquals(USER_ROLE.ADMIN, response.getBody().getRole());
        verify(userService, times(1)).getUserByAdminID(testUser1.getAdminID());
    }

    @Test
    void getUserByAdminID_ShouldReturnNotFound_WhenAdminDoesNotExist() {
        when(userService.getUserByAdminID("12312312312312312312")).thenReturn(null);

        ResponseEntity<User> response = userController.getUserByAdminID("12312312312312312312");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).getUserByAdminID("12312312312312312312");
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
        when(userService.getUserById(1)).thenReturn(testUser1);
        when(userService.createUser(any(User.class))).thenReturn(testUser1);

        ResponseEntity<User> response = userController.updateUser(1, updatedDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userService, times(1)).getUserById(1);
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void updateUser_ShouldReturnNotFound_WhenUserDoesNotExist() {
        User updatedDetails = userFactory.createUser("UpdatedUsername", "newPassword", USER_ROLE.ADMIN);
        when(userService.getUserById(999)).thenReturn(null);

        ResponseEntity<User> response = userController.updateUser(999, updatedDetails);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).getUserById(999);
        verify(userService, never()).createUser(any(User.class));
    }

    // DELETE /api/user/{id} - Delete User Tests
    @Test
    void deleteUser_ShouldReturnNoContent() {
        doNothing().when(userService).deleteUser(1);

        ResponseEntity<Void> response = userController.deleteUser(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).deleteUser(1);
    }

    @Test
    void deleteUser_ShouldReturnNoContent_EvenWhenUserDoesNotExist() {
        doNothing().when(userService).deleteUser(999);

        ResponseEntity<Void> response = userController.deleteUser(999);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).deleteUser(999);
    }
}