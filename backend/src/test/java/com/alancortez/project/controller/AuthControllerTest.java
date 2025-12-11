package com.alancortez.project.controller;

import com.alancortez.project.model.User;
import com.alancortez.project.service.UserService;
import com.alancortez.project.utils.USER_ROLE; // Assume USER_ROLE is imported from utils package
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private User testUser;
    private MockHttpSession mockSession; // Use Spring's MockHttpSession for easier testing

    // --- ASSUMED UTILITY FOR TESTING CONCRETE USER TYPES ---
    // Since UserFactory creates concrete types (Staff/Admin) which implement User,
    // we need a concrete class for Mockito to successfully mock getRole() and getId().
    // You must replace this with your actual concrete class (e.g., com.alancortez.project.model.Staff)
    // For this example, we will assume you have a Staff class:
    private class TestStaffUser extends User {
        public TestStaffUser(Integer id, String username, String password, USER_ROLE role) {
            // Note: Assuming your base User class has protected/package-private fields
            // and maybe an all-args constructor, or that you can set ID/Password/Username directly.
            // Since we can't see the User/Staff constructors, we mock the getter behavior.
            super(username, password, role); // Assuming a constructor exists in Staff/Admin
            this.id = id;
        }

        @Override // You may need to override getRole if it's abstract in User.
        public USER_ROLE getRole() {
            return USER_ROLE.STAFF;
        }

        // Add dummy ID field for testing, as it's not set in the factory method
        private Integer id;
        public Integer getId() { return this.id; }
    }


    @BeforeEach
    void setUp() {
        // Setup a test User object using a mock-friendly concrete class.
        // We simulate the user created by the factory, but add the ID for testing.
        testUser = new TestStaffUser(1, "teststaff", "securepassword", USER_ROLE.STAFF);

        // Setup a mock session
        mockSession = new MockHttpSession();
    }

    // POST /api/auth/login - Login Tests
    @Test
    void login_ShouldReturnOkAndUserInfo_WhenCredentialsAreValid() {
        // Arrange
        Map<String, String> validCredentials = Map.of(
                "userName", testUser.getUsername(),
                "password", testUser.getPassword()
        );

        when(userService.getUserByUserName(testUser.getUsername())).thenReturn(testUser);

        // Act
        ResponseEntity<Map<String, Object>> response = authController.login(validCredentials, mockSession);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> responseBody = response.getBody();
        assertTrue((Boolean) responseBody.get("success"));

        @SuppressWarnings("unchecked")
        Map<String, Object> userDetails = (Map<String, Object>) responseBody.get("user");
        assertEquals(testUser.getId(), userDetails.get("id"));
        assertEquals(testUser.getUsername(), userDetails.get("userName"));
        // The role check now correctly uses the overridden getRole() method on the concrete test class
        assertEquals(testUser.getRole().toString(), userDetails.get("role"));

        // Verify session attributes are set
        assertEquals(testUser.getId(), mockSession.getAttribute("userId"));
        assertEquals(testUser.getUsername(), mockSession.getAttribute("userName"));
        assertEquals(testUser.getRole().toString(), mockSession.getAttribute("userRole"));
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenUsernameIsInvalid() {
        // Arrange
        Map<String, String> invalidCredentials = Map.of(
                "userName", "nonexistentuser",
                "password", "anypassword"
        );

        when(userService.getUserByUserName("nonexistentuser")).thenReturn(null);

        // Act
        ResponseEntity<Map<String, Object>> response = authController.login(invalidCredentials, mockSession);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse((Boolean) response.getBody().get("success"));
        assertEquals("Invalid username or password", response.getBody().get("message"));

        verify(userService, times(1)).getUserByUserName("nonexistentuser");
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenPasswordIsInvalid() {
        // Arrange
        Map<String, String> invalidCredentials = Map.of(
                "userName", testUser.getUsername(),
                "password", "wrongpassword"
        );

        when(userService.getUserByUserName(testUser.getUsername())).thenReturn(testUser);

        // Act
        ResponseEntity<Map<String, Object>> response = authController.login(invalidCredentials, mockSession);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse((Boolean) response.getBody().get("success"));
        assertEquals("Invalid username or password", response.getBody().get("message"));

        verify(userService, times(1)).getUserByUserName(testUser.getUsername());
    }

    // POST /api/auth/logout - Logout Tests
    @Test
    void logout_ShouldInvalidateSessionAndReturnSuccess() {
        // Arrange
        mockSession.setAttribute("userId", testUser.getId());

        // Act
        ResponseEntity<Map<String, Object>> response = authController.logout(mockSession);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((Boolean) response.getBody().get("success"));

        // Verify session is invalidated
        assertTrue(mockSession.isInvalid());
    }

    // GET /api/auth/session - Check Session Tests
    @Test
    void checkSession_ShouldReturnAuthenticated_WhenUserIsInSession() {
        // Arrange
        mockSession.setAttribute("userId", testUser.getId());
        mockSession.setAttribute("userName", testUser.getUsername());
        mockSession.setAttribute("userRole", testUser.getRole().toString());

        // Act
        ResponseEntity<Map<String, Object>> response = authController.checkSession(mockSession);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> responseBody = response.getBody();
        assertTrue((Boolean) responseBody.get("authenticated"));

        @SuppressWarnings("unchecked")
        Map<String, Object> userDetails = (Map<String, Object>) responseBody.get("user");
        assertEquals(testUser.getId(), userDetails.get("id"));
        assertEquals(testUser.getUsername(), userDetails.get("userName"));
        assertEquals(testUser.getRole().toString(), userDetails.get("role"));
    }

    @Test
    void checkSession_ShouldReturnUnauthenticated_WhenUserIsNotInSession() {
        // Arrange - mockSession is clean/empty

        // Act
        ResponseEntity<Map<String, Object>> response = authController.checkSession(mockSession);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse((Boolean) response.getBody().get("authenticated"));
        assertNull(response.getBody().get("user"));
    }
}
