package com.example.FakeCommerce.schema;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * Generates a constructor with
 * all fields of the class.
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
 * Example:
 *
 * Category category = Category.builder()
 * .name("Electronics")
 * .build();
 */
@Builder

/*
 * @Entity
 *
 * Marks this class as a JPA Entity.
 *
 * Each object of this class
 * represents one row in the
 * categories table.
 */
@Entity

/*
 * @Table
 *
 * Specifies the database table name.
 *
 * This entity maps to:
 *
 * categories
 */
@Table(name = "categories")
@SQLDelete(sql = "UPDATE categories SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Category extends BaseEntity {

    /*
     * Stores the category name.
     *
     * Examples:
     * Electronics
     * Clothing
     * Furniture
     * Books
     */
    @Column(nullable = false)
    private String name;
}