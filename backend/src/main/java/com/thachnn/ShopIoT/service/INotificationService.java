package com.thachnn.ShopIoT.service;

import com.thachnn.ShopIoT.dto.request.NotificationRequest;
import com.thachnn.ShopIoT.model.Notification;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;

public interface INotificationService {

    public Notification create(NotificationRequest request);

    public Slice<Notification> getAllNotification(LocalDateTime timestamp, Integer number, Integer size);

    public void viewedNotification(String id);
}
