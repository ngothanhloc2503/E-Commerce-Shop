package com.shopping.admin.customer.repository;

import com.shopping.admin.paging.SearchRepository;
import com.shopping.common.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CustomerRepository extends CrudRepository<Customer, Integer>,
        SearchRepository<Customer, Integer> {

    @Query("SELECT c FROM Customer c WHERE CONCAT(c.email, ' ', c.firstName, ' ', c.lastName, ' ', "
            + "c.addressLine1, ' ', c.addressLine2, ' ', c.city, ' ', c.state, "
            + "' ', c.postalCode, ' ', c.country.name) LIKE %?1%")
    public Page<Customer> findAll(String keyword, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE c.email = ?1")
    public Customer findByEmail(String email);

    @Query("UPDATE Customer c SET c.enabled = ?2 WHERE c.id = ?1")
    @Modifying
    public void updateCustomerEnabledStatus(Integer id, boolean enabled);
}

