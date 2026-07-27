package com.example.FakeCommerce.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.FakeCommerce.adapters.OrderAdapter;
import com.example.FakeCommerce.dtos.CreateOrderRequestDto;
import com.example.FakeCommerce.dtos.GetOrderResponseDto;
import com.example.FakeCommerce.exceptions.ResourceNotFoundException;
import com.example.FakeCommerce.repositories.OrderProductsRepository;
import com.example.FakeCommerce.repositories.OrderRepository;
import com.example.FakeCommerce.repositories.ProductRepository;
import com.example.FakeCommerce.schema.Order;
import com.example.FakeCommerce.schema.OrderProducts;
import com.example.FakeCommerce.schema.OrderStatus;
import com.example.FakeCommerce.schema.Product;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderProductsRepository orderProductsRepository;
    private final OrderAdapter OrderAdapter;

    public List<GetOrderResponseDto> getAllOrders() {

        List<Order> orders = orderRepository.findAll();
        return OrderAdapter.mapToGetOrderResponseDtoList(orders);

    }

    public GetOrderResponseDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        return OrderAdapter.mapToGetOrderResponseDto(order);
    }

    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        orderRepository.delete(order);
    }

    public void createOrder(CreateOrderRequestDto createOrderRequestDto) {

        // Check if there is an existing pending order for the user
        Order order = Order.builder()
                .status(OrderStatus.PENDING)
                .build();

        orderRepository.save(order);

        if (createOrderRequestDto.getOrderItems() != null) {
            for (var orderItem : createOrderRequestDto.getOrderItems()) {
                Product product = productRepository.findById(orderItem.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Product not found with id: " + orderItem.getProductId()));

                OrderProducts orderProduct = OrderProducts.builder()
                        .order(order)
                        .product(product)
                        .quantity(orderItem.getQuantity())
                        .build();
                orderProductsRepository.save(orderProduct);
            }
        }
    }

    // User -> Cart -> Adds an item -> New Order (Pending)

    // User -> adds more items in the cart -> Same order will be updated

    // During checkout -> Order Pending -> Success/Failure

}
