package com.thachnn.ShopIoT.model;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @Column(length = 128, nullable = false, unique = true)
    String sku; //stock keeping unit

    @Column(length = 1024)
    String shortDescription;

    @Column(columnDefinition = "MEDIUMTEXT")
    String longDescription;

    @Column(nullable = false)
    Integer stock;
    boolean inStock;

    @Column(nullable = false)
    Integer salesNumber = 0;

    Long cost;
    Long price;
    Double discountPercentage;

    Double rating;

    Date createdAt;
    Date updatedAt;

    boolean active;

    String image;

    @Column(columnDefinition = "json")
    String productDetails;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    Brand brand;
}
