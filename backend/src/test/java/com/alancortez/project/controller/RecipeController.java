package com.alancortez.project.controller;

import com.alancortez.project.model.Recipe;
import com.alancortez.project.service.RecipeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(RecipeController.class)
public class RecipeController {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecipeService recipeService;

    private Recipe recipe1;
    private Ingredient ingredient1;
    private Ingredient ingredient2;
    private RecipeComponent recipe1component1;
    private RecipeComponent recipe1component2;

    @BeforeEach
    void setUp() {
        ingredient1 = new IngredientBuilder()
                .setId(1L)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setProductName("Columbia Coffee Beans")
                .setQuantityDetails(20, 100, 5, 0)
                .setUnitDetails(1.50, "grams")
                .createIngredient();

        ingredient2 = new IngredientBuilder()
                .setId(2L)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setProductName("Oat Milk")
                .setQuantityDetails(10, 70, 8, 0)
                .setUnitDetails(6.50, "gallons")
                .createIngredient();

        recipe1component1 = new RecipeComponent(ingredient1, 3);
        recipe1component2 = new RecipeComponent(ingredient2, 1);

        recipe1 = new RecipeBuilder().setRecipeName("Latte").setComponents(Arrays.asList(recipe1component1, recipe1component2));
    }

    @Test
    void testCreateRecipe_Success() throws Exception {
        Recipe testRecipe = new RecipeBuilder().setRecipeName("Latte").setComponents(Arrays.asList(recipe1component1, recipe1component2));

        when(recipeService.createRecipe(any(Recipe.class))).thenReturn(recipe1);

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRecipe)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.recipeName").value("Latte"));

        verify(recipeService, times(1)).createRecipe(any(Recipe.class));
    }

    @Test
    void testGetAllRecipe_Success() throws Exception {
        List<Recipe> testRecipes = Arrays.asList(recipe1);

        when(recipeService.getAllRecipes()).thenReturn(testRecipes);

        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].recipeName").value("Latte"));

        verify(recipeService, times(1)).getAllRecipes();
    }

    @Test
    void testGetAllRecipe_EmptyList() throws Exception {
        when(recipeService.getAllRecipes()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(recipeService, times(1)).getAllRecipes();
    }

    @Test
    void testGetRecipeById_Success() throws Exception {
        when(recipeService.getRecipeById(1L)).thenReturn(Optional.of(recipe1));

        mockMvc.perform(get("/api/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$[0].recipeName").value("Latte"));

        verify(recipeService, times(1)).getRecipeById(1L);
    }

    @Test
    void testGetRecipeById_NotFound() throws Exception {
        when(recipeService.getRecipeById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/recipes/999"))
                .andExpect(status().isNotFound());

        verify(recipeService, times(1)).getRecipeById(999L);
    }

    @Test
    void testUpdateRecipe_Success() throws Exception {
        Recipe testRecipe = new RecipeBuilder().setRecipeName("Flat White").setComponents(Arrays.asList(recipe1component1, recipe1component2));

        when(recipeService.updateRecipe(eq(1L), any(Recipe.class))).thenReturn(testRecipe);

        mockMvc.perform(put("/api/recipes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRecipe)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.recipeName").value("Flat White"));

        verify(recipeService, times(1)).updateRecipe(eq(1L), any(Recipe.class));
    }

    @Test
    void testUpdateStaff_NotFound() throws Exception {
        Recipe testRecipe = new RecipeBuilder().setRecipeName("Flat White").setComponents(Arrays.asList(recipe1component1, recipe1component2));

        when(recipeService.updateRecipe(eq(999L), any(Recipe.class)))
                .thenThrow(new IllegalArgumentException("Recipe not found"));

        mockMvc.perform(put("/api/recipes/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRecipe)))
                .andExpect(status().isNotFound());

        verify(recipeService, times(1)).updateRecipe(eq(999L), any(Recipe.class));
    }

    @Test
    void testDeleteRecipe_Success() throws Exception {
        doNothing().when(recipeService).deleteStaff(1L);

        mockMvc.perform(delete("/api/recipes/1"))
                .andExpect(status().isNoContent());

        verify(recipeService, times(1)).deleteRecipes(1L);
    }

    @Test
    void testDeleteRecipe_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Recipe not found"))
                .when(recipeService).deleteRecipes(999L);

        mockMvc.perform(delete("/api/recipes/999"))
                .andExpect(status().isNotFound());

        verify(recipeService, times(1)).deleteRecipes(999L);
    }
}