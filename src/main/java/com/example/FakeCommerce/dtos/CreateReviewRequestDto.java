package com.example.FakeCommerce.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateReviewRequestDto {
    
    private Long productId;

    private Long orderId;

    private Double rating;

    private String comment;

}
