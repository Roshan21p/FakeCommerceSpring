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

import com.example.FakeCommerce.dtos.GetReviewResponseDto;
import com.example.FakeCommerce.exceptions.ResourceNotFoundException;
import com.example.FakeCommerce.services.ReviewService;

// Loads only ReviewController and MVC infrastructure for HTTP-layer testing.
@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    // Spring injects MockMvc so requests can be tested without a running web
    // server.
    @Autowired
    private MockMvc mockMvc;

    // Registers a Mockito service bean so no real database or service logic is
    // used.
    @MockitoBean
    private ReviewService reviewService;

    private GetReviewResponseDto reviewDto(Long id) {
        return GetReviewResponseDto.builder()
                .id(id).productId(10L).orderId(20L)
                .rating(new BigDecimal("5.0")).comment("Excellent").build();
    }

    @Test
    void createReview_whenValidInput_returnsCreated() throws Exception {
        // Arrange
        when(reviewService.createReview(any())).thenReturn(reviewDto(1L));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/reviews")
                .contentType("application/json")
                .content("{\"productId\":10,\"orderId\":20,\"rating\":5.0,\"comment\":\"Excellent\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.productId").value(10L));
    }

    @Test
    void createReview_whenProductOrOrderDoesNotExist_returnsNotFound() throws Exception {
        // Arrange
        when(reviewService.createReview(any()))
                .thenThrow(new ResourceNotFoundException("Product with id 10 not found."));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/reviews")
                .contentType("application/json")
                .content("{\"productId\":10,\"orderId\":20,\"rating\":5.0}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Resource not found"));
    }

    @Test
    void getAllReviews_whenReviewsExist_returnsOkWithReviews() throws Exception {
        // Arrange
        when(reviewService.getAllReviews()).thenReturn(List.of(reviewDto(1L), reviewDto(2L)));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/reviews"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(2));
    }

    @Test
    void getAllReviews_whenNoReviewsExist_returnsEmptyList() throws Exception {
        // Arrange
        when(reviewService.getAllReviews()).thenReturn(Collections.emptyList());

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/reviews"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @Test
    void getReviewById_whenReviewExists_returnsOk() throws Exception {
        // Arrange
        when(reviewService.getReviewById(1L)).thenReturn(reviewDto(1L));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/reviews/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L));
    }

    @Test
    void getReviewById_whenReviewDoesNotExist_returnsNotFound() throws Exception {
        // Arrange
        when(reviewService.getReviewById(99L))
                .thenThrow(new ResourceNotFoundException("Review with id 99 not found."));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/reviews/99"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getReviewsByProductId_whenReviewsExist_returnsOk() throws Exception {
        // Arrange
        when(reviewService.getReviewsByProductId(10L)).thenReturn(List.of(reviewDto(1L)));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/reviews/product/10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(1));
    }

    @Test
    void getReviewsByProductId_whenNoReviewsExist_returnsEmptyList() throws Exception {
        // Arrange
        when(reviewService.getReviewsByProductId(10L)).thenReturn(Collections.emptyList());

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/reviews/product/10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @Test
    void getReviewsByOrderId_whenReviewsExist_returnsOk() throws Exception {
        // Arrange
        when(reviewService.getReviewsByOrderId(20L)).thenReturn(List.of(reviewDto(1L)));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/reviews/order/20"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(1));
    }

    @Test
    void getReviewsByOrderId_whenNoReviewsExist_returnsEmptyList() throws Exception {
        // Arrange
        when(reviewService.getReviewsByOrderId(20L)).thenReturn(Collections.emptyList());

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/reviews/order/20"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteReview_whenReviewExists_returnsNoContent() throws Exception {
        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/reviews/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Review deleted successfully"));
        verify(reviewService).deleteReview(1L);
    }

    @Test
    void deleteReview_whenReviewDoesNotExist_returnsNotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Review with id 99 not found"))
                .when(reviewService).deleteReview(99L);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/reviews/99"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
