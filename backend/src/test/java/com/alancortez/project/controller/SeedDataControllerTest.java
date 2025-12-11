package com.alancortez.project.controller;

import com.alancortez.project.model.Report;
import com.alancortez.project.model.User;
import com.alancortez.project.repository.ReportRepository;
import com.alancortez.project.service.UserService;
import com.alancortez.project.utils.USER_ROLE;
import com.alancortez.project.utils.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SeedDataControllerTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private SeedDataController seedDataController;

    @Captor
    private ArgumentCaptor<List<Report>> reportListCaptor;

    private User mockAdminUser;
    private User mockStaffUser;

    @BeforeEach
    void setUp() {
        // Initialize the Mocks, but DO NOT STUB THEM AT ALL here.
        mockAdminUser = mock(User.class);
        mockStaffUser = mock(User.class);
    }

    // =========================================================================
    // POST /api/seed/users - Seed Users Tests
    // =========================================================================

    @Test
    void seedUsers_ShouldCreate13UsersAndCallUserServiceCreate() {
        // ARRANGE: Setup static mocking for UserFactory
        try (MockedStatic<UserFactory> mockedFactory = mockStatic(UserFactory.class)) {

            // NOTE: We do NOT stub getUsername() because we expect all createUser() calls to succeed.

            // 1. Setup static factory mocking
            UserFactory factoryMockInstance = mock(UserFactory.class);
            mockedFactory.when(UserFactory::getInstance).thenReturn(factoryMockInstance);

            // 2. Stub the instance method createUser()
            when(factoryMockInstance.createUser(anyString(), anyString(), eq(USER_ROLE.ADMIN)))
                    .thenReturn(mockAdminUser);
            when(factoryMockInstance.createUser(anyString(), anyString(), eq(USER_ROLE.STAFF)))
                    .thenReturn(mockStaffUser);

            // 3. Stub UserService for SUCCESS (no exception thrown)
            int expectedTotalUsers = 13;
            when(userService.createUser(any(User.class))).thenReturn(mockAdminUser);

            // ACT
            ResponseEntity<String> response = seedDataController.seedUsers();

            // ASSERT
            assertEquals(HttpStatus.OK, response.getStatusCode());
            String expectedMessage = "Successfully created 13 users! (Admins: 3, Staff: 10)";
            assertEquals(expectedMessage, response.getBody());

            // Verify
            verify(factoryMockInstance, times(3)).createUser(anyString(), anyString(), eq(USER_ROLE.ADMIN));
            verify(factoryMockInstance, times(10)).createUser(anyString(), anyString(), eq(USER_ROLE.STAFF));
            verify(userService, times(expectedTotalUsers)).createUser(any(User.class));

            // Verify getUsername() was NEVER called because there were no exceptions
            verify(mockAdminUser, never()).getUsername();
            verify(mockStaffUser, never()).getUsername();

        }
    }

    @Test
    void seedUsers_ShouldHandleUserCreationFailureGracefully() {
        // ARRANGE: Setup static mocking for UserFactory
        try (MockedStatic<UserFactory> mockedFactory = mockStatic(UserFactory.class)) {

            // NOTE: Only stub getUsername() here because we expect an exception that triggers the catch block!
            when(mockStaffUser.getUsername()).thenReturn("mockStaff");

            // 1. Setup static factory mocking
            UserFactory factoryMockInstance = mock(UserFactory.class);
            mockedFactory.when(UserFactory::getInstance).thenReturn(factoryMockInstance);

            // 2. Stub the instance method createUser()
            when(factoryMockInstance.createUser(anyString(), anyString(), eq(USER_ROLE.ADMIN)))
                    .thenReturn(mockAdminUser);
            when(factoryMockInstance.createUser(anyString(), anyString(), eq(USER_ROLE.STAFF)))
                    .thenReturn(mockStaffUser);

            // 3. Stub userService.createUser with FAILURE simulation (User 4 fails)
            when(userService.createUser(any(User.class)))
                    .thenReturn(mockAdminUser) // 1
                    .thenReturn(mockAdminUser) // 2
                    .thenReturn(mockAdminUser) // 3
                    .thenThrow(new RuntimeException("User Exists")) // 4 (Fails, triggers getUsername() on mockStaffUser)
                    .thenReturn(mockStaffUser) // 5
                    .thenReturn(mockStaffUser) // ...
                    .thenReturn(mockStaffUser)
                    .thenReturn(mockStaffUser)
                    .thenReturn(mockStaffUser)
                    .thenReturn(mockStaffUser)
                    .thenReturn(mockStaffUser)
                    .thenReturn(mockStaffUser)
                    .thenReturn(mockStaffUser); // 13

            // ACT
            ResponseEntity<String> response = seedDataController.seedUsers();

            // ASSERT
            assertEquals(HttpStatus.OK, response.getStatusCode());
            String expectedMessage = "Successfully created 12 users! (Admins: 3, Staff: 9)";
            assertEquals(expectedMessage, response.getBody());

            // Verify
            verify(userService, times(13)).createUser(any(User.class));
            verify(factoryMockInstance, times(3)).createUser(anyString(), anyString(), eq(USER_ROLE.ADMIN));
            verify(factoryMockInstance, times(10)).createUser(anyString(), anyString(), eq(USER_ROLE.STAFF));

            // Verify getUsername() was called exactly once on the failed mock (mockStaffUser)
            verify(mockStaffUser, times(1)).getUsername();

        }
    }

    @Test
    void seedAll_ShouldCallBothSeedUsersAndSeedReports() {
        // ARRANGE: Setup static mocking for UserFactory
        try (MockedStatic<UserFactory> mockedFactory = mockStatic(UserFactory.class)) {

            // NOTE: We do NOT stub getUsername() because we expect the seedUsers() call to succeed.

            // 1. Setup static factory mocking
            UserFactory factoryMockInstance = mock(UserFactory.class);
            mockedFactory.when(UserFactory::getInstance).thenReturn(factoryMockInstance);

            // 2. Stub the instance method createUser()
            when(factoryMockInstance.createUser(anyString(), anyString(), eq(USER_ROLE.ADMIN)))
                    .thenReturn(mockAdminUser);
            when(factoryMockInstance.createUser(anyString(), anyString(), eq(USER_ROLE.STAFF)))
                    .thenReturn(mockStaffUser);

            // 3. Stub UserService for seedUsers SUCCESS
            when(userService.createUser(any(User.class))).thenReturn(mockAdminUser);

            // ACT
            ResponseEntity<String> response = seedDataController.seedAll();

            // ASSERT
            assertEquals(HttpStatus.OK, response.getStatusCode());

            // Check the combined success messages
            assertTrue(response.getBody().contains("Successfully created 13 users!"));
            assertTrue(response.getBody().contains("report entries!"));

            // Verify
            verify(userService, times(13)).createUser(any(User.class)); // From seedUsers
            verify(reportRepository, times(1)).saveAll(anyList());     // From seedReports
            verify(factoryMockInstance, times(3)).createUser(anyString(), anyString(), eq(USER_ROLE.ADMIN));
            verify(factoryMockInstance, times(10)).createUser(anyString(), anyString(), eq(USER_ROLE.STAFF));

            // Verify getUsername() was NEVER called because all users saved successfully
            verify(mockAdminUser, never()).getUsername();
            verify(mockStaffUser, never()).getUsername();
        }
    }

    // =========================================================================
    // POST /api/seed/reports - Seed Reports Tests (Unchanged)
    // =========================================================================

    @Test
    void seedReports_ShouldCallSaveAllWithNumerousReports() {
        // Act
        ResponseEntity<String> response = seedDataController.seedReports();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reportRepository, times(1)).saveAll(reportListCaptor.capture());
        assertTrue(reportListCaptor.getValue().size() >= 240);
    }

    // =========================================================================
    // DELETE /api/seed/reports/clear - Clear Reports Tests (Unchanged)
    // =========================================================================

    @Test
    void clearReports_ShouldCallDeleteAllAndReturnSuccess() {
        // Act
        ResponseEntity<String> response = seedDataController.clearReports();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reportRepository, times(1)).deleteAll();
    }

    // =========================================================================
    // DELETE /api/seed/users/clear - Clear Users Tests (Unchanged)
    // =========================================================================

    @Test
    void clearUsers_ShouldCallDeleteUserForEachUserAndReturnCount() {
        // Arrange
        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(1);
        User user2 = mock(User.class);
        when(user2.getId()).thenReturn(2);
        List<User> usersToClear = List.of(user1, user2);

        when(userService.getAllUsers()).thenReturn(usersToClear);

        // Act
        ResponseEntity<String> response = seedDataController.clearUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cleared 2 users!", response.getBody());

        verify(userService, times(1)).getAllUsers();
        verify(userService, times(1)).deleteUser(1);
        verify(userService, times(1)).deleteUser(2);
    }
}