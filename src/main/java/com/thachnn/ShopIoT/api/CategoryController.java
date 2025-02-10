package com.thachnn.ShopIoT.api;

import com.thachnn.ShopIoT.dto.request.CategoryRequest;
import com.thachnn.ShopIoT.dto.response.ApiResponse;
import com.thachnn.ShopIoT.dto.response.CategoryResponse;
import com.thachnn.ShopIoT.mapper.CategoryMapper;
import com.thachnn.ShopIoT.model.Category;
import com.thachnn.ShopIoT.service.impl.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping("/all") /*checked*/
    public ResponseEntity<?> getAll(){
        List<CategoryResponse> categoryResponseList =
                categoryService.getAll();

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
            CategoryResponse categoryResp = categoryService.getByName(name);
            ApiResponse<?> apiResponse = ApiResponse.builder()
                    .success(true)
                    .content(categoryResp)
                    .build();

            return ResponseEntity.ok().body(apiResponse);
        } else {
            List<CategoryResponse> categoryRespList
                    = categoryService.getCategoryTree();

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

    @GetMapping("/category-slug/{slug}") /*checked*/
    public ResponseEntity<?> getBySlug(@PathVariable String slug){
        CategoryResponse categoryResponse = categoryService.getBySlug(slug);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(categoryResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @PutMapping("/{id}") /*checked*/
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @RequestBody @Valid CategoryRequest request
    ){
        CategoryResponse
                categoryResponse = categoryService.update(id, request);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(categoryResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @PutMapping("/change-status/{id}")
    public ResponseEntity<?> changeStatus(
            @PathVariable Integer id,
            @RequestParam("enabled") boolean status
    ){
        String content;
        int res = categoryService.changeStatus(id, status);
        if(res > 0) {
            content = "CHANGE STATUS SUCCESSFULLY";
        } else {
            content = "Could not found category id";
        }

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(content)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }


    @PostMapping /*checked*/
    public ResponseEntity<?> create(@Valid @RequestBody CategoryRequest request){

        CategoryResponse categoryResponse = categoryService.create(request);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(categoryResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @DeleteMapping("/{id}") /*checked*/
    public ResponseEntity<?> delete(@PathVariable Integer id){
        categoryService.delete(id);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content("DELETE COMPLETED")
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }
}
