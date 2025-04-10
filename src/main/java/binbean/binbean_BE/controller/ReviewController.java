package binbean.binbean_BE.controller;

import binbean.binbean_BE.auth.UserDetailsImpl;
import binbean.binbean_BE.dto.request.ReviewRegisterRequest;
import binbean.binbean_BE.dto.response.ReviewResponse;
import binbean.binbean_BE.service.ReviewService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getReviewByUser(
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<ReviewResponse> responses = reviewService.getReviewByUser(userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @PostMapping("/{cafe_id}")
    public ResponseEntity<Void> registerReview(@PathVariable(name = "cafeId") Long cafeId,
        @RequestBody ReviewRegisterRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        reviewService.registerReview(cafeId, request, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
