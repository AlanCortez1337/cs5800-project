package com.alancortez.project.service;

import com.alancortez.project.model.Ingredient;
import com.alancortez.project.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    public Ingredient createIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public Ingredient getIngredientById(Long id) {
        return ingredientRepository.findById(id).orElse(null);
    }

    public void deleteIngredient(Long id) {
        ingredientRepository.deleteById(id);
    }

    public Ingredient getIngredientByProductName(String productName) {
        return ingredientRepository.findByProductName(productName).orElse(null);
    }
}