package com.shopping.admin.product.service;

import com.shopping.admin.paging.PagingAndSortingHelper;
import com.shopping.admin.product.repository.ProductRepository;
import com.shopping.common.entity.product.Product;
import com.shopping.common.exception.ProductNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ProductService {
    public static int PRODUCT_PER_PAGE = 5;

    @Autowired
    private ProductRepository productRepository;

    public Page<Product> findAll() {
        return (Page<Product>) productRepository.findAll();
    }

    public void listByPage(Integer pageNum, PagingAndSortingHelper helper, Integer categoryId) {
        Pageable pageable = helper.createPageable(pageNum, PRODUCT_PER_PAGE);
        String keyword = helper.getKeyword();
        Page<Product> page = null;

        if (keyword != null && !keyword.isEmpty()) {
            if (categoryId != null && categoryId > 0) {
                String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
                page = productRepository.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
            } else {
                page = productRepository.findAll(keyword, pageable);
            }
        } else {
            if (categoryId != null && categoryId > 0) {
                String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
                page = productRepository.findAllInCategory(categoryId, categoryIdMatch, pageable);
            } else {
                page = productRepository.findAll(pageable);
            }
        }

        helper.updateModelAttributes(pageNum, page);
    }

    public void searchProducts(Integer pageNum, PagingAndSortingHelper helper) {
        Pageable pageable = helper.createPageable(pageNum, 6);
        String keyword = helper.getKeyword();

        Page<Product> page = productRepository.searchProductByName(keyword, pageable);

        helper.updateModelAttributes(pageNum, page);
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setCreatedTime(new Date());
        }

        if (product.getAlias() == null || product.getAlias().isEmpty()) {
            product.setAlias(product.getName().replace(" ", "-"));
        } else {
            product.setAlias(product.getAlias().replace(" ", "-"));
        }

        product.setUpdatedTime(new Date());

        Product updatedProduct = productRepository.save(product);
        productRepository.updateReviewCountAndAverageRating(updatedProduct.getId());

        return updatedProduct;
    }

    public void saveProductPrice(Product product) {
        Product productInDB = productRepository.findById(product.getId()).get();
        productInDB.setCost(product.getCost());
        productInDB.setPrice(product.getPrice());
        productInDB.setDiscountPercent(product.getDiscountPercent());

        productRepository.save(productInDB);
    }

    public String checkUnique(Integer id, String name) {
        boolean isCreate = (id == null || id == 0);

        Product productByName = productRepository.findByName(name);

        if (isCreate) {
            if (productByName != null) {
                return "Duplicate";
            }
        } else {
            if (productByName != null && productByName.getId() != id) {
                return "Duplicate";
            }
        }
        return "OK";
    }

    public void updateEnabledStatus(Integer id, boolean enabled) {
        productRepository.updateCategoryEnabledStatus(id, enabled);
    }

    public void delete(Integer id) throws ProductNotFoundException {
        Long countById = productRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new ProductNotFoundException("Could not find any product with id " + id);
        }
        productRepository.deleteById(id);
    }

    public Product get(Integer id) throws ProductNotFoundException {
        try {
            return productRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new ProductNotFoundException("Could not find any product with id " + id);
        }
    }
}
