package com.thachnn.ShopIoT.repository;

import com.thachnn.ShopIoT.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {


    @Query(value =
            "SELECT p.id, p.image, p.name, p.price, p.cost, p.sku, p.slug, SUM(od.quantity) AS total_quantity " +
            "FROM order_detail od " +
            "JOIN orders o ON o.id = od.order_id AND o.order_status <> 5 " +
            "JOIN product p ON od.product_id = p.id " +
            "WHERE o.order_time BETWEEN ?1 AND ?2 " +
            "GROUP BY p.id " +
            "ORDER BY total_quantity DESC",
            nativeQuery = true
    )
    List<Object[]> getTopOrderedProduct(Date from, Date to);
}
