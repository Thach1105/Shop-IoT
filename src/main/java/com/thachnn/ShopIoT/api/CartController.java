package com.thachnn.ShopIoT.api;

import com.thachnn.ShopIoT.dto.request.CartItemRequest;
import com.thachnn.ShopIoT.dto.response.ApiResponse;
import com.thachnn.ShopIoT.dto.response.CartResponse;
import com.thachnn.ShopIoT.mapper.CartMapper;
import com.thachnn.ShopIoT.model.Cart;
import com.thachnn.ShopIoT.service.impl.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@Validated
public class CartController {

    @Autowired
    CartService cartService;

    @Autowired
    CartMapper cartMapper;

    @PostMapping("/add-to-cart")
    public ResponseEntity<?> addProductToCart(
            @Valid @RequestBody CartItemRequest request,
            @AuthenticationPrincipal Jwt jwt
    ){
        String username = (String) jwt.getClaimAsMap("data").get("username");
        Cart cart = cartService.addProductToCart(request, username);
        CartResponse cartResponse = cartMapper.toCartResponse(cart);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(cartResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/delete-from-cart")
    public ResponseEntity<?> deleteFromCart(
            @RequestParam(name = "productId") Long productId,
            @AuthenticationPrincipal Jwt jwt
    ){
        String username = (String) jwt.getClaimAsMap("data").get("username");
        String message = cartService.deleteProductFromCart(productId, username);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(message)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/my-cart")
    public ResponseEntity<?> getMyCart(
            @AuthenticationPrincipal Jwt jwt
    ){
        String username = (String) jwt.getClaimAsMap("data").get("username");
        Cart myCart = cartService.getMyCart(username);

        CartResponse cartResponse = cartMapper.toCartResponse(myCart);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(cartResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("{cartId}/change-quantity")
    public ResponseEntity<?> updateQuantity(
            @PathVariable(name = "cartId") Long cartId,
            @RequestBody @Valid CartItemRequest request,
            @AuthenticationPrincipal Jwt jwt
    ){
        String username = (String) jwt.getClaimAsMap("data").get("username");
        Cart cart = cartService.updateProductQuantityInCart(request, username, cartId);
        CartResponse cartResponse = cartMapper.toCartResponse(cart);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(cartResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

}
