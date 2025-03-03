package com.thachnn.ShopIoT.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
/*@JsonInclude(JsonInclude.Include.NON_NULL)*/
public class ReviewOverall {
    Long productId;
    Long totalReviews;
    Double averageRating;
    JsonNode detail;

}
