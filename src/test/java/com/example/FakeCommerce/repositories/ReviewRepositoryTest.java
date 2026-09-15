package com.example.FakeCommerce.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.example.FakeCommerce.config.TestJpaAuditingConfig;
import com.example.FakeCommerce.schema.Category;
import com.example.FakeCommerce.schema.Order;
import com.example.FakeCommerce.schema.OrderStatus;
import com.example.FakeCommerce.schema.Product;
import com.example.FakeCommerce.schema.Review;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(TestJpaAuditingConfig.class)
class ReviewRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ReviewRepository reviewRepository;

    private Product product;
    private Order order;
    private Review reviewOne;
    private Review reviewTwo;

    @BeforeEach
    void setup() {
        // Arrange
        Category category = Category.builder().name("Electronics").build();
        product = Product.builder()
                .title("Laptop")
                .description("Gaming laptop")
                .price(new BigDecimal("1500.00"))
                .rating(new BigDecimal("4.8"))
                .category(category)
                .build();

        order = Order.builder().status(OrderStatus.PENDING).build();

        reviewOne = Review.builder()
                .comment("Very good")
                .rating(new BigDecimal("5.0"))
                .product(product)
                .order(order)
                .build();

        reviewTwo = Review.builder()
                .comment("Nice product")
                .rating(new BigDecimal("4.5"))
                .product(product)
                .order(order)
                .build();

        // persistAndFlush() ensures the associated rows are inserted before repository
        // queries run.
        testEntityManager.persistAndFlush(category);
        testEntityManager.persistAndFlush(product);
        testEntityManager.persistAndFlush(order);
        testEntityManager.persistAndFlush(reviewOne);
        testEntityManager.persistAndFlush(reviewTwo);
        testEntityManager.clear();
    }

    @Test
    void findByProductId_whenReviewsExist_returnsMatchingReviews() {
        // Act
        List<Review> result = reviewRepository.findByProductId(product.getId());

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r -> r.getProduct().getId().equals(product.getId())));
    }

    @Test
    void findByProductId_whenNoReviewsExist_returnsEmptyList() {
        // Act
        List<Review> result = reviewRepository.findByProductId(999L);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void findByOrderId_whenReviewsExist_returnsMatchingReviews() {
        // Act
        List<Review> result = reviewRepository.findByOrderId(order.getId());

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r -> r.getOrder().getId().equals(order.getId())));
    }

    @Test
    void findByOrderId_whenNoReviewsExist_returnsEmptyList() {
        // Act
        List<Review> result = reviewRepository.findByOrderId(999L);

        // Assert
        assertTrue(result.isEmpty());
    }
}
