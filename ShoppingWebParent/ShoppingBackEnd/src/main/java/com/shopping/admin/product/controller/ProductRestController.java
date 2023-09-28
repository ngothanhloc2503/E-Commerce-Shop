package com.shopping.admin.product.controller;

import com.shopping.admin.product.ProductDTO;
import com.shopping.admin.product.service.ProductService;
import com.shopping.common.entity.product.Product;
import com.shopping.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRestController {
    @Autowired
    private ProductService productService;

    @PostMapping("/products/check_unique")
    public String checkUnique(Integer id, String name) {
        return productService.checkUnique(id, name);
    }

    @GetMapping("/products/get/{id}")
    public ProductDTO getProductInfo(@PathVariable("id") int productId) throws ProductNotFoundException {
        Product product = productService.get(productId);
        return new ProductDTO(product.getName(), product.getMainImagePath(), product.getDiscountPrice(), product.getCost());
    }
}
