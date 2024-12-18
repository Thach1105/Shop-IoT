package com.thachnn.ShopIoT.service;

import com.thachnn.ShopIoT.dto.request.BrandRequest;
import com.thachnn.ShopIoT.dto.response.BrandResponse;
import com.thachnn.ShopIoT.exception.AppException;
import com.thachnn.ShopIoT.exception.ErrorApp;
import com.thachnn.ShopIoT.mapper.BrandMapper;
import com.thachnn.ShopIoT.model.Brand;
import com.thachnn.ShopIoT.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private StorageService storageService;

    public BrandResponse create(BrandRequest request, MultipartFile logo){
        if(brandRepository.existsByName(request.getName())) throw new AppException(ErrorApp.BRAND_EXISTED);

        Brand brand = Brand.builder()
                .name(request.getName())
                .logo(logo.getOriginalFilename())
                .build();

        Brand newBrand = brandRepository.save(brand);
        String folderName = "brand-logo/" + newBrand.getId();
        storageService.uploadFileToS3(logo, folderName);

        return brandMapper.toBrandResponse(newBrand);
    }

    public List<BrandResponse> getAll(){
        List<Brand> brandList = brandRepository.findAll();
        return brandList.stream().map(brandMapper::toBrandResponse).collect(Collectors.toList());
    }

    public Brand getById(Integer id){
        return brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorApp.BRAND_NOTFOUND));
    }

    public BrandResponse getByName(String name){
        Brand brand = brandRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorApp.BRAND_NOTFOUND));
        return brandMapper.toBrandResponse(brand);
    }

    public BrandResponse update(Integer id, BrandRequest request, MultipartFile logo){
        Brand prevBrand = getById(id);
        prevBrand.setName(request.getName());
        String folderName = "brand-logo/" + id;

        if(logo != null) {
            storageService.deleteFile(folderName + "/" + prevBrand.getLogo());
            prevBrand.setLogo(logo.getOriginalFilename());
            storageService.uploadFileToS3(logo, folderName);
        }

        return brandMapper.toBrandResponse(brandRepository.save(prevBrand));
    }

    public void delete(Integer id){
        getById(id);
        String folderName = "brand-logo/" + id;
        storageService.deleteFile(folderName);
        brandRepository.deleteById(id);
    }
}
