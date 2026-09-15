package com.example.FakeCommerce.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.example.FakeCommerce.schema.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(TestJpaAuditingConfig.class)
public class ProductRepositoryTest {

    // TestEntityManager is a Spring Boot test helper for JPA tests.
    // It lets us save entities to the in-memory test database without starting the
    // full app.
    @Autowired
    private TestEntityManager testEntityManager;

    // ProductRepository is the actual repository being tested.
    // We will call its custom query methods and validate the database result.
    @Autowired
    private ProductRepository productRepository;

    private Product product;
    private Category category;

    @BeforeEach
    void setup() {
        // Arrange
        category = Category.builder()
                .name("Electronics")
                .build();

        product = Product.builder()
                .title("Smartphone")
                .description("A high-end smartphone")
                .price(BigDecimal.valueOf(999.99).setScale(2, RoundingMode.HALF_UP))
                .rating(BigDecimal.valueOf(4.5).setScale(2, RoundingMode.HALF_UP))
                .category(category)
                .build();

        // persistAndFlush() saves the entity into the test database and immediately
        // flushes SQL.
        // This ensures the data is really stored before we query it.
        testEntityManager.persistAndFlush(category);
        testEntityManager.persistAndFlush(product);

        // clear() detaches all managed entities from the persistence context.
        // This forces Hibernate to fetch fresh data from the database in the next
        // query.
        testEntityManager.clear();
    }

    @Test
    void findProductWithDetailsById_ShouldReturnProductWithCategory() {
        // Act
        List<Product> products = productRepository.findProductWithDetailsById(product.getId());

        // Assert
        assertEquals(1, products.size());
        assertEquals(category, products.get(0).getCategory());
        assertEquals(product.getTitle(), products.get(0).getTitle());
        assertEquals(product.getDescription(), products.get(0).getDescription());
        assertEquals(product.getPrice(), products.get(0).getPrice());
        assertEquals(product.getRating(), products.get(0).getRating());
        assertEquals(product.getImage(), products.get(0).getImage());
        assertEquals(product.getCategory().getName(), products.get(0).getCategory().getName());
    }

    @Test
    void findProductWithDetailsById_whenProductDoesNotExist_returnsEmptyList() {
        // Act
        List<Product> products = productRepository.findProductWithDetailsById(999L);

        // Assert
        assertTrue(products.isEmpty());
    }

    @Test
    void findByCategory_whenProductsExist_returnsMatchingProducts() {
        // Arrange
        Category clothingCategory = Category.builder().name("Clothing").build();
        Product clothingProduct = Product.builder()
                .title("T-Shirt")
                .description("Plain t-shirt")
                .price(BigDecimal.valueOf(49.99).setScale(2, RoundingMode.HALF_UP))
                .rating(BigDecimal.valueOf(4.0).setScale(2, RoundingMode.HALF_UP))
                .category(clothingCategory)
                .build();

        // Save extra test data in the same in-memory database so we can verify
        // filtering behavior.
        // persistAndFlush() is used to insert records immediately and make them
        // queryable.
        testEntityManager.persistAndFlush(clothingCategory);
        testEntityManager.persistAndFlush(clothingProduct);

        // clear() ensures the repository query reads fresh data from the database
        // instead of cached persistence state.
        testEntityManager.clear();

        // Act
        List<Product> products = productRepository.findByCategory(category);

        // Assert
        assertEquals(1, products.size());
        assertEquals(product.getId(), products.get(0).getId());
        assertEquals("Smartphone", products.get(0).getTitle());
    }

    @Test
    void findByCategory_whenNoProductsMatch_returnsEmptyList() {
        // Arrange
        Category otherCategory = Category.builder().name("Books").build();
        // We create an unrelated category to prove that the repository returns no rows
        // for a non-matching category.
        testEntityManager.persistAndFlush(otherCategory);
        testEntityManager.clear();

        // Act
        List<Product> products = productRepository.findByCategory(otherCategory);

        // Assert
        assertTrue(products.isEmpty());
    }

    @Test
    void findAllCategories_returnsDistinctCategoryIds() {
        // Arrange
        Category secondCategory = Category.builder().name("Clothing").build();
        Product secondProduct = Product.builder()
                .title("T-Shirt")
                .description("Plain t-shirt")
                .price(BigDecimal.valueOf(49.99).setScale(2, RoundingMode.HALF_UP))
                .rating(BigDecimal.valueOf(4.0).setScale(2, RoundingMode.HALF_UP))
                .category(secondCategory)
                .build();

        // Add a second category and product to validate that the query returns distinct
        // categories from multiple rows.
        testEntityManager.persistAndFlush(secondCategory);
        testEntityManager.persistAndFlush(secondProduct);
        testEntityManager.clear();

        // Act
        List<String> categoryIds = productRepository.findAllCategories();

        // Assert
        assertEquals(2, categoryIds.size());
        assertTrue(categoryIds.contains(String.valueOf(category.getId())));
        assertTrue(categoryIds.contains(String.valueOf(secondCategory.getId())));
    }

    @Test
    void findAllCategories_whenNoProductsExist_returnsEmptyList() {
        // Arrange
        // This is a test-only cleanup step.
        // We delete the product rows directly so the native SQL query for categories
        // can be tested with zero records.
        testEntityManager.getEntityManager().createNativeQuery("DELETE FROM products").executeUpdate();
        testEntityManager.clear();

        // Act
        List<String> categoryIds = productRepository.findAllCategories();

        // Assert
        assertTrue(categoryIds.isEmpty());
    }
}
