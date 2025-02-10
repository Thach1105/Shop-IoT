package com.thachnn.ShopIoT.config;


import com.thachnn.ShopIoT.repository.InvalidatedTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@EnableScheduling
public class ScheduledTasks {

    @Autowired
    private InvalidatedTokenRepository invalidatedTokenRepository;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 36000)
    public void removeExpiredTokens() {
        var listInvalidatedToken = invalidatedTokenRepository.findAll();

        for (var token : listInvalidatedToken){
            if(token.getExpiryTime().before(new Date())){
                invalidatedTokenRepository.delete(token);
            }
        }
    }

}
