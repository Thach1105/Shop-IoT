package com.thachnn.ShopIoT.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thachnn.ShopIoT.dto.request.ReviewRequest;
import com.thachnn.ShopIoT.dto.response.ReviewOverall;
import com.thachnn.ShopIoT.exception.AppException;
import com.thachnn.ShopIoT.exception.ErrorApp;
import com.thachnn.ShopIoT.mapper.ReviewMapper;
import com.thachnn.ShopIoT.model.Product;
import com.thachnn.ShopIoT.model.Review;
import com.thachnn.ShopIoT.model.User;
import com.thachnn.ShopIoT.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableMethodSecurity
public class ReviewService {

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    @Autowired
    ReviewMapper reviewMapper;

    @PreAuthorize("hasRole('USER')")
    public Review createReview(ReviewRequest request, String username){
        User user = userService.getByUsername(username);
        Product product = productService.getSingleProduct(request.getProductId());
        if(existReviewByUserAndProduct(user, product))
            throw new AppException(ErrorApp.REVIEW_PRODUCT_EXISTED);

        Review review = reviewMapper.toReview(request);
        review.setUser(user);
        review.setProduct(product);

        return reviewRepository.save(review);
    }

    public Review getSingleReview(Long id){
        return reviewRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorApp.REVIEW_NOT_FOUND));
    }

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
                .totalReviews(totalReview)
                .averageRating(average)
                .detail(detail)
                .build();
    }

    public boolean existReviewByUserAndProduct(User user, Product product){
        return reviewRepository.existsByUserAndProduct(user, product);
    }

    public Review getByUserAndProduct(String username, Long productId){
        User user = userService.getByUsername(username);
        Product product = productService.getSingleProduct(productId);

        return reviewRepository.findByUserAndProduct(user, product);
    }

    public Slice<Review> getByProductAndRating(
            Long productId, Integer rating, Integer number, Integer size
    ){
        Pageable pageable = PageRequest.of(number, size);
        if(rating == null){
            return reviewRepository.findByProduct(productId, pageable);
        } else {
            return reviewRepository.findByProductAndRating(productId, rating, pageable);
        }
    }

    public Review updateReview(Long id, ReviewRequest request, String username){
        Review review = getSingleReview(id);

        if(!review.getUser().getUsername().equals(username))
            throw new AppException(ErrorApp.ACCESS_DENIED);

        review.setComment(review.getComment());
        review.setRating(request.getRating());

        return reviewRepository.save(review);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteReview(Long id){
        reviewRepository.deleteById(id);
    }
}
