package com.thachnn.ShopIoT.repository;

import com.thachnn.ShopIoT.model.Cart;
import com.thachnn.ShopIoT.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByUser(User user);


    boolean existsByUser(User user);
}
