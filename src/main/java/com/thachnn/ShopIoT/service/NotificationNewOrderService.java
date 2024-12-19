package com.thachnn.ShopIoT.service;

import com.thachnn.ShopIoT.dto.request.NotificationNewOrderRequest;
import com.thachnn.ShopIoT.mapper.NotificationNewOrderMapper;
import com.thachnn.ShopIoT.model.NotificationNewOrder;
import com.thachnn.ShopIoT.repository.NotificationNewOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationNewOrderService {

    @Autowired
    NotificationNewOrderRepository notificationNewOrderRepository;

    @Autowired
    NotificationNewOrderMapper mapper;

    public void createNotification(NotificationNewOrderRequest request){
        NotificationNewOrder notificationNewOrder = mapper.toNotificationNewOrder(request);
        notificationNewOrderRepository.save(notificationNewOrder);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<NotificationNewOrder> getAllNotification() {
        return notificationNewOrderRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void viewedNotification(String id){
        NotificationNewOrder notificationNewOrder = notificationNewOrderRepository.findById(id).orElseThrow();
        notificationNewOrder.setHasViewed(true);
        notificationNewOrderRepository.save(notificationNewOrder);
    }
}
