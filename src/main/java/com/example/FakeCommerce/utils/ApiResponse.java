package com.example.FakeCommerce.utils;

// Lombok: Generates getters, setters, toString(), equals(), and hashCode()
import lombok.Data;

// Lombok: Generates a constructor with all fields
import lombok.AllArgsConstructor;

// Lombok: Implements the Builder Design Pattern
import lombok.Builder;

// Lombok: Generates a no-argument constructor
import lombok.NoArgsConstructor;

/*
 * ApiResponse<T>
 *
 * T is a generic type parameter.
 * It allows this class to hold any type of data.
 *
 * Examples:
 * ApiResponse<Product>
 * ApiResponse<Category>
 * ApiResponse<List<Product>>
 * ApiResponse<String>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {

    // Indicates whether the API call was successful.
    private boolean success;

    // Human-readable message.
    private String message;

    // Error details (used when success = false).
    private String error;

    /*
     * Generic response data.
     *
     * T can be:
     * Product
     * Category
     * List<Product>
     * String
     * Integer
     * etc.
     */
    private T data;

    /*
     * Generic static factory method.
     *
     * <T>
     * Declares a generic type for THIS METHOD.
     *
     * ApiResponse<T>
     * Return type.
     *
     * success
     * Method name.
     *
     * T data
     * Accepts any object type.
     */
    public static <T> ApiResponse<T> success(String message, T data) {

        /*
         * ApiResponse.<T>builder()
         *
         * Calls Lombok's generated builder().
         *
         * <T> explicitly tells Java:
         * "Create a Builder for ApiResponse<T>."
         *
         * Usually Java can infer T automatically,
         * so builder() would also work in many cases.
         */
        return ApiResponse.<T>builder()

                // success = true
                .success(true)

                // Set message
                .message(message)

                // Set generic data
                .data(data)

                // Create the object
                .build();
    }

    /*
     * Creates an error response.
     */
    public static <T> ApiResponse<T> error(String message, String error) {

        return ApiResponse.<T>builder()

                // success = false
                .success(false)

                // Set error text
                .error(error)

                // Set message
                .message(message)

                // Build ApiResponse object
                .build();
    }
}