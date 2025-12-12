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
        mockAdminUser = mock(User.class);
        mockStaffUser = mock(User.class);
    }

    @Test
    void seedUsers_ShouldCreate13UsersAndCallUserServiceCreate() {
        try (MockedStatic<UserFactory> mockedFactory = mockStatic(UserFactory.class)) {
            UserFactory factoryMockInstance = mock(UserFactory.class);
            mockedFactory.when(UserFactory::getInstance).thenReturn(factoryMockInstance);

            when(factoryMockInstance.createUser(anyString(), anyString(), eq(USER_ROLE.ADMIN)))
                    .thenReturn(mockAdminUser);
            when(factoryMockInstance.createUser(anyString(), anyString(), eq(USER_ROLE.STAFF)))
                    .thenReturn(mockStaffUser);

            int expectedTotalUsers = 13;
            when(userService.createUser(any(User.class))).thenReturn(mockAdminUser);
            ResponseEntity<String> response = seedDataController.seedUsers();
            String expectedMessage = "Successfully created 13 users! (Admins: 3, Staff: 10)";

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedMessage, response.getBody());
            verify(factoryMockInstance, times(3)).createUser(anyString(), anyString(), eq(USER_ROLE.ADMIN));
            verify(factoryMockInstance, times(10)).createUser(anyString(), anyString(), eq(USER_ROLE.STAFF));
            verify(userService, times(expectedTotalUsers)).createUser(any(User.class));
            verify(mockAdminUser, never()).getUsername();
            verify(mockStaffUser, never()).getUsername();

        }
    }

    @Test
    void seedUsers_ShouldHandleUserCreationFailureGracefully() {
        try (MockedStatic<UserFactory> mockedFactory = mockStatic(UserFactory.class)) {
            when(mockStaffUser.getUsername()).thenReturn("mockStaff");

            UserFactory factoryMockInstance = mock(UserFactory.class);
            mockedFactory.when(UserFactory::getInstance).thenReturn(factoryMockInstance);

            when(factoryMockInstance.createUser(anyString(), anyString(), eq(USER_ROLE.ADMIN)))
                    .thenReturn(mockAdminUser);
            when(factoryMockInstance.createUser(anyString(), anyString(), eq(USER_ROLE.STAFF)))
                    .thenReturn(mockStaffUser);

            when(userService.createUser(any(User.class)))
                    .thenReturn(mockAdminUser)
                    .thenReturn(mockAdminUser)
                    .thenReturn(mockAdminUser)
                    .thenThrow(new RuntimeException("User Exists"))
                    .thenReturn(mockStaffUser)
                    .thenReturn(mockStaffUser)
                    .thenReturn(mockStaffUser)
                    .thenReturn(mockStaffUser)
                    .thenReturn(mockStaffUser)
                    .thenReturn(mockStaffUser)
                    .thenReturn(mockStaffUser)
                    .thenReturn(mockStaffUser)
                    .thenReturn(mockStaffUser);
            ResponseEntity<String> response = seedDataController.seedUsers();
            String expectedMessage = "Successfully created 12 users! (Admins: 3, Staff: 9)";

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedMessage, response.getBody());
            verify(userService, times(13)).createUser(any(User.class));
            verify(factoryMockInstance, times(3)).createUser(anyString(), anyString(), eq(USER_ROLE.ADMIN));
            verify(factoryMockInstance, times(10)).createUser(anyString(), anyString(), eq(USER_ROLE.STAFF));
            verify(mockStaffUser, times(1)).getUsername();

        }
    }

    @Test
    void seedAll_ShouldCallBothSeedUsersAndSeedReports() {
        try (MockedStatic<UserFactory> mockedFactory = mockStatic(UserFactory.class)) {
            UserFactory factoryMockInstance = mock(UserFactory.class);
            mockedFactory.when(UserFactory::getInstance).thenReturn(factoryMockInstance);

            when(factoryMockInstance.createUser(anyString(), anyString(), eq(USER_ROLE.ADMIN)))
                    .thenReturn(mockAdminUser);
            when(factoryMockInstance.createUser(anyString(), anyString(), eq(USER_ROLE.STAFF)))
                    .thenReturn(mockStaffUser);

            when(userService.createUser(any(User.class))).thenReturn(mockAdminUser);

            ResponseEntity<String> response = seedDataController.seedAll();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().contains("Successfully created 13 users!"));
            assertTrue(response.getBody().contains("report entries!"));
            verify(userService, times(13)).createUser(any(User.class));
            verify(reportRepository, times(1)).saveAll(anyList());
            verify(factoryMockInstance, times(3)).createUser(anyString(), anyString(), eq(USER_ROLE.ADMIN));
            verify(factoryMockInstance, times(10)).createUser(anyString(), anyString(), eq(USER_ROLE.STAFF));
            verify(mockAdminUser, never()).getUsername();
            verify(mockStaffUser, never()).getUsername();
        }
    }

    @Test
    void seedReports_ShouldCallSaveAllWithNumerousReports() {
        ResponseEntity<String> response = seedDataController.seedReports();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reportRepository, times(1)).saveAll(reportListCaptor.capture());
        assertTrue(reportListCaptor.getValue().size() >= 240);
    }

    @Test
    void clearReports_ShouldCallDeleteAllAndReturnSuccess() {
        ResponseEntity<String> response = seedDataController.clearReports();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reportRepository, times(1)).deleteAll();
    }

    @Test
    void clearUsers_ShouldCallDeleteUserForEachUserAndReturnCount() {
        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(1);
        User user2 = mock(User.class);
        when(user2.getId()).thenReturn(2);
        List<User> usersToClear = List.of(user1, user2);

        when(userService.getAllUsers()).thenReturn(usersToClear);

        ResponseEntity<String> response = seedDataController.clearUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cleared 2 users!", response.getBody());
        verify(userService, times(1)).getAllUsers();
        verify(userService, times(1)).deleteUser(1);
        verify(userService, times(1)).deleteUser(2);
    }
}