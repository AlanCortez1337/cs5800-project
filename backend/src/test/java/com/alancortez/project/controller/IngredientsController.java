package com.alancortez.project.controller;

import com.alancortez.project.model.Ingredient;
import com.alancortez.project.service.IngredientsService;
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

@WebMvcTest(IngredientsController.class)
public class IngredientsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IngredientsService ingredientsService;

    private Ingredient ingredient1;
    private Ingredient ingredient2;

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
    }

    @Test
    void testCreateIngredient_Success() throws Exception {
        Ingredient newIngredient = new IngredientBuilder()
                .setId(1L)
                .setProductName("Columbia Coffee Beans")
                .createIngredient();

        when(ingredientsService.createIngredient(any(Ingredient.class))).thenReturn(ingredient1);

        mockMvc.perform(post("/api/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newIngredient)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productName").value("Columbia Coffee Beans"));

        verify(ingredientsService, times(1)).createIngredient(any(Ingredient.class));
    }

    @Test
    void testGetAllIngredients_Success() throws Exception {
        List<Ingredient> ingredients = Arrays.asList(ingredient1, ingredient2);

        when(ingredientsService.getAllIngredients()).thenReturn(ingredients);

        mockMvc.perform(get("/api/ingredients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].productName").value("Columbia Coffee Beans"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].productName").value("Oat Milk"));

        verify(ingredientsService, times(1)).getAllIngredients();
    }

    @Test
    void testGetAllIngredients_EmptyList() throws Exception {
        when(ingredientsService.getAllIngredients()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/ingredients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(ingredientsService, times(1)).getAllIngredients();
    }

    @Test
    void testGetIngredientById_Success() throws Exception {
        when(ingredientsService.getIngredientById(1L)).thenReturn(Optional.of(ingredient1));

        mockMvc.perform(get("/api/ingredients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productName").value("Columbia Coffee Beans"));

        verify(ingredientsService, times(1)).getIngredientById(1L);
    }

    @Test
    void testGetIngredientsById_NotFound() throws Exception {
        when(ingredientsService.getIngredientById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ingredients/999"))
                .andExpect(status().isNotFound());

        verify(ingredientsService, times(1)).getIngredientById(999L);
    }

    @Test
    void testUpdateIngredients_Success() throws Exception {
        Ingredient newIngredient = new IngredientBuilder()
                                        .setId(1L)
                                        .setProductName("Ethiopia Coffee Beans")
                                        .createIngredient();

        when(ingredientsService.updateIngredient(eq(1L), any(Ingredient.class))).thenReturn(newIngredient);

        mockMvc.perform(put("/api/ingredients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newIngredient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productName").value("Ethiopia Coffee Beans"));

        verify(ingredientsService, times(1)).updateIngredient(eq(1L), any(Ingredient.class));
    }

    @Test
    void testUpdateIngredient_NotFound() throws Exception {
        Ingredient newIngredient = new IngredientBuilder()
                .setId(1L)
                .setProductName("Ethiopia Coffee Beans")
                .createIngredient();

        when(ingredientsService.updateIngredient(eq(999L), any(Ingredient.class)))
                .thenThrow(new IllegalArgumentException("Ingredient not found"));

        mockMvc.perform(put("/api/ingredients/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateIngredient)))
                .andExpect(status().isNotFound());

        verify(ingredientsService, times(1)).updateIngredient(eq(999L), any(Ingredient.class));
    }

    @Test
    void testDeleteIngredient_Success() throws Exception {
        doNothing().when(ingredientsService).deleteIngredient(1L);

        mockMvc.perform(delete("/api/ingredients/1"))
                .andExpect(status().isNoContent());

        verify(ingredientsService, times(1)).deleteIngredient(1L);
    }

    @Test
    void testDeleteIngredient_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Ingredient not found"))
                .when(ingredientsService).deleteIngredient(999L);

        mockMvc.perform(delete("/api/staff/999"))
                .andExpect(status().isNotFound());

        verify(ingredientsService, times(1)).deleteIngredient(999L);
    }
}
