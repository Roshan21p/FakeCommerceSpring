package com.example.FakeCommerce.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.FakeCommerce.adapters.ReviewAdapter;
import com.example.FakeCommerce.dtos.CreateReviewRequestDto;
import com.example.FakeCommerce.dtos.GetReviewResponseDto;
import com.example.FakeCommerce.exceptions.ResourceNotFoundException;
import com.example.FakeCommerce.repositories.OrderRepository;
import com.example.FakeCommerce.repositories.ProductRepository;
import com.example.FakeCommerce.repositories.ReviewRepository;
import com.example.FakeCommerce.schema.Order;
import com.example.FakeCommerce.schema.Product;
import com.example.FakeCommerce.schema.Review;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Builder
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ReviewAdapter reviewAdapter;

    public GetReviewResponseDto createReview(CreateReviewRequestDto createReviewRequestDto) {

       Product product = productRepository.findById(createReviewRequestDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + createReviewRequestDto.getProductId() + " not found."));
    

        Order order = orderRepository.findById(createReviewRequestDto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + createReviewRequestDto.getOrderId() + " not found."));

        log.info("Order found: {}", order);
        
        Review review = Review.builder()
                .product(product)
                .order(order)
                .rating(BigDecimal.valueOf(createReviewRequestDto.getRating()))
                .comment(createReviewRequestDto.getComment())
                .build();

        /*
        Review review = new Review();
        review.setProduct(product);
        review.setOrder(order);
        review.setRating(BigDecimal.valueOf(createReviewRequestDto.getRating()));
        review.setComment(createReviewRequestDto.getComment());
        */
        Review savedReview = reviewRepository.save(review);

        return reviewAdapter.mapToGetReviewResponseDto(savedReview);
    }

    public List<GetReviewResponseDto> getAllReviews() {
        return reviewAdapter.mapToGetReviewResponseDtoList(reviewRepository.findAll());
    }

    public GetReviewResponseDto getReviewById(Long id) {

        return reviewRepository.findById(id)
                .map(reviewAdapter::mapToGetReviewResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Review with id " + id + " not found."));
    }

    public List<GetReviewResponseDto> getReviewsByProductId(Long productId) {

        return reviewAdapter.mapToGetReviewResponseDtoList(reviewRepository.findByProductId(productId));
    }

    public List<GetReviewResponseDto> getReviewsByOrderId(Long orderId) {

        return reviewAdapter.mapToGetReviewResponseDtoList(reviewRepository.findByOrderId(orderId));
    }

    public void deleteReview(Long id) {

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review with id " + id + " not found"));
        reviewRepository.delete(review);
    }
}
