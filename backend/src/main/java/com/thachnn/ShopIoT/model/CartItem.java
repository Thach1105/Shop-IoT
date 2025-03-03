package com.thachnn.ShopIoT.model;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "cart_item", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "cart_id"})
})
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    @ToString.Exclude
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Long cost;

    @PrePersist
    @PreUpdate
    protected void calculatePrice(){
        if(product != null){
            this.cost = this.product.getCost() * this.quantity;
        }
    }

}
