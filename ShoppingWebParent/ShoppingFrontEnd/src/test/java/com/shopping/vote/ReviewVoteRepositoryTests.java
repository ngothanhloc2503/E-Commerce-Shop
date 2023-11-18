package com.shopping.vote;

import com.shopping.common.entity.Customer;
import com.shopping.common.entity.Review;
import com.shopping.common.entity.ReviewVote;
import com.shopping.review.vote.ReviewVoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ReviewVoteRepositoryTests {

    @Autowired
    private ReviewVoteRepository repository;

    @Test
    public void testSaveVote() {
        Integer customerId = 1;
        Integer reviewId = 4;

        ReviewVote vote = new ReviewVote();
        vote.setCustomer(new Customer(customerId));
        vote.setReview(new Review(reviewId));
        vote.voteUp();

        ReviewVote savedVote = repository.save(vote);
        assertThat(savedVote.getId()).isGreaterThan(0);
    }

    @Test
    public void testFindByReviewAndCustomer() {
        Integer customerId = 1;
        Integer reviewId = 4;

        ReviewVote vote = repository.findByReviewAndCustomer(reviewId, customerId);
        System.out.println(vote);

        assertThat(vote).isNotNull();
    }

    @Test
    public void testFindByProductAndCustomer() {
        Integer customerId = 1;
        Integer productId = 1;

        List<ReviewVote> listVotes = repository.findByProductAndCustomer(productId, customerId);
        listVotes.forEach(System.out::println);

        assertThat(listVotes.size()).isGreaterThan(0);
    }
}
