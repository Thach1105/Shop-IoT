package com.thachnn.ShopIoT.mapper;

import com.thachnn.ShopIoT.dto.request.NotificationNewOrderRequest;
import com.thachnn.ShopIoT.model.NotificationNewOrder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationNewOrderMapper {
    NotificationNewOrder toNotificationNewOrder(NotificationNewOrderRequest request);
}
