package com.alancortez.project.controller;

import com.alancortez.project.model.Ingredient;
import com.alancortez.project.model.IngredientUnit;
import com.alancortez.project.service.IngredientService;
import com.alancortez.project.utils.IngredientUnitFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @GetMapping
    public List<Ingredient> getAllIngredients() {
        return ingredientService.getAllIngredients();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable Integer id) {
        Ingredient ingredient = ingredientService.getIngredientById(id);
        if (ingredient != null) {
            return ResponseEntity.ok(ingredient);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public Ingredient createIngredient(@RequestBody Ingredient ingredient) {
        return ingredientService.createIngredient(ingredient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ingredient> updateIngredient(@PathVariable Integer id, @RequestBody Ingredient ingredientDetails) {
        Ingredient ingredient = ingredientService.getIngredientById(id);
        if (ingredient != null) {
            ingredient.setProductName(ingredientDetails.getProductName());

            Double pricePerUnit = ingredientDetails.getUnitDetails().getPricePerUnit();
            String unitOfMeasurement = ingredientDetails.getUnitDetails().getUnitOfMeasurement();
            IngredientUnit ingredientUnit = IngredientUnitFactory.getInstance().getIngredientUnit(pricePerUnit, unitOfMeasurement);

            ingredient.setUnitDetails(ingredientUnit);
            ingredient.setQuantityDetails(ingredientDetails.getQuantityDetails());
            return ResponseEntity.ok(ingredientService.createIngredient(ingredient));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Integer id) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }
}