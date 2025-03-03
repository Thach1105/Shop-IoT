package com.thachnn.ShopIoT.dto.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderRequest {

    @NotEmpty
    private String consigneeName;

    private boolean homeDelivery;

    @NotEmpty(message = "ORDER_ADDRESS_EMPTY")
    private String address;

    @NotEmpty(message = "ORDER_PHONE_EMPTY")
    private String phone;

    private String paymentType;

    @NotNull(message = "ORDER_INFORMATION_EMPTY")
    List<OrderDetailRequest> details; //*
    private long totalPrice;

    private String notes;
}
