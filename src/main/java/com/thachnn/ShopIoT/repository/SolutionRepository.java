package com.thachnn.ShopIoT.repository;

import com.thachnn.ShopIoT.model.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, Integer> {

    Solution findByName(String name);

    boolean existsBySlug(String slug);

    boolean existsByName(String name);

    Optional<Solution> findBySlug(String slug);

    @Query("SELECT s FROM Solution s WHERE s.enabled = true")
    List<Solution> findAllForCustomer();

    @Query("SELECT s FROM Solution s ORDER BY s.id DESC")
    List<Solution> findAllForAdmin();
}
