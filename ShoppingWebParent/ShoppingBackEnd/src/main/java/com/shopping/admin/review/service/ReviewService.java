package com.shopping.admin.review.service;

import com.shopping.admin.paging.PagingAndSortingHelper;
import com.shopping.admin.review.repository.ReviewRepository;
import com.shopping.common.entity.Review;
import com.shopping.common.exception.BrandNotFoundException;
import com.shopping.common.exception.OrderNotFoundException;
import com.shopping.common.exception.ReviewNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReviewService {
    public static final int REVIEW_PER_PAGE = 5;

    @Autowired
    private ReviewRepository repository;

    public List<Review> listAll() {
        return repository.findAll();
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, REVIEW_PER_PAGE, repository);
    }

    public Review findById(Integer id) throws ReviewNotFoundException {
        try {
            return repository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new ReviewNotFoundException("Could not find any review with ID " + id);
        }
    }

    public void save(Review reviewInForm) {
        Review reviewInDB = repository.findById(reviewInForm.getId()).get();

        reviewInDB.setHeadline(reviewInForm.getHeadline());
        reviewInDB.setComment(reviewInForm.getComment());

        repository.save(reviewInDB);
    }

    public void delete(Integer id) throws ReviewNotFoundException {
        try {
            repository.findById(id).get();

            repository.deleteById(id);
        } catch (NoSuchElementException e) {
            throw new ReviewNotFoundException("Could not find any review with ID " + id);
        }
    }
}
