package com.thachnn.ShopIoT.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(length = 64, unique = true, nullable = false)
    String username;

    @Column(length = 64, nullable = false)
    String password;

    @Column(length = 64, nullable = false, name = "full_name")
    String fullName;

    @ManyToOne
    @JoinColumn(name = "role_name")
    Role role;

    @Column(length = 64, unique = true)
    String email;

    @Column(length = 32)
    String phoneNumber;

    LocalDate dob;
}
