package com.alancortez.project.controller;

import com.alancortez.project.model.Reports;
import com.alancortez.project.service.ReportsService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(ReportsController.class)
public class ReportsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReportsService reportsService;

    private Report report1;
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
                .setQuantityDetails(20, 100, 5, 40)
                .setUnitDetails(1.50, "grams")
                .createIngredient();

        ingredient2 = new IngredientBuilder()
                .setId(2L)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setProductName("Oat Milk")
                .setQuantityDetails(10, 70, 8, 20)
                .setUnitDetails(6.50, "gallons")
                .createIngredient();

        recipe1component1 = new RecipeComponent(ingredient1, 3);
        recipe1component2 = new RecipeComponent(ingredient2, 1);

        recipe1 = new RecipeBuilder().setRecipeName("Latte").setComponents(Arrays.asList(recipe1component1, recipe1component2));

        report1 = new Report("RECIPES_USED", Arrays.asList(recipe1));
        report1.setId(1L);
        report1.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateStaff_Success() throws Exception {
        Report reportTest = new Report("RECIPES_USED", Arrays.asList(recipe1));

        when(reportsService.createReport(any(Report.class))).thenReturn(reportTest);

        mockMvc.perform(post("/api/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reportTest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.dataPoint").value("RECIPES_USED"));

        verify(reportsService, times(1)).createReport(any(Report.class));
    }

    @Test
    void testGetAllReports_Success() throws Exception {
        List<Report> reports = Arrays.asList(report1);

        when(reportsService.getAllReports()).thenReturn(reports);

        mockMvc.perform(get("/api/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$.dataPoint").value("RECIPES_USED"));

        verify(reportsService, times(1)).getAllReports();
    }

    @Test
    void testGetAllReports_EmptyList() throws Exception {
        when(reportsService.getAllReports()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(reportsService, times(1)).getAllReports();
    }

    @Test
    void testGetReportsById_Success() throws Exception {
        when(reportsService.getReportsById(1L)).thenReturn(Optional.of(report1));

        mockMvc.perform(get("/api/reports/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.dataPoint").value("RECIPES_USED"));

        verify(reportsService, times(1)).getReportsById(1L);
    }

    @Test
    void testGetReportsById_NotFound() throws Exception {
        when(reportsService.getReportsById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/reports/999"))
                .andExpect(status().isNotFound());

        verify(reportsService, times(1)).getReportsById(999L);
    }

    @Test
    void testDeleteReport_Success() throws Exception {
        doNothing().when(reportsService).deleteReport(1L);

        mockMvc.perform(delete("/api/reports/1"))
                .andExpect(status().isNoContent());

        verify(reportsService, times(1)).deleteReport(1L);
    }

    @Test
    void testDeleteReport_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Report not found"))
                .when(reportsService).deleteReport(999L);

        mockMvc.perform(delete("/api/recipes/999"))
                .andExpect(status().isNotFound());

        verify(reportsService, times(1)).deleteReport(999L);
    }
}