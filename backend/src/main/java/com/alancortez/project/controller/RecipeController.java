package com.alancortez.project.controller;

import com.alancortez.project.model.Recipe;
import com.alancortez.project.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    // GET /api/recipes
    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    // GET /api/recipes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Integer id) {
        Recipe recipe = recipeService.getRecipeById(id);
        if (recipe != null) {
            return ResponseEntity.ok(recipe);
        }
        return ResponseEntity.notFound().build();
    }

    // POST /api/recipes
    @PostMapping
    public Recipe createRecipe(@RequestBody Recipe recipe) {
        return recipeService.createRecipe(recipe);
    }

    // PUT /api/recipes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable Integer id, @RequestBody Recipe recipeDetails) {
        Recipe recipe = recipeService.getRecipeById(id);
        if (recipe != null) {
            recipe.setRecipeName(recipeDetails.getRecipeName());
            recipe.setRecipeComponents(recipeDetails.getRecipeComponents());
            recipe.setUseCount(recipeDetails.getUseCount());
            recipe.setUseHistory(recipeDetails.getUseHistory());
            return ResponseEntity.ok(recipeService.createRecipe(recipe));
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE /api/recipes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Integer id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }
}