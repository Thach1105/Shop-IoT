package com.thachnn.ShopIoT.service;

import com.thachnn.ShopIoT.dto.request.ReviewRequest;
import com.thachnn.ShopIoT.dto.response.ReviewOverall;
import com.thachnn.ShopIoT.dto.response.ReviewResponse;
import com.thachnn.ShopIoT.model.Product;
import com.thachnn.ShopIoT.model.Review;
import com.thachnn.ShopIoT.model.User;
import org.springframework.data.domain.Slice;

public interface IReviewService {
    public ReviewResponse createReview(ReviewRequest request, String username);

    public Review getSingleReview(Long id);

    public ReviewOverall getOverallReviewForProduct(Long productId);

    public boolean existReviewByUserAndProduct(User user, Product product);

    public ReviewResponse getByUserAndProduct(String username, Long productId);

    public Slice<ReviewResponse> getByProductAndRating(
            Long productId, Integer rating, Integer number, Integer size
    );

    public ReviewResponse updateReview(Long id, ReviewRequest request, String username);

    public void deleteReview(Long id);
}
