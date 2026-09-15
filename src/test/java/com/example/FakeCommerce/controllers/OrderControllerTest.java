package com.example.FakeCommerce.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.FakeCommerce.dtos.GetOrderResponseDto;
import com.example.FakeCommerce.dtos.GetOrderSummaryResponseDto;
import com.example.FakeCommerce.exceptions.ResourceNotFoundException;
import com.example.FakeCommerce.schema.OrderStatus;
import com.example.FakeCommerce.services.OrderService;

// Loads only OrderController and MVC infrastructure for controller-level tests.
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    // MockMvc sends HTTP requests directly to the MVC layer without a real server.
    @Autowired
    private MockMvc mockMvc;

    // Injects a Mockito replacement for OrderService into the controller.
    @MockitoBean
    private OrderService orderService;

    private GetOrderResponseDto orderDto(Long id) {
        return GetOrderResponseDto.builder().id(id).status(OrderStatus.PENDING).build();
    }

    @Test
    void createOrder_whenValidInput_returnsCreated() throws Exception {
        // Arrange
        when(orderService.createOrder(any())).thenReturn(orderDto(1L));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
                .contentType("application/json")
                .content("{\"orderItems\":[]}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    void createOrder_whenServiceFails_returnsInternalServerError() throws Exception {
        // Arrange
        when(orderService.createOrder(any())).thenThrow(new RuntimeException("Create failed"));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
                .contentType("application/json")
                .content("{\"orderItems\":[]}"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Internal server error"));
    }

    @Test
    void getAllOrders_whenOrdersExist_returnsOkWithOrders() throws Exception {
        // Arrange
        when(orderService.getAllOrders()).thenReturn(List.of(orderDto(1L), orderDto(2L)));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(2));
    }

    @Test
    void getAllOrders_whenNoOrdersExist_returnsEmptyList() throws Exception {
        // Arrange
        when(orderService.getAllOrders()).thenReturn(Collections.emptyList());

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @Test
    void getOrderById_whenOrderExists_returnsOk() throws Exception {
        // Arrange
        when(orderService.getOrderById(1L)).thenReturn(orderDto(1L));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L));
    }

    @Test
    void getOrderById_whenOrderDoesNotExist_returnsNotFound() throws Exception {
        // Arrange
        when(orderService.getOrderById(99L))
                .thenThrow(new ResourceNotFoundException("Order not found with id: 99"));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/99"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Order not found with id: 99"));
    }

    @Test
    void deleteOrder_whenOrderExists_returnsNoContent() throws Exception {
        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/orders/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Order deleted successfully"));
        verify(orderService).deleteOrder(1L);
    }

    @Test
    void deleteOrder_whenOrderDoesNotExist_returnsNotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Order not found with id: 99"))
                .when(orderService).deleteOrder(99L);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/orders/99"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void updateOrder_whenRequestIsValid_returnsOk() throws Exception {
        // Arrange
        when(orderService.updateOrder(any(), any())).thenReturn(orderDto(1L));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/orders/1")
                .contentType("application/json")
                .content("{\"status\":\"SHIPPED\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    void updateOrder_whenOrderDoesNotExist_returnsNotFound() throws Exception {
        // Arrange
        when(orderService.updateOrder(any(), any()))
                .thenThrow(new ResourceNotFoundException("Order not found with id: 99"));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/orders/99")
                .contentType("application/json")
                .content("{\"status\":\"SHIPPED\"}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getOrderSummary_whenOrderExists_returnsOk() throws Exception {
        // Arrange
        GetOrderSummaryResponseDto summary = GetOrderSummaryResponseDto.builder()
                .id(1L).status(OrderStatus.PENDING).totalItems(0).build();
        when(orderService.getOrderSummary(1L)).thenReturn(summary);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/1/summary"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalItems").value(0));
    }

    @Test
    void getOrderSummary_whenOrderDoesNotExist_returnsNotFound() throws Exception {
        // Arrange
        when(orderService.getOrderSummary(99L))
                .thenThrow(new ResourceNotFoundException("Order not found with id: 99"));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/99/summary"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
