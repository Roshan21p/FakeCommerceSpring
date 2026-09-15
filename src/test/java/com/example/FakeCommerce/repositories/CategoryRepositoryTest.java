package com.example.FakeCommerce.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(TestJpaAuditingConfig.class)
class CategoryRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category electronics;

    @BeforeEach
    void setup() {
        // Arrange
        electronics = Category.builder()
                .name("Electronics")
                .build();

        // persistAndFlush() saves the category to the test database immediately.
        // This makes sure the repository query can read the row in the same test.
        testEntityManager.persistAndFlush(electronics);
        testEntityManager.clear();
    }

    @Test
    void findByName_whenCategoryExists_returnsCategory() {
        // Act
        Category result = categoryRepository.findByName("Electronics");

        // Assert
        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        assertEquals(electronics.getId(), result.getId());
    }

    @Test
    void findByName_whenCategoryDoesNotExist_returnsNull() {
        // Act
        Category result = categoryRepository.findByName("Books");

        // Assert
        assertNull(result);
    }

    @Test
    void saveAndFindAll_whenCategoryIsSaved_returnsPersistedCategories() {
        // Arrange
        Category clothing = Category.builder().name("Clothing").build();

        // Act
        categoryRepository.save(clothing);
        List<Category> result = categoryRepository.findAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Electronics", result.get(0).getName());
    }
}
