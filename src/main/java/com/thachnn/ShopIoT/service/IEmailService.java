package com.thachnn.ShopIoT.service;

import com.thachnn.ShopIoT.model.Order;
import com.thachnn.ShopIoT.model.User;

public interface IEmailService {

    public void sendSimpleMessage(
            String toEmail, String subject, String body
    );

    public void sendOrderConfirmEmail(
            User user, Order order
    );


}
