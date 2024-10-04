package com.thachnn.ShopIoT.exception;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorApp {

    USERNAME_NOT_EXISTED(ErrorCode.ERROR_LOGIN.getCode(), "Username not existed", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_EXISTED(ErrorCode.ERROR_LOGIN.getCode(), "Email not existed", HttpStatus.BAD_REQUEST),
    PASSWORD_INCORRECT(ErrorCode.ERROR_LOGIN.getCode(), "Password incorrect", HttpStatus.BAD_REQUEST),

    USER_INVALID(ErrorCode.ERROR_REGISTER.getCode(), "Username must be at least 5 characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(ErrorCode.ERROR_REGISTER.getCode(), "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    FULL_NAME_INVALID(ErrorCode.ERROR_REGISTER.getCode(), "Full name must be not empty", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(ErrorCode.ERROR_REGISTER.getCode(), "Email is malformed", HttpStatus.BAD_REQUEST),
    USERNAME_EXISTED(ErrorCode.ERROR_REGISTER.getCode(), "Username existed", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(ErrorCode.ERROR_REGISTER.getCode(), "Email existed", HttpStatus.BAD_REQUEST),

    USER_NOTFOUND(ErrorCode.ERROR_USER.getCode(), "Could not found user", HttpStatus.BAD_REQUEST),

    UNAUTHENTICATION(ErrorCode.ERROR_AUTHENTICATION.getCode(), "Unauthentication", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(ErrorCode.ERROR_AUTHENTICATION.getCode(), "Token invalid", HttpStatus.UNAUTHORIZED),

    BRAND_EXISTED(ErrorCode.ERROR_BRAND.getCode(), "Brand name existed", HttpStatus.BAD_REQUEST),
    BRAND_NAME_NOT_EMPTY(ErrorCode.ERROR_BRAND.getCode(), "Brand name must be not empty", HttpStatus.BAD_REQUEST),
    BRAND_NOTFOUND(ErrorCode.ERROR_BRAND.getCode(), "Could not found brand", HttpStatus.BAD_REQUEST),
    ;

    private String message;
    private HttpStatusCode httpStatusCode;
    private Integer code;

    ErrorApp(Integer code, String message, HttpStatusCode httpStatusCode){
        this.code = code;
        this.httpStatusCode = httpStatusCode;
        this.message = message;
    }
}
