package com.thachnn.ShopIoT.service.impl;

import com.thachnn.ShopIoT.dto.request.NotificationRequest;
import com.thachnn.ShopIoT.mapper.NotificationMapper;
import com.thachnn.ShopIoT.model.Notification;
import com.thachnn.ShopIoT.repository.NotificationRepository;
import com.thachnn.ShopIoT.service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class NotificationService implements INotificationService {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    NotificationMapper mapper;

    @Override
    public Notification create(NotificationRequest request){
        Notification notificationNewOrder = mapper.toNotification(request);
        return notificationRepository.save(notificationNewOrder);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Slice<Notification> getAllNotification(LocalDateTime timestamp, Integer number, Integer size) {
        Pageable pageable = PageRequest.of(number, size);
       return notificationRepository.findAllNotification(timestamp, pageable);

    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void viewedNotification(String id){
        Notification notification = notificationRepository.findById(id)
                .orElseThrow();
        notification.setHasViewed(true);
        notificationRepository.save(notification);
    }
}
