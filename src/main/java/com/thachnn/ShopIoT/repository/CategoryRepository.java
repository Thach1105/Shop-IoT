package com.thachnn.ShopIoT.repository;

import com.thachnn.ShopIoT.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    boolean existsByName(String name);
    Optional<Category> findByName(String name);
}
