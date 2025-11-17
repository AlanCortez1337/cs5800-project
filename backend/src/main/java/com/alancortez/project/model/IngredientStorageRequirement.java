package com.alancortez.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class IngredientStorageRequirement {

    @Column(name = "current_quantity")
    private Double currentQuantity;

    @Column(name = "max_quantity_limit")
    private Double maxQuantityLimit;

    @Column(name = "alert_low_quantity")
    private Double alertLowQuantity;

    @Column(name = "times_reached_low")
    private Integer timesReachedLow;

    public Double getCurrentQuantity() {
        return currentQuantity;
    }

    public Double getMaxQuantityLimit() {
        return maxQuantityLimit;
    }

    public Double getAlertLowQuantity() {
        return alertLowQuantity;
    }

    public Integer getTimesReachedLow() {
        return timesReachedLow;
    }

    public void setCurrentQuantity(Double currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public void setMaxQuantityLimit(Double maxQuantityLimit) {
        this.maxQuantityLimit = maxQuantityLimit;
    }

    public void setAlertLowQuantity(Double alertLowQuantity) {
        this.alertLowQuantity = alertLowQuantity;
    }

    public void setTimesReachedLow(Integer timesReachedLow) {
        this.timesReachedLow = timesReachedLow;
    }
}