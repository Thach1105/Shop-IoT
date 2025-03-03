package com.thachnn.ShopIoT.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LogoutRequest {

    @NotEmpty(message = "TOKEN_INVALID")
    String token;
}
