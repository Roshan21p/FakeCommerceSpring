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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.FakeCommerce.dtos.CreateProductRequestDto;
import com.example.FakeCommerce.dtos.GetProductResponseDto;
import com.example.FakeCommerce.dtos.GetProductWithDetailsResponseDto;
import com.example.FakeCommerce.schema.Product;
import com.example.FakeCommerce.services.ProductService;
import com.example.FakeCommerce.utils.ApiResponse;

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
 * GET /api/v1/products
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
 * this.productService = productService;
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
     * List<GetProductResponseDto> as JSON.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<GetProductResponseDto>>>  getAllProducts() {

        List<GetProductResponseDto> products = productService.getAllProducts();
        return ResponseEntity
                .ok(ApiResponse.success("Products fetched successfully", products));
    }

    /*
     * @GetMapping("/{id}/details")
     *
     * Handles HTTP GET requests.
     *
     * URL:
     * GET /api/v1/products/1/details
     *
     * {id} is a Path Variable.
     *
     * Example:
     * GET /api/v1/products/5/details
     *
     * Here,
     * id = 5
     *
     * Calls:
     * productService.getProductWithDetails(id)
     *
     * Returns:
     * GetProductWithDetailsResponseDto as JSON.
     *
     * This endpoint returns complete product details,
     * including additional information such as category,
     * rating, description, etc.
     */
    @GetMapping("/{id}/details")
    public ResponseEntity<ApiResponse<GetProductWithDetailsResponseDto>> getProductWithDetails(
            @PathVariable Long id) {

        GetProductWithDetailsResponseDto productDetails = productService.getProductWithDetails(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Product details fetched successfully", productDetails));
    }

    /*
     * @GetMapping("/{id}")
     *
     * Handles HTTP GET requests.
     *
     * URL:
     * GET /api/v1/products/1
     *
     * {id} is a Path Variable.
     *
     * Example:
     * GET /api/v1/products/10
     *
     * Here,
     * id = 10
     *
     * Calls:
     * productService.getProductById(id)
     *
     * Returns:
     * GetProductResponseDto as JSON.
     *
     * This endpoint returns basic product information
     * for the specified product ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GetProductResponseDto>> getProductById(
            @PathVariable Long id) {

        GetProductResponseDto product = productService.getProductById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Product fetched successfully", product));
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
     * "title":"Laptop",
     * "description":"Gaming Laptop",
     * "price":50000,
     * "image":"image.jpg",
     * "category":"Electronics",
     * "rating":"4.8"
     * }
     *
     * Spring automatically converts this JSON
     * into a CreateProductRequestDto object.
     */
    public ResponseEntity<ApiResponse<Product>> createProduct(@RequestBody CreateProductRequestDto requestDto) {

        /*
         * Calls the service layer to create
         * and save the product.
         *
         * Returns the saved Product as JSON.
         */
        Product savedProduct = productService.createProduct(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", savedProduct));
    }

    /*
     * @DeleteMapping("/{id}")
     *
     * Maps HTTP DELETE requests to this method.
     *
     * URL Example:
     * DELETE /api/v1/products/5
     *
     * "{id}" is a Path Variable.
     * The value in the URL is passed to the method parameter.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(

            /*
             * @PathVariable
             *
             * Extracts the value from the URL path.
             *
             * Example:
             * URL:
             * DELETE /api/v1/products/10
             *
             * Here,
             * id = 10
             */
            @PathVariable Long id) {

        // Calls the service layer to delete the product.
        productService.deleteProduct(id);
       return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Product deleted successfully", null));
    }

    /*
     * Another way to pass data is using @PathVariable.
     *
     * URL Example:
     * GET /api/v1/products/category/Electronics
     *
     * Here,
     * category = "Electronics"
     */

    // @GetMapping("/category/{category}")
    // public List<Product> getProductsByCategory(
    // @PathVariable String category) {
    //
    // return productService.getProductsByCategory(category);
    // }

    /*
     * @GetMapping("/search")
     *
     * Handles GET requests.
     *
     * URL:
     * GET /api/v1/products/search?categoryName=Electronics
     *
     * Query Parameter:
     * categoryName=Electronics
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByCategory(

            /*
             * @RequestParam
             *
             * Reads data from the query parameters.
             *
             * Example:
             * GET /search?categoryName=Electronics
             *
             * category = "Electronics"
             */
            @RequestParam("categoryName") String category) {

        // Calls the service layer to fetch products
        // belonging to the given category.
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity   
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Products fetched successfully", products));
    }

    // Write an api to get all the unique categories from the products table. The
    // api should be a get api and the url should be /api/v1/products/categories.
    // The response should be a list of strings containing the unique categories.
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getAllCategories() {
        List<String> categories = productService.getAllCategories();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Categories fetched successfully", categories));
    }
}