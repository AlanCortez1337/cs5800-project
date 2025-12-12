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

    @Test
    void createReport_ShouldReturnCreatedReport_OnSuccess() throws Exception {
        ReportRequest request = new ReportRequest();
        request.setReportType(ReportType.INGREDIENT_USED);
        request.setEntityId(202L);
        request.setEntityName("Test Ingredient");

        when(reportService.createReport(
                eq(ReportType.INGREDIENT_USED),
                eq(202L),
                eq("Test Ingredient")
        )).thenReturn(testReport);

        ResponseEntity<Report> response = reportController.createReport(request);

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
        ReportRequest request = new ReportRequest();
        request.setReportType(ReportType.RECIPES_CREATED);

        doThrow(new RuntimeException("Invalid entity ID")).when(reportService).createReport(
                eq(ReportType.RECIPES_CREATED),
                any(),
                any()
        );
        ResponseEntity<Report> response = reportController.createReport(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(reportService, times(1)).createReport(
                eq(ReportType.RECIPES_CREATED),
                any(),
                any()
        );
    }

    @Test
    void getAllReports_ShouldReturnListOfReports() {
        List<Report> reports = List.of(testReport, new Report());

        when(reportService.getAllReports()).thenReturn(reports);
        ResponseEntity<List<Report>> response = reportController.getAllReports();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(reportService, times(1)).getAllReports();
    }

    @Test
    void getAllReports_ShouldReturnEmptyList_WhenNoReportsExist() {
        when(reportService.getAllReports()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Report>> response = reportController.getAllReports();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(reportService, times(1)).getAllReports();
    }

    @Test
    void getReportsByType_ShouldReturnReportsMatchingType() {
        List<Report> matchingReports = List.of(testReport);
        when(reportService.getReportsByType(ReportType.RECIPE_USED)).thenReturn(matchingReports);

        ResponseEntity<List<Report>> response = reportController.getReportsByType(ReportType.RECIPE_USED);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(reportService, times(1)).getReportsByType(ReportType.RECIPE_USED);
    }

    @Test
    void getReportsByDateRange_ShouldCallServiceWithCorrectDates() {
        List<Report> reportsInRange = List.of(testReport);
        when(reportService.getReportsByDateRange(testStart, testEnd)).thenReturn(reportsInRange);

        ResponseEntity<List<Report>> response = reportController.getReportsByDateRange(testStart, testEnd);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(reportService, times(1)).getReportsByDateRange(testStart, testEnd);
    }

    @Test
    void getReportSummary_ShouldCallServiceWithProvidedDates() {
        Map<String, Object> mockSummary = Map.of("totalReports", 50, "uniqueTypes", 4);
        when(reportService.getReportSummary(testStart, testEnd)).thenReturn(mockSummary);

        ResponseEntity<Map<String, Object>> response = reportController.getReportSummary(testStart, testEnd);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(50, response.getBody().get("totalReports"));
        verify(reportService, times(1)).getReportSummary(testStart, testEnd);
    }

    @Test
    void getReportSummary_ShouldCallServiceWithDefaultDates_WhenNoParamsAreProvided() {
        Map<String, Object> mockSummary = Map.of("totalReports", 100);
        when(reportService.getReportSummary(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(mockSummary);

        ResponseEntity<Map<String, Object>> response = reportController.getReportSummary(null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reportService, times(1)).getReportSummary(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void getChartData_ShouldPassAllParametersCorrectly() {
        ReportType type = ReportType.INGREDIENTS_CREATED;
        String groupBy = "month";
        List<Map<String, Object>> mockData = List.of(Map.of("date", "2023-01", "count", 15));

        when(reportService.getChartData(eq(type), eq(testStart), eq(testEnd), eq(groupBy)))
                .thenReturn(mockData);

        ResponseEntity<List<Map<String, Object>>> response = reportController.getChartData(
                type, groupBy, testStart, testEnd
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(reportService, times(1)).getChartData(type, testStart, testEnd, groupBy);
    }

    @Test
    void getChartData_ShouldUseDefaultGroupByAndDateRange() {
        ReportType type = ReportType.RECIPES_CREATED;
        List<Map<String, Object>> mockData = Collections.emptyList();

        when(reportService.getChartData(eq(type), any(LocalDateTime.class), any(LocalDateTime.class), eq("day")))
                .thenReturn(mockData);

        ResponseEntity<List<Map<String, Object>>> response = reportController.getChartData(
                type, "day", null, null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reportService, times(1)).getChartData(eq(type), any(LocalDateTime.class), any(LocalDateTime.class), eq("day"));
    }

    @Test
    void getTopEntities_ShouldPassAllParametersCorrectly() {
        ReportType type = ReportType.RECIPE_USED;
        int limit = 5;
        List<Map<String, Object>> mockTopData = List.of(Map.of("name", "Pizza", "count", 100));

        when(reportService.getTopEntities(eq(type), eq(testStart), eq(testEnd), eq(limit)))
                .thenReturn(mockTopData);

        ResponseEntity<List<Map<String, Object>>> response = reportController.getTopEntities(
                type, limit, testStart, testEnd
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(reportService, times(1)).getTopEntities(type, testStart, testEnd, limit);
    }

    @Test
    void getTopEntities_ShouldUseDefaultLimitAndDateRange() {
        ReportType type = ReportType.INGREDIENT_USED;
        List<Map<String, Object>> mockTopData = Collections.emptyList();

        when(reportService.getTopEntities(eq(type), any(LocalDateTime.class), any(LocalDateTime.class), eq(10)))
                .thenReturn(mockTopData);

        ResponseEntity<List<Map<String, Object>>> response = reportController.getTopEntities(
                type, 10, null, null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reportService, times(1)).getTopEntities(eq(type), any(LocalDateTime.class), any(LocalDateTime.class), eq(10));
    }

    @Test
    void getDashboardData_ShouldCallServiceWithProvidedDates() {
        Map<String, Object> mockDashboardData = Map.of("recipesCreated", 10, "ingredientsLow", 2);
        when(reportService.getDashboardData(testStart, testEnd)).thenReturn(mockDashboardData);

        ResponseEntity<Map<String, Object>> response = reportController.getDashboardData(testStart, testEnd);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10, response.getBody().get("recipesCreated"));
        verify(reportService, times(1)).getDashboardData(testStart, testEnd);
    }

    @Test
    void getDashboardData_ShouldCallServiceWithDefaultDates_WhenNoParamsAreProvided() {
        Map<String, Object> mockDashboardData = Map.of("recipesCreated", 5);
        when(reportService.getDashboardData(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(mockDashboardData);

        ResponseEntity<Map<String, Object>> response = reportController.getDashboardData(null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reportService, times(1)).getDashboardData(any(LocalDateTime.class), any(LocalDateTime.class));
    }
}
