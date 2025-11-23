package com.alancortez.project.service;

import com.alancortez.project.model.Recipe;
import com.alancortez.project.model.RecipeComponent;
import com.alancortez.project.model.RecipeUseHistory;
import com.alancortez.project.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Transactional
    public Recipe updateRecipe(Integer recipeId, Recipe incomingRecipe) {
        Recipe existingRecipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        existingRecipe.setRecipeName(incomingRecipe.getRecipeName());
        existingRecipe.setUseCount(incomingRecipe.getUseCount());

        existingRecipe.getRecipeComponents().clear();

        if (incomingRecipe.getRecipeComponents() != null) {
            for (RecipeComponent component : incomingRecipe.getRecipeComponents()) {

                component.setRecipe(existingRecipe);
                existingRecipe.getRecipeComponents().add(component);
            }
        }

        existingRecipe.getUseHistory().clear();
        if (incomingRecipe.getUseHistory() != null) {
            for (RecipeUseHistory history : incomingRecipe.getUseHistory()) {
                history.setRecipe(existingRecipe);
                existingRecipe.getUseHistory().add(history);
            }
        }

        return recipeRepository.save(existingRecipe);
    }

    public Recipe createRecipe(Recipe recipe) {
            if (recipe.getRecipeComponents() != null) {
                for (RecipeComponent component : recipe.getRecipeComponents()) {
                    component.setRecipe(recipe);
                }
            }

            if (recipe.getUseHistory() != null) {
                for (RecipeUseHistory history : recipe.getUseHistory()) {
                    history.setRecipe(recipe);
                }
            }

            return recipeRepository.save(recipe);
        }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public Recipe getRecipeById(Integer id) {
        return recipeRepository.findById(id).orElse(null);
    }

    public void deleteRecipe(Integer id) {
        recipeRepository.deleteById(id);
    }

    public Recipe getIngredientByRecipeName(String recipeName) {
        return recipeRepository.findByRecipeName(recipeName).orElse(null);
    }
}