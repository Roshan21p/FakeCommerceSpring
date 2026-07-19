package com.example.FakeCommerce.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.FakeCommerce.dtos.CreateCategoryRequestDto;
import com.example.FakeCommerce.repositories.CategoryRepository;
import com.example.FakeCommerce.schema.Category;

import lombok.RequiredArgsConstructor;

/*
 * @Service
 *
 * Marks this class as a Service Layer.
 *
 * The Service Layer contains the
 * business logic of the application.
 *
 * It acts as a bridge between
 * Controller and Repository.
 */
@Service

/*
 * @RequiredArgsConstructor
 *
 * Generates a constructor for all
 * final fields.
 *
 * Spring uses it for
 * Dependency Injection.
 */
@RequiredArgsConstructor
public class CategoryService {

    /*
     * CategoryRepository is responsible
     * for performing database operations.
     *
     * Spring automatically injects
     * its object.
     */
    private final CategoryRepository categoryRepository;

    /*
     * ============================================================
     * Method:
     * createCategory()
     * ============================================================
     *
     * Creates and saves a new Category.
     *
     * Flow:
     *
     * Client
     * │
     * ▼
     * Request DTO
     * │
     * ▼
     * Category Entity
     * │
     * ▼
     * Repository.save()
     * │
     * ▼
     * Database
     */
    public Category createCategory(CreateCategoryRequestDto requestDto) {

        /*
         * Builder Pattern
         *
         * Creates a Category object
         * from the Request DTO.
         */
        Category category = Category.builder()
                .name(requestDto.getName())
                .build();

        /*
         * save()
         *
         * Built-in JpaRepository method.
         *
         * If ID is null
         * ↓
         * INSERT
         *
         * If ID exists
         * ↓
         * UPDATE
         */
        return categoryRepository.save(category);

    }

    /*
     * ============================================================
     * Method:
     * getAllCategories()
     * ============================================================
     *
     * Fetches all categories
     * from the database.
     *
     * SQL:
     *
     * SELECT *
     * FROM categories;
     */
    public List<Category> getAllCategories() {

        /*
         * findAll()
         *
         * Built-in JpaRepository method.
         *
         * Returns every row
         * from the categories table.
         */
        return categoryRepository.findAll();
    }

    /*
     * ============================================================
     * Method:
     * getCategoryById()
     * ============================================================
     *
     * Fetches one Category
     * using its Primary Key.
     *
     * Example:
     *
     * GET /categories/1
     */
    public Category getCategoryById(Long id) {

        /*
         * findById()
         *
         * Returns Optional<Category>.
         *
         * If category exists
         * ↓
         * Return Category
         *
         * Otherwise
         * ↓
         * Throw Exception.
         */
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    /*
     * ============================================================
     * Method:
     * deleteCategory()
     * ============================================================
     *
     * Deletes a Category
     * using its ID.
     *
     * SQL:
     *
     * DELETE
     * FROM categories
     * WHERE id = ?;
     */
    public void deleteCategory(Long id) {

        /*
         * deleteById()
         *
         * Built-in JpaRepository method.
         *
         * Deletes the record
         * having the given ID.
         */
        categoryRepository.deleteById(id);
    }

}