package com.thachnn.ShopIoT.dto.request;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CheckPrevOrderRequest {
    List<PrevOrder> prevOrderList;


    @ToString
    @Getter
    @Setter
    public static class PrevOrder{
        private Long productId;
        private Integer quantity;
    }
}


