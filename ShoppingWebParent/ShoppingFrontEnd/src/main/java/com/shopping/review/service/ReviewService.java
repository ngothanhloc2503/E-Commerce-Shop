package com.shopping.review.service;

import com.shopping.common.entity.Review;
import com.shopping.common.entity.product.Product;
import com.shopping.common.exception.ReviewNotFoundException;
import com.shopping.review.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class ReviewService {
    public static final int REVIEW_PER_PAGE = 5;

    @Autowired
    private ReviewRepository repository;

    public Page<Review> listByCustomerByPage(Integer customerId, Integer pageNum, String sortField,
                                             String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, REVIEW_PER_PAGE, sort);

        if (keyword != null) {
            return repository.findAllByCustomerId(customerId, keyword, pageable);
        }

        return repository.findAllByCustomerId(customerId, pageable);
    }

    public Page<Review> listByProduct(Product product, Integer pageNum, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, REVIEW_PER_PAGE, sort);

        return repository.findByProduct(product, pageable);
    }


    public Review getByIdAndCustomer(Integer id, Integer customerId) throws ReviewNotFoundException {
        try {
            return repository.findByIdAAndCustomer(id, customerId);
        } catch (NoSuchElementException e) {
            throw new ReviewNotFoundException("Could not find any review with ID " + id);
        }
    }

    public Page<Review> list3MostRecentReviewsByProduct(Product product) {
        Sort sort = Sort.by("reviewTime").descending();

        Pageable pageable = PageRequest.of(0, 3, sort);

        return repository.findByProduct(product, pageable);
    }
}
