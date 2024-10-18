package com.thachnn.ShopIoT.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemRequest {

    @NotNull(message = "")
    private Long product_id;
    private Integer quantity;
}
