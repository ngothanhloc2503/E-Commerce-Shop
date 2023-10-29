package com.shopping.admin.review.repository;

import com.shopping.admin.paging.SearchRepository;
import com.shopping.common.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Integer>,
        SearchRepository<Review, Integer> {

    @Query("SELECT r FROM Review r WHERE r.headline LIKE %?1% OR r.comment LIKE %?1% OR r.product.name LIKE %?1%"
            + " OR CONCAT(r.customer.firstName, ' ', r.customer.lastName) LIKE %?1%")
    public Page<Review> findAll(String keyword, Pageable pageable);
}
