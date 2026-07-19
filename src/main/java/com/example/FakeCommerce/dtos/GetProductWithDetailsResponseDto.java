package com.example.FakeCommerce.dtos;

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
 * Supports the Builder Pattern
 * with inheritance.
 *
 * Since this class extends
 * GetProductResponseDto,
 * 
 * @SuperBuilder allows building
 * objects containing fields from
 * both parent and child classes.
 */
@SuperBuilder

/*
 * GetProductWithDetailsResponseDto
 *
 * Extends GetProductResponseDto
 * by adding extra information.
 *
 * Inheritance allows us to reuse
 * all fields from the parent DTO
 * without writing them again.
 */
public class GetProductWithDetailsResponseDto extends GetProductResponseDto {

    /*
     * Category name of the product.
     *
     * Example:
     * Electronics
     * Clothing
     * Furniture
     */
    private String category;
}