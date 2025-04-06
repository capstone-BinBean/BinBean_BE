package binbean.binbean_BE.service;

import binbean.binbean_BE.dto.response.ReviewResponse;
import binbean.binbean_BE.entity.Cafe;
import binbean.binbean_BE.entity.Review;
import binbean.binbean_BE.entity.ReviewImg;
import binbean.binbean_BE.exception.NotFoundException;
import binbean.binbean_BE.repository.ReviewImgRepository;
import binbean.binbean_BE.repository.ReviewRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImgRepository reviewImgRepository;

    public ReviewService(ReviewRepository reviewRepository, ReviewImgRepository reviewImgRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewImgRepository = reviewImgRepository;
    }

    public List<ReviewResponse> getReview(Cafe cafe) {
        List<ReviewResponse> responses = new ArrayList<>();
        List<Review> reviews = reviewRepository.findByCafeId(cafe.getId());
        if (reviews.isEmpty()) {
            throw new NotFoundException("Review not found");
        }

        for (Review review : reviews) {
            responses.add(convertReviewToDto(review));
        }

        return responses;
    }

    public double getReviewAvg(Cafe cafe) {
        double reviewAvg = 0;
        List<Review> reviews = reviewRepository.findByCafeId(cafe.getId());

        for (Review review : reviews) {
            reviewAvg += review.getReviewScore();
        }

        return reviewAvg/reviews.size();
    }

    private ReviewResponse convertReviewToDto(Review review) {
        List<ReviewImg> reviewImg = reviewImgRepository.findByReview(review);
        List<String> reviewImgUrls = reviewImg.stream()
            .map(ReviewImg::getReviewImgUrl)
            .collect(Collectors.toList());

        return review.toReviewDto(reviewImgUrls);
    }
}
