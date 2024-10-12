package com.thachnn.ShopIoT.api;

import com.thachnn.ShopIoT.dto.request.ReviewRequest;
import com.thachnn.ShopIoT.dto.response.ApiResponse;
import com.thachnn.ShopIoT.dto.response.ReviewResponse;
import com.thachnn.ShopIoT.mapper.ReviewMapper;
import com.thachnn.ShopIoT.model.Review;
import com.thachnn.ShopIoT.service.ReviewService;
import com.thachnn.ShopIoT.util.PageInfo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private static final String SLICE_NUMBER = "1";
    private static final String SLICE_SIZE = "5";

    @Autowired
    ReviewService reviewService;

    @Autowired
    ReviewMapper reviewMapper;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ReviewRequest request,
                                    @AuthenticationPrincipal Jwt jwt
    ){
        Review review = reviewService.createReview(request, jwt.getSubject());
        ReviewResponse reviewResponse = reviewMapper.toReviewResponse(review);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(reviewResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        Review review = reviewService.getSingleReview(id);
        ReviewResponse reviewResponse = reviewMapper.toReviewResponse(review);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(reviewResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/forProductByRating/{productId}")
    public ResponseEntity<?> getForProduct(
            @PathVariable(name = "productId") Long productId,
            @RequestParam(name = "rating", required = false) Integer rating,
            @RequestParam(name = "number", defaultValue = SLICE_NUMBER) Integer number,
            @RequestParam(name = "size", defaultValue = SLICE_SIZE) Integer size
    ) {
        Slice<Review> slice = reviewService.getByProductAndRating(productId, rating, number - 1, size);
        PageInfo pageInfo = PageInfo.builder()
                .totalElements(slice.getNumberOfElements())
                .page(slice.getNumber() + 1)
                .size(slice.getSize())
                .hasNext(slice.hasNext())
                .build();

        List<Review> reviews = slice.getContent();
        List<ReviewResponse> responseList = reviews.stream()
                .map(reviewMapper::toReviewResponse).toList();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(responseList)
                .pageDetails(pageInfo)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/forProductByUser/{productId}")
    public ResponseEntity<?> getReviewForProductByUser(
            @PathVariable Long productId,
            @AuthenticationPrincipal Jwt jwt
    ){
       Review review = reviewService.getByUserAndProduct(jwt.getSubject(), productId);
       ReviewResponse reviewResponse = reviewMapper.toReviewResponse(review);

       ApiResponse<?> apiResponse = ApiResponse.builder()
               .success(true)
               .content(reviewResponse)
               .build();

       return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/update/{reviewId}")
    public ResponseEntity<?> updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewRequest request,
            @AuthenticationPrincipal Jwt jwt
    ){
        Map<String, Object> data = jwt.getClaimAsMap("data");
        System.out.println(data.get("email"));
        System.out.println(data.get("id"));
        System.out.println(data.get("username"));

        Review review = reviewService.updateReview(reviewId, request, jwt.getSubject());
        ReviewResponse reviewResponse = reviewMapper.toReviewResponse(review);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(reviewResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long reviewId
    ){
        reviewService.deleteReview(reviewId);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content("DELETE COMPLETED")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

}
