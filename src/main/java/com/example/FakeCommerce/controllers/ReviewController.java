package com.example.FakeCommerce.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.FakeCommerce.dtos.CreateReviewRequestDto;
import com.example.FakeCommerce.dtos.GetReviewResponseDto;
import com.example.FakeCommerce.services.ReviewService;
import com.example.FakeCommerce.utils.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {
    
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<GetReviewResponseDto>> createReview(@RequestBody CreateReviewRequestDto createReviewRequestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review create successfully", reviewService.createReview(createReviewRequestDto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GetReviewResponseDto>>> getAllReviews() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Reviews fetched successfully", reviewService.getAllReviews()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GetReviewResponseDto>> getReviewById(@PathVariable("id") Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Review fetched successfully", reviewService.getReviewById(id)));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<GetReviewResponseDto>>> getReviewsByProductId(@PathVariable("productId") Long productId) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Reviews fetched successfully", reviewService.getReviewsByProductId(productId)));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<List<GetReviewResponseDto>>> getReviewsByOrderId(@PathVariable("orderId") Long orderId) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Reviews fetched successfully", reviewService.getReviewsByOrderId(orderId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable("id") Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Review deleted successfully", null));
    }
}
