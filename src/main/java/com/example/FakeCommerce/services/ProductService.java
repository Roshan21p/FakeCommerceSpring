package com.example.FakeCommerce.services;

/*
 * ==========================================================
 * Package: services
 * ==========================================================
 *
 * This package contains the Service Layer of the application.
 *
 * Responsibilities:
 * • Business Logic
 * • Validation
 * • Entity ↔ DTO Conversion
 * • Calls Repository Layer
 *
 * Application Architecture
 *
 * Client
 *   │
 *   ▼
 * Controller
 *   │
 *   ▼
 * Service
 *   │
 *   ▼
 * Repository
 *   │
 *   ▼
 * Database
 *
 */

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.FakeCommerce.dtos.CreateProductRequestDto;
import com.example.FakeCommerce.dtos.GetProductResponseDto;
import com.example.FakeCommerce.dtos.GetProductWithDetailsResponseDto;
import com.example.FakeCommerce.repositories.CategoryRepository;
import com.example.FakeCommerce.repositories.ProductRepository;
import com.example.FakeCommerce.schema.Category;
import com.example.FakeCommerce.schema.Product;

import lombok.RequiredArgsConstructor;

/*
 * ==========================================================
 * Imports
 * ==========================================================
 *
 * Java Imports
 *
 * List
 * ----------
 * Stores multiple Product objects.
 *
 * Collectors
 * ----------
 * Used with Stream API to convert Stream
 * back into List.
 *
 * Spring Imports
 *
 * @Service
 * ----------
 * Makes this class a Spring Bean.
 *
 * Project Imports
 *
 * DTOs
 * ----
 * Used to receive requests
 * and send responses.
 *
 * Repository
 * ----------
 * Performs Database Operations.
 *
 * Entity
 * ------
 * Represents Database Tables.
 *
 * Lombok
 * ------
 * Generates Constructor automatically.
 */

/*
 * @Service
 * Marks this class as a Service component.
 *
 * A Service contains the business logic of the application.
 * Spring automatically detects it and creates an object (Bean)
 * that can be injected wherever needed.
 * 
 *  Additional Notes
 *
 * Spring creates only ONE object
 * of ProductService during application startup.
 *
 * This object is called a Bean.
 *
 * Every Controller that needs ProductService
 * receives the SAME Bean.
 *
 * Life Cycle
 *
 * Spring Boot Starts
 *        │
 *        ▼
 * Creates ProductService Bean
 *        │
 *        ▼
 * Stores inside IoC Container
 *        │
 *        ▼
 * Inject wherever required
 */

@Service

/*
 * @RequiredArgsConstructor
 *
 * Lombok generates a constructor for all final fields.
 *
 * Generated constructor:
 *
 * public ProductService(ProductRepository productRepository) {
 * this.productRepository = productRepository;
 * }
 *
 * This enables Constructor Dependency Injection without writing
 * the constructor manually.
 */

/*
 * Why Constructor Injection?
 *
 * Constructor Injection is preferred because:
 *
 * ✔ Dependencies cannot become null.
 * ✔ Easier to test.
 * ✔ Immutable (final fields).
 * ✔ Recommended by Spring.
 *
 * Without Lombok
 *
 * public ProductService(
 * ProductRepository repository,
 * CategoryService categoryService){
 *
 * this.productRepository = repository;
 * this.categoryService = categoryService;
 * }
 *
 * Lombok generates it automatically.
 */
@RequiredArgsConstructor
public class ProductService {

    /*
     * final means the reference cannot be changed after initialization.
     *
     * Spring injects the ProductRepository bean through the generated constructor.
     *
     * ProductRepository provides built-in database operations like:
     * - save()
     * - findAll()
     * - findById()
     * - deleteById()
     */

    /*
     * Repository Layer Responsibilities
     *
     * save()
     * ----------
     * Insert or Update Product.
     *
     * findAll()
     * ----------
     * Returns every Product.
     *
     * findById()
     * ----------
     * Returns Optional<Product>.
     *
     * deleteById()
     * ----------
     * Deletes Product.
     *
     * findByCategory()
     * ----------
     * Custom Query Method.
     *
     * findAllCategories()
     * ----------
     * Custom SQL Query.
     *
     */
    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    /*
     * CategoryService
     *
     * Used to fetch Category Entity.
     *
     * Why?
     *
     * Product now stores
     *
     * Category category;
     *
     * instead of
     *
     * String category;
     *
     * Therefore before saving Product,
     * we must first fetch Category.
     *
     * Flow
     *
     * RequestDTO
     * │
     * ▼
     * categoryId
     * │
     * ▼
     * CategoryService
     * │
     * ▼
     * Category Entity
     * │
     * ▼
     * Product
     */
    private final CategoryService categoryService;

    /*
     * ==========================================================
     * Method:
     * getAllProducts()
     * ==========================================================
     *
     * Fetches all products from the database.
     *
     * productRepository.findAll()
     * is provided automatically by JpaRepository.
     * 
     * 
     * Purpose
     * -------
     * Returns every Product available
     * in the database.
     *
     * Database
     * │
     * ▼
     * List<Product>
     * │
     * ▼
     * Convert Entity
     * │
     * ▼
     * List<GetProductResponseDto>
     * │
     * ▼
     * Controller
     * │
     * ▼
     * JSON Response
     *
     */
    public List<GetProductResponseDto> getAllProducts() {
        /*
         * findAll()
         *
         * Built-in JpaRepository Method.
         *
         * Generated SQL
         *
         * SELECT *
         * FROM products;
         *
         * Hibernate executes
         * this SQL automatically.
         */

        List<Product> products = productRepository.findAll();

        /*
         * Converts List<Product> to List<GetProductResponseDto>
         *
         * Using Java Streams:
         * 1. Convert the list to a stream.
         * 2. Map each Product to GetProductResponseDto.
         * 3. Collect the results back into a list.
         */

        // List<GetProductResponseDto> productDtos = new ArrayList<>();

        // for(Product product : products) {
        // GetProductResponseDto dto = GetProductResponseDto.builder()
        // .id(product.getId())
        // .title(product.getTitle())
        // .description(product.getDescription())
        // .image(product.getImage())
        // .rating(product.getRating())
        // .price(product.getPrice())
        // .build();
        // productDtos.add(dto);
        // }

        // return productDtos;

        // Using Streams (alternative approach):
        /*
         * map()
         *
         * Converts one object into another.
         *
         * Product
         * │
         * ▼
         * DTO
         *
         * Product(id,title,...)
         *
         * becomes
         *
         * GetProductResponseDto(id,title,...)
         *
         * One Product
         * ↓
         * One DTO
         *
         */

        /*
         * Builder Pattern
         *
         * Instead of
         *
         * new GetProductResponseDto(...)
         *
         * Builder creates object step by step.
         *
         * Advantages
         *
         * ✔ Readable
         * ✔ Flexible
         * ✔ Easy to maintain
         */

        /*
         * collect(Collectors.toList())
         *
         * Stream cannot be returned directly.
         *
         * collect()
         * converts Stream
         * back into List.
         */
        return products.stream()
                .map(product -> GetProductResponseDto.builder()
                        .id(product.getId())
                        .title(product.getTitle())
                        .description(product.getDescription())
                        .image(product.getImage())
                        .rating(product.getRating())
                        .price(product.getPrice())
                        .build())
                .collect(Collectors.toList());
    }

    /*
     * ============================================================
     * Method:
     * getProductWithDetails()
     * ============================================================
     *
     * Purpose
     * -------
     * Fetches complete details of a Product.
     *
     * Unlike getProductById(), this method also
     * loads the related Category information.
     *
     * Returns:
     * GetProductWithDetailsResponseDto
     *
     * Flow
     *
     * Client
     * │
     * ▼
     * Controller
     * │
     * ▼
     * ProductService
     * │
     * ▼
     * ProductRepository
     * │
     * ▼
     * Custom JOIN Query
     * │
     * ▼
     * Product + Category
     * │
     * ▼
     * DTO
     * │
     * ▼
     * JSON Response
     */
    public GetProductWithDetailsResponseDto getProductWithDetails(Long id) {

        /*
         * findProductWithDetailsById(id)
         *
         * This is a custom Repository method.
         *
         * It executes a custom SQL/JPQL query
         * that fetches Product together with
         * its Category.
         *
         * Instead of executing multiple queries,
         * Hibernate loads everything together.
         *
         * get(0)
         *
         * The repository returns a List<Product>.
         * Since only one product is expected,
         * we retrieve the first element.
         *
         * Example:
         *
         * [
         * Product
         * ]
         *
         * becomes
         *
         * Product
         */
        Product product = productRepository.findProductWithDetailsById(id).get(0);

        /*
         * Builder Pattern
         *
         * Converts Product Entity
         * into GetProductWithDetailsResponseDto.
         *
         * Entity
         * ↓
         * DTO
         *
         * Why?
         *
         * We should not expose Entity objects
         * directly to the client.
         *
         * DTO contains only the required data.
         */
        return GetProductWithDetailsResponseDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .image(product.getImage())
                .rating(product.getRating())
                .price(product.getPrice())
                /*
                 * Product contains a Category object.
                 *
                 * Product
                 * │
                 * ▼
                 * Category
                 * │
                 * ▼
                 * name
                 *
                 * getCategory()
                 * returns Category Entity.
                 *
                 * getName()
                 * returns category name.
                 *
                 * Example
                 *
                 * Category
                 * id = 1
                 * name = Electronics
                 *
                 * Response
                 *
                 * "Electronics"
                 */
                .category(product.getCategory().getName())
                .build();
    }

    /*
     * ============================================================
     * Method:
     * getProductById()
     * ============================================================
     *
     * Purpose
     * -------
     * Returns one Product using its ID.
     *
     * URL Example
     *
     * GET /api/v1/products/5
     *
     * Returns
     *
     * GetProductResponseDto
     */
    public GetProductResponseDto getProductById(Long id) {
        /*
         * findById()
         *
         * Built-in JpaRepository method.
         *
         * SQL Generated
         *
         * SELECT *
         * FROM products
         * WHERE id = ?;
         *
         * Return Type
         *
         * Optional<Product>
         *
         * Why Optional?
         *
         * Product may or may not exist.
         */

        return productRepository.findById(id)
                /*
                 * Optional.map()
                 *
                 * If Product exists,
                 * execute this block.
                 *
                 * Product
                 * │
                 * ▼
                 * DTO
                 *
                 * If Product does NOT exist,
                 * map() is skipped.
                 */
                .map(product -> GetProductResponseDto.builder()
                        .id(product.getId())
                        .title(product.getTitle())
                        .description(product.getDescription())
                        .image(product.getImage())
                        .rating(product.getRating())
                        .price(product.getPrice())
                        .build())
                /*
                 * orElseThrow()
                 *
                 * Executes only when
                 * Optional is Empty.
                 *
                 * Example
                 *
                 * Product Exists
                 *
                 * Optional(Product)
                 * │
                 * ▼
                 * map()
                 *
                 * Product Missing
                 *
                 * Optional.empty()
                 * │
                 * ▼
                 * Exception
                 *
                 * Throws RuntimeException
                 * with message:
                 *
                 * "Product not found."
                 */
                .orElseThrow(() -> new RuntimeException("Product not found."));
    }

    /*
     * Creates a new Product.
     *
     * Steps:
     * 1. Receive CreateProductRequestDto from the Controller.
     * 2. Convert DTO into Product Entity.
     * 3. Save the Product in the database.
     * 4. Return the saved Product.
     */

    /*
     * ============================================================
     * Method:
     * createProduct()
     * ============================================================
     *
     * Purpose
     * -------
     * Creates and saves a new Product.
     *
     * Flow:
     *
     * Client
     * │
     * ▼
     * Request JSON
     * │
     * ▼
     * CreateProductRequestDto
     * │
     * ▼
     * ProductService
     * │
     * ▼
     * Fetch Category
     * │
     * ▼
     * Create Product Entity
     * │
     * ▼
     * Save Product
     * │
     * ▼
     * Database
     * │
     * ▼
     * Return Saved Product
     */
    public Product createProduct(CreateProductRequestDto requestDto) {

        /*
         * The request contains categoryId,
         * not the complete Category object.
         *
         * Example Request:
         *
         * {
         * "title":"Laptop",
         * "categoryId":1
         * }
         *
         * We first fetch the Category entity
         * using CategoryService.
         *
         * categoryId
         * │
         * ▼
         * CategoryService
         * │
         * ▼
         * Category Entity
         */
        Category category = categoryService.getCategoryById(requestDto.getCategoryId());
        /*
         * Builder Pattern
         *
         * Creates a Product object step by step.
         *
         * Advantages:
         *
         * ✔ Readable
         * ✔ No large constructors
         * ✔ Easy to maintain
         *
         * Equivalent to:
         *
         * Product product = new Product();
         * product.setTitle(...);
         * product.setDescription(...);
         * product.setPrice(...);
         * ...
         */
        Product product = Product.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .price(requestDto.getPrice())
                .image(requestDto.getImage())
                /*
                 * Instead of storing category name,
                 * Product stores Category Entity.
                 *
                 * Product
                 * │
                 * ▼
                 * Category
                 *
                 * Hibernate automatically stores
                 * the Category's Primary Key
                 * as a Foreign Key.
                 *
                 * Database
                 *
                 * Products
                 * --------------------------
                 * id
                 * title
                 * category_id
                 *
                 * Categories
                 * --------------------------
                 * id
                 * name
                 */
                .category(category)
                .rating(requestDto.getRating())
                .build();

        /*
         * save()
         *
         * Built-in JpaRepository Method.
         *
         * Hibernate checks:
         *
         * id == null
         * │
         * ▼
         * INSERT
         *
         * id != null
         * │
         * ▼
         * UPDATE
         *
         * Generated SQL
         *
         * INSERT INTO products(...)
         * VALUES(...);
         *
         * Returns
         *
         * Saved Product
         * including generated ID.
         */
        return productRepository.save(product);
    }

    /*
     * ============================================================
     * Method:
     * deleteProduct()
     * ============================================================
     *
     * Deletes Product by Primary Key.
     *
     * URL
     *
     * DELETE /api/v1/products/5
     *
     * Flow
     *
     * Controller
     * │
     * ▼
     * ProductService
     * │
     * ▼
     * Repository
     * │
     * ▼
     * DELETE Query
     * │
     * ▼
     * Database
     */
    public void deleteProduct(Long id) {

        /*
         * deleteById()
         *
         * This is a built-in JpaRepository method.
         *
         * SQL executed:
         * DELETE FROM products WHERE id = ?;
         *
         * If the product exists:
         * It is deleted.
         *
         * If the product does not exist:
         * Spring may throw an EmptyResultDataAccessException.
         */
        productRepository.deleteById(id);
    }

    /*
     * ============================================================
     * Method:
     * getProductsByCategory()
     * ============================================================
     *
     * Returns every Product
     * belonging to one Category.
     *
     * Example
     *
     * Electronics
     *
     * returns
     *
     * Laptop
     * Phone
     * Mouse
     *
     */
    public List<Product> getProductsByCategory(String categoryName) {

        Category category = categoryRepository.findByName(categoryName);

        /*
         * findByCategory()
         *
         * This is a custom repository method.
         *
         * Spring Data JPA automatically generates the query
         * based on the method name.
         *
         * Method:
         * findByCategory(String category)
         *
         * Generated SQL:
         * SELECT * FROM products
         * WHERE category = ?;
         *
         * Example:
         * category = "Electronics"
         *
         * Returns all products whose category is
         * "Electronics".
         */
        return productRepository.findByCategory(category);
    }

    /*
     * Returns all unique categories from the products table.
     *
     * This method retrieves all products, extracts their categories,
     * and returns a list of unique category names.
     *
     * Returns:
     * List<String> - A list of unique category names.
     */

    /*
     * Returns all unique product categories.
     *
     * Example Output:
     * [
     * "Electronics",
     * "Clothing",
     * "Furniture"
     * ]
     */
    public List<String> getAllCategories() {

        /*
         * Option 1 (Commented Out)
         *
         * Fetch all products from the database.
         *
         * SQL:
         * SELECT * FROM products;
         */
        // List<Product> products = productRepository.findAll();

        /*
         * Stream API Explanation
         *
         * products.stream()
         * Converts the List<Product> into a Stream so
         * we can perform operations like map(), filter(),
         * distinct(), and collect().
         *
         * .map(product -> product.getCategory())
         * Extracts only the category from each Product.
         *
         * Example:
         *
         * Products:
         * [
         * {title:"Laptop", category:"Electronics"},
         * {title:"Phone", category:"Electronics"},
         * {title:"Shirt", category:"Clothing"}
         * ]
         *
         * After map():
         * [
         * "Electronics",
         * "Electronics",
         * "Clothing"
         * ]
         *
         * .distinct()
         * Removes duplicate categories.
         *
         * Result:
         * [
         * "Electronics",
         * "Clothing"
         * ]
         *
         * .toList()
         * Converts the Stream back into a List.
         */
        // return products.stream()
        // .map(product -> product.getCategory())
        // .distinct()
        // .toList();

        /*
         * Option 2 (Recommended)
         *
         * Calls a custom repository method that directly
         * fetches distinct categories from the database.
         *
         * This is much more efficient because:
         * - Only category values are fetched.
         * - Duplicate removal happens in the database.
         * - Less memory usage.
         */
        return productRepository.findAllCategories();
    }
}