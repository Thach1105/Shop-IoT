package com.thachnn.ShopIoT.repository;

import com.thachnn.ShopIoT.model.Order;
import com.thachnn.ShopIoT.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Transactional
    @Modifying
    @Query("UPDATE Order o SET o.orderStatus = ?2 WHERE o.orderCode = ?1")
    int updateOrderStatus(String orderCode, OrderStatus status);

    Optional<Order> findByOrderCode(String orderCode);

    @Query("SELECT o FROM Order o WHERE o.user.username = ?1")
    List<Order> getAllOrderByUser(String username);
}
