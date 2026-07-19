package com.example.FakeCommerce.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
 * Generates a constructor
 * with all fields.
 */
@AllArgsConstructor

/*
 * @NoArgsConstructor
 *
 * Generates a constructor
 * with no arguments.
 *
 * Required by frameworks like
 * Jackson for object creation.
 */
@NoArgsConstructor

/*
 * @SuperBuilder
 *
 * Similar to @Builder but supports inheritance.
 *
 * It allows child classes to also
 * use the Builder Pattern.
 *
 * Example:
 *
 * GetProductResponseDto dto =
 * GetProductResponseDto.builder()
 * .id(1L)
 * .title("Laptop")
 * .price(new BigDecimal("50000"))
 * .build();
 */
@SuperBuilder
public class GetProductResponseDto {

    /*
     * Unique identifier of the product.
     *
     * Example:
     * 1
     * 2
     * 3
     */
    private Long id;

    /*
     * Name of the product.
     *
     * Example:
     * Laptop
     * Mobile
     * Shoes
     */
    private String title;

    /*
     * Detailed description
     * of the product.
     */
    private String description;

    /*
     * Product image URL or path.
     *
     * Example:
     * laptop.jpg
     * https://example.com/laptop.png
     */
    private String image;

    /*
     * Product rating.
     *
     * Example:
     * 4.5
     * 3.8
     * 5.0
     */
    private String rating;

    /*
     * Product price.
     *
     * BigDecimal is used because it
     * provides accurate decimal calculations,
     * making it ideal for currency values.
     *
     * Example:
     * 49999.99
     */
    private BigDecimal price;
}