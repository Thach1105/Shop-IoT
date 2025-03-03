package com.thachnn.ShopIoT.service;

import com.thachnn.ShopIoT.dto.request.BrandRequest;
import com.thachnn.ShopIoT.dto.response.BrandResponse;
import com.thachnn.ShopIoT.model.Brand;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IBrandService {

    public BrandResponse create(BrandRequest request, MultipartFile logo);

    public List<BrandResponse> getAll();

    public Brand getById(Integer id);

    public BrandResponse getByName(String name);

    public BrandResponse update(Integer id, BrandRequest request, MultipartFile logo);

    public void delete(Integer id);
}
