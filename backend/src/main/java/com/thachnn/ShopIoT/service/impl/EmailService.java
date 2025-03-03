package com.thachnn.ShopIoT.service.impl;

import com.thachnn.ShopIoT.model.Order;
import com.thachnn.ShopIoT.model.OrderDetail;
import com.thachnn.ShopIoT.model.User;
import com.thachnn.ShopIoT.service.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements IEmailService {

    @Autowired
    JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String javaMailSender;

    @Override
    public void sendSimpleMessage(
            String toEmail, String subject, String body
    ){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(javaMailSender);
        mailSender.send(message);
    }

    @Override
    public void sendOrderConfirmEmail(
            User user, Order order
    ){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Xác nhận đơn hàng của bạn");
        StringBuilder body = new StringBuilder();
        body.append("Xin chào ").append(user.getFullName()).append(", \n\n");
        body.append("Cảm ơn bạn đã đặt hàng ở cửa hàng chúng tôi. Dưới đây là thông tin đơn hàng của bạn: \n\n");
        body.append("Mã đơn hàng: ").append(order.getOrderCode()).append("\n");
        for (OrderDetail detail : order.getOrderDetailList()){
            body.append("Sản phẩm: ").append(detail.getProduct().getName()).append(",\n");
            body.append("Số lượng: ").append(detail.getQuantity()).append(",\n");
            body.append("Đơn giá: ").append(detail.getProduct().getCost()).append("\n");
        }

        body.append("Tổng tiền: ").append(order.getTotalPrice()).append("\n");

        message.setText(body.toString());
        message.setFrom(javaMailSender);
        mailSender.send(message);
    }
}
