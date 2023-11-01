package com.shopping.admin.product.repository;

import com.shopping.admin.paging.SearchRepository;
import com.shopping.common.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Integer>,
        SearchRepository<Product, Integer> {

    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%"
            + " OR p.shortDescription LIKE %?1%"
            + " OR p.fullDescription LIKE %?1%"
            + " OR p.brand.name LIKE %?1%"
            + " OR p.category.name LIKE %?1%")
    public Page<Product> findAll(String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id = ?1"
            + " OR p.category.allParentIDs LIKE %?2%")
    public Page<Product> findAllInCategory(Integer categoryId, String categoryIdMatch, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE (p.category.id = ?1"
            + " OR p.category.allParentIDs LIKE %?2%)"
            + " AND (p.name LIKE %?3%"
            + " OR p.shortDescription LIKE %?3%"
            + " OR p.fullDescription LIKE %?3%"
            + " OR p.brand.name LIKE %?3%"
            + " OR p.category.name LIKE %?3%)")
    public Page<Product> searchInCategory(Integer categoryId, String categoryIdMatch, String keyword, Pageable pageable);

    public Product findByName(String name);

    @Query("UPDATE Product p SET p.enabled = ?2 WHERE p.id = ?1")
    @Modifying
    public void updateCategoryEnabledStatus(Integer id, boolean enabled);

    public Long countById(Integer id);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%")
    public Page<Product> searchProductByName(String keyword, Pageable pageable);

    @Query("Update Product p SET p.averageRating = COALESCE(CAST((SELECT AVG(r.rating) FROM Review r WHERE r.product.id = ?1) AS FLOAT), 0),"
            + " p.reviewCount = (SELECT COUNT(r.id) FROM Review r WHERE r.product.id =?1)"
            + " WHERE p.id = ?1")
    @Modifying
    public void updateReviewCountAndAverageRating(Integer productId);
}
