package com.thachnn.ShopIoT.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.thachnn.ShopIoT.model.OrderDetail;
import lombok.*;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {

    Integer id;
    String orderCode;
    String orderStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "Asia/Ho_Chi_Minh")
    Date orderTime;

    //user information
    String fullName;
    String address;
    String phone;
    String email;

    String paymentType;
    boolean paymentStatus;

    List<OrderDetailResponse> orderDetail;
    long totalPrice;

    String notes;
}
