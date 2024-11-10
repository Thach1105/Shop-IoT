package com.thachnn.ShopIoT.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService{

    @Autowired
    JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String javaMailSender;

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


}
