package com.alancortez.project.controller;

import com.alancortez.project.model.User;
import com.alancortez.project.service.UserService;
import com.alancortez.project.utils.USER_ROLE;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;

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
    private MockHttpSession mockSession;

    private class TestStaffUser extends User {
        public TestStaffUser(Integer id, String username, String password, USER_ROLE role) {
            super(username, password, role);
            this.id = id;
        }

        @Override
        public USER_ROLE getRole() {
            return USER_ROLE.STAFF;
        }

        private Integer id;
        public Integer getId() { return this.id; }
    }


    @BeforeEach
    void setUp() {
        testUser = new TestStaffUser(1, "teststaff", "securepassword", USER_ROLE.STAFF);
        mockSession = new MockHttpSession();
    }

    @Test
    void login_ShouldReturnOkAndUserInfo_WhenCredentialsAreValid() {
        Map<String, String> validCredentials = Map.of(
                "userName", testUser.getUsername(),
                "password", testUser.getPassword()
        );

        when(userService.getUserByUserName(testUser.getUsername())).thenReturn(testUser);
        ResponseEntity<Map<String, Object>> response = authController.login(validCredentials, mockSession);
        Map<String, Object> responseBody = response.getBody();
        Map<String, Object> userDetails = (Map<String, Object>) responseBody.get("user");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals(testUser.getId(), userDetails.get("id"));
        assertEquals(testUser.getUsername(), userDetails.get("userName"));
        assertEquals(testUser.getRole().toString(), userDetails.get("role"));
        assertEquals(testUser.getId(), mockSession.getAttribute("userId"));
        assertEquals(testUser.getUsername(), mockSession.getAttribute("userName"));
        assertEquals(testUser.getRole().toString(), mockSession.getAttribute("userRole"));
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenUsernameIsInvalid() {
        Map<String, String> invalidCredentials = Map.of(
                "userName", "nonexistentuser",
                "password", "anypassword"
        );

        when(userService.getUserByUserName("nonexistentuser")).thenReturn(null);
        ResponseEntity<Map<String, Object>> response = authController.login(invalidCredentials, mockSession);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse((Boolean) response.getBody().get("success"));
        assertEquals("Invalid username or password", response.getBody().get("message"));
        verify(userService, times(1)).getUserByUserName("nonexistentuser");
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenPasswordIsInvalid() {
        Map<String, String> invalidCredentials = Map.of(
                "userName", testUser.getUsername(),
                "password", "wrongpassword"
        );

        when(userService.getUserByUserName(testUser.getUsername())).thenReturn(testUser);
        ResponseEntity<Map<String, Object>> response = authController.login(invalidCredentials, mockSession);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse((Boolean) response.getBody().get("success"));
        assertEquals("Invalid username or password", response.getBody().get("message"));
        verify(userService, times(1)).getUserByUserName(testUser.getUsername());
    }

    @Test
    void logout_ShouldInvalidateSessionAndReturnSuccess() {
        mockSession.setAttribute("userId", testUser.getId());
        ResponseEntity<Map<String, Object>> response = authController.logout(mockSession);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((Boolean) response.getBody().get("success"));
        assertTrue(mockSession.isInvalid());
    }

    @Test
    void checkSession_ShouldReturnAuthenticated_WhenUserIsInSession() {
        mockSession.setAttribute("userId", testUser.getId());
        mockSession.setAttribute("userName", testUser.getUsername());
        mockSession.setAttribute("userRole", testUser.getRole().toString());

        ResponseEntity<Map<String, Object>> response = authController.checkSession(mockSession);
        Map<String, Object> responseBody = response.getBody();
        Map<String, Object> userDetails = (Map<String, Object>) responseBody.get("user");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((Boolean) responseBody.get("authenticated"));
        assertEquals(testUser.getId(), userDetails.get("id"));
        assertEquals(testUser.getUsername(), userDetails.get("userName"));
        assertEquals(testUser.getRole().toString(), userDetails.get("role"));
    }

    @Test
    void checkSession_ShouldReturnUnauthenticated_WhenUserIsNotInSession() {
        ResponseEntity<Map<String, Object>> response = authController.checkSession(mockSession);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse((Boolean) response.getBody().get("authenticated"));
        assertNull(response.getBody().get("user"));
    }
}
