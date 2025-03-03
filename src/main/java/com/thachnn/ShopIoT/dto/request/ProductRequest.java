package com.thachnn.ShopIoT.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {

    @NotEmpty(message = "PRODUCT_NAME_NOT_EMPTY")
    String name;
    @NotEmpty(message = "PRODUCT_SKU_NOT_EMPTY")
    String sku;

    String slug;

    String shortDescription;
    String longDescription;

    @Min(value = 0)
    @NotNull(message = "PRODUCT_STOCK_NOT_NULL")
    Integer stock;

    @Min(value = 0)
    Long cost;

    @Min(value = 0)
    Long price;

    @Min(value = 0)
    Double discountPercentage;

    boolean active;

    Map<String, Object> productDetails;

    Integer category_id;
    Integer brand_id;

}
