package com.thachnn.ShopIoT.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;

@Service
public class StorageService {

    private static final Logger log = LoggerFactory.getLogger(StorageService.class);
    @Value("${cloud.aws.s3.bucket.name}")
    private String BUCKET_NAME;

    @Value(("${cloud.aws.region}"))
    private String REGION;

    @Autowired
    private S3Client s3Client;

    // upload file to aws s3, if it is folder then folder name is "folder1/folder2"
    public String uploadFileToS3(MultipartFile multipartFile, String folderName){
       try {
           String s3Key = multipartFile.getOriginalFilename();
           if(folderName != null &&  !folderName.isEmpty()){
               s3Key = folderName + "/" + s3Key;
           }

           //lấy InputStream từ multipart file
           InputStream inputStream = multipartFile.getInputStream();

           PutObjectRequest putOb = PutObjectRequest.builder()
                   .bucket(BUCKET_NAME)
                   .key(s3Key)
                   .contentType(multipartFile.getOriginalFilename())
                   .build();


           s3Client.putObject(putOb, RequestBody.fromInputStream(inputStream, multipartFile.getSize()));
           inputStream.close();

            return "https://" + BUCKET_NAME + ".s3." + REGION + ".amazonaws.com/" + s3Key;
       } catch (IOException e){
           log.error("Unable upload file to AWS S3");
       }

       return null;
    }

    public void deleteFile(String fileName){

        DeleteObjectRequest deleteOb = DeleteObjectRequest
                .builder()
                .bucket(BUCKET_NAME)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteOb);

        log.info("{} removed", fileName);
    }
}
