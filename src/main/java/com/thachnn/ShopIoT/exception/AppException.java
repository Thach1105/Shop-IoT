package com.thachnn.ShopIoT.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AppException extends RuntimeException {

    private ErrorApp errorApp;

    public AppException(ErrorApp errorApp){
        super(errorApp.getMessage());
        this.errorApp = errorApp;
    }

    public AppException(ErrorApp errorApp, String customMessage){
        super(customMessage);
        this.errorApp = errorApp;
    }

}
