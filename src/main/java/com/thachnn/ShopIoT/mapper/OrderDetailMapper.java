package com.thachnn.ShopIoT.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thachnn.ShopIoT.dto.request.OrderDetailRequest;
import com.thachnn.ShopIoT.dto.response.OrderDetailResponse;
import com.thachnn.ShopIoT.model.OrderDetail;
import com.thachnn.ShopIoT.model.Product;
import org.json.JSONObject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {

    @Mapping(target = "product", ignore = true)
    OrderDetail toOrderDetail(OrderDetailRequest request);

    @Mapping(target = "product", expression = "java(buildProductInfo(orderDetail))")
    OrderDetailResponse toOrderDetailResp(OrderDetail orderDetail);

    default JsonNode buildProductInfo(OrderDetail orderDetail){
        Product product = orderDetail.getProduct();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put("productName", product.getName());
        jsonObject.put("imageUrl", "https://shopiot-files.s3.ap-southeast-1.amazonaws.com/products-image/"
                + product.getId() + "/" + product.getImage());
        jsonObject.put("sku", product.getSku());

        return jsonObject;
    }

}
