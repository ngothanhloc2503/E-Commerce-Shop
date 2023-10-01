package com.shopping.order;

import com.shopping.common.entity.Customer;
import com.shopping.common.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od JOIN od.product p "
            + "WHERE o.customer.id = ?1 "
            + "AND (p.name LIKE %?2% OR CAST(o.status AS STRING) LIKE %?2%)")
    public Page<Order> findAll(Integer customerId, String keyword, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.customer.id = ?1")
    public Page<Order> findAll(Integer customerId, Pageable pageable);

    public Order findByIdAndCustomer(Integer id, Customer customer);
}
