package com.thachnn.ShopIoT.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExchangeTokenRequest {

    String code;

    @JsonProperty("client_id")
    String clientId;

    @JsonProperty("client_secret")
    String clientSecret;

    @JsonProperty("redirect_uri")
    String redirectUri;

    @JsonProperty("grant_type")
    String grantType;
}
