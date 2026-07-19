package com.example.FakeCommerce.dtos;

import lombok.AllArgsConstructor;
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
 * Jackson to convert JSON into
 * Java objects.
 */
@NoArgsConstructor

/*
 * CreateCategoryRequestDto
 *
 * DTO (Data Transfer Object)
 * used to receive category data
 * from the client.
 *
 * It is used only for API requests
 * and is not a database entity.
 *
 * Example Request JSON:
 *
 * {
 *     "name": "Electronics"
 * }
 */
public class CreateCategoryRequestDto {

    /*
     * Name of the category.
     *
     * Examples:
     * Electronics
     * Clothing
     * Furniture
     * Books
     */
    private String name;
}