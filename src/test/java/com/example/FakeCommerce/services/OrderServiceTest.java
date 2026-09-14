package com.example.FakeCommerce.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.FakeCommerce.adapters.OrderAdapter;
import com.example.FakeCommerce.dtos.CreateOrderRequestDto;
import com.example.FakeCommerce.dtos.GetOrderResponseDto;
import com.example.FakeCommerce.dtos.GetOrderSummaryResponseDto;
import com.example.FakeCommerce.dtos.OrderItemActionDto;
import com.example.FakeCommerce.dtos.OrderItemRequestDto;
import com.example.FakeCommerce.dtos.OrderItemResponseDto;
import com.example.FakeCommerce.dtos.UpdateOrderRequestDto;
import com.example.FakeCommerce.exceptions.ResourceNotFoundException;
import com.example.FakeCommerce.repositories.OrderProductsRepository;
import com.example.FakeCommerce.repositories.OrderRepository;
import com.example.FakeCommerce.repositories.ProductRepository;
import com.example.FakeCommerce.schema.Order;
import com.example.FakeCommerce.schema.OrderProducts;
import com.example.FakeCommerce.schema.OrderStatus;
import com.example.FakeCommerce.schema.Product;
import com.example.FakeCommerce.dtos.OrderItemAction;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderProductsRepository orderProductsRepository;

    @Mock
    private OrderAdapter orderAdapter;

    @InjectMocks
    private OrderService orderService;

    @Test
    void getAllOrders_mapsRepositoryResults() {
        // Arrange
        List<Order> orders = List.of(Order.builder().status(OrderStatus.PENDING).build());
        List<GetOrderResponseDto> responses = List.of(GetOrderResponseDto.builder().build());
        when(orderRepository.findAll()).thenReturn(orders);
        when(orderAdapter.mapToGetOrderResponseDtoList(orders)).thenReturn(responses);

        // Act and Assert
        assertEquals(responses, orderService.getAllOrders());
    }

    @Test
    void getAllOrders_whenRepositoryIsEmpty_returnsEmptyList() {
        // Arrange
        List<Order> orders = List.of();
        List<GetOrderResponseDto> responses = List.of();
        when(orderRepository.findAll()).thenReturn(orders);
        when(orderAdapter.mapToGetOrderResponseDtoList(orders)).thenReturn(responses);

        // Act
        List<GetOrderResponseDto> result = orderService.getAllOrders();

        // Assert
        assertEquals(responses, result);
    }

    @Test
    void getOrderById_whenMissing_throwsException() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> orderService.getOrderById(1L));

        assertEquals("Order not found with id: 1", exception.getMessage());
    }

    @Test
    void getOrderById_whenOrderExists_returnsMappedResponse() {
        // Arrange
        Order order = Order.builder().status(OrderStatus.PENDING).build();
        GetOrderResponseDto response = GetOrderResponseDto.builder().build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderAdapter.mapToGetOrderResponseDto(order)).thenReturn(response);

        // Act
        GetOrderResponseDto result = orderService.getOrderById(1L);

        // Assert
        assertEquals(response, result);
        verify(orderAdapter).mapToGetOrderResponseDto(order);
    }

    @Test
    void deleteOrder_whenOrderExists_deletesOrder() {
        // Arrange
        Order order = Order.builder().status(OrderStatus.PENDING).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        orderService.deleteOrder(1L);

        // Assert
        verify(orderRepository).delete(order);
    }

    @Test
    void deleteOrder_whenOrderMissing_throwsException() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> orderService.deleteOrder(1L));

        assertEquals("Order not found with id: 1", exception.getMessage());
    }

    @Test
    void createOrder_withoutItems_savesPendingOrderAndSkipsItemPersistence() {
        // Arrange
        GetOrderResponseDto response = GetOrderResponseDto.builder().build();
        when(orderAdapter.mapToGetOrderResponseDto(any(Order.class))).thenReturn(response);

        // Act
        GetOrderResponseDto result = orderService.createOrder(CreateOrderRequestDto.builder().build());

        // Assert
        assertEquals(response, result);
        verify(orderRepository).save(any(Order.class));
        verify(orderProductsRepository, never()).saveAll(any());
    }

    @Test
    void createOrder_withItems_savesPendingOrderAndOrderItems() {
        // Arrange
        Product product = product(2L, "Laptop", "10.00");
        GetOrderResponseDto response = GetOrderResponseDto.builder().build();
        when(productRepository.findAllById(List.of(2L))).thenReturn(List.of(product));
        when(orderAdapter.mapToGetOrderResponseDto(any(Order.class))).thenReturn(response);

        // Act
        GetOrderResponseDto result = orderService.createOrder(CreateOrderRequestDto.builder()
                .orderItems(List.of(OrderItemRequestDto.builder().productId(2L).quantity(3).build()))
                .build());

        assertEquals(response, result);
        verify(orderRepository).save(any(Order.class));
        ArgumentCaptor<List<OrderProducts>> itemsCaptor = ArgumentCaptor.forClass(List.class);
        verify(orderProductsRepository).saveAll(itemsCaptor.capture());
        assertEquals(3, itemsCaptor.getValue().get(0).getQuantity());
        assertEquals(product, itemsCaptor.getValue().get(0).getProduct());
    }

    @Test
    void createOrder_whenQuantityIsNull_defaultsQuantityToOne() {
        // Arrange
        Product product = product(2L, "Laptop", "10.00");
        when(productRepository.findAllById(List.of(2L))).thenReturn(List.of(product));
        when(orderAdapter.mapToGetOrderResponseDto(any(Order.class)))
                .thenReturn(GetOrderResponseDto.builder().build());

        // Act
        orderService.createOrder(CreateOrderRequestDto.builder()
                .orderItems(List.of(OrderItemRequestDto.builder().productId(2L).quantity(null).build()))
                .build());

        // Assert
        ArgumentCaptor<List<OrderProducts>> itemsCaptor = ArgumentCaptor.forClass(List.class);
        verify(orderProductsRepository).saveAll(itemsCaptor.capture());
        assertEquals(1, itemsCaptor.getValue().get(0).getQuantity());
    }

    @Test
    void createOrder_whenProductMissing_throwsException() {
        // Arrange
        when(productRepository.findAllById(List.of(2L))).thenReturn(List.of());

        // Act and Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> orderService.createOrder(CreateOrderRequestDto.builder()
                        .orderItems(List.of(OrderItemRequestDto.builder().productId(2L).build()))
                        .build()));

        assertEquals("Product not found with id: 2", exception.getMessage());
    }

    @Test
    void updateOrder_status_updatesAndSavesOrder() {
        // Arrange
        Order order = Order.builder().status(OrderStatus.PENDING).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderAdapter.mapToGetOrderResponseDto(order)).thenReturn(GetOrderResponseDto.builder().build());

        // Act
        orderService.updateOrder(1L, UpdateOrderRequestDto.builder().status(OrderStatus.DELIVERED).build());

        // Assert
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrder_whenOrderMissing_throwsException() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> orderService.updateOrder(1L, UpdateOrderRequestDto.builder().build()));

        assertEquals("Order not found with id: 1", exception.getMessage());
    }

    @Test
    void updateOrder_incrementAction_increasesExistingQuantity() {
        // Arrange
        Order order = Order.builder().status(OrderStatus.PENDING).build();
        Product product = product(2L, "Laptop", "10.00");
        OrderProducts item = OrderProducts.builder().order(order).product(product).quantity(2).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findAllById(List.of(2L))).thenReturn(List.of(product));
        when(orderProductsRepository.findByOrderWithProduct(order)).thenReturn(List.of(item));
        when(orderAdapter.mapToGetOrderResponseDto(order)).thenReturn(GetOrderResponseDto.builder().build());

        // Act
        orderService.updateOrder(1L, UpdateOrderRequestDto.builder()
                .orderItems(List.of(OrderItemActionDto.builder().productId(2L)
                        .action(OrderItemAction.INCREMENT).build()))
                .build());

        assertEquals(3, item.getQuantity());
        verify(orderProductsRepository).saveAll(List.of(item));
    }

    @Test
    void updateOrder_removeAction_whenItemDoesNotExist_throwsException() {
        // Arrange
        Order order = Order.builder().status(OrderStatus.PENDING).build();
        Product product = product(2L, "Laptop", "10.00");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findAllById(List.of(2L))).thenReturn(List.of(product));
        when(orderProductsRepository.findByOrderWithProduct(order)).thenReturn(List.of());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrder(1L,
                UpdateOrderRequestDto.builder().orderItems(List.of(OrderItemActionDto.builder()
                        .productId(2L).action(OrderItemAction.REMOVE).build())).build()));
    }

    @Test
    void updateOrder_incrementAction_whenItemDoesNotExist_throwsException() {
        // Arrange
        Order order = Order.builder().status(OrderStatus.PENDING).build();
        Product product = product(2L, "Laptop", "10.00");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findAllById(List.of(2L))).thenReturn(List.of(product));
        when(orderProductsRepository.findByOrderWithProduct(order)).thenReturn(List.of());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrder(1L,
                UpdateOrderRequestDto.builder().orderItems(List.of(OrderItemActionDto.builder()
                        .productId(2L).action(OrderItemAction.INCREMENT).build())).build()));
    }

    @Test
    void updateOrder_addAction_increasesExistingQuantity() {
        // Arrange
        Order order = Order.builder().status(OrderStatus.PENDING).build();
        Product product = product(2L, "Laptop", "10.00");
        OrderProducts item = OrderProducts.builder().order(order).product(product).quantity(2).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findAllById(List.of(2L))).thenReturn(List.of(product));
        when(orderProductsRepository.findByOrderWithProduct(order)).thenReturn(List.of(item));
        when(orderAdapter.mapToGetOrderResponseDto(order)).thenReturn(GetOrderResponseDto.builder().build());

        // Act
        orderService.updateOrder(1L, UpdateOrderRequestDto.builder().orderItems(List.of(
                OrderItemActionDto.builder().productId(2L).quantity(3).action(OrderItemAction.ADD).build()))
                .build());

        // Assert
        assertEquals(5, item.getQuantity());
        verify(orderProductsRepository).saveAll(List.of(item));
    }

    @Test
    void updateOrder_addAction_forNewProduct_createsItem() {
        // Arrange
        Order order = Order.builder().status(OrderStatus.PENDING).build();
        Product product = product(2L, "Laptop", "10.00");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findAllById(List.of(2L))).thenReturn(List.of(product));
        when(orderProductsRepository.findByOrderWithProduct(order)).thenReturn(List.of());
        when(orderAdapter.mapToGetOrderResponseDto(order)).thenReturn(GetOrderResponseDto.builder().build());

        // Act
        orderService.updateOrder(1L, UpdateOrderRequestDto.builder().orderItems(List.of(
                OrderItemActionDto.builder().productId(2L).quantity(2).action(OrderItemAction.ADD).build()))
                .build());

        // Assert
        ArgumentCaptor<List<OrderProducts>> itemsCaptor = ArgumentCaptor.forClass(List.class);
        verify(orderProductsRepository).saveAll(itemsCaptor.capture());
        assertEquals(2, itemsCaptor.getValue().get(0).getQuantity());
        assertEquals(product, itemsCaptor.getValue().get(0).getProduct());
    }

    @Test
    void updateOrder_removeAction_deletesExistingItem() {
        // Arrange
        Order order = Order.builder().status(OrderStatus.PENDING).build();
        Product product = product(2L, "Laptop", "10.00");
        OrderProducts item = OrderProducts.builder().order(order).product(product).quantity(2).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findAllById(List.of(2L))).thenReturn(List.of(product));
        when(orderProductsRepository.findByOrderWithProduct(order)).thenReturn(List.of(item));
        when(orderAdapter.mapToGetOrderResponseDto(order)).thenReturn(GetOrderResponseDto.builder().build());

        // Act
        orderService.updateOrder(1L, UpdateOrderRequestDto.builder().orderItems(List.of(
                OrderItemActionDto.builder().productId(2L).action(OrderItemAction.REMOVE).build()))
                .build());

        // Assert
        verify(orderProductsRepository).deleteAll(List.of(item));
    }

    @Test
    void updateOrder_decrementAction_whenQuantityAboveOne_decreasesQuantity() {
        // Arrange
        Order order = Order.builder().status(OrderStatus.PENDING).build();
        Product product = product(2L, "Laptop", "10.00");
        OrderProducts item = OrderProducts.builder().order(order).product(product).quantity(2).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findAllById(List.of(2L))).thenReturn(List.of(product));
        when(orderProductsRepository.findByOrderWithProduct(order)).thenReturn(List.of(item));
        when(orderAdapter.mapToGetOrderResponseDto(order)).thenReturn(GetOrderResponseDto.builder().build());

        // Act
        orderService.updateOrder(1L, UpdateOrderRequestDto.builder().orderItems(List.of(
                OrderItemActionDto.builder().productId(2L).action(OrderItemAction.DECREMENT).build()))
                .build());

        // Assert
        assertEquals(1, item.getQuantity());
        verify(orderProductsRepository).saveAll(List.of(item));
    }

    @Test
    void updateOrder_decrementAction_whenItemDoesNotExist_throwsException() {
        // Arrange
        Order order = Order.builder().status(OrderStatus.PENDING).build();
        Product product = product(2L, "Laptop", "10.00");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findAllById(List.of(2L))).thenReturn(List.of(product));
        when(orderProductsRepository.findByOrderWithProduct(order)).thenReturn(List.of());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrder(1L,
                UpdateOrderRequestDto.builder().orderItems(List.of(OrderItemActionDto.builder()
                        .productId(2L).action(OrderItemAction.DECREMENT).build())).build()));
    }

    @Test
    void updateOrder_decrementAction_whenQuantityIsOne_deletesItem() {
        // Arrange
        Order order = Order.builder().status(OrderStatus.PENDING).build();
        Product product = product(2L, "Laptop", "10.00");
        OrderProducts item = OrderProducts.builder().order(order).product(product).quantity(1).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findAllById(List.of(2L))).thenReturn(List.of(product));
        when(orderProductsRepository.findByOrderWithProduct(order)).thenReturn(List.of(item));
        when(orderAdapter.mapToGetOrderResponseDto(order)).thenReturn(GetOrderResponseDto.builder().build());

        // Act
        orderService.updateOrder(1L, UpdateOrderRequestDto.builder().orderItems(List.of(
                OrderItemActionDto.builder().productId(2L).action(OrderItemAction.DECREMENT).build()))
                .build());

        // Assert
        verify(orderProductsRepository).deleteAll(List.of(item));
    }

    @Test
    void updateOrder_whenProductMissing_throwsException() {
        // Arrange
        Order order = Order.builder().status(OrderStatus.PENDING).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findAllById(List.of(2L))).thenReturn(List.of());

        // Act and Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> orderService.updateOrder(1L, UpdateOrderRequestDto.builder().orderItems(List.of(
                        OrderItemActionDto.builder().productId(2L).action(OrderItemAction.ADD).build()))
                        .build()));

        assertEquals("Product not found with id: 2", exception.getMessage());
    }

    @Test
    void getOrderSummary_calculatesTotals() {
        // Arrange
        Order order = Order.builder().status(OrderStatus.PENDING).build();
        order.setId(1L);
        Product product = product(2L, "Laptop", "10.00");
        OrderProducts item = OrderProducts.builder().order(order).product(product).quantity(3).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderProductsRepository.findByOrderWithProduct(order)).thenReturn(List.of(item));
        when(orderAdapter.mapToOrderItemResponseDto(List.of(item)))
                .thenReturn(List.of(OrderItemResponseDto.builder().build()));

        // Act
        GetOrderSummaryResponseDto result = orderService.getOrderSummary(1L);

        // Assert
        assertEquals(3, result.getTotalItems());
        assertEquals(new BigDecimal("30.00"), result.getTotalPrice());
        assertEquals(1L, result.getId());
    }

    @Test
    void getOrderSummary_whenOrderHasNoItems_returnsZeroTotals() {
        // Arrange
        Order order = Order.builder().status(OrderStatus.PENDING).build();
        order.setId(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderProductsRepository.findByOrderWithProduct(order)).thenReturn(List.of());
        when(orderAdapter.mapToOrderItemResponseDto(List.of())).thenReturn(List.of());

        // Act
        GetOrderSummaryResponseDto result = orderService.getOrderSummary(1L);

        // Assert
        assertEquals(0, result.getTotalItems());
        assertEquals(BigDecimal.ZERO, result.getTotalPrice());
        assertEquals(List.of(), result.getOrderItems());
    }

    @Test
    void getOrderSummary_whenOrderMissing_throwsException() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> orderService.getOrderSummary(1L));

        assertEquals("Order not found with id: 1", exception.getMessage());
    }

    private Product product(Long id, String title, String price) {
        Product product = Product.builder().title(title).price(new BigDecimal(price)).build();
        product.setId(id);
        return product;
    }
}