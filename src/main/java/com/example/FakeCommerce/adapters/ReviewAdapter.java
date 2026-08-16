package com.example.FakeCommerce.adapters;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.FakeCommerce.dtos.GetReviewResponseDto;
import com.example.FakeCommerce.schema.Review;

@Component
public class ReviewAdapter {
    
    public List<GetReviewResponseDto> mapToGetReviewResponseDtoList(List<Review> reviews) {

        return reviews.stream()
                // .map(review -> mapToGetReviewResponseDto(review))
                .map(this::mapToGetReviewResponseDto)
                .collect(Collectors.toList());


        // Another way
        /*
           List<GetReviewResponseDto> response = new ArrayList<>();

            for (Review review : reviews) {
                response.add(mapToGetReviewResponseDto(review));
            }

            return response;
        */
    }

    public GetReviewResponseDto mapToGetReviewResponseDto(Review review) {

        return GetReviewResponseDto.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .orderId(review.getOrder().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();

    }
}
