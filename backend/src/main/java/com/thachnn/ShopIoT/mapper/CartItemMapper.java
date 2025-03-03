package com.thachnn.ShopIoT.mapper;

import com.thachnn.ShopIoT.dto.request.CartItemRequest;
import com.thachnn.ShopIoT.model.CartItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    
    CartItem toCartItem(CartItemRequest request);
}
