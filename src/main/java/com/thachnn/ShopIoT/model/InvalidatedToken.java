package com.thachnn.ShopIoT.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Table
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvalidatedToken {

    @Id
    String id;

    Date expiryTime;
}
