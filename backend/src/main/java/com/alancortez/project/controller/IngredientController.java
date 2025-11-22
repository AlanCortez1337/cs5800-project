package com.alancortez.project.controller;

import com.alancortez.project.model.Ingredient;
import com.alancortez.project.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    // GET /api/ingredients
    @GetMapping
    public List<Ingredient> getAllIngredients() {
        return ingredientService.getAllIngredients();
    }

    // GET /api/ingredients/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable Long id) {
        Ingredient ingredient = ingredientService.getIngredientById(id);
        if (ingredient != null) {
            return ResponseEntity.ok(ingredient);
        }
        return ResponseEntity.notFound().build();
    }

    // POST /api/ingredients
    @PostMapping
    public Ingredient createIngredient(@RequestBody Ingredient ingredient) {
        return ingredientService.createIngredient(ingredient);
    }

    // PUT /api/ingredients/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Ingredient> updateIngredient(@PathVariable Long id, @RequestBody Ingredient ingredientDetails) {
        Ingredient ingredient = ingredientService.getIngredientById(id);
        if (ingredient != null) {
            ingredient.setProductName(ingredientDetails.getProductName());
            ingredient.setUnitDetails(ingredientDetails.getUnitDetails());
            ingredient.setQuantityDetails(ingredientDetails.getQuantityDetails());
            return ResponseEntity.ok(ingredientService.createIngredient(ingredient));
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE /api/ingredients/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }
}