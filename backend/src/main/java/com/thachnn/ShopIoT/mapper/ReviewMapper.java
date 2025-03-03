package com.thachnn.ShopIoT.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thachnn.ShopIoT.dto.request.ReviewRequest;
import com.thachnn.ShopIoT.dto.response.ReviewResponse;
import com.thachnn.ShopIoT.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    Review toReview(ReviewRequest request);

    @Mapping(target = "user", expression = "java(buildUser(review))")
    @Mapping(target = "product", expression = "java(buildProduct(review))")
    ReviewResponse toReviewResponse(Review review);

    default JsonNode buildUser(Review review){
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode userNode = objectMapper.createObjectNode();
        userNode.put("id", review.getUser().getId());
        userNode.put("fullName", review.getUser().getFullName());

        return userNode;
    }

    default JsonNode buildProduct(Review review){
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode productNode = objectMapper.createObjectNode();
        productNode.put("id", review.getProduct().getId());
        productNode.put("productName", review.getProduct().getName());

        return productNode;
    }
}
