package com.example.FakeCommerce.schema;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * @Data
 *
 * Lombok automatically generates:
 * - Getters
 * - Setters
 * - toString()
 * - equals()
 * - hashCode()
 */
@Data

/*
 * @AllArgsConstructor
 *
 * Generates a constructor containing
 * all fields of this class.
 */
@AllArgsConstructor

/*
 * @NoArgsConstructor
 *
 * Generates a constructor with
 * no arguments.
 *
 * Required by Hibernate/JPA.
 */
@NoArgsConstructor

/*
 * @Builder
 *
 * Implements the Builder Pattern.
 *
 * Allows object creation like:
 *
 * Product product = Product.builder()
 * .title("Laptop")
 * .price(new BigDecimal("50000"))
 * .build();
 */
@Builder

/*
 * @Entity
 *
 * Marks this class as a JPA Entity.
 *
 * Every object of this class
 * represents one row in the
 * products table.
 */
@Entity

/*
 * @Table
 *
 * Specifies the database table name.
 *
 * This entity maps to:
 *
 * products
 */
@Table(name = "products")
public class Product extends BaseEntity {

    /*
     * @Column
     *
     * Maps this field to the "title"
     * column in the database.
     *
     * nullable = false
     * means this field cannot be NULL.
     */
    @Column(name = "title", nullable = false)
    private String title;

    /*
     * columnDefinition = "TEXT"
     *
     * Stores large text values.
     *
     * Useful for long descriptions.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /*
     * Maps the price column.
     *
     * BigDecimal is preferred for
     * currency because it avoids
     * floating-point precision errors.
     */
    @Column(nullable = false)
    private BigDecimal price;

    /*
     * Stores the product image URL.
     *
     * Since no @Column is specified,
     * Hibernate automatically creates
     * a column named "image".
     */
    private String image;

    /*
     * @ManyToOne
     *
     * Many Products belong to
     * one Category.
     *
     * Example:
     *
     * Electronics
     * │
     * ├── Laptop
     * ├── Phone
     * └── Mouse
     *
     * fetch = FetchType.LAZY
     *
     * Category is loaded only when
     * product.getCategory() is called.
     *
     * Improves performance.
     */
    @ManyToOne(fetch = FetchType.LAZY)

    /*
     * @JoinColumn
     *
     * Specifies the Foreign Key column.
     *
     * products.category_id
     * │
     * ▼
     * categories.id
     *
     * nullable = false
     * means every Product must
     * belong to a Category.
     */
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /*
     * Stores the product rating.
     *
     * Example:
     * 4.5
     * 3.9
     * 5.0
     */
    private String rating;
}