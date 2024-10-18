package com.thachnn.ShopIoT.repository;

import com.thachnn.ShopIoT.model.Cart;
import com.thachnn.ShopIoT.model.CartItem;
import com.thachnn.ShopIoT.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("SELECT i FROM CartItem i " +
            "WHERE i.product.id = ?1 " +
            "AND i.cart.id = ?2")
    CartItem findByProductIdAndCartId(Long productId, Long cartId);

    void deleteByProductAndCart(Product product, Cart cart);
}
