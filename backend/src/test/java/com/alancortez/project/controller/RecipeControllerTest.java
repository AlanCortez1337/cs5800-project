package com.alancortez.project.controller;

import com.alancortez.project.model.Ingredient;
import com.alancortez.project.model.Recipe;
import com.alancortez.project.model.RecipeComponent;
import com.alancortez.project.model.RecipeUseHistory;
import com.alancortez.project.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeControllerTest {

    @Mock
    private RecipeService recipeService;

    @InjectMocks
    private RecipeController recipeController;

    private Recipe testRecipe1;
    private Recipe testRecipe2;
    private List<RecipeComponent> components1;
    private List<RecipeComponent> components2;
    private List<RecipeUseHistory> useHistory1;
    private List<RecipeUseHistory> useHistory2;
    private Ingredient ingredient1;
    private Ingredient ingredient2;
    private Ingredient ingredient3;
    private Ingredient ingredient4;

    @BeforeEach
    void setUp() {
        ingredient1 = new Ingredient();
        ingredient1.setProductName("Flour");
        ingredient2 = new Ingredient();
        ingredient2.setProductName("Sugar");
        ingredient3 = new Ingredient();
        ingredient3.setProductName("Eggs");
        ingredient4 = new Ingredient();
        ingredient4.setProductName("Butter");

        testRecipe1 = new Recipe();
        testRecipe1.setRecipeName("Chocolate Chip Cookies");
        testRecipe1.setUseCount(5);

        components1 = new ArrayList<>();

        RecipeComponent comp1 = new RecipeComponent();
        comp1.setRecipe(testRecipe1);
        comp1.setIngredient(ingredient1);
        comp1.setQuantity(200);
        components1.add(comp1);

        RecipeComponent comp2 = new RecipeComponent();
        comp2.setRecipe(testRecipe1);
        comp2.setIngredient(ingredient2);
        comp2.setQuantity(100);
        components1.add(comp2);

        RecipeComponent comp3 = new RecipeComponent();
        comp3.setRecipe(testRecipe1);
        comp3.setIngredient(ingredient3);
        comp3.setQuantity(2);
        components1.add(comp3);

        testRecipe1.setRecipeComponents(components1);

        useHistory1 = new ArrayList<>();

        RecipeUseHistory history1 = new RecipeUseHistory();
        history1.setRecipe(testRecipe1);
        history1.setLastUsed(new Date(1705276800000L));
        useHistory1.add(history1);

        RecipeUseHistory history2 = new RecipeUseHistory();
        history2.setRecipe(testRecipe1);
        history2.setLastUsed(new Date(1708387200000L));
        useHistory1.add(history2);

        testRecipe1.setUseHistory(useHistory1);

        testRecipe2 = new Recipe();
        testRecipe2.setRecipeName("Spaghetti Marinara");
        testRecipe2.setUseCount(3);

        components2 = new ArrayList<>();

        RecipeComponent comp4 = new RecipeComponent();
        comp4.setRecipe(testRecipe2);
        comp4.setIngredient(ingredient4);
        comp4.setQuantity(50);
        components2.add(comp4);

        testRecipe2.setRecipeComponents(components2);

        useHistory2 = new ArrayList<>();

        RecipeUseHistory history3 = new RecipeUseHistory();
        history3.setRecipe(testRecipe2);
        history3.setLastUsed(new Date(1710028800000L));
        useHistory2.add(history3);

        testRecipe2.setUseHistory(useHistory2);
    }

    @Test
    void getAllRecipes_ShouldReturnListOfRecipes() {
        List<Recipe> recipes = Arrays.asList(testRecipe1, testRecipe2);
        when(recipeService.getAllRecipes()).thenReturn(recipes);

        List<Recipe> result = recipeController.getAllRecipes();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Chocolate Chip Cookies", result.get(0).getRecipeName());
        assertEquals("Spaghetti Marinara", result.get(1).getRecipeName());
        assertEquals(5, result.get(0).getUseCount());
        assertEquals(3, result.get(1).getUseCount());
        assertEquals(3, result.get(0).getRecipeComponents().size());
        assertEquals(1, result.get(1).getRecipeComponents().size());
        verify(recipeService, times(1)).getAllRecipes();
    }

    @Test
    void getAllRecipes_ShouldReturnEmptyList_WhenNoRecipes() {
        when(recipeService.getAllRecipes()).thenReturn(Arrays.asList());

        List<Recipe> result = recipeController.getAllRecipes();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(recipeService, times(1)).getAllRecipes();
    }

    @Test
    void getRecipeById_ShouldReturnRecipe_WhenRecipeExists() {
        when(recipeService.getRecipeById(1)).thenReturn(testRecipe1);

        ResponseEntity<Recipe> response = recipeController.getRecipeById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Chocolate Chip Cookies", response.getBody().getRecipeName());
        assertEquals(3, response.getBody().getRecipeComponents().size());
        assertEquals(5, response.getBody().getUseCount());
        verify(recipeService, times(1)).getRecipeById(1);
    }

    @Test
    void getRecipeById_ShouldReturnNotFound_WhenRecipeDoesNotExist() {
        when(recipeService.getRecipeById(999)).thenReturn(null);

        ResponseEntity<Recipe> response = recipeController.getRecipeById(999);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(recipeService, times(1)).getRecipeById(999);
    }

    @Test
    void getRecipeById_ShouldReturnRecipeWithAllComponentsAndHistory() {
        when(recipeService.getRecipeById(1)).thenReturn(testRecipe1);

        ResponseEntity<Recipe> response = recipeController.getRecipeById(1);
        RecipeComponent firstComponent = response.getBody().getRecipeComponents().get(0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getRecipeComponents());
        assertEquals(3, response.getBody().getRecipeComponents().size());
        assertEquals("Flour", firstComponent.getIngredient().getProductName());
        assertEquals(200, firstComponent.getQuantity());
        assertNotNull(response.getBody().getUseHistory());
        assertEquals(2, response.getBody().getUseHistory().size());
    }

    @Test
    void createRecipe_ShouldReturnCreatedRecipe() {
        Recipe newRecipe = new Recipe();
        newRecipe.setRecipeName("Banana Bread");
        newRecipe.setUseCount(0);

        List<RecipeComponent> newComponents = new ArrayList<>();
        RecipeComponent comp = new RecipeComponent();
        comp.setIngredient(ingredient1);
        comp.setQuantity(300);
        newComponents.add(comp);

        newRecipe.setRecipeComponents(newComponents);
        newRecipe.setUseHistory(new ArrayList<>());
        Recipe savedRecipe = new Recipe();
        savedRecipe.setRecipeName("Banana Bread");
        savedRecipe.setRecipeComponents(newComponents);
        savedRecipe.setUseCount(0);
        savedRecipe.setUseHistory(new ArrayList<>());

        when(recipeService.createRecipe(any(Recipe.class))).thenReturn(savedRecipe);
        Recipe result = recipeController.createRecipe(newRecipe);

        assertNotNull(result);
        assertEquals("Banana Bread", result.getRecipeName());
        assertEquals(1, result.getRecipeComponents().size());
        assertEquals(0, result.getUseCount());
        verify(recipeService, times(1)).createRecipe(any(Recipe.class));
    }

    @Test
    void createRecipe_ShouldAcceptRecipeWithEmptyComponents() {
        Recipe newRecipe = new Recipe();
        newRecipe.setRecipeName("Simple Recipe");
        newRecipe.setRecipeComponents(new ArrayList<>());
        newRecipe.setUseCount(0);
        newRecipe.setUseHistory(new ArrayList<>());

        when(recipeService.createRecipe(any(Recipe.class))).thenReturn(newRecipe);
        Recipe result = recipeController.createRecipe(newRecipe);

        assertNotNull(result);
        assertEquals("Simple Recipe", result.getRecipeName());
        assertNotNull(result.getRecipeComponents());
        assertEquals(0, result.getRecipeComponents().size());
        verify(recipeService, times(1)).createRecipe(newRecipe);
    }

    @Test
    void createRecipe_ShouldHandleMultipleComponentsWithQuantities() {
        Recipe newRecipe = new Recipe();
        newRecipe.setRecipeName("Complex Recipe");

        List<RecipeComponent> newComponents = new ArrayList<>();

        RecipeComponent comp1 = new RecipeComponent();
        comp1.setIngredient(ingredient1);
        comp1.setQuantity(500);
        newComponents.add(comp1);

        RecipeComponent comp2 = new RecipeComponent();
        comp2.setIngredient(ingredient2);
        comp2.setQuantity(250);
        newComponents.add(comp2);

        newRecipe.setRecipeComponents(newComponents);
        newRecipe.setUseCount(0);
        newRecipe.setUseHistory(new ArrayList<>());

        when(recipeService.createRecipe(any(Recipe.class))).thenReturn(newRecipe);
        Recipe result = recipeController.createRecipe(newRecipe);

        assertNotNull(result);
        assertEquals(2, result.getRecipeComponents().size());
        assertEquals(500, result.getRecipeComponents().get(0).getQuantity());
        assertEquals(250, result.getRecipeComponents().get(1).getQuantity());
        verify(recipeService, times(1)).createRecipe(any(Recipe.class));
    }

    @Test
    void updateRecipe_ShouldReturnUpdatedRecipe_WhenRecipeExists() {
        Recipe updatedDetails = new Recipe();
        updatedDetails.setRecipeName("Updated Chocolate Cookies");

        List<RecipeComponent> updatedComponents = new ArrayList<>();
        RecipeComponent newComp = new RecipeComponent();
        newComp.setIngredient(ingredient1);
        newComp.setQuantity(250);
        updatedComponents.add(newComp);

        updatedDetails.setRecipeComponents(updatedComponents);
        updatedDetails.setUseCount(10);

        List<RecipeUseHistory> updatedHistory = new ArrayList<>();
        RecipeUseHistory newHistory = new RecipeUseHistory();
        newHistory.setLastUsed(new Date());
        updatedHistory.add(newHistory);
        updatedDetails.setUseHistory(updatedHistory);

        Recipe updatedRecipe = new Recipe();
        updatedRecipe.setRecipeName("Updated Chocolate Cookies");
        updatedRecipe.setRecipeComponents(updatedComponents);
        updatedRecipe.setUseCount(10);
        updatedRecipe.setUseHistory(updatedHistory);

        when(recipeService.getRecipeById(1)).thenReturn(testRecipe1);
        when(recipeService.createRecipe(any(Recipe.class))).thenReturn(updatedRecipe);
        ResponseEntity<Recipe> response = recipeController.updateRecipe(1, updatedDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Chocolate Cookies", response.getBody().getRecipeName());
        assertEquals(1, response.getBody().getRecipeComponents().size());
        assertEquals(10, response.getBody().getUseCount());
        assertEquals(1, response.getBody().getUseHistory().size());
        verify(recipeService, times(1)).getRecipeById(1);
        verify(recipeService, times(1)).createRecipe(any(Recipe.class));
    }

    @Test
    void updateRecipe_ShouldReturnNotFound_WhenRecipeDoesNotExist() {
        Recipe updatedDetails = new Recipe();
        updatedDetails.setRecipeName("Updated Recipe");
        updatedDetails.setRecipeComponents(new ArrayList<>());
        updatedDetails.setUseCount(5);
        updatedDetails.setUseHistory(new ArrayList<>());

        when(recipeService.getRecipeById(999)).thenReturn(null);

        ResponseEntity<Recipe> response = recipeController.updateRecipe(999, updatedDetails);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(recipeService, times(1)).getRecipeById(999);
        verify(recipeService, never()).createRecipe(any(Recipe.class));
    }

    @Test
    void updateRecipe_ShouldUpdateAllFields() {
        Recipe existingRecipe = new Recipe();
        existingRecipe.setRecipeName("Original Name");
        existingRecipe.setRecipeComponents(new ArrayList<>());
        existingRecipe.setUseCount(1);
        existingRecipe.setUseHistory(new ArrayList<>());

        List<RecipeComponent> newComponents = new ArrayList<>();
        RecipeComponent comp1 = new RecipeComponent();
        comp1.setIngredient(ingredient1);
        comp1.setQuantity(100);
        newComponents.add(comp1);

        RecipeComponent comp2 = new RecipeComponent();
        comp2.setIngredient(ingredient2);
        comp2.setQuantity(200);
        newComponents.add(comp2);

        List<RecipeUseHistory> newHistory = new ArrayList<>();
        RecipeUseHistory history1 = new RecipeUseHistory();
        history1.setLastUsed(new Date());
        newHistory.add(history1);

        Recipe updatedDetails = new Recipe();
        updatedDetails.setRecipeName("New Name");
        updatedDetails.setRecipeComponents(newComponents);
        updatedDetails.setUseCount(20);
        updatedDetails.setUseHistory(newHistory);

        when(recipeService.getRecipeById(1)).thenReturn(existingRecipe);
        when(recipeService.createRecipe(any(Recipe.class))).thenReturn(existingRecipe);
        ResponseEntity<Recipe> response = recipeController.updateRecipe(1, updatedDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("New Name", existingRecipe.getRecipeName());
        assertEquals(2, existingRecipe.getRecipeComponents().size());
        assertEquals(20, existingRecipe.getUseCount());
        assertEquals(1, existingRecipe.getUseHistory().size());
        verify(recipeService, times(1)).getRecipeById(1);
        verify(recipeService, times(1)).createRecipe(existingRecipe);
    }

    @Test
    void deleteRecipe_ShouldReturnNoContent() {
        doNothing().when(recipeService).deleteRecipe(1);

        ResponseEntity<Void> response = recipeController.deleteRecipe(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(recipeService, times(1)).deleteRecipe(1);
    }

    @Test
    void deleteRecipe_ShouldReturnNoContent_EvenWhenRecipeDoesNotExist() {
        doNothing().when(recipeService).deleteRecipe(999);

        ResponseEntity<Void> response = recipeController.deleteRecipe(999);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(recipeService, times(1)).deleteRecipe(999);
    }

    @Test
    void deleteRecipe_ShouldHandleServiceException() {
        doThrow(new RuntimeException("Database error")).when(recipeService).deleteRecipe(1);

        assertThrows(RuntimeException.class, () -> {
            recipeController.deleteRecipe(1);
        });

        verify(recipeService, times(1)).deleteRecipe(1);
    }
}