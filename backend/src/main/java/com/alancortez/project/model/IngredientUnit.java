package com.alancortez.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class IngredientUnit {

    @Column(name = "price_per_unit")
    private Double pricePerUnit;

    @Column(name = "unit_of_measurement")
    private String unitOfMeasurement;

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public String getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public void setUnitOfMeasurement(String unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
    }
}