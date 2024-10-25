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

    @Query("SELECT COUNT(o) > 0 FROM Order o " +
            "WHERE o.orderCode = ?1 " +
            "AND o.transactionId = ?2")
    boolean existsByOrderCodeAndTransactionId(String orderCode,String transactionId);

    Optional<Order> findByOrderCode(String orderCode);

    Optional<Order> findByTransactionId(String transactionId);

    boolean existsByOrderCode(String orderCode);

    boolean existsByTransactionId(String transactionId);

    @Query("SELECT o FROM Order o WHERE o.user.username = ?1")
    List<Order> getAllOrderByUser(String username);
}
