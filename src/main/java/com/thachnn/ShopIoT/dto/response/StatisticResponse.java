package com.thachnn.ShopIoT.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatisticResponse {
    private Long totalCustomer;
    private Long totalOrder;
    private Long totalPrice;
    private Long totalProduct;

    private List<JsonNode> topOrderedProduct;
}
