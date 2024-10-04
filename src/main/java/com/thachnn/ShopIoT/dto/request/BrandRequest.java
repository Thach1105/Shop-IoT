package com.thachnn.ShopIoT.dto.request;


import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BrandRequest {

    @NotEmpty(message = "BRAND_NAME_NOT_EMPTY")
    private String name;
}
