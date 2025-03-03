package com.thachnn.ShopIoT.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {

    VNPAY(11, "VNPAY"),
    ZALOPAY(22, "ZALOPAY"),

    ;
    private final int code;
    private final String name;

    PaymentMethod(int code, String name){
        this.code = code;
        this.name = name;
    }
}
