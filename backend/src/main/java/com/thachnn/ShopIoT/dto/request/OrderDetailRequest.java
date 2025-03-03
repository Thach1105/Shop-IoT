package com.thachnn.ShopIoT.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailRequest {

    private Long product;
    private int quantity;
    private int totalPrice;
    private int unitPrice;
}
