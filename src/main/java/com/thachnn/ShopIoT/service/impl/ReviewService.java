package com.thachnn.ShopIoT.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thachnn.ShopIoT.dto.request.ReviewRequest;
import com.thachnn.ShopIoT.dto.response.ReviewOverall;
import com.thachnn.ShopIoT.dto.response.ReviewResponse;
import com.thachnn.ShopIoT.exception.AppException;
import com.thachnn.ShopIoT.exception.ErrorApp;
import com.thachnn.ShopIoT.mapper.ReviewMapper;
import com.thachnn.ShopIoT.model.Product;
import com.thachnn.ShopIoT.model.Review;
import com.thachnn.ShopIoT.model.User;
import com.thachnn.ShopIoT.repository.ReviewRepository;
import com.thachnn.ShopIoT.service.IReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Service;

@Service
@EnableMethodSecurity
public class ReviewService implements IReviewService {

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    @Autowired
    ReviewMapper reviewMapper;

    @Override
    @PreAuthorize("hasRole('USER')")
    public ReviewResponse createReview(ReviewRequest request, String username){
        User user = userService.getByUsername(username);
        Product product = productService.getSingleProduct(request.getProductId());
        if(existReviewByUserAndProduct(user, product))
            throw new AppException(ErrorApp.REVIEW_PRODUCT_EXISTED);

        Review review = reviewMapper.toReview(request);

        review.setUser(user);
        review.setProduct(product);

        return reviewMapper.toReviewResponse(reviewRepository.save(review));
    }

    @Override
    public Review getSingleReview(Long id){
        return reviewRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorApp.REVIEW_NOT_FOUND));
    }

    @Override
    public ReviewOverall getOverallReviewForProduct(Long productId){
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode detail = objectMapper.createObjectNode();
        long totalReview = reviewRepository.countReviewsByProductId(productId);
        Double average = reviewRepository.averageRatingByProductId(productId);
        var ratingCounts  = reviewRepository.countRatingsByProductId(productId);

        for (Object[] ratingCount : ratingCounts) {
            Integer rating = (Integer) ratingCount[0];
            Long count = (Long) ratingCount[1];
            detail.put("_"+rating+"Star", count);
        }

        return ReviewOverall.builder()
                .productId(productId)
                .totalReviews(totalReview)
                .averageRating(average == null ? Double.parseDouble("0") : average)
                .detail(detail)
                .build();
    }

    @Override
    public boolean existReviewByUserAndProduct(User user, Product product){
        return reviewRepository.existsByUserAndProduct(user, product);
    }

    @Override
    public ReviewResponse getByUserAndProduct(String username, Long productId){
        User user = userService.getByUsername(username);
        Product product = productService.getSingleProduct(productId);

        Review review = reviewRepository.findByUserAndProduct(user, product);
        return reviewMapper.toReviewResponse(review);
    }

    @Override
    public Slice<ReviewResponse> getByProductAndRating(
            Long productId, Integer rating, Integer number, Integer size
    ){
        Pageable pageable = PageRequest.of(number, size);
        Slice<Review> reviewSlice;
        if(rating == null){
            reviewSlice = reviewRepository.findByProduct(productId, pageable);
        } else {
            reviewSlice = reviewRepository.findByProductAndRating(productId, rating, pageable);
        }

        return reviewSlice.map(reviewMapper::toReviewResponse);
    }

    @Override
    public ReviewResponse updateReview(Long id, ReviewRequest request, String username){
        Review review = getSingleReview(id);

        if(!review.getUser().getUsername().equals(username))
            throw new AppException(ErrorApp.ACCESS_DENIED);

        review.setComment(request.getComment());
        review.setRating(request.getRating());

        return reviewMapper.toReviewResponse(reviewRepository.save(review));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteReview(Long id){
        reviewRepository.deleteById(id);
    }
}
