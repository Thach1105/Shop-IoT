package com.thachnn.ShopIoT.mapper;

import com.thachnn.ShopIoT.dto.request.OrderDetailRequest;
import com.thachnn.ShopIoT.dto.response.OrderDetailResponse;
import com.thachnn.ShopIoT.model.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {

    @Mapping(target = "product", ignore = true)
    OrderDetail toOrderDetail(OrderDetailRequest request);

    @Mapping(target = "product", source = "product.name")
    OrderDetailResponse toOrderDetailResp(OrderDetail orderDetail);

}
