package com.example.FakeCommerce.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.FakeCommerce.schema.Product;

/*
 * @Repository
 *
 * Marks this interface as a Repository (DAO - Data Access Object).
 *
 * A Repository is responsible for interacting with the database.
 *
 * Spring automatically creates an implementation of this interface
 * at runtime, so you don't need to implement it yourself.
 *
 * It also translates database exceptions into Spring's
 * DataAccessException hierarchy.
 */
@Repository

/*
 * ProductRepository extends JpaRepository
 *
 * JpaRepository<Entity, PrimaryKeyType>
 *
 * Entity : Product
 * Primary Key : Long
 *
 * By extending JpaRepository, ProductRepository automatically
 * gets many CRUD methods without writing any SQL.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /*
     * Custom Query Method
     *
     * Spring Data JPA reads the method name:
     *
     * find → Retrieve data
     * By → Start filtering
     * Category → Filter using the "category" field
     *
     * Spring automatically generates SQL similar to:
     *
     * SELECT * FROM products
     * WHERE category = ?;
     *
     * Example:
     * findByCategory("Electronics");
     */
    List<Product> findByCategory(String category);
}