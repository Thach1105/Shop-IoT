package com.thachnn.ShopIoT.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReviewRequest {

    @NotEmpty(message = "REVIEW_COMMENT_EMPTY")
    String comment;

    @NotNull(message = "REVIEW_RATING_NULL")
    Integer rating;

    @NotNull(message = "REVIEW_PRODUCT_NULL")
    Long productId;
}
