package com.thachnn.ShopIoT.service;
import com.thachnn.ShopIoT.dto.request.ProductRequest;
import com.thachnn.ShopIoT.exception.AppException;
import com.thachnn.ShopIoT.exception.ErrorApp;
import com.thachnn.ShopIoT.mapper.ProductMapper;
import com.thachnn.ShopIoT.model.Brand;
import com.thachnn.ShopIoT.model.Category;
import com.thachnn.ShopIoT.model.Product;
import com.thachnn.ShopIoT.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
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

    public Product create(ProductRequest request, MultipartFile image) {
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

        return productRepository.save(newProduct);
    }

    public Product getSingleProduct(Long id){

        return productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorApp.PRODUCT_NOT_FOUND));
    }

    public Page<Product> getAll(Integer number, Integer size, String sortBy, String order){
        Sort sort = Sort.by(Sort.Direction.valueOf(order.toUpperCase()),sortBy);
        Pageable pageable = PageRequest.of(number, size, sort);

        return productRepository.findAll(pageable);
    }

    public Page<Product> search(Integer number, Integer size, String keyword){
        Pageable pageable = PageRequest.of(number, size);
        return (keyword != null && !keyword.isEmpty())
                ? productRepository.findAll(keyword, pageable)
                : productRepository.findAll(pageable);
    }

    public Page<Product> getProductByCategory(Integer categoryId, Integer number, Integer size,
                                              Long minPrice, Long maxPrice){
        Category category = categoryService.getById(categoryId);
        Pageable pageable = PageRequest.of(number, size);
        return productRepository.findAllWithCategoryId(category.getId(), minPrice, maxPrice, pageable);
    }

    public Page<Product> getProductByBrand(Integer brandId, Integer number, Integer size,
                                           Long minPrice, Long maxPrice){
        Brand brand = brandService.getById(brandId);
        Pageable pageable = PageRequest.of(number, size);
        return productRepository.findAllWithBrandId(brand.getId(), minPrice, maxPrice, pageable);
    }

    public Product update(Long id, ProductRequest request, MultipartFile image){
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
        postProduct.setUpdatedAt(new Date());

        if(image != null) {
            String folderName = "products-image/" + id;
            if(prevProduct.getImage() != null){
                storageService.deleteFile(folderName + "/" + prevProduct.getImage());
            }
            storageService.uploadFileToS3(image, folderName);
            postProduct.setImage(image.getOriginalFilename());
        }

        return productRepository.save(postProduct);
    }

    public void delete(Long id){
        Product product = getSingleProduct(id);
        storageService.deleteFile("products-image/" + id + product.getImage());
        productRepository.deleteById(id);
    }

    public Product addProductStock(Long id, Integer quantity){
        Product product = getSingleProduct(id);

        product.setStock(product.getStock() + quantity);
        return productRepository.save(product);
    }
}