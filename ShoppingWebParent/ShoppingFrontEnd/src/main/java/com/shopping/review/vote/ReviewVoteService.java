package com.shopping.review.vote;

import com.shopping.common.entity.Customer;
import com.shopping.common.entity.Review;
import com.shopping.common.entity.ReviewVote;
import com.shopping.review.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ReviewVoteService {

    @Autowired
    private ReviewVoteRepository voteRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    public VoteResult undoVote(ReviewVote vote, Integer reviewId, VoteType voteType) {
        voteRepository.delete(vote);
        reviewRepository.updateVoteCount(reviewId);
        int voteCount = reviewRepository.getVoteCount(reviewId);

        return VoteResult.success("You have unvoted " + voteType + " that review.", voteCount);
    }

    public VoteResult doVote(Integer reviewId, Customer customer, VoteType voteType) {
        Review review = null;
        try {
            review = reviewRepository.findById(reviewId).get();
        } catch (NoSuchElementException e) {
            return VoteResult.fail("The review ID " + reviewId + " no longer exists.");
        }

        ReviewVote vote = voteRepository.findByReviewAndCustomer(reviewId, customer.getId());

        if (vote != null) {
            if ((vote.isUpVoted() && voteType.equals(VoteType.UP)) ||
                    (vote.isDownVoted() && voteType.equals(VoteType.DOWN))) {
                return undoVote(vote, reviewId, voteType);
            } else if (vote.isUpVoted() && voteType.equals(VoteType.DOWN)) {
                vote.voteDown();
            } else if (vote.isDownVoted() && voteType.equals(VoteType.UP)) {
                vote.voteUp();
            }
        } else {
            vote = new ReviewVote();
            vote.setCustomer(customer);
            vote.setReview(review);

            if (voteType.equals(VoteType.UP)) {
                vote.voteUp();
            } else {
                vote.voteDown();
            }
        }

        voteRepository.save(vote);
        reviewRepository.updateVoteCount(reviewId);
        Integer voteCount = reviewRepository.getVoteCount(reviewId);

        return VoteResult.success("You have successfully voted " + voteType + " that review.", voteCount);
    }

    public void markReviewsVotedForProductByCustomer(List<Review> listReviews, Integer productId, Integer customerId) {
        List<ReviewVote> listVotes = voteRepository.findByProductAndCustomer(productId, customerId);

        for (ReviewVote vote : listVotes) {
            Review votedReview = vote.getReview();

            if (listReviews.contains(votedReview)) {
                int index = listReviews.indexOf(votedReview);
                Review review = listReviews.get(index);

                if (vote.isUpVoted()) {
                    review.setUpVotedByCurrentCustomer(true);
                } else if (vote.isDownVoted()) {
                    review.setDownVotedByCurrentCustomer(true);
                }
            }
        }
    }
}
