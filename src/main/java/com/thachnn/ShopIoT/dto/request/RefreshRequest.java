package com.thachnn.ShopIoT.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshRequest {

    @NotEmpty(message = "TOKEN_INVALID")
    String token;
}
