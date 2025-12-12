package com.alancortez.project.service;
import com.alancortez.project.model.Report;
import com.alancortez.project.repository.ReportRepository;
import com.alancortez.project.utils.ReportType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Transactional
    public Report createReport(ReportType reportType, Long entityId, String entityName) {
        Report report = new Report(reportType, entityId, entityName);
        return reportRepository.save(report);
    }

    @Transactional
    public List<Report> createReports(List<Report> reports) {
        return reportRepository.saveAll(reports);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public List<Report> getReportsByType(ReportType reportType) {
        return reportRepository.findByReportType(reportType);
    }

    public List<Report> getReportsByDateRange(LocalDateTime start, LocalDateTime end) {
        return reportRepository.findByDateRange(start, end);
    }

    public List<Report> getReportsByTypeAndDateRange(
            ReportType reportType,
            LocalDateTime start,
            LocalDateTime end
    ) {
        return reportRepository.findByReportTypeAndTimestampBetween(reportType, start, end);
    }

    public Map<String, Object> getReportSummary(LocalDateTime start, LocalDateTime end) {
        List<Object[]> results = reportRepository.getReportSummary(start, end);

        Map<String, Object> summary = new HashMap<>();

        for (ReportType type : ReportType.values()) {
            summary.put(type.name(), 0L);
        }

        for (Object[] result : results) {
            ReportType type = (ReportType) result[0];
            Long count = (Long) result[1];
            summary.put(type.name(), count);
        }

        return summary;
    }

    public List<Map<String, Object>> getChartData(
            ReportType reportType,
            LocalDateTime start,
            LocalDateTime end,
            String groupBy
    ) {
        List<Report> reports = getReportsByTypeAndDateRange(reportType, start, end);

        Map<String, Long> groupedData = reports.stream()
                .collect(Collectors.groupingBy(
                        report -> formatDateByGrouping(report.getTimestamp(), groupBy),
                        Collectors.counting()
                ));

        List<Map<String, Object>> chartData = groupedData.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> dataPoint = new HashMap<>();
                    dataPoint.put("date", entry.getKey());
                    dataPoint.put("count", entry.getValue());
                    return dataPoint;
                })
                .sorted(Comparator.comparing(m -> (String) m.get("date")))
                .collect(Collectors.toList());

        return fillMissingDates(chartData, start, end, groupBy);
    }

    public List<Map<String, Object>> getTopEntities(
            ReportType reportType,
            LocalDateTime start,
            LocalDateTime end,
            int limit
    ) {
        List<Object[]> results = reportRepository.getTopEntitiesByType(reportType, start, end);

        return results.stream()
                .limit(limit)
                .map(result -> {
                    Map<String, Object> entity = new HashMap<>();
                    entity.put("name", result[0]);
                    entity.put("id", result[1]);
                    entity.put("count", result[2]);
                    return entity;
                })
                .collect(Collectors.toList());
    }

    public Map<String, Object> getDashboardData(LocalDateTime start, LocalDateTime end) {
        Map<String, Object> dashboard = new HashMap<>();

        dashboard.put("summary", getReportSummary(start, end));

        dashboard.put("topRecipes", getTopEntities(ReportType.RECIPE_USED, start, end, 5));

        dashboard.put("topIngredients", getTopEntities(ReportType.INGREDIENT_USED, start, end, 5));

        Long lowStockCount = reportRepository.countByTypeAndDateRange(
                ReportType.TIMES_INGREDIENT_REACHED_LOW, start, end
        );
        dashboard.put("lowStockCount", lowStockCount);

        Long recipesCreatedCount = reportRepository.countByTypeAndDateRange(
                ReportType.RECIPES_CREATED, start, end
        );
        dashboard.put("recipesCreatedCount", recipesCreatedCount);

        Long ingredientsCreatedCount = reportRepository.countByTypeAndDateRange(
                ReportType.INGREDIENTS_CREATED, start, end
        );
        dashboard.put("ingredientsCreatedCount", ingredientsCreatedCount);

        return dashboard;
    }

    public Map<String, Object> getComparisonData(
            ReportType reportType,
            LocalDateTime currentStart,
            LocalDateTime currentEnd,
            LocalDateTime previousStart,
            LocalDateTime previousEnd
    ) {
        Long currentCount = reportRepository.countByTypeAndDateRange(
                reportType, currentStart, currentEnd
        );
        Long previousCount = reportRepository.countByTypeAndDateRange(
                reportType, previousStart, previousEnd
        );

        Map<String, Object> comparison = new HashMap<>();
        comparison.put("current", currentCount);
        comparison.put("previous", previousCount);

        if (previousCount > 0) {
            double percentChange = ((currentCount - previousCount) / (double) previousCount) * 100;
            comparison.put("percentChange", Math.round(percentChange * 100.0) / 100.0);
        } else {
            comparison.put("percentChange", currentCount > 0 ? 100.0 : 0.0);
        }

        return comparison;
    }

    private String formatDateByGrouping(LocalDateTime dateTime, String groupBy) {
        switch (groupBy.toLowerCase()) {
            case "day":
                return dateTime.toLocalDate().toString(); // YYYY-MM-DD
            case "week":
                LocalDate date = dateTime.toLocalDate();
                int weekOfYear = date.getDayOfYear() / 7 + 1;
                return date.getYear() + "-W" + String.format("%02d", weekOfYear);
            case "month":
                return dateTime.getYear() + "-" + String.format("%02d", dateTime.getMonthValue());
            default:
                return dateTime.toLocalDate().toString();
        }
    }

    private List<Map<String, Object>> fillMissingDates(
            List<Map<String, Object>> chartData,
            LocalDateTime start,
            LocalDateTime end,
            String groupBy
    ) {
        Map<String, Long> existingData = chartData.stream()
                .collect(Collectors.toMap(
                        m -> (String) m.get("date"),
                        m -> ((Number) m.get("count")).longValue()
                ));

        List<String> allDates = generateDateLabels(start, end, groupBy);

        return allDates.stream()
                .map(date -> {
                    Map<String, Object> dataPoint = new HashMap<>();
                    dataPoint.put("date", date);
                    dataPoint.put("count", existingData.getOrDefault(date, 0L));
                    return dataPoint;
                })
                .collect(Collectors.toList());
    }

    private List<String> generateDateLabels(LocalDateTime start, LocalDateTime end, String groupBy) {
        List<String> labels = new ArrayList<>();
        LocalDateTime current = start;

        switch (groupBy.toLowerCase()) {
            case "day":
                while (!current.isAfter(end)) {
                    labels.add(current.toLocalDate().toString());
                    current = current.plusDays(1);
                }
                break;
            case "week":
                while (!current.isAfter(end)) {
                    labels.add(formatDateByGrouping(current, "week"));
                    current = current.plusWeeks(1);
                }
                break;
            case "month":
                while (!current.isAfter(end)) {
                    labels.add(formatDateByGrouping(current, "month"));
                    current = current.plusMonths(1);
                }
                break;
            default:
                while (!current.isAfter(end)) {
                    labels.add(current.toLocalDate().toString());
                    current = current.plusDays(1);
                }
        }

        return labels;
    }

    @Transactional
    public void deleteReportsOlderThan(LocalDateTime cutoffDate) {
        List<Report> oldReports = reportRepository.findByDateRange(
                LocalDateTime.of(2000, 1, 1, 0, 0),
                cutoffDate
        );
        reportRepository.deleteAll(oldReports);
    }
}
