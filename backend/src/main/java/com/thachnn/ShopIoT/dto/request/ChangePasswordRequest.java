package com.thachnn.ShopIoT.dto.request;


import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {

    @Size(min = 8, message = "PASSWORD_INVALID")
    String oldPassword;

    @Size(min = 8, message = "PASSWORD_INVALID")
    String newPassword;
}
