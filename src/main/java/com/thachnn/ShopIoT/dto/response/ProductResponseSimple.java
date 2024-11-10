package com.thachnn.ShopIoT.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponseSimple {
    Long id;
    String name;

    Long cost;
    Long price;
    Double discountPercentage;

    Double rating;
    boolean inStock;

    String slug;
    String sku;
    Integer stock;
    Integer salesNumber;
    boolean active;

    JsonNode category;
    JsonNode brand;

    String image_url;
}

