package com.thachnn.ShopIoT.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    ERROR_LOGIN(1001, "Login error"),
    ERROR_REGISTER(1002, "Register error"),
    ERROR_USER(1003, "User error"),
    ERROR_AUTHENTICATION(1004, "Authentication error"),
    ERROR_BRAND(1005, "Brand error"),
    ERROR_CATEGORY(1006, "Category error"),
    ERROR_PRODUCT(1007, "Product error"),
    ERROR_ORDER(1008, "Order error"),
    ERROR_REVIEW(1009, "Review error"),
    ERROR_CART(1010, "Cart error"),
    ERROR_PAYMENT(1011, "Payment error"),
    ERROR_SOLUTION(1012, "Solution error")
;
    private final Integer code;
    private final String note;

    ErrorCode(Integer code, String note){
        this.code = code;
        this.note = note;
    }
}
