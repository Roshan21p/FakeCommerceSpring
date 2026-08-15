package com.example.FakeCommerce.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.FakeCommerce.schema.Order;
import com.example.FakeCommerce.schema.OrderProducts;

@Repository
public interface OrderProductsRepository extends JpaRepository<OrderProducts, Long> {
    
    List<OrderProducts> findByOrderId(Long orderId);

    List<OrderProducts> findByOrderIdAndProductId(Long orderId, Long productId);

    @Query("SELECT op FROM OrderProducts op JOIN FETCH op.product WHERE op.order = :order")
    List<OrderProducts> findByOrderWithProduct(Order order);

}
