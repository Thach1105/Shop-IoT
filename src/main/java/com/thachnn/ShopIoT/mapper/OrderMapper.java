package com.thachnn.ShopIoT.mapper;

import com.thachnn.ShopIoT.dto.request.OrderRequest;
import com.thachnn.ShopIoT.dto.response.OrderResponse;
import com.thachnn.ShopIoT.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    Order toOrder(OrderRequest request);

    @Mapping(target = "fullName", source = "user.fullName")
    @Mapping(target = "orderStatus", source = "orderStatus.statusName")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "orderDetail", ignore = true)
    OrderResponse toOrderResponse(Order order);
}
