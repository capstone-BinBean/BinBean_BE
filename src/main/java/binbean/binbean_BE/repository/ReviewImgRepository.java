package binbean.binbean_BE.repository;

import binbean.binbean_BE.entity.Review;
import binbean.binbean_BE.entity.ReviewImg;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewImgRepository extends JpaRepository<ReviewImg, Long> {

    List<ReviewImg> findByReview(Review review);
}
