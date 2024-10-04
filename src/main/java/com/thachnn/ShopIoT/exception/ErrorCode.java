package com.thachnn.ShopIoT.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    ERROR_LOGIN(1001, "Login error"),
    ERROR_REGISTER(1002, "Register error"),
    ERROR_USER(1003, "User error"),
    ERROR_AUTHENTICATION(1004, "Authentication error"),
    ERROR_BRAND(1005, "Brand error")
;
    private Integer code;
    private String note;

    ErrorCode(Integer code, String note){
        this.code = code;
        this.note = note;
    }
}
