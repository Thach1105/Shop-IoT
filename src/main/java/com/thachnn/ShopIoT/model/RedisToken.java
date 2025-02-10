package com.thachnn.ShopIoT.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("RedisToken")
public class RedisToken implements Serializable {
    @Id
    private String id;

    private Date expiryTime;

    private String accessToken;
}
