package com.thachnn.ShopIoT.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "solutions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Solution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

     @Column(unique = true, nullable = false)
    private String slug;

    @Column(columnDefinition = "LONGTEXT")
    private String content;
    private boolean enabled;
}
