package com.shopping.review.repository;

import com.shopping.common.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    @Query("SELECT r FROM Review r WHERE r.customer.id = ?1")
    public Page<Review> findAllByCustomerId(Integer customerId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.customer.id = ?1 AND r.headline LIKE %?2% OR r.comment LIKE %?2%"
            + " OR r.product.name LIKE %?2%")
    public Page<Review> findAllByCustomerId(Integer customerId, String keyword, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.id = ?1 AND r.customer.id = ?2")
    public Review findByIdAAndCustomer(Integer id, Integer customerId);
}
