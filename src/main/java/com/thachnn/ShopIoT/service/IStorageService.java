package com.thachnn.ShopIoT.service;

import org.springframework.web.multipart.MultipartFile;

public interface IStorageService {

    public String uploadFileToS3(MultipartFile multipartFile, String folderName);

    public void deleteFile(String fileName);
}
