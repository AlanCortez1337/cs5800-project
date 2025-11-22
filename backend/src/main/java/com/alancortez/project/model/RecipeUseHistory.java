package com.alancortez.project.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "recipe_use_history")
public class RecipeUseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_used")
    private Date lastUsed;

    public Integer getId() {
        return id;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Date date) {
        lastUsed = date;
    }

    @PrePersist
    protected void onCreate() {
        lastUsed = new Date();
    }
}