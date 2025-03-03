package com.thachnn.ShopIoT.service.impl;
import com.thachnn.ShopIoT.dto.request.ProductRequest;
import com.thachnn.ShopIoT.dto.response.ProductResponse;
import com.thachnn.ShopIoT.dto.response.ProductResponseSimple;
import com.thachnn.ShopIoT.exception.AppException;
import com.thachnn.ShopIoT.exception.ErrorApp;
import com.thachnn.ShopIoT.mapper.ProductMapper;
import com.thachnn.ShopIoT.model.Brand;
import com.thachnn.ShopIoT.model.Category;
import com.thachnn.ShopIoT.model.Product;
import com.thachnn.ShopIoT.repository.ProductRepository;
import com.thachnn.ShopIoT.service.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Slf4j
@Service
public class ProductService implements IProductService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    // save new product
    @Override
    public ProductResponse create(ProductRequest request, MultipartFile image) {
        if(productRepository.existsByName(request.getName()))
            throw new AppException(ErrorApp.PRODUCT_NAME_EXISTED);

        Product newProduct = productMapper.toProduct(request);

        if(request.getCategory_id() != null){
            Category category = categoryService.getById(request.getCategory_id());
            newProduct.setCategory(category);
        }

        if(request.getBrand_id() != null){
            Brand brand = brandService.getById(request.getBrand_id());
            newProduct.setBrand(brand);
        }

        //set create time and update time
        newProduct.setCreatedAt(new Date());
        newProduct.setUpdatedAt(new Date());
        newProduct.setInStock(newProduct.getStock() > 0);
        newProduct.setImage(image.getOriginalFilename());

        newProduct = productRepository.save(newProduct);
        String folderName = "products-image/" + newProduct.getId();
        storageService.uploadFileToS3(image, folderName);

        return productMapper.toProductResponse(newProduct);
    }

    // get product by id
    @Override
    public Product getSingleProduct(Long id){

        return productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorApp.PRODUCT_NOT_FOUND));
    }

    // get product by slug
    @Override
    public ProductResponse getProductBySlug(String slug){
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException(ErrorApp.PRODUCT_NOT_FOUND));

        return productMapper.toProductResponse(product);
    }

    // get all product
    @Override
    public Page<ProductResponseSimple> getAll(Integer number, Integer size, String sortBy, String order){
        Sort sort = Sort.by(Sort.Direction.valueOf(order.toUpperCase()),sortBy);
        Pageable pageable = PageRequest.of(number, size, sort);
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(productMapper::toProductResponseSimple);
    }

    //search
    @Override
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
    ){
        Sort sort = Sort.unsorted();
        if(sortField != null && !sortField.isEmpty()){
            String[] sortParts = sortField.split("_");
            String fieldName = sortParts[0];
            String direction = sortParts[1].toUpperCase();
            sort = Sort.by(Sort.Direction.valueOf(direction), fieldName);
        }
        Pageable pageable = PageRequest.of(number, size, sort);

        Page<Product> productPage = productRepository.searchProducts(
                categoryId, active, inStock, keyword, brand, minPrice, maxPrice, pageable);

        return productPage.map(productMapper::toProductResponseSimple);
    }

    //get product by category
    @Override
    public Page<ProductResponseSimple> getProductByCategory(
            Integer categoryId,
            Integer number,
            Integer size,
            Long minPrice,
            Long maxPrice,
            String sortField
    ){
        Category category = categoryService.getById(categoryId);

        Sort sort = Sort.unsorted();
        if(sortField != null && !sortField.isEmpty()){
            String[] sortParts = sortField.split("_");
            String fieldName = sortParts[0];
            String direction = sortParts[1].toUpperCase();

            sort = Sort.by(Sort.Direction.valueOf(direction), fieldName);
        }

        Pageable pageable = PageRequest.of(number, size, sort);
        Page<Product> productPage = productRepository.findAllWithCategoryId(
                category.getId(), minPrice, maxPrice, pageable);

        return productPage.map(productMapper::toProductResponseSimple);
    }

    // get product by brand
    @Override
    public Page<ProductResponseSimple> getProductByBrand(
            Integer brandId,
            Integer number,
            Integer size,
            Long minPrice,
            Long maxPrice
    ){
        Brand brand = brandService.getById(brandId);
        Pageable pageable = PageRequest.of(number, size);
        Page<Product> productPage =
                productRepository.findAllWithBrandId(brand.getId(), minPrice, maxPrice, pageable);

        return productPage.map(productMapper::toProductResponseSimple);
    }

    // update product
    @Override
    public ProductResponse update(Long id, ProductRequest request, MultipartFile image){
        Product prevProduct = getSingleProduct(id);

        if(!request.getName().equals(prevProduct.getName())
            && productRepository.existsByName(request.getName())
        ) throw new AppException(ErrorApp.PRODUCT_NAME_EXISTED);

        Product postProduct = productMapper.toProduct(request);

        if(request.getCategory_id() != null){
            Category category = categoryService.getById(request.getCategory_id());
            postProduct.setCategory(category);
        }

        if(request.getBrand_id() != null){
            Brand brand = brandService.getById(request.getBrand_id());
            postProduct.setBrand(brand);
        }

        postProduct.setId(prevProduct.getId());
        postProduct.setInStock(postProduct.getStock() > 0);
        postProduct.setCreatedAt(prevProduct.getCreatedAt());
        postProduct.setRating(prevProduct.getRating());
        postProduct.setSalesNumber(prevProduct.getSalesNumber());
        postProduct.setUpdatedAt(new Date());
        postProduct.setImage(prevProduct.getImage());

        if(image != null) {
            String folderName = "products-image/" + id;
            if(prevProduct.getImage() != null){
                storageService.deleteFile(folderName + "/" + prevProduct.getImage());
            }
            storageService.uploadFileToS3(image, folderName);
            postProduct.setImage(image.getOriginalFilename());
        }

        return productMapper.toProductResponse(productRepository.save(postProduct));
    }

    //delete product
    @Override
    public void delete(Long id){
        Product product = getSingleProduct(id);
        storageService.deleteFile("products-image/" + id + product.getImage());
        productRepository.deleteById(id);
    }

    // add quantity to product
    @Override
    public ProductResponse addProductStock(Long id, Integer quantity){
        Product product = getSingleProduct(id);

        product.setStock(product.getStock() + quantity);
        return productMapper.toProductResponse(productRepository.save(product));
    }

    // subtract quantity from product
    @Override
    public void subStock(Long id, Integer quantity){
        Product product = getSingleProduct(id);
        int stock = product.getStock() - quantity;

        product.setStock(stock);
        productRepository.save(product);
    }
}
