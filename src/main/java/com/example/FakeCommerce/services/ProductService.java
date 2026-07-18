package com.example.FakeCommerce.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.FakeCommerce.dtos.CreateProductRequestDto;
import com.example.FakeCommerce.repositories.ProductRepository;
import com.example.FakeCommerce.schema.Product;

import lombok.RequiredArgsConstructor;

/*
 * @Service
 * Marks this class as a Service component.
 *
 * A Service contains the business logic of the application.
 * Spring automatically detects it and creates an object (Bean)
 * that can be injected wherever needed.
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
    private final ProductRepository productRepository;

    /*
     * Fetches all products from the database.
     *
     * productRepository.findAll()
     * is provided automatically by JpaRepository.
     *
     * Returns:
     * List<Product>
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /*
     * Fetches a single product using its ID.
     *
     * findById() returns an Optional<Product>.
     *
     * If the product exists:
     * return Product
     *
     * Otherwise:
     * throw RuntimeException("Product not found.")
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
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
    public Product createProduct(CreateProductRequestDto requestDto) {

        /*
         * Builder Pattern
         *
         * Creates a Product object by copying values
         * from the request DTO.
         */
        Product product = Product.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .price(requestDto.getPrice())
                .image(requestDto.getImage())
                .category(requestDto.getCategory())
                .rating(requestDto.getRating())
                .build();

        /*
         * save()
         *
         * Inserts the product into the database.
         *
         * If the ID is null:
         * INSERT
         *
         * If the ID already exists:
         * UPDATE
         *
         * Returns the saved Product (including generated ID).
         */
        return productRepository.save(product);
    }
}