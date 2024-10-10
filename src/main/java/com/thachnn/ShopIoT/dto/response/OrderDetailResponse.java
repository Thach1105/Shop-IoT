package com.thachnn.ShopIoT.dto.response;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderDetailResponse {

    String product;
    int quantity;
    int unitPrice;
    int totalPrice;
}
