package com.example.FakeCommerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.FakeCommerce.schema.Category;

/*
 * @Repository
 *
 * Marks this interface as a Repository (DAO).
 *
 * Repository is responsible for
 * performing database operations.
 *
 * Spring automatically creates
 * its implementation at runtime.
 */
@Repository

/*
 * CategoryRepository
 *
 * Extends JpaRepository to inherit
 * all built-in CRUD operations.
 *
 * JpaRepository<Entity, PrimaryKeyType>
 *
 * Entity : Category
 * Primary Key Type: Long
 *
 * Common methods available:
 *
 * save()
 * findAll()
 * findById()
 * deleteById()
 * existsById()
 * count()
 *
 * No SQL needs to be written for
 * these basic operations.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /*
     * No custom methods are required
     * because JpaRepository already
     * provides all basic CRUD operations.
     *
     * If needed, custom query methods
     * can be added here.
     *
     * Examples:
     *
     * Optional<Category> findByName(String name);
     *
     * List<Category> findByNameContaining(String keyword);
     *
     * @Query("SELECT c FROM Category c WHERE c.name = :name")
     * Optional<Category> getCategoryByName(String name);
     */

    Category findByName(String name);

}