package com.thachnn.ShopIoT.repository;

import com.thachnn.ShopIoT.model.NotificationNewOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationNewOrderRepository extends JpaRepository<NotificationNewOrder, String> {
}
