package com.thachnn.ShopIoT.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private Integer sender;

    @Column(nullable = false)
    private String orderCode;

    @Column(nullable = false)
    private String message;

    private LocalDateTime timestamp;

    private boolean hasViewed;

    @PrePersist
    protected void onCreate(){
        this.timestamp = LocalDateTime.now();
    }
}
