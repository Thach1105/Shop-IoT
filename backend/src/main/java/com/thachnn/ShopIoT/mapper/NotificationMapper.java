package com.thachnn.ShopIoT.mapper;

import com.thachnn.ShopIoT.dto.request.NotificationRequest;
import com.thachnn.ShopIoT.model.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    Notification toNotification(NotificationRequest request);
}
