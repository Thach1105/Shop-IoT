package com.thachnn.ShopIoT.repository;

import com.thachnn.ShopIoT.model.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    @Query( "SELECT n FROM Notification n " +
            "WHERE :lastTimestamp IS NULL " +
            "OR n.timestamp < :lastTimestamp " +
            "ORDER BY n.timestamp DESC")
    Slice<Notification> findAllNotification(
            @Param("lastTimestamp") LocalDateTime timestamp,
            Pageable pageable);
}
