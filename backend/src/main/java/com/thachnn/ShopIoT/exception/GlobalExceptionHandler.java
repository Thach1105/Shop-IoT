package com.thachnn.ShopIoT.exception;

import com.thachnn.ShopIoT.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handlingAppException(AppException exception){
        ErrorApp errorApp = exception.getErrorApp();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(false)
                .code(errorApp.getCode())
                .message(exception.getMessage())
                .build();

        return ResponseEntity.status(errorApp.getHttpStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> handlingMethodArgumentNotValidException(
            MethodArgumentNotValidException exception){
        String enumKey = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        ErrorApp errorApp = ErrorApp.valueOf(enumKey);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(false)
                .code(errorApp.getCode())
                .message(errorApp.getMessage())
                .build();


        return ResponseEntity
                .status(errorApp.getHttpStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(value = JwtException.class)
    ResponseEntity<ApiResponse<?>> handlingJwtException(JwtException exception) {

        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(false);
        apiResponse.setMessage(ErrorApp.TOKEN_INVALID.getMessage());

        return ResponseEntity
                .status(ErrorApp.TOKEN_INVALID.getHttpStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<?>> handlingAccessDeniedException(AccessDeniedException exception) {

        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(false);
        apiResponse.setMessage(ErrorApp.ACCESS_DENIED.getMessage());

        return ResponseEntity
                .status(ErrorApp.ACCESS_DENIED.getHttpStatusCode())
                .body(apiResponse);
    }

    @ExceptionHandler(value = AuthenticationServiceException.class)
    ResponseEntity<ApiResponse<?>> handlingAuthenticationServiceException(
            AuthenticationServiceException exception){


        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(false);
        apiResponse.setMessage(ErrorApp.UNAUTHENTICATION.getMessage());

        return ResponseEntity
                .status(ErrorApp.UNAUTHENTICATION.getHttpStatusCode())
                .body(apiResponse);
    }

   /* @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<?>> handlingAppException(Exception exception){
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(false)
                .message(exception.getMessage())
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }*/

}
