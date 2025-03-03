package com.thachnn.ShopIoT.service;

import com.thachnn.ShopIoT.dto.request.CategoryRequest;
import com.thachnn.ShopIoT.dto.response.CategoryResponse;
import com.thachnn.ShopIoT.model.Category;

import java.util.List;

public interface ICategoryService {

    public Category getById(Integer id);

    public CategoryResponse getBySlug(String slug);

    public List<CategoryResponse> getAll();

    public CategoryResponse create(CategoryRequest request);

    public CategoryResponse update(Integer id, CategoryRequest request);

    public void delete(Integer id);

    public List<CategoryResponse> getCategoryTree();

    public CategoryResponse getByName(String name);

    public int changeStatus(Integer id, boolean status);
}
