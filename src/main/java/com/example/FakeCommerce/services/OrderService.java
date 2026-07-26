package com.example.FakeCommerce.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.FakeCommerce.adapters.OrderAdapter;
import com.example.FakeCommerce.dtos.GetOrderResponseDto;
import com.example.FakeCommerce.repositories.OrderProductsRepository;
import com.example.FakeCommerce.repositories.OrderRepository;
import com.example.FakeCommerce.schema.Order;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderProductsRepository orderProductsRepository;
    private final ProductService productService;
    private final OrderAdapter OrderAdapter;

    public List<GetOrderResponseDto> getAllOrders() {
        
        List<Order> orders = orderRepository.findAll();
        return OrderAdapter.mapToGetOrderResponseDtoList(orders);
           
    }
}
