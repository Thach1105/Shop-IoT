package com.thachnn.ShopIoT.service.impl;

import com.thachnn.ShopIoT.dto.request.BrandRequest;
import com.thachnn.ShopIoT.dto.response.BrandResponse;
import com.thachnn.ShopIoT.exception.AppException;
import com.thachnn.ShopIoT.exception.ErrorApp;
import com.thachnn.ShopIoT.mapper.BrandMapper;
import com.thachnn.ShopIoT.model.Brand;
import com.thachnn.ShopIoT.repository.BrandRepository;
import com.thachnn.ShopIoT.service.IBrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "BRAND-SERVICE")
@RequiredArgsConstructor
public class BrandService implements IBrandService {

    private final BrandMapper brandMapper;
    private final BrandRepository brandRepository;
    private final StorageService storageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BrandResponse create(BrandRequest request, MultipartFile logo){
        log.info("Creating new brand: {}", request);
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

    @Override
    public List<BrandResponse> getAll(){
        log.info("Finding brands");
        List<Brand> brandList = brandRepository.findAll();
        return brandList.stream().map(brandMapper::toBrandResponse).collect(Collectors.toList());
    }

    @Override
    public Brand getById(Integer id){

        log.info("Get brand by ID: {}", id);
        return brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorApp.BRAND_NOTFOUND));
    }

    @Override
    public BrandResponse getByName(String name){
        log.info("Get brand by name: {}", name);
        Brand brand = brandRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorApp.BRAND_NOTFOUND));
        return brandMapper.toBrandResponse(brand);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BrandResponse update(Integer id, BrandRequest request, MultipartFile logo){
        log.info("Updating brand: {}", request);
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

    @Override
    public void delete(Integer id){
        getById(id);
        String folderName = "brand-logo/" + id;
        storageService.deleteFile(folderName);
        brandRepository.deleteById(id);
    }
}
