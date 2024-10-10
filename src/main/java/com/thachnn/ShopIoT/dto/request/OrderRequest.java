package com.thachnn.ShopIoT.dto.request;


import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderRequest {

    @NotEmpty(message = "ORDER_ADDRESS_EMPTY")
    private String address;

    @NotEmpty(message = "ORDER_PHONE_EMPTY")
    private String phone;

    private String paymentType;

    @NotEmpty(message = "ORDER_INFORMATION_EMPTY")
    List<OrderDetailRequest> details; //*
    private long totalPrice;
    private String notes;
}
