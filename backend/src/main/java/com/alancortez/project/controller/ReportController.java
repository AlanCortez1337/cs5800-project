package com.alancortez.project.controller;

import com.alancortez.project.model.Report;
import com.alancortez.project.utils.REPORT_TYPE;
import com.alancortez.project.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping
    public ResponseEntity<Report> createReport(@RequestBody ReportRequest request) {
        try {
            Report report = reportService.createReport(
                    request.getReportType(),
                    request.getEntityId(),
                    request.getEntityName()
            );
            return new ResponseEntity<>(report, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Report>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/type/{reportType}")
    public ResponseEntity<List<Report>> getReportsByType(@PathVariable REPORT_TYPE reportType) {
        return ResponseEntity.ok(reportService.getReportsByType(reportType));
    }

    @GetMapping("/range")
    public ResponseEntity<List<Report>> getReportsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return ResponseEntity.ok(reportService.getReportsByDateRange(start, end));
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getReportSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        LocalDateTime startDate = start != null ? start : LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = end != null ? end : LocalDateTime.now();

        return ResponseEntity.ok(reportService.getReportSummary(startDate, endDate));
    }

    @GetMapping("/chart")
    public ResponseEntity<List<Map<String, Object>>> getChartData(
            @RequestParam REPORT_TYPE reportType,
            @RequestParam(defaultValue = "day") String groupBy,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        LocalDateTime startDate = start != null ? start : LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = end != null ? end : LocalDateTime.now();

        return ResponseEntity.ok(
                reportService.getChartData(reportType, startDate, endDate, groupBy)
        );
    }

    @GetMapping("/top")
    public ResponseEntity<List<Map<String, Object>>> getTopEntities(
            @RequestParam REPORT_TYPE reportType,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        LocalDateTime startDate = start != null ? start : LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = end != null ? end : LocalDateTime.now();

        return ResponseEntity.ok(
                reportService.getTopEntities(reportType, startDate, endDate, limit)
        );
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        LocalDateTime startDate = start != null ? start : LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = end != null ? end : LocalDateTime.now();

        return ResponseEntity.ok(reportService.getDashboardData(startDate, endDate));
    }

    public static class ReportRequest {
        private REPORT_TYPE reportType;
        private Long entityId;
        private String entityName;

        public REPORT_TYPE getReportType() {
            return reportType;
        }

        public void setReportType(REPORT_TYPE reportType) {
            this.reportType = reportType;
        }

        public Long getEntityId() {
            return entityId;
        }

        public void setEntityId(Long entityId) {
            this.entityId = entityId;
        }

        public String getEntityName() {
            return entityName;
        }

        public void setEntityName(String entityName) {
            this.entityName = entityName;
        }
    }
}