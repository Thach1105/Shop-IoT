package com.thachnn.ShopIoT.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@FieldDefaults(level =  AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {

    @NotEmpty(message = "FULL_NAME_INVALID")
    String fullName;

    @Email(message = "EMAIL_INVALID")
    String email;

    String phoneNumber;
    String sex;
    LocalDate dob;
}
