package com.thachnn.ShopIoT.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Getter
@Setter
@Builder
@FieldDefaults(level =  AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {

    @Size(min = 5, message = "USER_INVALID")
    String username;

    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;

    @NotEmpty(message = "FULL_NAME_INVALID")
    String fullName;

    @Email(message = "EMAIL_INVALID")
    String email;
    String phoneNumber;
    LocalDate dob;
}
