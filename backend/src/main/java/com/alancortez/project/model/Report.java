package com.alancortez.project.model;

import com.alancortez.project.utils.REPORT_TYPE;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private REPORT_TYPE reportType;

    @Column(nullable = false)
    private Long entityId;

    @Column(nullable = false)
    private String entityName;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column
    private Integer count;

    public Report() {
        this.timestamp = LocalDateTime.now();
        this.count = 1;
    }

    public Report(REPORT_TYPE reportType, Long entityId, String entityName) {
        this.reportType = reportType;
        this.entityId = entityId;
        this.entityName = entityName;
        this.timestamp = LocalDateTime.now();
        this.count = 1;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}