package com.thachnn.ShopIoT.mapper;


import com.thachnn.ShopIoT.dto.request.CategoryRequest;
import com.thachnn.ShopIoT.dto.response.CategoryResponse;
import com.thachnn.ShopIoT.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "parent", ignore = true)
    Category toCategory(CategoryRequest request);

    @Mapping(target = "parent_id", expression = "java(getParentID(category))")
    @Mapping(target = "children", expression = "java(buildChildren(category))")
    CategoryResponse toCategoryResponse(Category category);


    default List<CategoryResponse> buildChildren(Category category) {
        return category.getChildren() != null
                ? category.getChildren().stream()
                .map(this::toCategoryResponse)
                .collect(Collectors.toList())
                : null;
    }

    default Integer getParentID(Category category){
        return category.getParent() != null
                ? category.getParent().getId()
                : null;
    }
}
