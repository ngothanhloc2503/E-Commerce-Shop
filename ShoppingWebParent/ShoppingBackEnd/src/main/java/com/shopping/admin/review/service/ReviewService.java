package com.shopping.admin.review.service;

import com.shopping.admin.paging.PagingAndSortingHelper;
import com.shopping.admin.product.repository.ProductRepository;
import com.shopping.admin.review.repository.ReviewRepository;
import com.shopping.common.entity.Review;
import com.shopping.common.exception.ReviewNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ReviewService {
    public static final int REVIEW_PER_PAGE = 5;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<Review> listAll() {
        return reviewRepository.findAll();
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, REVIEW_PER_PAGE, reviewRepository);
    }

    public Review findById(Integer id) throws ReviewNotFoundException {
        try {
            return reviewRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new ReviewNotFoundException("Could not find any review with ID " + id);
        }
    }

    public void save(Review reviewInForm) {
        Review reviewInDB = reviewRepository.findById(reviewInForm.getId()).get();

        reviewInDB.setHeadline(reviewInForm.getHeadline());
        reviewInDB.setComment(reviewInForm.getComment());

        reviewRepository.save(reviewInDB);
        productRepository.updateReviewCountAndAverageRating(reviewInDB.getProduct().getId());
    }

    public void delete(Integer id) throws ReviewNotFoundException {
        try {
            reviewRepository.findById(id).get();

            reviewRepository.deleteById(id);
            productRepository.updateReviewCountAndAverageRating(id);
        } catch (NoSuchElementException e) {
            throw new ReviewNotFoundException("Could not find any review with ID " + id);
        }
    }
}
