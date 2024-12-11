package com.thachnn.ShopIoT.repository;
import com.thachnn.ShopIoT.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.category c " +
            "LEFT JOIN FETCH p.brand b " +
            "WHERE (:category IS NULL OR p.category.id = :category OR p.category.parent.id = :category) " +
            "AND (:brand IS NULL OR p.brand.id = :brand) " +
            "AND (:active IS NULL OR p.active = :active) " +
            "AND (:inStock IS NULL OR p.inStock = :inStock) " +
            "AND (:keyword IS NULL OR p.name LIKE %:keyword% " +
            "OR p.sku LIKE %:keyword% " +
            "OR c.name LIKE %:keyword% " +
            "OR b.name LIKE %:keyword%) " +
            "AND p.cost >= :minPrice " +
            "AND p.cost <= :maxPrice"
    )
    Page<Product> searchProducts(@Param("category") Integer category,
                                 @Param("active") Boolean active,
                                 @Param("inStock") Boolean inStock,
                                 @Param("keyword") String keyword,
                                 @Param("brand") Integer brand,
                                 @Param("minPrice") Long minPrice,
                                 @Param("maxPrice") Long maxPrice,
                                 Pageable pageable);


    @Query("SELECT p FROM Product p " +
            "WHERE (p.category.id = ?1 OR p.category.parent.id = ?1) " +
            "AND p.cost >= ?2 " +
            "AND p.cost <= ?3")
    Page<Product> findAllWithCategoryId(Integer id, Long minPrice, Long maxPrice, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE p.brand.id = ?1 " +
            "AND p.cost >= ?2 " +
            "AND p.cost <= ?3")
    Page<Product> findAllWithBrandId(Integer id, Long minPrice, Long maxPrice, Pageable pageable);

    boolean existsByName(String name);

    Optional<Product> findBySlug(String slug);
}
