package FIS.iLUVit.service;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Review;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ReviewServiceTest {

    @Autowired ReviewRepository reviewRepository;

    @Test
    public void saveReviewTest() {
        Parent findUser = new Parent(); // userRepository.findById(userId); 학부모 정보 find
        Center findCenter = new Center(); // centerRepository.findByName(centerId); 센터 정보 find
        Review review = Review.createReview("내용", 3,
                false, null, null);
        reviewRepository.save(review);
    }

}