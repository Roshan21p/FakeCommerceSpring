package com.example.FakeCommerce.integration;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.FakeCommerce.dtos.CreateCategoryRequestDto;
import com.example.FakeCommerce.dtos.CreateOrderRequestDto;
import com.example.FakeCommerce.dtos.CreateProductRequestDto;
import com.example.FakeCommerce.dtos.OrderItemRequestDto;
import com.example.FakeCommerce.dtos.UpdateOrderRequestDto;
import com.example.FakeCommerce.schema.OrderStatus;
import com.example.FakeCommerce.services.cache.ProductRedisCache;

import tools.jackson.databind.ObjectMapper;

/*
 * @SpringBootTest loads the complete Spring Boot application context instead
 * of loading only one controller or one repository. This allows the test to
 * exercise the real controllers, services, repositories, JPA mappings, and
 * exception handling together.
 *
 * WebEnvironment.MOCK creates Spring's mock servlet environment. Requests are
 * processed through MVC without starting an actual HTTP server or opening a
 * network port.
 *
 * The properties disable Redis auto-configuration because this integration
 * test replaces ProductRedisCache with a Mockito bean. H2 is used through the
 * test application configuration for isolated database state.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = {
        "spring.autoconfigure.exclude=" +
                "org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.redis.DataRedisReactiveAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration",
        "spring.main.allow-bean-definition-overriding=true"
})

/*
 * @AutoConfigureMockMvc creates and configures MockMvc for the full
 * application context. It lets the test send realistic GET, POST, PUT, and
 * DELETE requests through controller routing, JSON conversion, and response
 * handling without starting a server.
 */
@AutoConfigureMockMvc
public class EcomFlowIntegrationTest {

    /*
     * @Autowired asks Spring to inject the configured MockMvc bean into this
     * field. The test can then execute requests against the application MVC
     * layer instead of constructing controllers manually.
     */
    @Autowired
    private MockMvc mockMvc;

    /*
     * @Autowired injects the application's configured Jackson-compatible
     * ObjectMapper. Using the application mapper ensures request and response
     * JSON is serialized consistently with production configuration.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /*
     * @MockitoBean replaces the real ProductRedisCache Spring bean with a
     * Mockito mock in this test application context. Database operations and
     * product business logic remain real, while the test does not require a
     * running Redis server.
     */
    @MockitoBean
    private ProductRedisCache productRedisCache;

    /*
     * @BeforeEach runs before every test method. It resets the expected cache
     * behavior for each test so every product cache lookup behaves as a cache
     * miss and the flow reads product data from the real H2 database.
     */
    @BeforeEach
    void setup() {
        // ProductService expects Optional.empty() for a Redis cache miss.
        when(productRedisCache.getSummary(anyLong())).thenReturn(Optional.empty());
        when(productRedisCache.getAllProducts()).thenReturn(Optional.empty());
        when(productRedisCache.getProductWithDetails(anyLong())).thenReturn(Optional.empty());
    }

    private Long extractId(MvcResult result) throws Exception {
        String jsonObject = result.getResponse().getContentAsString();
        return objectMapper.readTree(jsonObject).path("data").path("id").asLong();
    }

    /*
     * @Test marks this method as an executable JUnit test. JUnit runs the
     * complete ecommerce workflow and reports any failed HTTP assertion or
     * unexpected exception.
     */
    @Test
    void testFullEcommerceFlow() throws Exception {

        // 1. Create a category
        String categoryJson = objectMapper.writeValueAsString(
                CreateCategoryRequestDto.builder().name("Electronics").build());

        MvcResult categoryResult = mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(categoryJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Electronics"))
                .andReturn();

        Long categoryId = extractId(categoryResult);

        // ── 2. Verify category retrieval ──

        mockMvc.perform(get("/api/v1/categories/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Electronics"));

        // ── 3. Create 3 products ──

        Long productAId = createProduct("Laptop", "High-end laptop", "laptop.jpg",
                new BigDecimal("999.99"), categoryId, new BigDecimal("4.5"));

        Long productBId = createProduct("Phone", "Flagship phone", "phone.jpg",
                new BigDecimal("499.99"), categoryId, new BigDecimal("4.2"));

        Long productCId = createProduct("Tablet", "Portable tablet", "tablet.jpg",
                new BigDecimal("299.99"), categoryId, new BigDecimal("4.0"));

        // ── 4. Verify each product by ID and the full list ──

        mockMvc.perform(get("/api/v1/products/{id}", productAId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Laptop"));

        mockMvc.perform(get("/api/v1/products/{id}", productBId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Phone"));

        mockMvc.perform(get("/api/v1/products/{id}", productCId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Tablet"));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3));

        // ── 5. Create an order with all 3 products (different quantities) ──

        CreateOrderRequestDto orderRequest = CreateOrderRequestDto.builder()
                .orderItems(List.of(
                        OrderItemRequestDto.builder().productId(productAId).quantity(2).build(),
                        OrderItemRequestDto.builder().productId(productBId).quantity(1).build(),
                        OrderItemRequestDto.builder().productId(productCId).quantity(3).build()))
                .build();

        MvcResult orderResult = mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.orderItems.length()").value(3))
                .andReturn();

        Long orderId = extractId(orderResult);

        // ── 6. Verify order by ID ──

        mockMvc.perform(get("/api/v1/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.orderItems.length()").value(3));

        // ── 7. Fetch order summary and verify totals ──
        // totalItems = 2 + 1 + 3 = 6
        // totalPrice = (999.99 * 2) + (499.99 * 1) + (299.99 * 3) = 1999.98 + 499.99 +
        // 899.97 = 3399.94

        mockMvc.perform(get("/api/v1/orders/{id}/summary", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.totalItems").value(6))
                .andExpect(jsonPath("$.data.totalPrice").value(3399.94));

        // ── 8. Update order status to SHIPPED ──

        UpdateOrderRequestDto updateRequest = UpdateOrderRequestDto.builder()
                .status(OrderStatus.SHIPPED)
                .build();

        mockMvc.perform(put("/api/v1/orders/{id}", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SHIPPED"));

        // ── 9. Fetch summary again — status updated, totals unchanged ──

        mockMvc.perform(get("/api/v1/orders/{id}/summary", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SHIPPED"))
                .andExpect(jsonPath("$.data.totalItems").value(6))
                .andExpect(jsonPath("$.data.totalPrice").value(3399.94));

        // ── 10. Delete the order and verify it's gone ──

        mockMvc.perform(delete("/api/v1/orders/{id}", orderId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/orders/{id}", orderId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));

        // ── 11. Delete each product and verify ──

        for (Long productId : List.of(productAId, productBId, productCId)) {
            mockMvc.perform(delete("/api/v1/products/{id}", productId))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/api/v1/products/{id}", productId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }

        // ── 12. Delete the category and verify ──

        mockMvc.perform(delete("/api/v1/categories/{id}", categoryId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/categories/{id}", categoryId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    private Long createProduct(String title, String description, String image,
            BigDecimal price, Long categoryId, BigDecimal rating) throws Exception {
        CreateProductRequestDto dto = CreateProductRequestDto.builder()
                .title(title)
                .description(description)
                .image(image)
                .price(price)
                .categoryId(categoryId)
                .rating(rating)
                .build();

        MvcResult result = mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();

        return extractId(result);
    }

}
