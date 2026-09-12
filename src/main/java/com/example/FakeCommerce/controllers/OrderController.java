package com.example.FakeCommerce.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.FakeCommerce.dtos.CreateOrderRequestDto;
import com.example.FakeCommerce.dtos.GetOrderResponseDto;
import com.example.FakeCommerce.dtos.GetOrderSummaryResponseDto;
import com.example.FakeCommerce.dtos.UpdateOrderRequestDto;
import com.example.FakeCommerce.services.OrderService;
import com.example.FakeCommerce.utils.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<GetOrderResponseDto>> createOrder(@RequestBody CreateOrderRequestDto createOrderRequestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", orderService.createOrder(createOrderRequestDto)));
    } 
    @GetMapping
    public ResponseEntity<ApiResponse<List<GetOrderResponseDto>>> getAllOrders() {

        List<GetOrderResponseDto> orders = orderService.getAllOrders();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Orders fetched successfully", orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GetOrderResponseDto>> getOrderById(@PathVariable("id") Long id) {
        GetOrderResponseDto order = orderService.getOrderById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Order fetched successfully", order));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable("id") Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Order deleted successfully", null));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GetOrderResponseDto>> updateOrder(@PathVariable("id") Long id, @RequestBody UpdateOrderRequestDto updateOrderRequestDto) {
        GetOrderResponseDto updatedOrder = orderService.updateOrder(id, updateOrderRequestDto);
        
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Order updated successfully", updatedOrder));
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<ApiResponse<GetOrderSummaryResponseDto>> getOrderSummary(@PathVariable("id") Long id) {
        GetOrderSummaryResponseDto orderSummary = orderService.getOrderSummary(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Order summary fetched successfully", orderSummary));
    }
}
