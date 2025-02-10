package com.thachnn.ShopIoT.api;

import com.thachnn.ShopIoT.dto.request.ProductRequest;
import com.thachnn.ShopIoT.dto.response.ApiResponse;
import com.thachnn.ShopIoT.dto.response.ProductResponse;
import com.thachnn.ShopIoT.dto.response.ProductResponseSimple;
import com.thachnn.ShopIoT.mapper.ProductMapper;
import com.thachnn.ShopIoT.model.Product;
import com.thachnn.ShopIoT.service.impl.ProductService;
import com.thachnn.ShopIoT.service.impl.StorageService;
import com.thachnn.ShopIoT.util.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    public static final String PAGE_SIZE = "20";
    public static final String PAGE_NUMBER = "1";

    @Autowired
    private ProductService productService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private ProductMapper productMapper;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestPart(name = "product")ProductRequest request,
            @RequestPart(name = "image")MultipartFile image
    ){

        ProductResponse productResponse = productService.create(request, image);
        return ResponseEntity.ok()
                .body(
                        ApiResponse.builder()
                                .success(true)
                                .content(productResponse)
                                .build()
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSingleProduct(@PathVariable Long id){
        Product product = productService.getSingleProduct(id);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(productMapper.toProductResponse(product))
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getProductBySlug(@PathVariable String slug){

        ProductResponse product = productService.getProductBySlug(slug);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(product)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping()
    public ResponseEntity<?> getAllProduct(
            @RequestParam(name = "size", defaultValue = PAGE_SIZE) Integer size,
            @RequestParam(name = "page", defaultValue = PAGE_NUMBER) Integer number,
            @RequestParam(name = "sortBy", defaultValue = "id")String sortBy,
            @RequestParam(name = "order", defaultValue = "asc")String order
    ){
        Page<ProductResponseSimple> productPage = productService.getAll(number-1, size, sortBy, order);
        PageInfo pageInfo = PageInfo.builder()
                .page(productPage.getNumber()+1)
                .size(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .build();
        List<ProductResponseSimple> responseList = productPage.getContent();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(responseList)
                .pageDetails(pageInfo)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam(name = "size", defaultValue = PAGE_SIZE) Integer size,
            @RequestParam(name = "page", defaultValue = PAGE_NUMBER) Integer number,
            @RequestParam(name = "q", defaultValue = "") String keyword,
            @RequestParam(name = "category", required = false) Integer categoryId,
            @RequestParam(name = "brand", required = false) Integer brandId,
            @RequestParam(name = "active", required = false) Boolean active,
            @RequestParam(name = "inStock", required = false) Boolean inStock,
            @RequestParam(name = "minPrice", defaultValue = "0")Long minPrice,
            @RequestParam(name = "maxPrice", defaultValue = "1000000000")Long maxPrice,
            @RequestParam(name = "sortField", required = false) String sortField
    ){

        Page<ProductResponseSimple> productPage =
                productService.search(
                        number-1, size, keyword, categoryId, brandId,
                        active, inStock, sortField, minPrice, maxPrice);

        PageInfo pageInfo = PageInfo.builder()
                .page(productPage.getNumber()+1)
                .size(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .build();

        List<ProductResponseSimple> responseList = productPage.getContent();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(responseList)
                .pageDetails(pageInfo)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<?> getProductsByACategory(
            @PathVariable Integer id,
            @RequestParam(name = "size", defaultValue = PAGE_SIZE) Integer size,
            @RequestParam(name = "number", defaultValue = PAGE_NUMBER) Integer number,
            @RequestParam(name = "minPrice", defaultValue = "0")Long minPrice,
            @RequestParam(name = "maxPrice", defaultValue = "1000000000")Long maxPrice,
            @RequestParam(name = "sortField", required = false) String sortField
    ){
        Page<ProductResponseSimple> productPage =
                productService.getProductByCategory(
                        id,
                        number-1,
                        size,
                        minPrice,
                        maxPrice,
                        sortField);

        PageInfo pageInfo = PageInfo.builder()
                .page(productPage.getNumber()+1)
                .size(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .build();

        List<ProductResponseSimple> responseList = productPage.getContent();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(responseList)
                .pageDetails(pageInfo)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/brand/{id}")
    public ResponseEntity<?> getProductsByABrand(
            @PathVariable Integer id,
            @RequestParam(name = "size", defaultValue = PAGE_SIZE) Integer size,
            @RequestParam(name = "number", defaultValue = PAGE_NUMBER) Integer number,
            @RequestParam(name = "minPrice", defaultValue = "0")Long minPrice,
            @RequestParam(name = "maxPrice", defaultValue = "1000000000")Long maxPrice
    ){
        Page<ProductResponseSimple> productPage =
                productService.getProductByBrand(id,number-1, size, minPrice, maxPrice);

        PageInfo pageInfo = PageInfo.builder()
                .page(productPage.getNumber()+1)
                .size(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .build();

        List<ProductResponseSimple> responseList = productPage.getContent();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(responseList)
                .pageDetails(pageInfo)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestPart("product") ProductRequest request,
            @RequestPart(name = "image", required = false)MultipartFile image
    ){
        ProductResponse productResponse = productService.update(id, request, image);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(productResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){

        productService.delete(id);
        return ResponseEntity.ok()
                .body(
                        ApiResponse.builder()
                                .success(true)
                                .content("DELETE COMPLETED")
                                .build()
                );
    }

    @PutMapping("/{id}/add-stock")
    public ResponseEntity<?> addProductStock(
            @PathVariable("id") Long id,
            @RequestParam("quantity") Integer quantity
    ){
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(productService.addProductStock(id, quantity))
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }

}
