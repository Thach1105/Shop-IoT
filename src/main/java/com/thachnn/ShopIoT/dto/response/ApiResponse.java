package com.thachnn.ShopIoT.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.thachnn.ShopIoT.util.PageInfo;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    boolean success;
    Integer code;
    String message;
    T content;
    PageInfo pageDetails;
}
