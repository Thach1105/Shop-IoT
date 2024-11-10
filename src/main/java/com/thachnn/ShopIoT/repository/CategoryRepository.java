package com.thachnn.ShopIoT.repository;

import com.thachnn.ShopIoT.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Transactional
    @Modifying
    @Query("UPDATE Category c SET c.enabled = ?2 WHERE c.id = ?1 OR c.parent.id = ?1")
    int updateCategoryStatusAllChildren(Integer id, boolean status);

    @Transactional
    @Modifying
    @Query("UPDATE Category c SET c.enabled = ?2 WHERE c.id = ?1")
    int updateSingleCategoryStatus(Integer id, boolean status);

    boolean existsByName(String name);
    Optional<Category> findByName(String name);
}
