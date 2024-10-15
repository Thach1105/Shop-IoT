package com.thachnn.ShopIoT.api;

import com.thachnn.ShopIoT.dto.request.CategoryRequest;
import com.thachnn.ShopIoT.dto.response.ApiResponse;
import com.thachnn.ShopIoT.dto.response.CategoryResponse;
import com.thachnn.ShopIoT.mapper.CategoryMapper;
import com.thachnn.ShopIoT.model.Category;
import com.thachnn.ShopIoT.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping("/all") /*checked*/
    public ResponseEntity<?> getAll(){
        List<Category> categories = categoryService.getAll();
        List<CategoryResponse> categoryResponseList =
            categories.stream().map(categoryMapper::toCategoryResponse).toList();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(categoryResponseList)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping() /*checked*/
    public ResponseEntity<?> getCategory(
            @RequestParam(name = "name", required = false) String name
    ){
        if(name != null){
            Category category = categoryService.getByName(name);
            CategoryResponse categoryResp = categoryMapper.toCategoryResponse(category);
            ApiResponse<?> apiResponse = ApiResponse.builder()
                    .success(true)
                    .content(categoryResp)
                    .build();

            return ResponseEntity.ok().body(apiResponse);
        } else {
            List<Category> categories = categoryService.getCategoryTree();
            List<CategoryResponse> categoryRespList
                    = categories.stream().map(categoryMapper::toCategoryResponse).toList();

            ApiResponse<?> apiResponse = ApiResponse.builder()
                    .success(true)
                    .content(categoryRespList)
                    .build();

            return ResponseEntity.ok().body(apiResponse);
        }
    }

    @GetMapping("/{id}") /*checked*/
    public ResponseEntity<?> getById(@PathVariable Integer id){
        Category category = categoryService.getById(id);
        CategoryResponse categoryResponse = categoryMapper.toCategoryResponse(category);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(categoryResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @PutMapping("/{id}") /*checked*/
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @RequestBody CategoryRequest request
    ){

        Category category = categoryService.update(id, request);
        CategoryResponse
                categoryResponse = categoryMapper.toCategoryResponse(category);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(categoryResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @PostMapping /*checked*/
    public ResponseEntity<?> create(@Valid @RequestBody CategoryRequest request){

        Category newCategory = categoryService.create(request);
        CategoryResponse categoryResponse = categoryMapper.toCategoryResponse(newCategory);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(categoryResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @DeleteMapping("/{id}") /*checked*/
    public ResponseEntity<?> delete(@PathVariable Integer id){
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content("DELETE COMPLETED")
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }
}
