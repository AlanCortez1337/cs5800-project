package com.alancortez.project.repository;

import com.alancortez.project.model.Report;
import com.alancortez.project.utils.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // Find all reports by type
    List<Report> findByReportType(ReportType reportType);

    // Find reports by type within a date range
    List<Report> findByReportTypeAndTimestampBetween(
            ReportType reportType,
            LocalDateTime start,
            LocalDateTime end
    );

    // Find all reports within a date range
    @Query("SELECT r FROM Report r WHERE r.timestamp >= :start AND r.timestamp <= :end ORDER BY r.timestamp DESC")
    List<Report> findByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // Get count of each report type within a date range
    @Query("SELECT r.reportType as type, COUNT(r) as count FROM Report r " +
            "WHERE r.timestamp >= :start AND r.timestamp <= :end " +
            "GROUP BY r.reportType")
    List<Object[]> getReportSummary(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // Get top entities (recipes/ingredients) by usage within a date range
    @Query("SELECT r.entityName as name, r.entityId as id, SUM(r.count) as total FROM Report r " +
            "WHERE r.reportType = :type AND r.timestamp >= :start AND r.timestamp <= :end " +
            "GROUP BY r.entityName, r.entityId " +
            "ORDER BY total DESC")
    List<Object[]> getTopEntitiesByType(
            @Param("type") ReportType type,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // Get time series data for a specific report type
    @Query("SELECT DATE(r.timestamp) as date, COUNT(r) as count FROM Report r " +
            "WHERE r.reportType = :type AND r.timestamp >= :start AND r.timestamp <= :end " +
            "GROUP BY DATE(r.timestamp) " +
            "ORDER BY date ASC")
    List<Object[]> getTimeSeriesData(
            @Param("type") ReportType type,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // Count reports by type within date range
    @Query("SELECT COUNT(r) FROM Report r WHERE r.reportType = :type AND r.timestamp >= :start AND r.timestamp <= :end")
    Long countByTypeAndDateRange(
            @Param("type") ReportType type,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // Get all unique entity names for a report type
    @Query("SELECT DISTINCT r.entityName FROM Report r WHERE r.reportType = :type")
    List<String> findDistinctEntityNamesByType(@Param("type") ReportType type);
}
