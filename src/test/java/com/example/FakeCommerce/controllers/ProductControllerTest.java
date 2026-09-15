package com.example.FakeCommerce.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.FakeCommerce.dtos.GetProductResponseDto;
import com.example.FakeCommerce.dtos.GetProductWithDetailsResponseDto;
import com.example.FakeCommerce.exceptions.ResourceNotFoundException;
import com.example.FakeCommerce.schema.Product;
import com.example.FakeCommerce.services.ProductService;

// Loads only ProductController and the MVC infrastructure for fast HTTP-layer testing.
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    // Spring injects MockMvc, which sends requests without starting a real web server.
    @Autowired
    private MockMvc mockMvc;

    // Replaces ProductService with a Mockito mock so controller behavior is isolated.
    @MockitoBean
    private ProductService productService;

    @Test
    void getAllProducts_whenProductsExist_returnsOkWithProducts() throws Exception {
        // Arrange
        GetProductResponseDto product = GetProductResponseDto.builder()
                .id(1L).title("Laptop").price(new BigDecimal("1500.00")).build();
        when(productService.getAllProducts()).thenReturn(List.of(product));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].title").value("Laptop"));
    }

    @Test
    void getAllProducts_whenNoProductsExist_returnsOkWithEmptyList() throws Exception {
        // Arrange
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @Test
    void getAllProducts_whenServiceFails_returnsInternalServerError() throws Exception {
        // Arrange
        when(productService.getAllProducts()).thenThrow(new RuntimeException("Database failure"));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Internal server error"));
    }

    @Test
    void getProductWithDetails_whenProductExists_returnsOk() throws Exception {
        // Arrange
        GetProductWithDetailsResponseDto product = GetProductWithDetailsResponseDto.builder()
                .id(1L).title("Laptop").category("Electronics").build();
        when(productService.getProductWithDetails(1L)).thenReturn(product);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/1/details"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.category").value("Electronics"));
    }

    @Test
    void getProductWithDetails_whenProductDoesNotExist_returnsNotFound() throws Exception {
        // Arrange
        when(productService.getProductWithDetails(99L))
                .thenThrow(new ResourceNotFoundException("Product not found with id: 99"));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/99/details"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Product not found with id: 99"));
    }

    @Test
    void getProductById_whenProductExists_returnsOk() throws Exception {
        // Arrange
        GetProductResponseDto product = GetProductResponseDto.builder()
                .id(1L).title("Laptop").build();
        when(productService.getProductById(1L)).thenReturn(product);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value("Laptop"));
    }

    @Test
    void getProductById_whenProductDoesNotExist_returnsNotFound() throws Exception {
        // Arrange
        when(productService.getProductById(99L))
                .thenThrow(new ResourceNotFoundException("Product not found with id: 99"));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/99"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Resource not found"));
    }

    @Test
    void createProduct_whenValidInput_returnsCreated() throws Exception {
        // Arrange
        Product product = Product.builder().title("Laptop").price(new BigDecimal("1500.00")).build();
        product.setId(1L);
        when(productService.createProduct(any())).thenReturn(product);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
                .contentType("application/json")
                .content("{\"title\":\"Laptop\",\"categoryId\":1,\"price\":1500.00}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value("Laptop"));
    }

    @Test
    void createProduct_whenCategoryDoesNotExist_returnsNotFound() throws Exception {
        // Arrange
        when(productService.createProduct(any()))
                .thenThrow(new ResourceNotFoundException("Category not found"));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
                .contentType("application/json")
                .content("{\"title\":\"Laptop\",\"categoryId\":99}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Category not found"));
    }

    @Test
    void deleteProduct_whenProductExists_returnsOk() throws Exception {
        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Product deleted successfully"));
        verify(productService).deleteProduct(1L);
    }

    @Test
    void deleteProduct_whenProductDoesNotExist_returnsNotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Product not found with id: 99"))
                .when(productService).deleteProduct(99L);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/99"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Product not found with id: 99"));
    }

    @Test
    void getProductsByCategory_whenProductsExist_returnsOk() throws Exception {
        // Arrange
        Product product = Product.builder().title("Laptop").build();
        product.setId(1L);
        when(productService.getProductsByCategory("Electronics")).thenReturn(List.of(product));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/search")
                .param("categoryName", "Electronics"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].title").value("Laptop"));
    }

    @Test
    void getProductsByCategory_whenNoProductsMatch_returnsEmptyList() throws Exception {
        // Arrange
        when(productService.getProductsByCategory("Books")).thenReturn(Collections.emptyList());

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/search")
                .param("categoryName", "Books"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @Test
    void getAllCategories_whenCategoriesExist_returnsOk() throws Exception {
        // Arrange
        when(productService.getAllCategories()).thenReturn(List.of("Electronics", "Books"));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/categories"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0]").value("Electronics"));
    }

    @Test
    void getAllCategories_whenNoCategoriesExist_returnsEmptyList() throws Exception {
        // Arrange
        when(productService.getAllCategories()).thenReturn(Collections.emptyList());

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/categories"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }
}
