package com.thachnn.ShopIoT.dto.response;

import lombok.*;

public abstract class PaymentResponse {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VNPayResponse{
        public String code;
        public String message;
        public String paymentUrl;
    }
}
