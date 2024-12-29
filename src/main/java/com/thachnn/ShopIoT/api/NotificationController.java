package com.thachnn.ShopIoT.api;

import com.thachnn.ShopIoT.dto.response.ApiResponse;
import com.thachnn.ShopIoT.model.Notification;
import com.thachnn.ShopIoT.service.NotificationService;
import com.thachnn.ShopIoT.util.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(value = "number", defaultValue = "1") Integer number,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestParam(value = "lastTimestamp", required = false) LocalDateTime lastTimestamp
    ){
        Slice<Notification> slice = notificationService.getAllNotification(lastTimestamp,number-1, size);
        List<Notification> notifications = slice.getContent();

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .content(notifications)
                        .pageDetails(
                                PageInfo.builder()
                                        .hasPrevious(slice.hasPrevious())
                                        .hasNext(slice.hasNext())
                                        .build()
                        )
                        .build()
        );
    }

    @PutMapping("/viewed/{id}")
    public ResponseEntity<?> changeViewed(
            @PathVariable("id") String id
    ){
        notificationService.viewedNotification(id);
        return ResponseEntity.ok(
                ApiResponse.builder()
                .success(true)
                .build()
        );
    }
}
