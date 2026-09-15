package com.example.FakeCommerce.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.example.FakeCommerce.config.TestJpaAuditingConfig;
import com.example.FakeCommerce.schema.Order;
import com.example.FakeCommerce.schema.OrderStatus;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(TestJpaAuditingConfig.class)
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private OrderRepository orderRepository;

    private Order pendingOrder;

    @BeforeEach
    void setup() {
        // Arrange
        pendingOrder = Order.builder()
                .status(OrderStatus.PENDING)
                .build();

        // persistAndFlush() inserts the order into the in-memory database so findById
        // and findAll can work on real data.
        testEntityManager.persistAndFlush(pendingOrder);
        testEntityManager.clear();
    }

    @Test
    void findById_whenOrderExists_returnsOrder() {
        // Act
        Optional<Order> result = orderRepository.findById(pendingOrder.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(OrderStatus.PENDING, result.get().getStatus());
    }

    @Test
    void findById_whenOrderDoesNotExist_returnsEmptyOptional() {
        // Act
        Optional<Order> result = orderRepository.findById(999L);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void saveAndFindAll_whenOrdersExist_returnsPersistedOrders() {
        // Arrange
        Order shippedOrder = Order.builder().status(OrderStatus.SHIPPED).build();

        // Act
        orderRepository.save(shippedOrder);
        List<Order> result = orderRepository.findAll();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(order -> order.getStatus() == OrderStatus.PENDING));
        assertTrue(result.stream().anyMatch(order -> order.getStatus() == OrderStatus.SHIPPED));
    }
}
