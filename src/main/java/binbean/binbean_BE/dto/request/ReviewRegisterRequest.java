package binbean.binbean_BE.dto.request;

import binbean.binbean_BE.dto.ImgUrl;
import binbean.binbean_BE.entity.Cafe;
import binbean.binbean_BE.entity.Review;
import binbean.binbean_BE.entity.ReviewImg;
import binbean.binbean_BE.entity.User;
import java.util.List;

public record ReviewRegisterRequest(String reviewText, double reviewScore, List<ImgUrl> reviewImgUrlList) {

    public Review toReviewEntity(Cafe cafe, User user) {

        return Review.create(cafe, user, this.reviewText, this.reviewScore);
    }

    public ReviewImg toReviewImgEntity(Review review, String reviewImgUrl) {

        return ReviewImg.create(review, reviewImgUrl);
    }
}
