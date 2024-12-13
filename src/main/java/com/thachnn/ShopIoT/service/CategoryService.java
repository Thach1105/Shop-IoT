package com.thachnn.ShopIoT.service;

import com.thachnn.ShopIoT.dto.request.CategoryRequest;
import com.thachnn.ShopIoT.exception.AppException;
import com.thachnn.ShopIoT.exception.ErrorApp;
import com.thachnn.ShopIoT.mapper.CategoryMapper;
import com.thachnn.ShopIoT.model.Category;
import com.thachnn.ShopIoT.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    public Category getById(Integer id){
        return categoryRepository.findById(id).
                orElseThrow(() -> new AppException(ErrorApp.CATEGORY_NOT_FOUND));
    }

    public Category getBySlug(String slug){
        return categoryRepository.findBySlug(slug).
                orElseThrow(() -> new AppException(ErrorApp.CATEGORY_NOT_FOUND));
    }

    public List<Category> getAll(){
        return categoryRepository.findAll();
    }

    public Category create(CategoryRequest request){

        Category newCategory = categoryMapper.toCategory(request);
        System.out.println(newCategory);
        if(categoryRepository.existsByName(request.getName()))
            throw new AppException(ErrorApp.CATEGORY_NAME_EXISTED);

        Category parent;
        if(request.getParent() != null){
            parent = getById(request.getParent());
            newCategory.setParent(parent);
        }

        return categoryRepository.save(newCategory);
    }

    public Category update(Integer id, CategoryRequest request){

        Category prevCategory = getById(id);

        if(!request.getName().equals(prevCategory.getName())
            && categoryRepository.existsByName(request.getName())
        ) throw new AppException(ErrorApp.CATEGORY_NAME_EXISTED);

        prevCategory.setName(request.getName());
        prevCategory.setDescription(request.getDescription());
        prevCategory.setEnabled(request.isEnabled());
        prevCategory.setSlug(request.getSlug());

        Category parent;
        if(request.getParent() != null){
            parent = getById(request.getParent());
            prevCategory.setParent(parent);
        }

        if(!request.isEnabled()){
            categoryRepository.updateCategoryStatusAllChildren(id, false);
        }

        return categoryRepository.save(prevCategory);
    }

    public void delete(Integer id){
        getById(id);
        categoryRepository.deleteById(id);
    }

    public List<Category> getCategoryTree(){

        List<Category> categories = getAll();
        List<Category> result = new ArrayList<>();

        for (var c : categories){
            if(c.getParent() == null) result.add(c);
        }

        return result;
    }

    public Category getByName(String name){
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorApp.CATEGORY_NOT_FOUND));
    }

    public int changeStatus(Integer id, boolean status){
        if(status) {
            return categoryRepository.updateSingleCategoryStatus(id, true);
        } else {
            return categoryRepository.updateCategoryStatusAllChildren(id, false);
        }

    }

}
