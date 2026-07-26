package com.example.FakeCommerce.adapters;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.FakeCommerce.dtos.GetOrderResponseDto;
import com.example.FakeCommerce.dtos.OrderItemResponseDto;
import com.example.FakeCommerce.repositories.OrderProductsRepository;
import com.example.FakeCommerce.schema.Order;
import com.example.FakeCommerce.schema.OrderProducts;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderAdapter {

    private final OrderProductsRepository orderProductsRepository;

    public List<GetOrderResponseDto> mapToGetOrderResponseDtoList(List<Order> orders) {
        return orders.stream()
                .map(this::mapToGetOrderResponseDto)
                .collect(Collectors.toList());
    }

    public GetOrderResponseDto mapToGetOrderResponseDto(Order order) {

        List<OrderProducts> orderProducts = orderProductsRepository.findByOrderId(order.getId());
        // Map orderProducts to List<OrderItemResponseDto>
        List<OrderItemResponseDto> items = mapToOrderItemResponseDto(orderProducts);

        // Create and return GetOrderResponseDto
        return GetOrderResponseDto.builder()
                .id(order.getId())
                .status(order.getStatus())
                .orderItems(items)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public List<OrderItemResponseDto> mapToOrderItemResponseDto(List<OrderProducts> orderProducts) {
        // Map orderProducts to List<OrderItemResponseDto>

        return orderProducts.stream()
                .map(op -> {
                    return OrderItemResponseDto.builder()
                            .productId(op.getProduct().getId())
                            .productName(op.getProduct().getTitle())
                            .productPrice(op.getProduct().getPrice())
                            .productImage(op.getProduct().getImage())
                            .quantity(op.getQuantity())
                            .subtotal(op.getProduct().getPrice().multiply(BigDecimal.valueOf(op.getQuantity())))
                            .build();
                })
                .collect(Collectors.toList());
    }
}
