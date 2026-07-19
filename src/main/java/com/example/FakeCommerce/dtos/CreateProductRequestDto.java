package com.example.FakeCommerce.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * DTO = Data Transfer Object
 *
 * A DTO is used to transfer data between the Client and the Server.
 *
 * It does NOT represent a database table.
 *
 * Purpose:
 * - Receive data from the client.
 * - Send data to another layer (Service).
 * - Avoid exposing the Entity directly.
 *
 * Flow:
 * Client JSON
 *      ↓
 * CreateProductRequestDto
 *      ↓
 * ProductService
 *      ↓
 * Product Entity
 *      ↓
 * Database
 */

/*
 * @Data
 *
 * Lombok generates:
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
 * Generates a constructor with all fields.
 *
 * Example:
 *
 * new CreateProductRequestDto(
 *      "Laptop",
 *      "Gaming Laptop",
 *      new BigDecimal("50000"),
 *      "laptop.jpg",
 *      "Electronics",
 *      "4.8"
 * );
 */
@AllArgsConstructor

/*
 * @NoArgsConstructor
 *
 * Generates an empty constructor.
 *
 * Spring uses it while converting JSON
 * into this DTO object.
 */
@NoArgsConstructor
public class CreateProductRequestDto {

    /*
     * Product title.
     *
     * Example:
     * "iPhone 16"
     */
    private String title;

    /*
     * Product description.
     *
     * Example:
     * "Latest Apple smartphone with A18 chip."
     */
    private String description;

    /*
     * Product price.
     *
     * BigDecimal is used because it provides
     * accurate decimal calculations for money.
     *
     * Example:
     * 49999.99
     */
    private BigDecimal price;

    /*
     * Product image URL.
     *
     * Example:
     * "https://example.com/images/iphone.jpg"
     */
    private String image;

    /*
     * Product category.
     *
     * Example:
     * "Electronics"
     */
    private Long categoryId;

    /*
     * Product rating.
     *
     * Example:
     * "4.8"
     *
     * Note:
     * If you need calculations (sorting,
     * averages, filtering), consider using
     * BigDecimal or Double instead of String.
     */
    private String rating;
}