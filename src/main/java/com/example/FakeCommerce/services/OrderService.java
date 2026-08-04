package com.example.FakeCommerce.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.FakeCommerce.adapters.OrderAdapter;
import com.example.FakeCommerce.dtos.CreateOrderRequestDto;
import com.example.FakeCommerce.dtos.GetOrderResponseDto;
import com.example.FakeCommerce.dtos.OrderItemActionDto;
import com.example.FakeCommerce.dtos.UpdateOrderRequestDto;
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
    private final OrderAdapter orderAdapter;

    public List<GetOrderResponseDto> getAllOrders() {

        List<Order> orders = orderRepository.findAll();
        return orderAdapter.mapToGetOrderResponseDtoList(orders);

    }

    public GetOrderResponseDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        return orderAdapter.mapToGetOrderResponseDto(order);
    }

    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        orderRepository.delete(order);
    }

    public GetOrderResponseDto createOrder(CreateOrderRequestDto createOrderRequestDto) {

        // Check if there is an existing pending order for the user
        Order order = Order.builder()
                .status(OrderStatus.PENDING)
                .build();

        orderRepository.save(order);

        if (createOrderRequestDto.getOrderItems() != null) {

            List<Long> productsIds = createOrderRequestDto.getOrderItems().stream()
                    .map(item -> item.getProductId()).collect(Collectors.toList());

            List<Product> products = productRepository.findAllById(productsIds);

            Map<Long, Product> productMap = products.stream()
                    .collect(Collectors.toMap(Product::getId, Function.identity()));

            for (Long id : productsIds) {
                if (!productMap.containsKey(id)) {
                    throw new ResourceNotFoundException("Product not found with id: " + id);
                }
            }

            List<OrderProducts> orderProducts = new ArrayList<>();

            for (var itemDto : createOrderRequestDto.getOrderItems()) {

                Product product = productMap.get(itemDto.getProductId());

                orderProducts.add(OrderProducts.builder()
                        .order(order)
                        .product(product)
                        .quantity(itemDto.getQuantity() != null ? itemDto.getQuantity() : 1)
                        .build());
            }
            orderProductsRepository.saveAll(orderProducts);
        }

        return orderAdapter.mapToGetOrderResponseDto(order);
    }

    public GetOrderResponseDto updateOrder(Long id, UpdateOrderRequestDto updateOrderRequestDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (updateOrderRequestDto.getStatus() != null) {
            order.setStatus(updateOrderRequestDto.getStatus());
            orderRepository.save(order);
        }

        if (updateOrderRequestDto.getOrderItems() != null) {

            List<Long> productIds = updateOrderRequestDto.getOrderItems().stream()
                    .map(item -> item.getProductId()).collect(Collectors.toList());

            List<Product> products = productRepository.findAllById(productIds);


            Map<Long, Product> productMap = products.stream().collect(Collectors.toMap(Product::getId, Function.identity()));

            for(var pid : productIds) {
                if(!productMap.containsKey(pid)) {
                    throw new ResourceNotFoundException("Product not found with id: "+ pid);
                }
            }

            List<OrderProducts> toSave = new ArrayList<>();
            List<OrderProducts> toDelete = new ArrayList<>();

            Map<Long, OrderProducts> existingItems = orderProductsRepository.findByOrderId(id).stream()
                    .collect(Collectors.toMap(op -> op.getProduct().getId(), Function.identity()));

            
            for(OrderItemActionDto itemDto : updateOrderRequestDto.getOrderItems()) {
                Product product = productMap.get(itemDto.getProductId());
                OrderProducts existingItem = existingItems.get(itemDto.getProductId());

                switch(itemDto.getAction()) {
                    case ADD -> {
                        if(existingItem != null) {
                            int addQty = (itemDto.getQuantity() != null ? itemDto.getQuantity() : 1);
                            existingItem.setQuantity(existingItem.getQuantity() + addQty);
                            toSave.add(existingItem);
                        } else {
                            OrderProducts newItem = OrderProducts.builder()
                                    .order(order)
                                    .product(product)
                                    .quantity(itemDto.getQuantity() != null ? itemDto.getQuantity() : 1)
                                    .build();
                            existingItems.put(product.getId(), newItem);
                            toSave.add(newItem);
                        }
                    }
                    case REMOVE -> {
                        if(existingItem == null) {
                            throw new ResourceNotFoundException("Order item not found for product id: " + product.getId());
                        }

                        if(existingItem.getQuantity() <= 1){
                            toDelete.add(existingItem);
                            existingItems.remove(product.getId());
                        } else {
                            existingItem.setQuantity(existingItem.getQuantity() - 1);
                            toSave.add(existingItem);
                        }

                    } 
                }

                if(!toSave.isEmpty()) {
                    orderProductsRepository.saveAll(toSave);
                }

                if(!toDelete.isEmpty()) {
                    orderProductsRepository.deleteAll(toDelete);
                }
            }

        }
     return orderAdapter.mapToGetOrderResponseDto(order);

    }
    // User -> Cart -> Adds an item -> New Order (Pending)

    // User -> adds more items in the cart -> Same order will be updated

    // During checkout -> Order Pending -> Success/Failure
}
