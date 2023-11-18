package com.shopping.review;

import com.shopping.common.entity.Customer;
import com.shopping.common.entity.Review;
import com.shopping.common.entity.order.OrderStatus;
import com.shopping.common.entity.product.Product;
import com.shopping.common.exception.ReviewNotFoundException;
import com.shopping.order.OrderDetailRepository;
import com.shopping.product.ProductRepository;
import com.shopping.review.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ReviewService {
    public static final int REVIEW_PER_PAGE = 5;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    public Page<Review> listByCustomerByPage(Integer customerId, Integer pageNum, String sortField,
                                             String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, REVIEW_PER_PAGE, sort);

        if (keyword != null) {
            return reviewRepository.findAllByCustomerId(customerId, keyword, pageable);
        }

        return reviewRepository.findAllByCustomerId(customerId, pageable);
    }

    public Page<Review> listByProduct(Product product, Integer pageNum, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, REVIEW_PER_PAGE, sort);

        return reviewRepository.findByProduct(product, pageable);
    }


    public Review getByIdAndCustomer(Integer id, Integer customerId) throws ReviewNotFoundException {
        try {
            return reviewRepository.findByIdAAndCustomer(id, customerId);
        } catch (NoSuchElementException e) {
            throw new ReviewNotFoundException("Could not find any review with ID " + id);
        }
    }

    public Page<Review> list3MostRecentReviewsByProduct(Product product) {
        Sort sort = Sort.by("votes").descending();

        Pageable pageable = PageRequest.of(0, 3, sort);

        return reviewRepository.findByProduct(product, pageable);
    }

    public boolean didCustomerReviewProduct(Customer customer, Integer productId) {
        Long count = reviewRepository.countByCustomerAndProduct(customer.getId(), productId);
        return count > 0;
    }

    public boolean canCustomerReviewProduct(Customer customer, Integer productId) {
        Long count = orderDetailRepository.countByProductAndCustomerAndOrderStatus(productId, customer.getId(), OrderStatus.DELIVERED);

        return count > 0;
    }

    public Review save(Review review) {
        review.setReviewTime(new Date());

        Review savedReview = reviewRepository.save(review);
        productRepository.updateReviewCountAndAverageRating(review.getProduct().getId());

        return savedReview;
    }
}
