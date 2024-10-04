package com.thachnn.ShopIoT.service;

import com.thachnn.ShopIoT.dto.request.BrandRequest;
import com.thachnn.ShopIoT.exception.AppException;
import com.thachnn.ShopIoT.exception.ErrorApp;
import com.thachnn.ShopIoT.model.Brand;
import com.thachnn.ShopIoT.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private StorageService storageService;

    public Brand create(BrandRequest request, MultipartFile logo){
        if(brandRepository.existsByName(request.getName())) throw new AppException(ErrorApp.BRAND_EXISTED);

        Brand brand = Brand.builder()
                .name(request.getName())
                .logo(logo.getOriginalFilename())
                .build();

        Brand newBrand = brandRepository.save(brand);
        String folderName = "brand-logo/" + newBrand.getId();
        storageService.uploadFileToS3(logo, folderName);

        return newBrand;
    }

    public List<Brand> getAll(){
        return brandRepository.findAll();
    }

    public Brand getById(Integer id){
        return brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorApp.BRAND_NOTFOUND));
    }

    public Brand getByName(String name){
        return brandRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorApp.BRAND_NOTFOUND));
    }

    public Brand update(Integer id, BrandRequest request, MultipartFile logo){
        Brand prevBrand = getById(id);
        prevBrand.setName(request.getName());
        String folderName = "brand-logo/" + id;

        if(logo != null) {
            storageService.deleteFile(folderName + "/" + prevBrand.getLogo());
            prevBrand.setLogo(logo.getOriginalFilename());
            storageService.uploadFileToS3(logo, folderName);
        }

        return brandRepository.save(prevBrand);
    }

    public void delete(Integer id){
        getById(id);
        String folderName = "brand-logo/" + id;
        storageService.deleteFile(folderName);
        brandRepository.deleteById(id);
    }
}
