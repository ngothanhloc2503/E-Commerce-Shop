package com.shopping.review.vote;

import com.shopping.ControllerHelper;
import com.shopping.common.entity.Customer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VoteReviewRestController {

    @Autowired
    private ReviewVoteService reviewVoteService;

    @Autowired
    private ControllerHelper controllerHelper;

    @PostMapping("/vote_review/{id}/{type}")
    public VoteResult voteReview(@PathVariable("id") Integer reviewId,
                                 @PathVariable("type") String type,
                                 HttpServletRequest request) {
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        if (customer == null) {
            return VoteResult.fail("You must login to vote the review");
        }

        VoteType voteType = VoteType.valueOf(type.toUpperCase());
        return reviewVoteService.doVote(reviewId, customer, voteType);
    }
}
