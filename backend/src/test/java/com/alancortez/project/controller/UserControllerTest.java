package com.alancortez.project.controller;
import com.alancortez.project.model.Admin;
import com.alancortez.project.model.Staff;
import com.alancortez.project.model.User;
import com.alancortez.project.service.UserService;
import com.alancortez.project.utils.PRIVILEGES;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private String ADMIN_ID;
    private String STAFF_ID;

    @BeforeEach
    void setUp() {
        this.userFactory = UserFactory.getInstance();
        testUser1 = (Admin) userFactory.createUser("LebronJames23", "password", USER_ROLE.ADMIN);
        testUser2 = (Staff) userFactory.createUser("LukaDoncic77", "password", USER_ROLE.STAFF);

        ADMIN_ID = testUser1.getAdminID();
        STAFF_ID = testUser2.getStaffID();
    }

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

    @Test
    void createUser_ShouldReturnCreatedUser() {
        Map<String, String> request = new HashMap<>();
        request.put("userName", "KobeBryant24");
        request.put("password", "password");
        request.put("role", "STAFF");

        User newUser = userFactory.createUser("KobeBryant24", "password", USER_ROLE.STAFF);
        when(userService.createUser(any(User.class))).thenReturn(newUser);
        User result = userController.createUser(request);

        assertNotNull(result);
        assertEquals("KobeBryant24", result.getUsername());
        assertEquals(USER_ROLE.STAFF, result.getRole());
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void createUser_ShouldCreateAdminUser_WhenRoleIsAdmin() {
        Map<String, String> request = new HashMap<>();
        request.put("userName", "AdminUser");
        request.put("password", "adminpass");
        request.put("role", "ADMIN");

        User adminUser = userFactory.createUser("AdminUser", "adminpass", USER_ROLE.ADMIN);
        when(userService.createUser(any(User.class))).thenReturn(adminUser);

        User result = userController.createUser(request);

        assertNotNull(result);
        assertEquals("AdminUser", result.getUsername());
        assertEquals(USER_ROLE.ADMIN, result.getRole());
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenInvalidRole() {
        Map<String, String> request = new HashMap<>();
        request.put("userName", "TestUser");
        request.put("password", "password");
        request.put("role", "INVALID_ROLE");

        assertThrows(IllegalArgumentException.class, () -> {
            userController.createUser(request);
        });
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenUserExists() {
        Map<String, String> request = new HashMap<>();
        request.put("username", "UpdatedUsername");
        request.put("password", "newPassword");

        when(userService.getUserById(1)).thenReturn(testUser1);
        when(userService.createUser(any(User.class))).thenReturn(testUser1);

        ResponseEntity<User> response = userController.updateUser(1, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userService, times(1)).getUserById(1);
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void updateUser_ShouldUpdateOnlyUsername_WhenPasswordIsNull() {
        Map<String, String> request = new HashMap<>();
        request.put("username", "UpdatedUsername");

        when(userService.getUserById(1)).thenReturn(testUser1);
        when(userService.createUser(any(User.class))).thenReturn(testUser1);

        ResponseEntity<User> response = userController.updateUser(1, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userService, times(1)).getUserById(1);
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void updateUser_ShouldNotUpdatePassword_WhenPasswordIsEmpty() {
        Map<String, String> request = new HashMap<>();
        request.put("username", "UpdatedUsername");
        request.put("password", "");

        String originalPassword = testUser1.getPassword();
        when(userService.getUserById(1)).thenReturn(testUser1);
        when(userService.createUser(any(User.class))).thenReturn(testUser1);

        ResponseEntity<User> response = userController.updateUser(1, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(originalPassword, testUser1.getPassword());
        verify(userService, times(1)).getUserById(1);
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void updateUser_ShouldReturnNotFound_WhenUserDoesNotExist() {
        Map<String, String> request = new HashMap<>();
        request.put("username", "UpdatedUsername");
        request.put("password", "newPassword");

        when(userService.getUserById(999)).thenReturn(null);

        ResponseEntity<User> response = userController.updateUser(999, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).getUserById(999);
        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    void updateUser_ShouldHandleEmptyRequest() {
        Map<String, String> request = new HashMap<>();

        when(userService.getUserById(1)).thenReturn(testUser1);
        when(userService.createUser(any(User.class))).thenReturn(testUser1);

        ResponseEntity<User> response = userController.updateUser(1, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userService, times(1)).getUserById(1);
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void updateStaffPrivileges_ShouldReturnOk_OnSuccess() {
        String[] privilegeStrings = {"CREATE_RECIPE", "READ_INGREDIENT"};
        PRIVILEGES[] expectedPrivileges = {PRIVILEGES.CREATE_RECIPE, PRIVILEGES.READ_INGREDIENT};

        doNothing().when(userService).changeStaffPrivilege(ADMIN_ID, STAFF_ID, expectedPrivileges);

        ResponseEntity<String> response = userController.updateStaffPrivileges(
                ADMIN_ID,
                STAFF_ID,
                privilegeStrings
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Staff privileges updated successfully.", response.getBody());
        verify(userService, times(1)).changeStaffPrivilege(eq(ADMIN_ID), eq(STAFF_ID), eq(expectedPrivileges));
    }

    @Test
    void updateStaffPrivileges_ShouldReturnBadRequest_OnInvalidPrivilege() {
        String[] privilegeStrings = {"CREATE_RECIPE", "INVALID_PRIVILEGE"};

        ResponseEntity<String> response = userController.updateStaffPrivileges(
                ADMIN_ID,
                STAFF_ID,
                privilegeStrings
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid privilege name provided."));

        verify(userService, never()).changeStaffPrivilege(anyString(), anyString(), any(PRIVILEGES[].class));
    }

    @Test
    void updateStaffPrivileges_ShouldReturnNotFound_OnUserNotFound() {
        String[] privilegeStrings = {"CREATE_RECIPE"};
        PRIVILEGES[] expectedPrivileges = {PRIVILEGES.CREATE_RECIPE};

        doThrow(new RuntimeException("Admin or Staff user not found"))
                .when(userService).changeStaffPrivilege(ADMIN_ID, STAFF_ID, expectedPrivileges);

        ResponseEntity<String> response = userController.updateStaffPrivileges(
                ADMIN_ID,
                STAFF_ID,
                privilegeStrings
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService, times(1)).changeStaffPrivilege(eq(ADMIN_ID), eq(STAFF_ID), eq(expectedPrivileges));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() {
        doNothing().when(userService).deleteUser(1);

        ResponseEntity<Void> response = userController.deleteUser(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).deleteUser(1);
    }

    @Test
    void deleteUser_ShouldCallServiceDelete() {
        userController.deleteUser(999);

        verify(userService, times(1)).deleteUser(999);
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