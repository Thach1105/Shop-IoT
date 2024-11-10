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


@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private static final String SLICE_NUMBER = "1";
    private static final String SLICE_SIZE = "5";

    @Autowired
    ReviewService reviewService;

    @Autowired
    ReviewMapper reviewMapper;

    @PostMapping /*checked*/
    public ResponseEntity<?> create(@Valid @RequestBody ReviewRequest request,
                                    @AuthenticationPrincipal Jwt jwt
    ){
        String username = (String) jwt.getClaimAsMap("data").get("username");
        Review review = reviewService.createReview(request, username);
        ReviewResponse reviewResponse = reviewMapper.toReviewResponse(review);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(reviewResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}") /*checked*/
    public ResponseEntity<?> getById(@PathVariable Long id){
        Review review = reviewService.getSingleReview(id);
        ReviewResponse reviewResponse = reviewMapper.toReviewResponse(review);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(reviewResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/overall/{productId}")
    public ResponseEntity<?> getOverallReviewForProduct(
            @PathVariable(name = "productId") Long productId
    ){
        var overall = reviewService.getOverallReviewForProduct(productId);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(overall)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/forProductByRating/{productId}") /*checked*/
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
                .hasPrevious(slice.hasPrevious())
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

    @GetMapping("/forProductByUser/{productId}") /*checked*/
    public ResponseEntity<?> getReviewForProductByUser(
            @PathVariable Long productId,
            @AuthenticationPrincipal Jwt jwt
    ){
        String username = (String) jwt.getClaimAsMap("data").get("username");
       Review review = reviewService.getByUserAndProduct(username, productId);
       ReviewResponse reviewResponse = reviewMapper.toReviewResponse(review);

       ApiResponse<?> apiResponse = ApiResponse.builder()
               .success(true)
               .content(reviewResponse)
               .build();

       return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/update/{reviewId}") /*checked*/
    public ResponseEntity<?> updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewRequest request,
            @AuthenticationPrincipal Jwt jwt
    ){
        String username = (String) jwt.getClaimAsMap("data").get("username");
        Review review = reviewService.updateReview(reviewId, request, username);
        ReviewResponse reviewResponse = reviewMapper.toReviewResponse(review);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(reviewResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{reviewId}") /*checked*/
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
