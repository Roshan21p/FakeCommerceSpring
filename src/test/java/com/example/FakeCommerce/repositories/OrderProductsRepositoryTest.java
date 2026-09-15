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
import com.example.FakeCommerce.schema.OrderProducts;
import com.example.FakeCommerce.schema.OrderStatus;
import com.example.FakeCommerce.schema.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(TestJpaAuditingConfig.class)
class OrderProductsRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private OrderProductsRepository orderProductsRepository;

    private Order order;
    private Product product;
    private OrderProducts orderProduct;

    @BeforeEach
    void setup() {
        // Arrange
        Category category = Category.builder().name("Electronics").build();
        product = Product.builder()
                .title("Phone")
                .description("Smartphone")
                .price(new BigDecimal("699.99"))
                .rating(new BigDecimal("4.7"))
                .category(category)
                .build();

        order = Order.builder().status(OrderStatus.PENDING).build();
        orderProduct = OrderProducts.builder()
                .order(order)
                .product(product)
                .quantity(2)
                .build();

        // persistAndFlush() ensures the order, product, and joined row are inserted
        // before repository queries run.
        testEntityManager.persistAndFlush(category);
        testEntityManager.persistAndFlush(product);
        testEntityManager.persistAndFlush(order);
        testEntityManager.persistAndFlush(orderProduct);
        testEntityManager.clear();
    }

    @Test
    void findByOrderId_whenOrderItemsExist_returnsOrderProducts() {
        // Act
        List<OrderProducts> result = orderProductsRepository.findByOrderId(order.getId());

        // Assert
        assertEquals(1, result.size());
        assertEquals(order.getId(), result.get(0).getOrder().getId());
        assertEquals(product.getId(), result.get(0).getProduct().getId());
    }

    @Test
    void findByOrderId_whenOrderHasNoItems_returnsEmptyList() {
        // Act
        List<OrderProducts> result = orderProductsRepository.findByOrderId(999L);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void findByOrderIdAndProductId_whenMatchingItemExists_returnsItem() {
        // Act
        List<OrderProducts> result = orderProductsRepository.findByOrderIdAndProductId(order.getId(), product.getId());

        // Assert
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getQuantity());
    }

    @Test
    void findByOrderWithProduct_whenOrderHasItems_returnsProductsWithOrderData() {
        // Act
        List<OrderProducts> result = orderProductsRepository.findByOrderWithProduct(order);

        // Assert
        assertEquals(1, result.size());
        assertEquals(product.getId(), result.get(0).getProduct().getId());
        assertEquals(order.getId(), result.get(0).getOrder().getId());
    }
}
