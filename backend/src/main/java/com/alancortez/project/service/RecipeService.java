package com.alancortez.project.service;

import com.alancortez.project.model.Recipe;
import com.alancortez.project.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    public Recipe createRecipe(Recipe ingredient) {
        return recipeRepository.save(ingredient);
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id).orElse(null);
    }

    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }

    public Recipe getIngredientByRecipeName(String recipeName) {
        return recipeRepository.findByRecipeName(recipeName).orElse(null);
    }
}