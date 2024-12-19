package com.thachnn.ShopIoT.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationNewOrderRequest {

    @NotEmpty
    private String message;

    @NotEmpty
    private String orderCode;
}
