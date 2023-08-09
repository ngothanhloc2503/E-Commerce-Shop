package com.shopping.admin.product.repository;

import com.shopping.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer>, PagingAndSortingRepository<Product, Integer> {

    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%")
    Page<Product> findAll(String keyword, Pageable pageable);

    Product findByName(String name);

    @Query("UPDATE Product p SET p.enabled = ?2 WHERE p.id = ?1")
    @Modifying
    public void updateCategoryEnabledStatus(Integer id, boolean enabled);

    Long countById(Integer id);
}