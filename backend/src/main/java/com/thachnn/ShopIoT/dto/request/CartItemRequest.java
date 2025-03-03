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

    @NotNull(message = "PRODUCT_ID_INVALID")
    private Long product_id;

    @NotNull(message = "QUANTITY_NOT_NULL")
    private Integer quantity;
}
