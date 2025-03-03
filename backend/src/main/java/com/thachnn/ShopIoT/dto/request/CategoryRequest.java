package com.thachnn.ShopIoT.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryRequest {

    @NotEmpty(message = "CATEGORY_NAME_NOT_EMPTY")
    String name;
    Integer parent;
    String description;

    @NotEmpty(message = "CATEGORY_SLUG_NOT_EMPTY")
    String slug;
    boolean enabled;
}
