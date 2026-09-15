package com.example.FakeCommerce.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import com.example.FakeCommerce.exceptions.ResourceNotFoundException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.FakeCommerce.schema.Category;
import com.example.FakeCommerce.services.CategoryService;

// Loads only the Spring MVC layer for CategoryController instead of starting the full application.
@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    // Spring creates MockMvc and injects it here so the controller can be tested
    // without a real server.
    @Autowired
    private MockMvc mockMvc;

    // Registers a Mockito mock of CategoryService in Spring's test context.
    // The controller receives this mock instead of calling the real service or
    // database.
    @MockitoBean
    private CategoryService categoryService;

    // Marks this method as a JUnit test that should be executed by the test runner.
    @Test
    void createCategory_whenValidInput_returnsCreated() throws Exception {

        // Arrange
        Category testCategory = Category.builder()
                .name("Electronics")
                .build();
        testCategory.setId(1L);
        when(categoryService.createCategory(any())).thenReturn(testCategory);

        // Act
        // Perform a POST request to the /categories endpoint with the testCategory as
        // the request body
        // Assert
        // Verify that the response status is 201 Created and the response body contains
        // the expected category

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/categories")
                .contentType("application/json")
                .content("{\"name\": \"Electronics\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("Electronics"));

    }

    @Test
    void createCategory_whenServiceFails_returnsInternalServerError() throws Exception {

        // Arrange: make the mocked service throw an unexpected exception.
        when(categoryService.createCategory(any()))
                .thenThrow(new RuntimeException("Unexpected failure"));

        // Act and Assert: the global exception handler converts it to HTTP 500.
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/categories")
                .contentType("application/json")
                .content("{\"name\": \"Electronics\"}"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Internal server error"));
    }

    @Test
    void getAllCategories_whenCategoriesExist_returnsOkWithCategories() throws Exception {

        // Arrange
        Category electronics = Category.builder().name("Electronics").build();
        electronics.setId(1L);
        Category books = Category.builder().name("Books").build();
        books.setId(2L);
        when(categoryService.getAllCategories()).thenReturn(List.of(electronics, books));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/categories"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].name").value("Electronics"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].name").value("Books"));
    }

    @Test
    void getAllCategories_whenNoCategoriesExist_returnsOkWithEmptyList() throws Exception {

        // Arrange
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/categories"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @Test
    void getAllCategories_whenServiceFails_returnsInternalServerError() throws Exception {

        // Arrange
        when(categoryService.getAllCategories()).thenThrow(new RuntimeException("Database failure"));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/categories"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Something went wrong"));
    }

    @Test
    void getCategoryById_whenCategoryExists_returnsOkWithCategory() throws Exception {

        // Arrange
        Category category = Category.builder().name("Electronics").build();
        category.setId(1L);
        when(categoryService.getCategoryById(1L)).thenReturn(category);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/categories/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("Electronics"));
    }

    @Test
    void getCategoryById_whenCategoryDoesNotExist_returnsNotFound() throws Exception {

        // Arrange
        when(categoryService.getCategoryById(99L))
                .thenThrow(new ResourceNotFoundException("Category not found with id: 99"));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/categories/99"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Category not found with id: 99"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Resource not found"));
    }

    @Test
    void getCategoryById_whenIdIsNotNumeric_returnsBadRequest() throws Exception {

        // Act and Assert: Spring rejects a non-numeric value before invoking the
        // controller method.
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/categories/not-a-number"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Invalid input type for parameter"));
    }

    @Test
    void deleteCategory_whenCategoryExists_returnsNoContent() throws Exception {

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/categories/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Category deleted successfully"));

        // Verify that the path variable was forwarded to the service.
        verify(categoryService).deleteCategory(1L);
    }

    @Test
    void deleteCategory_whenCategoryDoesNotExist_returnsNotFound() throws Exception {

        // Arrange
        doThrow(new ResourceNotFoundException("Category not found with id: 99"))
                .when(categoryService).deleteCategory(99L);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/categories/99"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Category not found with id: 99"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Resource not found"));
    }

    @Test
    void deleteCategory_whenIdIsNotNumeric_returnsBadRequest() throws Exception {

        // Act and Assert: Spring rejects the invalid path variable before service
        // invocation.
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/categories/not-a-number"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Invalid input type for parameter"));
    }
}
