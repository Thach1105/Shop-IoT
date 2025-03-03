package com.thachnn.ShopIoT.service;

import com.thachnn.ShopIoT.dto.request.ProductRequest;
import com.thachnn.ShopIoT.dto.response.ProductResponse;
import com.thachnn.ShopIoT.dto.response.ProductResponseSimple;
import com.thachnn.ShopIoT.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface IProductService {

    public ProductResponse create(ProductRequest request, MultipartFile image);

    public Product getSingleProduct(Long id);

    public ProductResponse getProductBySlug(String slug);

    public Page<ProductResponseSimple> getAll(Integer number, Integer size, String sortBy, String order);

    public Page<ProductResponseSimple> search(
            Integer number,
            Integer size,
            String keyword,
            Integer categoryId,
            Integer brand,
            Boolean active,
            Boolean inStock,
            String sortField,
            Long minPrice,
            Long maxPrice
    );

    public Page<ProductResponseSimple> getProductByCategory(
            Integer categoryId,
            Integer number,
            Integer size,
            Long minPrice,
            Long maxPrice,
            String sortField
    );

    public Page<ProductResponseSimple> getProductByBrand(
            Integer brandId,
            Integer number,
            Integer size,
            Long minPrice,
            Long maxPrice
    );

    public ProductResponse update(Long id, ProductRequest request, MultipartFile image);

    public void delete(Long id);

    public ProductResponse addProductStock(Long id, Integer quantity);

    public void subStock(Long id, Integer quantity);
}
