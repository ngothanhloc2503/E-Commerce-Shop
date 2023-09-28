package com.shopping.admin.order.controller;

import com.shopping.admin.paging.PagingAndSortingHelper;
import com.shopping.admin.paging.PagingAndSortingParam;
import com.shopping.admin.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProductSearchController {
    @Autowired
    private ProductService productService;

    @GetMapping("/orders/search_product")
    public String showSearchProductPage() {
        return "orders/search_product";
    }

    @PostMapping("/orders/search_product")
    public String searchProduct(String keyword) {
        return "redirect:/orders/search_product/page/1?sortField=name&sortDir=asc&keyword=" + keyword;
    }

    @GetMapping("/orders/search_product/page/{pageNum}")
    public String searchProductByPage(
            @PagingAndSortingParam(listName = "listProducts", moduleURL = "/orders/search_product") PagingAndSortingHelper helper,
            @PathVariable("pageNum") int pageNum) {
        productService.searchProducts(pageNum, helper);
        return "/orders/search_product";
    }
}
