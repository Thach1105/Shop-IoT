package com.thachnn.ShopIoT.service;
import com.thachnn.ShopIoT.dto.request.CartItemRequest;
import com.thachnn.ShopIoT.exception.AppException;
import com.thachnn.ShopIoT.exception.ErrorApp;
import com.thachnn.ShopIoT.model.Cart;
import com.thachnn.ShopIoT.model.CartItem;
import com.thachnn.ShopIoT.model.Product;
import com.thachnn.ShopIoT.model.User;

import com.thachnn.ShopIoT.repository.CartItemRepository;
import com.thachnn.ShopIoT.repository.CartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class CartService {

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    public Cart getMyCart(String username){
        User user = userService.getByUsername(username);
        Cart cart = cartRepository.findByUser(user);

        if(cart == null) throw new AppException(ErrorApp.CART_EMPTY);
        return cart;
    }

    public Cart addProductToCart(CartItemRequest request, String username){
        int quantity = request.getQuantity();
        User user = userService.getByUsername(username);
        Cart cart = cartRepository.findByUser(user);
        Product product = productService.getSingleProduct(request.getProduct_id());

        if(cart == null){
            Cart newCart = new Cart();
            newCart.setUser(user);

            CartItem cartItem = CartItem.builder()
                    .product(product)
                    .quantity(request.getQuantity())
                    .cart(newCart)
                    .build();

            newCart.setItems(List.of(cartItem));
            return cartRepository.save(newCart);
        } else {
            CartItem cartItem =
                    cartItemRepository.findByProductIdAndCartId(product.getId(), cart.getId());

            if (cartItem != null){
                cartItem.setQuantity(quantity + cartItem.getQuantity());
                cartItemRepository.save(cartItem);
            } else {
                CartItem newItem = CartItem.builder()
                        .cart(cart)
                        .quantity(quantity)
                        .product(product)
                        .build();

                cartItemRepository.save(newItem);
            }

            return cartRepository.findByUser(user);
        }
    }

    public Cart updateProductQuantityInCart(CartItemRequest request, String username, Long cartId){
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new AppException(ErrorApp.CART_EMPTY));

        if (!cart.getUser().getUsername().equals(username)) throw new AppException(ErrorApp.ACCESS_DENIED);

        CartItem cartItem =
                cartItemRepository.findByProductIdAndCartId(request.getProduct_id(), cartId);

        if(cartItem == null) throw new AppException(ErrorApp.NOT_FOUND_PRODUCT_IN_CART);
        else {
            if(cartItem.getQuantity() + request.getQuantity() <= 0){
                cartItemRepository.delete(cartItem);
            } else {
                cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
                cartItemRepository.save(cartItem);
            }
        }

        return getMyCart(username);
    }

    @Transactional
    public String deleteProductFromCart(Long productId, String username){
        Cart cart = getMyCart(username);
        Product product = productService.getSingleProduct(productId);
        cartItemRepository.deleteByProductAndCart(product, cart);

        return product.getName() + " removed from the cart !!!";
    }
}
