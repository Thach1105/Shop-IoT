package com.thachnn.ShopIoT.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartRequest {
    private List<CartItemRequest> cart;
}
