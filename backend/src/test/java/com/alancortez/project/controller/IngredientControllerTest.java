package com.alancortez.project.controller;
import com.alancortez.project.model.Ingredient;
import com.alancortez.project.model.IngredientUnit;
import com.alancortez.project.model.IngredientStorageRequirement;
import com.alancortez.project.service.IngredientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientControllerTest {

    @Mock
    private IngredientService ingredientService;

    @InjectMocks
    private IngredientController ingredientController;

    private Ingredient testIngredient1;
    private Ingredient testIngredient2;
    private IngredientUnit unit1;
    private IngredientUnit unit2;
    private IngredientStorageRequirement storage1;
    private IngredientStorageRequirement storage2;

    @BeforeEach
    void setUp() {
        // Setup unit details for ingredient 1
        unit1 = new IngredientUnit();
        unit1.setUnitOfMeasurement("kg");
        unit1.setPricePerUnit(2.50);

        // Setup storage requirements for ingredient 1
        storage1 = new IngredientStorageRequirement();
        storage1.setCurrentQuantity(5.0);
        storage1.setMaxQuantityLimit(20.0);
        storage1.setAlertLowQuantity(3.0);
        storage1.setTimesReachedLow(2);

        testIngredient1 = new Ingredient();
        testIngredient1.setProductName("All-Purpose Flour");
        testIngredient1.setUnitDetails(unit1);
        testIngredient1.setQuantityDetails(storage1);

        // Setup unit details for ingredient 2
        unit2 = new IngredientUnit();
        unit2.setUnitOfMeasurement("kg");
        unit2.setPricePerUnit(3.00);

        // Setup storage requirements for ingredient 2
        storage2 = new IngredientStorageRequirement();
        storage2.setCurrentQuantity(3.5);
        storage2.setMaxQuantityLimit(15.0);
        storage2.setAlertLowQuantity(2.0);
        storage2.setTimesReachedLow(0);

        testIngredient2 = new Ingredient();
        testIngredient2.setProductName("Granulated Sugar");
        testIngredient2.setUnitDetails(unit2);
        testIngredient2.setQuantityDetails(storage2);
    }

    // GET /api/ingredients - Get All Ingredients Tests
    @Test
    void getAllIngredients_ShouldReturnListOfIngredients() {
        List<Ingredient> ingredients = Arrays.asList(testIngredient1, testIngredient2);
        when(ingredientService.getAllIngredients()).thenReturn(ingredients);

        List<Ingredient> result = ingredientController.getAllIngredients();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("All-Purpose Flour", result.get(0).getProductName());
        assertEquals("Granulated Sugar", result.get(1).getProductName());
        assertEquals("kg", result.get(0).getUnitDetails().getUnitOfMeasurement());
        assertEquals(5.0, result.get(0).getQuantityDetails().getCurrentQuantity());
        verify(ingredientService, times(1)).getAllIngredients();
    }

    @Test
    void getAllIngredients_ShouldReturnEmptyList_WhenNoIngredients() {
        when(ingredientService.getAllIngredients()).thenReturn(Arrays.asList());

        List<Ingredient> result = ingredientController.getAllIngredients();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(ingredientService, times(1)).getAllIngredients();
    }

    @Test
    void getAllIngredients_ShouldReturnIngredientsWithAllDetails() {
        List<Ingredient> ingredients = Arrays.asList(testIngredient1, testIngredient2);
        when(ingredientService.getAllIngredients()).thenReturn(ingredients);

        List<Ingredient> result = ingredientController.getAllIngredients();

        // Verify first ingredient details
        assertEquals("All-Purpose Flour", result.get(0).getProductName());
        assertEquals("kg", result.get(0).getUnitDetails().getUnitOfMeasurement());
        assertEquals(2.50, result.get(0).getUnitDetails().getPricePerUnit());
        assertEquals(5.0, result.get(0).getQuantityDetails().getCurrentQuantity());
        assertEquals(20.0, result.get(0).getQuantityDetails().getMaxQuantityLimit());
        assertEquals(3.0, result.get(0).getQuantityDetails().getAlertLowQuantity());
        assertEquals(2, result.get(0).getQuantityDetails().getTimesReachedLow());

        // Verify second ingredient details
        assertEquals("Granulated Sugar", result.get(1).getProductName());
        assertEquals("kg", result.get(1).getUnitDetails().getUnitOfMeasurement());
        assertEquals(3.00, result.get(1).getUnitDetails().getPricePerUnit());
        assertEquals(3.5, result.get(1).getQuantityDetails().getCurrentQuantity());
    }

    // GET /api/ingredients/{id} - Get Ingredient By ID Tests
    @Test
    void getIngredientById_ShouldReturnIngredient_WhenIngredientExists() {
        when(ingredientService.getIngredientById(1)).thenReturn(testIngredient1);

        ResponseEntity<Ingredient> response = ingredientController.getIngredientById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("All-Purpose Flour", response.getBody().getProductName());
        assertEquals("kg", response.getBody().getUnitDetails().getUnitOfMeasurement());
        assertEquals(2.50, response.getBody().getUnitDetails().getPricePerUnit());
        assertEquals(5.0, response.getBody().getQuantityDetails().getCurrentQuantity());
        assertEquals(20.0, response.getBody().getQuantityDetails().getMaxQuantityLimit());
        verify(ingredientService, times(1)).getIngredientById(1);
    }

    @Test
    void getIngredientById_ShouldReturnNotFound_WhenIngredientDoesNotExist() {
        when(ingredientService.getIngredientById(999)).thenReturn(null);

        ResponseEntity<Ingredient> response = ingredientController.getIngredientById(999);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(ingredientService, times(1)).getIngredientById(999);
    }

    @Test
    void getIngredientById_ShouldReturnIngredientWithStorageAlerts() {
        when(ingredientService.getIngredientById(2)).thenReturn(testIngredient2);

        ResponseEntity<Ingredient> response = ingredientController.getIngredientById(2);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3.5, response.getBody().getQuantityDetails().getCurrentQuantity());
        assertEquals(2.0, response.getBody().getQuantityDetails().getAlertLowQuantity());
        assertEquals(0, response.getBody().getQuantityDetails().getTimesReachedLow());
    }

    // POST /api/ingredients - Create Ingredient Tests
    @Test
    void createIngredient_ShouldReturnCreatedIngredient() {
        IngredientUnit newUnit = new IngredientUnit();
        newUnit.setUnitOfMeasurement("kg");
        newUnit.setPricePerUnit(4.00);

        IngredientStorageRequirement newStorage = new IngredientStorageRequirement();
        newStorage.setCurrentQuantity(2.0);
        newStorage.setMaxQuantityLimit(10.0);
        newStorage.setAlertLowQuantity(1.5);
        newStorage.setTimesReachedLow(0);

        Ingredient newIngredient = new Ingredient();
        newIngredient.setProductName("Brown Sugar");
        newIngredient.setUnitDetails(newUnit);
        newIngredient.setQuantityDetails(newStorage);

        Ingredient savedIngredient = new Ingredient();
        savedIngredient.setProductName("Brown Sugar");
        savedIngredient.setUnitDetails(newUnit);
        savedIngredient.setQuantityDetails(newStorage);

        when(ingredientService.createIngredient(any(Ingredient.class))).thenReturn(savedIngredient);

        Ingredient result = ingredientController.createIngredient(newIngredient);

        assertNotNull(result);
        assertEquals("Brown Sugar", result.getProductName());
        assertEquals("kg", result.getUnitDetails().getUnitOfMeasurement());
        assertEquals(4.00, result.getUnitDetails().getPricePerUnit());
        assertEquals(2.0, result.getQuantityDetails().getCurrentQuantity());
        assertEquals(10.0, result.getQuantityDetails().getMaxQuantityLimit());
        verify(ingredientService, times(1)).createIngredient(any(Ingredient.class));
    }

    @Test
    void createIngredient_ShouldHandleDifferentUnits() {
        IngredientUnit newUnit = new IngredientUnit();
        newUnit.setUnitOfMeasurement("ml");
        newUnit.setPricePerUnit(0.15);

        IngredientStorageRequirement newStorage = new IngredientStorageRequirement();
        newStorage.setCurrentQuantity(100.0);
        newStorage.setMaxQuantityLimit(500.0);
        newStorage.setAlertLowQuantity(50.0);
        newStorage.setTimesReachedLow(0);

        Ingredient newIngredient = new Ingredient();
        newIngredient.setProductName("Vanilla Extract");
        newIngredient.setUnitDetails(newUnit);
        newIngredient.setQuantityDetails(newStorage);

        when(ingredientService.createIngredient(any(Ingredient.class))).thenReturn(newIngredient);

        Ingredient result = ingredientController.createIngredient(newIngredient);

        assertNotNull(result);
        assertEquals("Vanilla Extract", result.getProductName());
        assertEquals("ml", result.getUnitDetails().getUnitOfMeasurement());
        assertEquals(0.15, result.getUnitDetails().getPricePerUnit());
        assertEquals(100.0, result.getQuantityDetails().getCurrentQuantity());
        verify(ingredientService, times(1)).createIngredient(newIngredient);
    }

    @Test
    void createIngredient_ShouldHandleStorageRequirementTracking() {
        IngredientUnit newUnit = new IngredientUnit();
        newUnit.setUnitOfMeasurement("g");
        newUnit.setPricePerUnit(0.02);

        IngredientStorageRequirement newStorage = new IngredientStorageRequirement();
        newStorage.setCurrentQuantity(15.5);
        newStorage.setMaxQuantityLimit(100.0);
        newStorage.setAlertLowQuantity(20.0);
        newStorage.setTimesReachedLow(3);

        Ingredient newIngredient = new Ingredient();
        newIngredient.setProductName("Baking Powder");
        newIngredient.setUnitDetails(newUnit);
        newIngredient.setQuantityDetails(newStorage);

        when(ingredientService.createIngredient(any(Ingredient.class))).thenReturn(newIngredient);

        Ingredient result = ingredientController.createIngredient(newIngredient);

        assertNotNull(result);
        assertEquals(15.5, result.getQuantityDetails().getCurrentQuantity());
        assertEquals(20.0, result.getQuantityDetails().getAlertLowQuantity());
        assertEquals(3, result.getQuantityDetails().getTimesReachedLow());
        verify(ingredientService, times(1)).createIngredient(any(Ingredient.class));
    }

    // PUT /api/ingredients/{id} - Update Ingredient Tests
    @Test
    void updateIngredient_ShouldReturnUpdatedIngredient_WhenIngredientExists() {
        IngredientUnit updatedUnit = new IngredientUnit();
        updatedUnit.setUnitOfMeasurement("kg");
        updatedUnit.setPricePerUnit(3.50);

        IngredientStorageRequirement updatedStorage = new IngredientStorageRequirement();
        updatedStorage.setCurrentQuantity(10.0);
        updatedStorage.setMaxQuantityLimit(25.0);
        updatedStorage.setAlertLowQuantity(5.0);
        updatedStorage.setTimesReachedLow(1);

        Ingredient updatedDetails = new Ingredient();
        updatedDetails.setProductName("Organic All-Purpose Flour");
        updatedDetails.setUnitDetails(updatedUnit);
        updatedDetails.setQuantityDetails(updatedStorage);

        Ingredient updatedIngredient = new Ingredient();
        updatedIngredient.setProductName("Organic All-Purpose Flour");
        updatedIngredient.setUnitDetails(updatedUnit);
        updatedIngredient.setQuantityDetails(updatedStorage);

        when(ingredientService.getIngredientById(1)).thenReturn(testIngredient1);
        when(ingredientService.createIngredient(any(Ingredient.class))).thenReturn(updatedIngredient);

        ResponseEntity<Ingredient> response = ingredientController.updateIngredient(1, updatedDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Organic All-Purpose Flour", response.getBody().getProductName());
        assertEquals("kg", response.getBody().getUnitDetails().getUnitOfMeasurement());
        assertEquals(3.50, response.getBody().getUnitDetails().getPricePerUnit());
        assertEquals(10.0, response.getBody().getQuantityDetails().getCurrentQuantity());
        assertEquals(25.0, response.getBody().getQuantityDetails().getMaxQuantityLimit());
        verify(ingredientService, times(1)).getIngredientById(1);
        verify(ingredientService, times(1)).createIngredient(any(Ingredient.class));
    }

    @Test
    void updateIngredient_ShouldReturnNotFound_WhenIngredientDoesNotExist() {
        IngredientUnit updatedUnit = new IngredientUnit();
        updatedUnit.setUnitOfMeasurement("kg");
        updatedUnit.setPricePerUnit(2.00);

        IngredientStorageRequirement updatedStorage = new IngredientStorageRequirement();
        updatedStorage.setCurrentQuantity(5.0);
        updatedStorage.setMaxQuantityLimit(15.0);
        updatedStorage.setAlertLowQuantity(2.0);
        updatedStorage.setTimesReachedLow(0);

        Ingredient updatedDetails = new Ingredient();
        updatedDetails.setProductName("Updated Ingredient");
        updatedDetails.setUnitDetails(updatedUnit);
        updatedDetails.setQuantityDetails(updatedStorage);

        when(ingredientService.getIngredientById(999)).thenReturn(null);

        ResponseEntity<Ingredient> response = ingredientController.updateIngredient(999, updatedDetails);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(ingredientService, times(1)).getIngredientById(999);
        verify(ingredientService, never()).createIngredient(any(Ingredient.class));
    }

    @Test
    void updateIngredient_ShouldUpdateAllFields() {
        IngredientUnit existingUnit = new IngredientUnit();
        existingUnit.setUnitOfMeasurement("g");
        existingUnit.setPricePerUnit(0.01);

        IngredientStorageRequirement existingStorage = new IngredientStorageRequirement();
        existingStorage.setCurrentQuantity(100.0);
        existingStorage.setMaxQuantityLimit(200.0);
        existingStorage.setAlertLowQuantity(50.0);
        existingStorage.setTimesReachedLow(0);

        Ingredient existingIngredient = new Ingredient();
        existingIngredient.setProductName("Original Product");
        existingIngredient.setUnitDetails(existingUnit);
        existingIngredient.setQuantityDetails(existingStorage);

        IngredientUnit newUnit = new IngredientUnit();
        newUnit.setUnitOfMeasurement("ml");
        newUnit.setPricePerUnit(0.25);

        IngredientStorageRequirement newStorage = new IngredientStorageRequirement();
        newStorage.setCurrentQuantity(250.5);
        newStorage.setMaxQuantityLimit(500.0);
        newStorage.setAlertLowQuantity(100.0);
        newStorage.setTimesReachedLow(5);

        Ingredient updatedDetails = new Ingredient();
        updatedDetails.setProductName("New Product Name");
        updatedDetails.setUnitDetails(newUnit);
        updatedDetails.setQuantityDetails(newStorage);

        when(ingredientService.getIngredientById(1)).thenReturn(existingIngredient);
        when(ingredientService.createIngredient(any(Ingredient.class))).thenReturn(existingIngredient);

        ResponseEntity<Ingredient> response = ingredientController.updateIngredient(1, updatedDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Verify all fields were updated
        assertEquals("New Product Name", existingIngredient.getProductName());
        assertEquals("ml", existingIngredient.getUnitDetails().getUnitOfMeasurement());
        assertEquals(0.25, existingIngredient.getUnitDetails().getPricePerUnit());
        assertEquals(250.5, existingIngredient.getQuantityDetails().getCurrentQuantity());
        assertEquals(500.0, existingIngredient.getQuantityDetails().getMaxQuantityLimit());
        assertEquals(100.0, existingIngredient.getQuantityDetails().getAlertLowQuantity());
        assertEquals(5, existingIngredient.getQuantityDetails().getTimesReachedLow());
        verify(ingredientService, times(1)).getIngredientById(1);
        verify(ingredientService, times(1)).createIngredient(existingIngredient);
    }

    @Test
    void updateIngredient_ShouldHandleUnitConversion() {
        IngredientUnit updatedUnit = new IngredientUnit();
        updatedUnit.setUnitOfMeasurement("g");
        updatedUnit.setPricePerUnit(0.0025);

        IngredientStorageRequirement updatedStorage = new IngredientStorageRequirement();
        updatedStorage.setCurrentQuantity(5000.0);
        updatedStorage.setMaxQuantityLimit(20000.0);
        updatedStorage.setAlertLowQuantity(3000.0);
        updatedStorage.setTimesReachedLow(2);

        Ingredient updatedDetails = new Ingredient();
        updatedDetails.setProductName("All-Purpose Flour");
        updatedDetails.setUnitDetails(updatedUnit);
        updatedDetails.setQuantityDetails(updatedStorage);

        when(ingredientService.getIngredientById(1)).thenReturn(testIngredient1);
        when(ingredientService.createIngredient(any(Ingredient.class))).thenReturn(testIngredient1);

        ResponseEntity<Ingredient> response = ingredientController.updateIngredient(1, updatedDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("g", testIngredient1.getUnitDetails().getUnitOfMeasurement());
        assertEquals(5000.0, testIngredient1.getQuantityDetails().getCurrentQuantity());
    }

    // DELETE /api/ingredients/{id} - Delete Ingredient Tests
    @Test
    void deleteIngredient_ShouldReturnNoContent() {
        doNothing().when(ingredientService).deleteIngredient(1);

        ResponseEntity<Void> response = ingredientController.deleteIngredient(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(ingredientService, times(1)).deleteIngredient(1);
    }

    @Test
    void deleteIngredient_ShouldReturnNoContent_EvenWhenIngredientDoesNotExist() {
        doNothing().when(ingredientService).deleteIngredient(999);

        ResponseEntity<Void> response = ingredientController.deleteIngredient(999);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(ingredientService, times(1)).deleteIngredient(999);
    }

    @Test
    void deleteIngredient_ShouldHandleServiceException() {
        doThrow(new RuntimeException("Database error")).when(ingredientService).deleteIngredient(1);

        assertThrows(RuntimeException.class, () -> {
            ingredientController.deleteIngredient(1);
        });

        verify(ingredientService, times(1)).deleteIngredient(1);
    }

    @Test
    void deleteIngredient_ShouldCallServiceWithCorrectId() {
        doNothing().when(ingredientService).deleteIngredient(2);

        ResponseEntity<Void> response = ingredientController.deleteIngredient(2);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(ingredientService, times(1)).deleteIngredient(2);
        verify(ingredientService, never()).deleteIngredient(1);
    }
}
