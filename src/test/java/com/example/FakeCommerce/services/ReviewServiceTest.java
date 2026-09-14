package com.example.FakeCommerce.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ReviewAdapter reviewAdapter;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void createReview_savesReviewAndMapsResponse() {
        // Arrange
        Product product = Product.builder().build();
        product.setId(2L);
        Order order = Order.builder().build();
        order.setId(3L);
        CreateReviewRequestDto request = CreateReviewRequestDto.builder()
                .productId(2L)
                .orderId(3L)
                .rating(4.5)
                .comment("Good product")
                .build();
        Review savedReview = Review.builder().product(product).order(order)
                .rating(new BigDecimal("4.5")).comment("Good product").build();
        GetReviewResponseDto response = GetReviewResponseDto.builder().id(1L).build();

        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(orderRepository.findById(3L)).thenReturn(Optional.of(order));
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);
        when(reviewAdapter.mapToGetReviewResponseDto(savedReview)).thenReturn(response);

        // Act
        GetReviewResponseDto result = reviewService.createReview(request);

        // Assert
        assertEquals(response, result);
        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewCaptor.capture());
        assertEquals(product, reviewCaptor.getValue().getProduct());
        assertEquals(new BigDecimal("4.5"), reviewCaptor.getValue().getRating());
    }

    @Test
    void createReview_whenProductMissing_throwsException() {
        // Arrange
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        // Act and Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.createReview(CreateReviewRequestDto.builder()
                        .productId(2L).orderId(3L).rating(4.0).build()));

        assertEquals("Product with id 2 not found.", exception.getMessage());
        verify(orderRepository, never()).findById(3L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createReview_whenOrderMissing_throwsException() {
        // Arrange
        Product product = Product.builder().build();
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(orderRepository.findById(3L)).thenReturn(Optional.empty());

        // Act and Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.createReview(CreateReviewRequestDto.builder()
                        .productId(2L).orderId(3L).rating(4.0).build()));

        assertEquals("Order with id 3 not found.", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void getAllReviews_mapsRepositoryResults() {
        // Arrange
        List<Review> reviews = List.of(Review.builder().build());
        List<GetReviewResponseDto> responses = List.of(GetReviewResponseDto.builder().build());
        when(reviewRepository.findAll()).thenReturn(reviews);
        when(reviewAdapter.mapToGetReviewResponseDtoList(reviews)).thenReturn(responses);

        // Act and Assert
        assertEquals(responses, reviewService.getAllReviews());
    }

    @Test
    void getAllReviews_whenRepositoryIsEmpty_returnsEmptyList() {
        // Arrange
        List<Review> reviews = List.of();
        List<GetReviewResponseDto> responses = List.of();
        when(reviewRepository.findAll()).thenReturn(reviews);
        when(reviewAdapter.mapToGetReviewResponseDtoList(reviews)).thenReturn(responses);

        // Act
        List<GetReviewResponseDto> result = reviewService.getAllReviews();

        // Assert
        assertEquals(responses, result);
    }

    @Test
    void getReviewById_whenMissing_throwsException() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.getReviewById(1L));

        assertEquals("Review with id 1 not found.", exception.getMessage());
    }

    @Test
    void getReviewById_whenReviewExists_returnsMappedResponse() {
        // Arrange
        Review review = Review.builder().comment("Good product").build();
        GetReviewResponseDto response = GetReviewResponseDto.builder().id(1L).build();
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewAdapter.mapToGetReviewResponseDto(review)).thenReturn(response);

        // Act
        GetReviewResponseDto result = reviewService.getReviewById(1L);

        // Assert
        assertEquals(response, result);
        verify(reviewAdapter).mapToGetReviewResponseDto(review);
    }

    @Test
    void getReviewsByProductId_delegatesToRepositoryAndAdapter() {
        // Arrange
        List<Review> reviews = List.of(Review.builder().build());
        List<GetReviewResponseDto> responses = List.of(GetReviewResponseDto.builder().build());
        when(reviewRepository.findByProductId(2L)).thenReturn(reviews);
        when(reviewAdapter.mapToGetReviewResponseDtoList(reviews)).thenReturn(responses);

        // Act and Assert
        assertEquals(responses, reviewService.getReviewsByProductId(2L));
    }

    @Test
    void getReviewsByProductId_whenNoReviews_returnsEmptyList() {
        // Arrange
        List<Review> reviews = List.of();
        List<GetReviewResponseDto> responses = List.of();
        when(reviewRepository.findByProductId(2L)).thenReturn(reviews);
        when(reviewAdapter.mapToGetReviewResponseDtoList(reviews)).thenReturn(responses);

        // Act
        List<GetReviewResponseDto> result = reviewService.getReviewsByProductId(2L);

        // Assert
        assertEquals(responses, result);
    }

    @Test
    void getReviewsByOrderId_delegatesToRepositoryAndAdapter() {
        // Arrange
        List<Review> reviews = List.of(Review.builder().build());
        List<GetReviewResponseDto> responses = List.of(GetReviewResponseDto.builder().build());
        when(reviewRepository.findByOrderId(3L)).thenReturn(reviews);
        when(reviewAdapter.mapToGetReviewResponseDtoList(reviews)).thenReturn(responses);

        // Act and Assert
        assertEquals(responses, reviewService.getReviewsByOrderId(3L));
    }

    @Test
    void getReviewsByOrderId_whenNoReviews_returnsEmptyList() {
        // Arrange
        List<Review> reviews = List.of();
        List<GetReviewResponseDto> responses = List.of();
        when(reviewRepository.findByOrderId(3L)).thenReturn(reviews);
        when(reviewAdapter.mapToGetReviewResponseDtoList(reviews)).thenReturn(responses);

        // Act
        List<GetReviewResponseDto> result = reviewService.getReviewsByOrderId(3L);

        // Assert
        assertEquals(responses, result);
    }

    @Test
    void deleteReview_whenReviewExists_deletesReview() {
        // Arrange
        Review review = Review.builder().build();
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        // Act
        reviewService.deleteReview(1L);

        // Assert
        verify(reviewRepository).delete(review);
    }

    @Test
    void deleteReview_whenReviewMissing_throwsException() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.deleteReview(1L));

        assertEquals("Review with id 1 not found", exception.getMessage());
    }
}