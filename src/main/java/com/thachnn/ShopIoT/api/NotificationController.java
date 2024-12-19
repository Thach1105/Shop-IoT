package com.thachnn.ShopIoT.api;

import com.thachnn.ShopIoT.dto.response.ApiResponse;
import com.thachnn.ShopIoT.service.NotificationNewOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    NotificationNewOrderService notificationNewOrderService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .content(notificationNewOrderService.getAllNotification())
                        .build()
        );
    }

    @PutMapping("/viewedNotification/{id}")
    public ResponseEntity<?> changeViewed(
            @PathVariable("id") String id
    ){
        notificationNewOrderService.viewedNotification(id);
        return ResponseEntity.ok(
                ApiResponse.builder()
                .success(true)
                .build()
        );
    }
}
