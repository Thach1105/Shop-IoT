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

    @Column(nullable = false, length = 512)
    String name;

    @Column(length = 128, nullable = false, unique = true)
    String sku; //stock keeping unit

    @Column(name = "short_description", length = 1024)
    String shortDescription;

    @Column(name = "long_description", columnDefinition = "MEDIUMTEXT")
    String longDescription;

    @Column(nullable = false)
    Integer stock;
    boolean inStock;

    @Column(name = "sales_number", nullable = false)
    Integer salesNumber = 0;

    Long cost;
    Long price;

    @Column(name = "discount_percentage")
    Double discountPercentage;

    Double rating;

    @Column(name = "created_at", nullable = false)
    Date createdAt;

    @Column(name = "updated_at",nullable = false)
    Date updatedAt;

    boolean active;

    String slug;
    String image;

    @Column(name = "product_details", columnDefinition = "json")
    String productDetails;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    Brand brand;
}
