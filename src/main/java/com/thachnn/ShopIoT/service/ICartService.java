package com.thachnn.ShopIoT.service;

import com.thachnn.ShopIoT.dto.request.CartItemRequest;
import com.thachnn.ShopIoT.model.Cart;

public interface ICartService {
    public Cart getMyCart(String username);

    public Cart addProductToCart(CartItemRequest request, String username);

    public Cart updateProductQuantityInCart(CartItemRequest request, String username, Long cartId);

    public String deleteProductFromCart(Long productId, String username);
}
