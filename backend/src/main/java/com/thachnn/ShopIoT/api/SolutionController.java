package com.thachnn.ShopIoT.api;

import com.thachnn.ShopIoT.dto.request.SolutionRequest;
import com.thachnn.ShopIoT.dto.response.ApiResponse;
import com.thachnn.ShopIoT.dto.response.SolutionResponse;
import com.thachnn.ShopIoT.service.impl.SolutionService;
import com.thachnn.ShopIoT.service.impl.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/solutions")
public class SolutionController {


    @Autowired
    SolutionService solutionService;

    @Autowired
    StorageService storageService;

    @GetMapping
    public ResponseEntity<?> getAllSolution(Authentication authentication){

        List<SolutionResponse> solutionList = solutionService.getListSolution(authentication);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .content(solutionList)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getSolutionById(
            @PathVariable("id") Integer id
    ){
        SolutionResponse solutionResponse = solutionService.getSolutionById(id);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .content(solutionResponse)
                        .build()
        );
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getSolutionBySlug(
            @PathVariable("slug") String slug
    ){
        SolutionResponse solutionResponse = solutionService.getSolutionBySlug(slug);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .content(solutionResponse)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<?> createNewSolution(
            @RequestBody SolutionRequest request
    ){
        SolutionResponse solutionResponse = solutionService.createNewSolution(request);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .content(solutionResponse)
                        .build()
        );
    }


    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(
            @RequestPart("file") MultipartFile file
    ){
        String folderName = "solution";
        String imageUrl = storageService.uploadFileToS3(file, folderName);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .content(imageUrl)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public  ResponseEntity<?> updateSolution(
        @PathVariable("id") Integer id,
        @RequestBody SolutionRequest request
    ){

        SolutionResponse solutionResponse = solutionService.update(request, id);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .content(solutionResponse)
                        .build()
        );
    }
}
