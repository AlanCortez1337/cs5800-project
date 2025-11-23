package com.alancortez.project.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "recipe")
public class Recipe {

    @Id
    @Column(name = "recipe_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer recipeID;

    @Column(name = "recipe_name", nullable = false)
    private String recipeName;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeComponent> recipeComponents;

    @Column(name = "use_count")
    private Integer useCount = 0;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeUseHistory> useHistory;

    public Integer getRecipeID() {
        return recipeID;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public List<RecipeComponent> getRecipeComponents() {
        return recipeComponents;
    }

    public void setRecipeComponents(List<RecipeComponent> recipeComponents) {
        this.recipeComponents = recipeComponents;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public List<RecipeUseHistory> getUseHistory() {
        return useHistory;
    }

    public void setUseHistory(List<RecipeUseHistory> useHistory) {
        this.useHistory = useHistory;
    }
}