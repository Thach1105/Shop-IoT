package com.thachnn.ShopIoT.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thachnn.ShopIoT.dto.response.CartResponse;
import com.thachnn.ShopIoT.model.Cart;
import com.thachnn.ShopIoT.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "cart_id", source = "id")
    @Mapping(target = "user", expression = "java(buildUser(cart))")
    @Mapping(target = "products", expression = "java(buildItems(cart))")
    @Mapping(target = "cartSummary", expression = "java(buildSummary(cart))")
    CartResponse toCartResponse(Cart cart);

    default JsonNode buildUser(Cart cart){
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode userNode = objectMapper.createObjectNode();
        userNode.put("id", cart.getUser().getId());
        userNode.put("username", cart.getUser().getUsername());
        return userNode;
    }

    default List<JsonNode> buildItems(Cart cart){
        ObjectMapper objectMapper = new ObjectMapper();
        List<JsonNode> items = new ArrayList<>();

        Set<CartItem> cartItems = cart.getItems();
        if(cartItems != null){
            for(var i : cartItems){
                ObjectNode objectNode = objectMapper.createObjectNode();
                objectNode.put("id", i.getProduct().getId());
                objectNode.put("name", i.getProduct().getName());
                objectNode.put("unitPrice", i.getProduct().getPrice());
                objectNode.put("discountPercentage", i.getProduct().getDiscountPercentage());
                objectNode.put("unitCost", i.getProduct().getCost());
                objectNode.put("quantity", i.getQuantity());
                objectNode.put("total", i.getCost());

                items.add(objectNode);
            }
        }
        return items;
    }

    default JsonNode buildSummary(Cart cart){
        long total = 0;
        long discountedTotal = 0;
        int totalProducts = cart.getItems().size();
        int totalQuantity = 0;

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonSummary = objectMapper.createObjectNode();

        List<CartItem> cartItems = cart.getItems().stream().toList();
        for (var i : cartItems) {
            total += (i.getCost());
            discountedTotal += (i.getQuantity() * i.getProduct().getPrice() - i.getCost());
            totalQuantity += i.getQuantity();
        }

        jsonSummary.put("total", total);
        jsonSummary.put("discountedTotal", discountedTotal);
        jsonSummary.put("totalProducts", totalProducts);
        jsonSummary.put("totalQuantity", totalQuantity);

        return jsonSummary;
    }

}
