package com.thachnn.ShopIoT.repository;

import com.thachnn.ShopIoT.model.Product;
import com.thachnn.ShopIoT.model.Review;
import com.thachnn.ShopIoT.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r " +
            "WHERE r.product.id = ?1 " +
            "AND r.rating = ?2")
    Slice<Review> findByProductAndRating(Long productId, Integer rating, Pageable pageable);

    @Query("SELECT r FROM Review r " +
            "WHERE r.product.id = ?1")
    Slice<Review> findByProduct(Long productId, Pageable pageable);

    boolean existsByUserAndProduct(User user, Product product);

    Review findByUserAndProduct(User user, Product product);
}
