package com.thachnn.ShopIoT.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SolutionRequest {

    @NotEmpty
    private String name;

    @NotEmpty
    private String slug;

    private String content;
    private boolean enabled;
}
