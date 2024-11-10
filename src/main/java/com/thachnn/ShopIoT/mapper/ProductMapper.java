package com.thachnn.ShopIoT.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thachnn.ShopIoT.dto.request.ProductRequest;
import com.thachnn.ShopIoT.dto.response.ProductResponse;
import com.thachnn.ShopIoT.dto.response.ProductResponseSimple;
import com.thachnn.ShopIoT.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    Logger log = LoggerFactory.getLogger(ProductMapper.class);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "productDetails", expression = "java(convertJsonToString(request))")
    Product toProduct(ProductRequest request);

    @Mapping(target = "image_url", expression = "java(getImageURL(product))")
    @Mapping(target = "category", expression = "java(buildCategory(product))")
    @Mapping(target = "brand", expression = "java(buildBrand(product))")
    @Mapping(target = "productDetails", expression = "java(convertStringToJSON(product))")
    ProductResponse toProductResponse(Product product);

    @Mapping(target = "image_url", expression = "java(getImageURL(product))")
    @Mapping(target = "category", expression = "java(buildCategory(product))")
    @Mapping(target = "brand", expression = "java(buildBrand(product))")
    ProductResponseSimple toProductResponseSimple(Product product);

    default String convertJsonToString(ProductRequest request){
        //convert Map<String, Object> productDetails to JSON
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> details = request.getProductDetails();
            return objectMapper.writeValueAsString(details);
        } catch (JsonProcessingException e) {
            log.error("could not convert details from Map<String, Object> to String");
            return null;
        }
    }

    default JsonNode buildCategory(Product product){

        if(product.getCategory() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode category = objectMapper.createObjectNode();
            category.put("id", product.getCategory().getId());
            category.put("name", product.getCategory().getName());
            return category;
        }
        return null;
    }

    default JsonNode buildBrand(Product product){
        if(product.getBrand() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode brand = objectMapper.createObjectNode();
            brand.put("id", product.getBrand().getId());
            brand.put("name", product.getBrand().getName());
            return brand;
        }
        return null;
    }

    default JsonNode convertStringToJSON(Product product) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(product.getProductDetails());
        } catch (JsonProcessingException e) {
            log.error("could not convert details from String to JSON");
            return null;
        }
    }

    default String getImageURL(Product product){
        return "https://shopiot-files.s3.ap-southeast-1.amazonaws.com/products-image/"
                + product.getId() + "/" + product.getImage();
    }
}
