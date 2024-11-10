package com.thachnn.ShopIoT.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OutboundUser {

    private String id;
    private String email;

    @JsonProperty("family_name")
    private String familyName;

    @JsonProperty("given_name")
    private String givenName;
    private String name;
    private String picture;

    @JsonProperty("verified_email")
    private boolean verifiedEmail;
}
