package com.thachnn.ShopIoT.dto.request;

import com.thachnn.ShopIoT.model.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {

    @NotEmpty
    private String message;

    @NotEmpty
    private String orderCode;

    @NotNull
    private Integer sender;
}
