package com.shopping.admin.product.controller;

import com.shopping.admin.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
}
