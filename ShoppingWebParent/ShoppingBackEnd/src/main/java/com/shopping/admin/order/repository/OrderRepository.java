package com.shopping.admin.order.repository;

import com.shopping.admin.paging.SearchRepository;
import com.shopping.common.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Integer>,
        SearchRepository<Order, Integer> {

    @Query("SELECT o FROM Order o WHERE CONCAT( o.firstName, ' ', o.lastName, ' ', o.paymentMethod, ' ', " +
            " o.status, ' ', o.city , ' ', o.state, ' ', o.country, ' ', o.addressLine1, ' ', o.addressLine2) LIKE %?1%")
    public Page<Order> findAll(String keyword, Pageable pageable);
}
