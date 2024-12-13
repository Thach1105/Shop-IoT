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

    OLD_PASSWORD_INCORRECT(ErrorCode.ERROR_USER.getCode(), "Incorrect password", HttpStatus.BAD_REQUEST),
    USER_NOTFOUND(ErrorCode.ERROR_USER.getCode(), "Could not found user", HttpStatus.BAD_REQUEST),

    UNAUTHENTICATION(ErrorCode.ERROR_AUTHENTICATION.getCode(), "Unauthentication", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(ErrorCode.ERROR_AUTHENTICATION.getCode(), "Token invalid", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(ErrorCode.ERROR_AUTHENTICATION.getCode(), "You don't have permission", HttpStatus.FORBIDDEN),

    BRAND_EXISTED(ErrorCode.ERROR_BRAND.getCode(), "Brand name existed", HttpStatus.BAD_REQUEST),
    BRAND_NAME_NOT_EMPTY(ErrorCode.ERROR_BRAND.getCode(), "Brand name must be not empty", HttpStatus.BAD_REQUEST),
    BRAND_NOTFOUND(ErrorCode.ERROR_BRAND.getCode(), "Could not found brand", HttpStatus.BAD_REQUEST),

    CATEGORY_NAME_NOT_EMPTY(ErrorCode.ERROR_CATEGORY.getCode(), "Category name must be not empty", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND(ErrorCode.ERROR_CATEGORY.getCode(), "Could not found category", HttpStatus.BAD_REQUEST),
    CATEGORY_NAME_EXISTED(ErrorCode.ERROR_CATEGORY.getCode(), "Category name existed", HttpStatus.BAD_REQUEST),

    PRODUCT_NOT_FOUND(ErrorCode.ERROR_PRODUCT.getCode(), "Could not found product", HttpStatus.BAD_REQUEST),
    PRODUCT_NAME_EXISTED(ErrorCode.ERROR_PRODUCT.getCode(), "Product name existed", HttpStatus.BAD_REQUEST),
    PRODUCT_NAME_NOT_EMPTY(ErrorCode.ERROR_PRODUCT.getCode(), "Product name must be not empty", HttpStatus.BAD_REQUEST),
    PRODUCT_SKU_NOT_EMPTY(ErrorCode.ERROR_PRODUCT.getCode(), "Product sku must be not empty", HttpStatus.BAD_REQUEST),
    PRODUCT_STOCK_NOT_NULL(ErrorCode.ERROR_PRODUCT.getCode(), "Stock must be not null", HttpStatus.BAD_REQUEST),
    PRODUCT_OUT_OF_STOCK(ErrorCode.ERROR_PRODUCT.getCode(), "", HttpStatus.BAD_REQUEST),

    ORDER_NOT_FOUND(ErrorCode.ERROR_ORDER.getCode(), "Could not found order", HttpStatus.BAD_REQUEST),
    CHANGE_STATUS_FAILED(ErrorCode.ERROR_ORDER.getCode(), "Change status order failed", HttpStatus.BAD_REQUEST),
    ORDER_ADDRESS_EMPTY(ErrorCode.ERROR_ORDER.getCode(), "Address must not be empty", HttpStatus.BAD_REQUEST),
    ORDER_PHONE_EMPTY(ErrorCode.ERROR_ORDER.getCode(), "Phone number must not be empty", HttpStatus.BAD_REQUEST),
    ORDER_INFORMATION_EMPTY(ErrorCode.ERROR_ORDER.getCode(), "Order detail must not be empty", HttpStatus.BAD_REQUEST),
    ORDER_NOT_CANCEL(ErrorCode.ERROR_ORDER.getCode(), "Order have been shipped cannot be cancelled", HttpStatus.BAD_REQUEST),

    REVIEW_COMMENT_EMPTY(ErrorCode.ERROR_REVIEW.getCode(), "Comment must not be empty", HttpStatus.BAD_REQUEST),
    REVIEW_RATING_NULL(ErrorCode.ERROR_REVIEW.getCode(), "Rating must not be null", HttpStatus.BAD_REQUEST),
    REVIEW_PRODUCT_NULL(ErrorCode.ERROR_REVIEW.getCode(), "Product id must not be null", HttpStatus.BAD_REQUEST),
    REVIEW_NOT_FOUND(ErrorCode.ERROR_REVIEW.getCode(), "Could not found review", HttpStatus.BAD_REQUEST),
    REVIEW_PRODUCT_EXISTED(ErrorCode.ERROR_REVIEW.getCode(), "You have rated this product ", HttpStatus.BAD_REQUEST),

    CART_EMPTY(ErrorCode.ERROR_CART.getCode(), "Your cart is empty", HttpStatus.BAD_REQUEST),
    NOT_FOUND_PRODUCT_IN_CART(ErrorCode.ERROR_CART.getCode(), "The product is not available in the cart", HttpStatus.BAD_REQUEST),

    PAID_ORDER(ErrorCode.ERROR_PAYMENT.getCode(), "The order has been paid", HttpStatus.BAD_REQUEST),

    SOLUTION_NAME_EXISTED(ErrorCode.ERROR_SOLUTION.getCode(), "Solution name already exists", HttpStatus.BAD_REQUEST),
    SOLUTION_SLUG_EXISTED(ErrorCode.ERROR_SOLUTION.getCode(), "The path to this solution already exists", HttpStatus.BAD_REQUEST),
    SOLUTION_NOT_FOUND(ErrorCode.ERROR_SOLUTION.getCode(), "Could not found solution", HttpStatus.BAD_REQUEST),
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
