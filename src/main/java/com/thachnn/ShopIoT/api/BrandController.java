package com.thachnn.ShopIoT.api;

import com.thachnn.ShopIoT.dto.request.BrandRequest;
import com.thachnn.ShopIoT.dto.response.ApiResponse;
import com.thachnn.ShopIoT.dto.response.BrandResponse;
import com.thachnn.ShopIoT.mapper.BrandMapper;
import com.thachnn.ShopIoT.model.Brand;
import com.thachnn.ShopIoT.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @Autowired
    private BrandMapper brandMapper;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestPart("brand") BrandRequest request,
                                    @RequestPart("logo")MultipartFile logo
    ){

        String logoName = StringUtils.cleanPath(Objects.requireNonNull(logo.getOriginalFilename()));
        Brand newBrand = brandService.create(request, logo);
        BrandResponse brandResponse = brandMapper.toBrandResponse(newBrand);

        return ResponseEntity.ok()
                .body(
                        ApiResponse.builder()
                                .success(true)
                                .content(brandResponse)
                                .build()
                );
    }

    @GetMapping
    public ResponseEntity<?> getBrand(
            @RequestParam(name = "name", required = false) String name
    ){
        if(name != null && !name.isEmpty()){
            Brand brand = brandService.getByName(name);
            ApiResponse<?> apiResponse = ApiResponse.builder()
                    .success(true)
                    .content(brandMapper.toBrandResponse(brand))
                    .build();

            return ResponseEntity.ok().body(apiResponse);
        } else {
            List<Brand> brands = brandService.getAll();
            List<BrandResponse> brandResponses = brands.stream()
                    .map(brandMapper::toBrandResponse).toList();

            ApiResponse<?> apiResponse = ApiResponse.builder()
                    .success(true)
                    .content(brandResponses)
                    .build();

            return ResponseEntity.ok().body(apiResponse);
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id")Integer id){

        Brand brand = brandService.getById(id);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(brandMapper.toBrandResponse(brand))
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id")Integer id,
                                    @RequestPart("brand") BrandRequest request,
                                    @RequestPart(name = "logo", required = false) MultipartFile logo
    ){
        Brand brand = brandService.update(id, request, logo);
        BrandResponse brandResponse = brandMapper.toBrandResponse(brand);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(brandResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id){
        brandService.delete(id);
        return ResponseEntity.ok().body("DELETE COMPLETED");
    }

}
