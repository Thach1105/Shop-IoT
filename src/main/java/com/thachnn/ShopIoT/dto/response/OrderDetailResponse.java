package com.thachnn.ShopIoT.dto.response;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderDetailResponse {

    JsonNode product;
    int quantity;
    int unitPrice;
    int totalPrice;
}
