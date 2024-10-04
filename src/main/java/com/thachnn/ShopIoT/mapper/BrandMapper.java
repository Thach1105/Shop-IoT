package com.thachnn.ShopIoT.mapper;

import com.thachnn.ShopIoT.dto.response.BrandResponse;
import com.thachnn.ShopIoT.model.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    @Mapping(target = "logo_url", expression = "java(buildLogoURL(brand))")
    BrandResponse toBrandResponse(Brand brand);

    default String buildLogoURL(Brand brand){

        return  "https://shopiot-files.s3.ap-southeast-1.amazonaws.com/brand-logo/"
                + brand.getId() + "/" + brand.getLogo();
    }
}
