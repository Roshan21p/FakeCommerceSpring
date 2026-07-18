package com.example.FakeCommerce.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.FakeCommerce.dtos.CreateProductRequestDto;
import com.example.FakeCommerce.schema.Product;
import com.example.FakeCommerce.services.ProductService;

import lombok.RequiredArgsConstructor;

/*
 * @RestController
 *
 * Marks this class as a REST Controller.
 *
 * It is a combination of:
 *
 * @Controller
 * +
 * @ResponseBody
 *
 * Every method returns JSON instead of a View (HTML).
 *
 * Example:
 * GET /api/v1/products
 *
 * Response:
 * [
 *   {
 *     "id":1,
 *     "title":"Laptop",
 *     "price":50000
 *   }
 * ]
 */
@RestController

/*
 * @RequestMapping
 *
 * Defines the base URL for every API inside this controller.
 *
 * Base URL:
 * /api/v1/products
 *
 * Examples:
 * GET  /api/v1/products
 * POST /api/v1/products
 */
@RequestMapping("/api/v1/products")

/*
 * @RequiredArgsConstructor
 *
 * Generates a constructor for all final fields.
 *
 * Generated constructor:
 *
 * public ProductController(ProductService productService){
 *     this.productService = productService;
 * }
 *
 * Spring automatically injects the ProductService object.
 */
@RequiredArgsConstructor
public class ProductController {

    /*
     * ProductService contains the business logic.
     *
     * Spring injects this dependency through the generated constructor.
     */
    private final ProductService productService;

    /*
     * @GetMapping
     *
     * Handles HTTP GET requests.
     *
     * URL:
     * GET /api/v1/products
     *
     * Calls:
     * productService.getAllProducts()
     *
     * Returns:
     * List<Product> as JSON.
     */
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    /*
     * @PostMapping
     *
     * Handles HTTP POST requests.
     *
     * URL:
     * POST /api/v1/products
     *
     * Used to create a new product.
     */
    @PostMapping

    /*
     * @RequestBody
     *
     * Converts the incoming JSON request body
     * into a Java object (CreateProductRequestDto).
     *
     * Request JSON:
     *
     * {
     *   "title":"Laptop",
     *   "description":"Gaming Laptop",
     *   "price":50000,
     *   "image":"image.jpg",
     *   "category":"Electronics",
     *   "rating":"4.8"
     * }
     *
     * Spring automatically converts this JSON
     * into a CreateProductRequestDto object.
     */
    public Product createProduct(@RequestBody CreateProductRequestDto requestDto) {

        /*
         * Calls the service layer to create
         * and save the product.
         *
         * Returns the saved Product as JSON.
         */
        return productService.createProduct(requestDto);
    }
}