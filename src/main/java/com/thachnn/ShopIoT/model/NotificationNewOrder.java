package com.thachnn.ShopIoT.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationNewOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String orderCode;

    @Column(nullable = false)
    private String message;

    private Date createAt;

    private boolean hasViewed;

    @PrePersist
    protected void onCreate(){
        this.createAt = new Date();
    }
}
