package com.example.FakeCommerce.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.FakeCommerce.dtos.CreateCategoryRequestDto;
import com.example.FakeCommerce.exceptions.ResourceNotFoundException;
import com.example.FakeCommerce.repositories.CategoryRepository;
import com.example.FakeCommerce.schema.Category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


/*
 * @ExtendWith(MockitoExtension.class)
 *
 * This enables Mockito in our JUnit test.
 *
 * It allows us to use:
 * @Mock
 * @InjectMocks
 */
@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    /*
     * @Mock
     *
     * Creates a fake CategoryRepository.
     *
     * This means:
     * - We do NOT connect to the real database.
     * - We do NOT execute a real INSERT query.
     * - Mockito controls the repository behavior.
     */
    @Mock
    private CategoryRepository categoryRepository;


    /*
     * @InjectMocks
     *
     * Creates CategoryService and injects the mocked
     * CategoryRepository into it.
     *
     * So the flow becomes:
     *
     * CategoryService
     *       ↓
     * Mock CategoryRepository
     *
     * instead of:
     *
     * CategoryService
     *       ↓
     * Real CategoryRepository
     *       ↓
     * Database
     */
    @InjectMocks
    private CategoryService categoryService;


    /*
     * Test:
     *
     * createCategory_savesAndReturnsCategory()
     *
     * We are testing the createCategory() method
     * of CategoryService.
     */
    @Test
    void createCategory_savesAndReturnsCategory() {

        /*
         * ========================================================
         * ARRANGE
         * ========================================================
         *
         * Arrange means:
         * Prepare everything required for the test.
         */


        /*
         * Create the request DTO.
         *
         * This is what our service will receive.
         *
         * dto:
         * name = "Test Category"
         */
        CreateCategoryRequestDto dto =
                CreateCategoryRequestDto.builder()
                        .name("Test Category")
                        .build();


        /*
         * Create the Category object that we want
         * our mocked repository to return.
         *
         * Normally, the database would generate the ID.
         *
         * Since we are NOT using the real database,
         * we manually set the ID to 1L.
         */
        Category testCategory =
                Category.builder()
                        .name("Test Category")
                        .build();

        testCategory.setId(1L);


        /*
         * Tell Mockito what should happen when
         * categoryRepository.save() is called.
         *
         * any(Category.class)
         * means:
         * "If save() receives any Category object..."
         *
         * thenReturn(testCategory)
         * means:
         * "...return this testCategory object."
         *
         * Without this line, Mockito would return null
         * by default from save().
         */
        when(categoryRepository.save(any(Category.class)))
                .thenReturn(testCategory);


        /*
         * ========================================================
         * ACT
         * ========================================================
         *
         * Act means:
         * Call the actual method that we want to test.
         *
         * This calls our real CategoryService method.
         */
        Category result = categoryService.createCategory(dto);


        /*
         * ========================================================
         * ASSERT
         * ========================================================
         *
         * Assert means:
         * Check whether the result is what we expected.
         */


        /*
         * Check that the returned Category
         * has the expected name.
         *
         * Expected: "Test Category"
         * Actual:   result.getName()
         */
        assertEquals("Test Category", result.getName());


        /*
         * Check that the returned Category
         * has the expected ID.
         *
         * Expected: 1L
         * Actual:   result.getId()
         */
        assertEquals(1L, result.getId());
    }

    @Test 
    void getCategoryById_whenCategoryExists_returnsCategory() {
        
        // Arrange
        Category testCategory = Category.builder()
                .name("Test Category")
                .build();
        testCategory.setId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // Act
        Category result = categoryService.getCategoryById(1L);

        // Assert
        assertEquals("Test Category", result.getName());
        assertEquals(1L, result.getId());
    }

    @Test
    void getCategoryById_whenCategoryDoesNotExist_throwsException() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
       ResourceNotFoundException exception = assertThrows(
  ResourceNotFoundException.class,
                             () -> categoryService.getCategoryById(1L));
        
        // Verify the exception message
        assertEquals(
            "Category not found with id: 1",
            exception.getMessage()
        );
    }

    @Test
    void getAllCategories_returnsListOfCategories() {
        // Arrange
        Category category1 = Category.builder().name("Category 1").build();
        category1.setId(1L);
        Category category2 = Category.builder().name("Category 2").build();
        category2.setId(2L);

        when(categoryRepository.findAll()).thenReturn(List.of(category1, category2));

        // Act
        List<Category> result = categoryService.getAllCategories();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Category 1", result.get(0).getName());
        assertEquals("Category 2", result.get(1).getName());
    }

    @Test
    void getAllCategories_whenNoCategoriesExist_returnEmptyList() {

        // Arrange
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Category> result = categoryService.getAllCategories();

        // Assert
        assertTrue(result.isEmpty(), "Expected an empty list when no categories exist");
    }

    @Test
    void deleteCategoryById_WhenCategoryExists_ShouldDeleteCategory() {

        // Arrange
        Category testCategory = Category.builder()
                .name("Test Category")
                .build();
        testCategory.setId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // Act
        categoryService.deleteCategory(1L);

        // Assert
       verify(categoryRepository, times(1)).delete(testCategory);
    }

    @Test 
    void deleteCategoryById_WhenCategoryDoesNotExist_ShouldThrowException() {

        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> categoryService.deleteCategory(1L)
        );
        
        // Verify the exception message
        assertEquals(
            "Category not found with id: 1",
            exception.getMessage()
        );
    }
}   