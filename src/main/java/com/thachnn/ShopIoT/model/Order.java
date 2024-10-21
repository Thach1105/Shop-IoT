package com.thachnn.ShopIoT.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "order_code", nullable = false, unique = true)
    String orderCode;

    @Column(length = 256, nullable = false)
    String address;

    boolean paymentStatus;
    String paymentType;

    @Column(name = "transaction_reference")
    String transactionReference;

    @Column(name = "order_time", nullable = false)
    Date orderTime;

    @ManyToOne
    @JoinColumn(name = "order_status", nullable = false)
    OrderStatus orderStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    User user;

    String phone;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    List<OrderDetail> orderDetailList;

    @Column(name = "total_price", nullable = false)
    long totalPrice;

    @Column(length = 256)
    String notes;

    @PrePersist
    protected void onCreate(){
        if(this.orderCode == null){
            this.orderCode = UUID.randomUUID().toString().toUpperCase().replace("-", "");
        }
        this.orderTime = new Date();
    }
}
