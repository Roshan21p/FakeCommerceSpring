package com.example.FakeCommerce.schema;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

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
 * @MappedSuperclass
 *
 * Indicates that this class is NOT a database table.
 *
 * Instead, its fields are inherited by other Entity classes.
 *
 * The fields declared in this class become part of the
 * child entity's table.
 *
 * Hibernate DOES NOT create a separate table named BaseEntity.
 */
@MappedSuperclass

@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    /*
     * @Id
     *
     * Marks this field as the Primary Key.
     *
     * Every entity must have exactly one primary key.
     */
    @Id

    /*
     * @GeneratedValue
     *
     * Automatically generates the primary key value.
     *
     * GenerationType.IDENTITY
     *
     * Uses the database's AUTO_INCREMENT feature.
     *
     * Example:
     * id = 1
     * id = 2
     * id = 3
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}