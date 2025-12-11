package com.alancortez.project.controller;

import com.alancortez.project.controller.ReportController.ReportRequest;
import com.alancortez.project.model.Report;
import com.alancortez.project.service.ReportService;
import com.alancortez.project.utils.ReportType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    private Report testReport;
    private LocalDateTime testStart;
    private LocalDateTime testEnd;

    @BeforeEach
    void setUp() {
        testReport = new Report(ReportType.RECIPE_USED, 101L, "Test Recipe");
        testReport.setId(1L);
        testReport.setTimestamp(LocalDateTime.of(2023, 10, 26, 10, 0));

        testStart = LocalDateTime.of(2023, 1, 1, 0, 0);
        testEnd = LocalDateTime.of(2023, 12, 31, 23, 59);
    }

    // =========================================================================
    // POST /api/reports - Create Report Tests
    // =========================================================================

    @Test
    void createReport_ShouldReturnCreatedReport_OnSuccess() throws Exception {
        // Arrange
        ReportRequest request = new ReportRequest();
        request.setReportType(ReportType.INGREDIENT_USED);
        request.setEntityId(202L);
        request.setEntityName("Test Ingredient");

        when(reportService.createReport(
                eq(ReportType.INGREDIENT_USED),
                eq(202L),
                eq("Test Ingredient")
        )).thenReturn(testReport);

        // Act
        ResponseEntity<Report> response = reportController.createReport(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testReport, response.getBody());
        verify(reportService, times(1)).createReport(
                eq(ReportType.INGREDIENT_USED),
                eq(202L),
                eq("Test Ingredient")
        );
    }

    @Test
    void createReport_ShouldReturnBadRequest_OnServiceException() throws Exception {
        // Arrange
        ReportRequest request = new ReportRequest();
        request.setReportType(ReportType.RECIPES_CREATED);

        // Simulate a failure in the service (e.g., validation error)
        doThrow(new RuntimeException("Invalid entity ID")).when(reportService).createReport(
                eq(ReportType.RECIPES_CREATED),
                any(),
                any()
        );

        // Act
        ResponseEntity<Report> response = reportController.createReport(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(reportService, times(1)).createReport(
                eq(ReportType.RECIPES_CREATED),
                any(),
                any()
        );
    }

    // =========================================================================
    // GET /api/reports - Get All Reports Tests
    // =========================================================================

    @Test
    void getAllReports_ShouldReturnListOfReports() {
        // Arrange
        List<Report> reports = List.of(testReport, new Report());
        when(reportService.getAllReports()).thenReturn(reports);

        // Act
        ResponseEntity<List<Report>> response = reportController.getAllReports();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(reportService, times(1)).getAllReports();
    }

    @Test
    void getAllReports_ShouldReturnEmptyList_WhenNoReportsExist() {
        // Arrange
        when(reportService.getAllReports()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<Report>> response = reportController.getAllReports();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(reportService, times(1)).getAllReports();
    }

    // =========================================================================
    // GET /api/reports/type/{reportType} - Get Reports By Type Tests
    // =========================================================================

    @Test
    void getReportsByType_ShouldReturnReportsMatchingType() {
        // Arrange
        List<Report> matchingReports = List.of(testReport);
        when(reportService.getReportsByType(ReportType.RECIPE_USED)).thenReturn(matchingReports);

        // Act
        ResponseEntity<List<Report>> response = reportController.getReportsByType(ReportType.RECIPE_USED);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(reportService, times(1)).getReportsByType(ReportType.RECIPE_USED);
    }

    // =========================================================================
    // GET /api/reports/range - Get Reports By Date Range Tests
    // =========================================================================

    @Test
    void getReportsByDateRange_ShouldCallServiceWithCorrectDates() {
        // Arrange
        List<Report> reportsInRange = List.of(testReport);
        when(reportService.getReportsByDateRange(testStart, testEnd)).thenReturn(reportsInRange);

        // Act
        ResponseEntity<List<Report>> response = reportController.getReportsByDateRange(testStart, testEnd);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(reportService, times(1)).getReportsByDateRange(testStart, testEnd);
    }

    // =========================================================================
    // GET /api/reports/summary - Get Report Summary Tests
    // =========================================================================

    @Test
    void getReportSummary_ShouldCallServiceWithProvidedDates() {
        // Arrange
        Map<String, Object> mockSummary = Map.of("totalReports", 50, "uniqueTypes", 4);
        when(reportService.getReportSummary(testStart, testEnd)).thenReturn(mockSummary);

        // Act
        ResponseEntity<Map<String, Object>> response = reportController.getReportSummary(testStart, testEnd);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(50, response.getBody().get("totalReports"));
        // Verify service was called with the explicit dates
        verify(reportService, times(1)).getReportSummary(testStart, testEnd);
    }

    @Test
    void getReportSummary_ShouldCallServiceWithDefaultDates_WhenNoParamsAreProvided() {
        // Arrange
        Map<String, Object> mockSummary = Map.of("totalReports", 100);
        when(reportService.getReportSummary(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(mockSummary);

        // Act
        ResponseEntity<Map<String, Object>> response = reportController.getReportSummary(null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify service was called with calculated default dates (now - 30 days to now)
        verify(reportService, times(1)).getReportSummary(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    // =========================================================================
    // GET /api/reports/chart - Get Chart Data Tests
    // =========================================================================

    @Test
    void getChartData_ShouldPassAllParametersCorrectly() {
        // Arrange
        ReportType type = ReportType.INGREDIENTS_CREATED;
        String groupBy = "month";
        List<Map<String, Object>> mockData = List.of(Map.of("date", "2023-01", "count", 15));

        when(reportService.getChartData(eq(type), eq(testStart), eq(testEnd), eq(groupBy)))
                .thenReturn(mockData);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = reportController.getChartData(
                type, groupBy, testStart, testEnd
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        // Verify service was called with all explicit parameters
        verify(reportService, times(1)).getChartData(type, testStart, testEnd, groupBy);
    }

    @Test
    void getChartData_ShouldUseDefaultGroupByAndDateRange() {
        // Arrange
        ReportType type = ReportType.RECIPES_CREATED;
        List<Map<String, Object>> mockData = Collections.emptyList();

        when(reportService.getChartData(eq(type), any(LocalDateTime.class), any(LocalDateTime.class), eq("day")))
                .thenReturn(mockData);

        // Act
        // Omitting groupBy (default "day"), start (default -30 days), and end (default now)
        ResponseEntity<List<Map<String, Object>>> response = reportController.getChartData(
                type, "day", null, null
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Verify service was called with default values
        verify(reportService, times(1)).getChartData(eq(type), any(LocalDateTime.class), any(LocalDateTime.class), eq("day"));
    }

    // =========================================================================
    // GET /api/reports/top - Get Top Entities Tests
    // =========================================================================

    @Test
    void getTopEntities_ShouldPassAllParametersCorrectly() {
        // Arrange
        ReportType type = ReportType.RECIPE_USED;
        int limit = 5;
        List<Map<String, Object>> mockTopData = List.of(Map.of("name", "Pizza", "count", 100));

        when(reportService.getTopEntities(eq(type), eq(testStart), eq(testEnd), eq(limit)))
                .thenReturn(mockTopData);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = reportController.getTopEntities(
                type, limit, testStart, testEnd
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        // Verify service was called with all explicit parameters
        verify(reportService, times(1)).getTopEntities(type, testStart, testEnd, limit);
    }

    @Test
    void getTopEntities_ShouldUseDefaultLimitAndDateRange() {
        // Arrange
        ReportType type = ReportType.INGREDIENT_USED;
        List<Map<String, Object>> mockTopData = Collections.emptyList();

        when(reportService.getTopEntities(eq(type), any(LocalDateTime.class), any(LocalDateTime.class), eq(10)))
                .thenReturn(mockTopData);

        // Act
        // Omitting limit (default 10), start (default -30 days), and end (default now)
        ResponseEntity<List<Map<String, Object>>> response = reportController.getTopEntities(
                type, 10, null, null
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Verify service was called with default limit (10) and default dates
        verify(reportService, times(1)).getTopEntities(eq(type), any(LocalDateTime.class), any(LocalDateTime.class), eq(10));
    }

    // =========================================================================
    // GET /api/reports/dashboard - Get Dashboard Data Tests
    // =========================================================================

    @Test
    void getDashboardData_ShouldCallServiceWithProvidedDates() {
        // Arrange
        Map<String, Object> mockDashboardData = Map.of("recipesCreated", 10, "ingredientsLow", 2);
        when(reportService.getDashboardData(testStart, testEnd)).thenReturn(mockDashboardData);

        // Act
        ResponseEntity<Map<String, Object>> response = reportController.getDashboardData(testStart, testEnd);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10, response.getBody().get("recipesCreated"));
        // Verify service was called with the explicit dates
        verify(reportService, times(1)).getDashboardData(testStart, testEnd);
    }

    @Test
    void getDashboardData_ShouldCallServiceWithDefaultDates_WhenNoParamsAreProvided() {
        // Arrange
        Map<String, Object> mockDashboardData = Map.of("recipesCreated", 5);
        when(reportService.getDashboardData(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(mockDashboardData);

        // Act
        ResponseEntity<Map<String, Object>> response = reportController.getDashboardData(null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify service was called with calculated default dates (now - 30 days to now)
        verify(reportService, times(1)).getDashboardData(any(LocalDateTime.class), any(LocalDateTime.class));
    }
}
