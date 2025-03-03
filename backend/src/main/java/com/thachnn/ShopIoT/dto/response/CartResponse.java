package com.thachnn.ShopIoT.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {
    private Long cart_id;
    private JsonNode user;
    private List<JsonNode> products;
    private JsonNode cartSummary;

}
