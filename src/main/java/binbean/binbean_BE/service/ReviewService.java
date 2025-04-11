package binbean.binbean_BE.service;

import binbean.binbean_BE.constants.Constants.ErrorMsg;
import binbean.binbean_BE.dto.ImgUrl;
import binbean.binbean_BE.dto.request.ReviewRegisterRequest;
import binbean.binbean_BE.dto.response.ReviewResponse;
import binbean.binbean_BE.entity.Cafe;
import binbean.binbean_BE.entity.Review;
import binbean.binbean_BE.entity.ReviewImg;
import binbean.binbean_BE.entity.User;
import binbean.binbean_BE.exception.NotFoundException;
import binbean.binbean_BE.repository.CafeRepository;
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
    public final CafeRepository cafeRepository;

    public ReviewService(ReviewRepository reviewRepository, ReviewImgRepository reviewImgRepository,
        CafeRepository cafeRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewImgRepository = reviewImgRepository;
        this.cafeRepository = cafeRepository;
    }

    public List<ReviewResponse> getReviewByUser(User user) {
        List<ReviewResponse> responses = new ArrayList<>();
        List<Review> reviews = reviewRepository.findByCafeId(user.getId());
        if (reviews.isEmpty()) {
            throw new NotFoundException("Review not found");
        }

        for (Review review : reviews) {
            responses.add(convertReviewToDto(review));
        }

        return responses;
    }

    public List<ReviewResponse> getReviewByCafe(Cafe cafe) {
        List<ReviewResponse> responses = new ArrayList<>();
        List<Review> reviews = reviewRepository.findByCafeId(cafe.getId());

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

    public void registerReview(Long cafeId, ReviewRegisterRequest request, User user) {
        Cafe cafe = cafeRepository.findById(cafeId)
            .orElseThrow(() -> new NotFoundException(String.format(ErrorMsg.CAFE_NOT_FOUND)));
        Review review = request.toReviewEntity(cafe, user);
        review.setCreatedAt();
        reviewRepository.save(review);

        for (ImgUrl reviewImgUrl : request.reviewImgUrlList()) {
            ReviewImg reviewImg = request.toReviewImgEntity(review, reviewImgUrl.url());
            reviewImgRepository.save(reviewImg);
        }
    }

    private ReviewResponse convertReviewToDto(Review review) {
        List<ReviewImg> reviewImg = reviewImgRepository.findByReview(review);
        List<ImgUrl> reviewImgUrls = reviewImg.stream()
            .map(ReviewImg::toImgUrlDto)
            .collect(Collectors.toList());

        return review.toReviewDto(reviewImgUrls);
    }
}
