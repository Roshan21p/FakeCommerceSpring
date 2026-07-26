package com.example.FakeCommerce.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.FakeCommerce.dtos.CreateCategoryRequestDto;
import com.example.FakeCommerce.schema.Category;
import com.example.FakeCommerce.services.CategoryService;
import com.example.FakeCommerce.utils.ApiResponse;

import lombok.RequiredArgsConstructor;

/*
 * @RestController
 *
 * Marks this class as a REST Controller.
 *
 * It handles HTTP requests and
 * returns JSON responses.
 */
@RestController

/*
 * @RequestMapping
 *
 * Defines the base URL
 * for all APIs in this controller.
 *
 * Base URL:
 * /api/v1/categories
 */
@RequestMapping("/api/v1/categories")

/*
 * @RequiredArgsConstructor
 *
 * Generates a constructor for
 * all final fields.
 *
 * Spring uses this constructor
 * for Dependency Injection.
 */
@RequiredArgsConstructor
public class CategoryController {

    /*
     * CategoryService contains
     * the business logic.
     *
     * Spring automatically injects
     * its object.
     */
    private final CategoryService categoryService;

    /*
     * @PostMapping
     *
     * Handles HTTP POST requests.
     *
     * URL:
     * POST /api/v1/categories
     *
     * Creates a new Category.
     */
    // ResponseEntity is used to customize the HTTP response.
    @PostMapping
    public ResponseEntity<ApiResponse<Category>> createCategory(@RequestBody CreateCategoryRequestDto requestDto) {

        Category category = categoryService.createCategory(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", category));
    }

    /*
     * @GetMapping
     *
     * Handles HTTP GET requests.
     *
     * URL:
     * GET /api/v1/categories
     *
     * Returns all categories.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Categories fetched successfully", categories));
    }

    /*
     * @GetMapping("/{id}")
     *
     * Handles GET requests
     * using Category ID.
     *
     * Example:
     *
     * GET /api/v1/categories/1
     */
    @GetMapping("/{id}")
    public  ResponseEntity<ApiResponse<Category>> getCategoryById(

            /*
             * @PathVariable
             *
             * Reads the ID
             * from the URL.
             *
             * Example:
             *
             * URL:
             * /categories/5
             *
             * id = 5
             */
            @PathVariable Long id) {

        Category category = categoryService.getCategoryById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Category fetched successfully", category));
    }

    /*
     * @DeleteMapping("/{id}")
     *
     * Handles HTTP DELETE requests.
     *
     * Example:
     *
     * DELETE /api/v1/categories/3
     *
     * Deletes the Category
     * having the given ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(

            /*
             * Reads ID from
             * the URL path.
             */
            @PathVariable Long id) {

        categoryService.deleteCategory(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Category deleted successfully", null));
    }
}