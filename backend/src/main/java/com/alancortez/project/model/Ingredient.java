package com.alancortez.project.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ingredient")
public class Ingredient {

    @Id
    @Column(name = "ingredient_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String ingredientID;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Embedded
    private IngredientStorageRequirement quantityDetails;

    @Embedded
    private IngredientUnit unitDetails;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_added", updatable = false)
    private Date dateAdded;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_updated")
    private Date dateUpdated;

    public String getIngredientID() {
        return ingredientID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public IngredientStorageRequirement getQuantityDetails() {
        return quantityDetails;
    }

    public void setQuantityDetails(IngredientStorageRequirement quantityDetails) {
        this.quantityDetails = quantityDetails;
    }

    public IngredientUnit getUnitDetails() {
        return unitDetails;
    }

    public void setUnitDetails(IngredientUnit unitDetails) {
        this.unitDetails = unitDetails;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    @PrePersist
    protected void onCreate() {
        dateAdded = new Date();
        dateUpdated = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        dateUpdated = new Date();
    }
}