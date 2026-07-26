package com.example.FakeCommerce.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemResponseDto {
    
    private Long productId;

    private String productName;

    private BigDecimal productPrice;

    private String productImage;

    private Integer quantity;

    private BigDecimal subtotal;
}
