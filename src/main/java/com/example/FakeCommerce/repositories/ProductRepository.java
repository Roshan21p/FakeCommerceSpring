package com.example.FakeCommerce.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.FakeCommerce.schema.Category;
import com.example.FakeCommerce.schema.Product;

/*
 * @Repository
 *
 * Marks this interface as a Repository (DAO).
 *
 * Repository is responsible for interacting
 * with the database.
 *
 * Spring automatically creates its implementation
 * at runtime.
 */
@Repository

/*
 * JpaRepository<Entity, PrimaryKeyType>
 *
 * Entity : Product
 * Primary Key : Long
 *
 * JpaRepository provides built-in CRUD methods like:
 *
 * save()
 * findAll()
 * findById()
 * deleteById()
 * count()
 *
 * No SQL is required for these operations.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /*
     * Custom Query Method
     *
     * Spring reads the method name and
     * automatically generates the SQL.
     *
     * Method:
     * findByCategory(...)
     *
     * SQL (similar to):
     *
     * SELECT *
     * FROM products
     * WHERE category_id = ?;
     *
     * Returns all products belonging
     * to the given category.
     */
    List<Product> findByCategory(Category category);

    /*
     * @Query
     *
     * Used to write custom SQL or JPQL.
     *
     * nativeQuery = true
     * means execute plain SQL.
     *
     * DISTINCT removes duplicate values.
     *
     * SQL:
     *
     * SELECT DISTINCT category_id
     * FROM products;
     *
     * Returns all unique category IDs.
     */
    @Query(nativeQuery = true, value = "SELECT DISTINCT category_id FROM products")
    List<String> findAllCategories();

    /*
     * JPQL Query
     *
     * JPQL works with Entity names,
     * not database table names.
     *
     * JOIN FETCH loads Product and
     * its Category together in one query.
     *
     * This avoids unnecessary database queries
     * (N+1 problem).
     */

    // @Query(nativeQuery = true, value = "SELECT p.*, c.name AS category FROM
    // products p INNER JOIN categories c ON p.category_id = c.id WHERE p.id = :id")
    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.id = :id")

    /*
     * Returns Product along with
     * complete Category details.
     */
    List<Product> findProductWithDetailsById(Long id);

}