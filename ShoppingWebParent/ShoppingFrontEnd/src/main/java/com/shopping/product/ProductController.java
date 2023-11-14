package com.shopping.product;

import com.shopping.ControllerHelper;
import com.shopping.Utility;
import com.shopping.category.CategoryService;
import com.shopping.common.entity.Category;
import com.shopping.common.entity.Customer;
import com.shopping.common.entity.Review;
import com.shopping.common.entity.product.Product;
import com.shopping.common.exception.CategoryNotFoundException;
import com.shopping.common.exception.ProductNotFoundException;
import com.shopping.customer.CustomerService;
import com.shopping.review.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ControllerHelper controllerHelper;

    @GetMapping("/c/{category_alias}")
    public String viewCategoryFirstPage(@PathVariable("category_alias") String alias, Model model) {
        return viewCategoryByPage(alias, model, 1);
    }

    @GetMapping("/c/{category_alias}/page/{pageNum}")
    public String viewCategoryByPage(@PathVariable("category_alias") String alias, Model model,
                                     @PathVariable("pageNum") int pageNum) {
        try {
            Category category = categoryService.getCategoryByAliasEnabled(alias);
            List<Category> listCategoryParents = categoryService.getCategoryParents(category);

            Page<Product> pageProducts = productService.listByCategory(pageNum, category.getId());
            List<Product> listProducts = pageProducts.getContent();

            long startCount = (pageNum - 1) * productService.PRODUCTS_PER_PAGE + 1;
            long endCount = startCount + productService.PRODUCTS_PER_PAGE - 1;

            if (endCount > pageProducts.getTotalElements()) {
                endCount = pageProducts.getTotalElements();
            }

            model.addAttribute("currentPage", pageNum);
            model.addAttribute("startCount", startCount);
            model.addAttribute("endCount", endCount);
            model.addAttribute("totalItems", pageProducts.getTotalElements());
            model.addAttribute("totalPages", pageProducts.getTotalPages());
            model.addAttribute("pageTitle", category.getName());
            model.addAttribute("category", category);
            model.addAttribute("listCategoryParents", listCategoryParents);
            model.addAttribute("listProducts", listProducts);

            return "products/products_by_category";
        } catch (CategoryNotFoundException e) {
            return "error/404";
        }
    }

    @GetMapping("/p/{product_alias}")
    public String viewProductDetail(@PathVariable("product_alias") String alias, Model model,
                                    HttpServletRequest request) {
        try {
            Product product = productService.getProduct(alias);
            List<Category> listCategoryParents = categoryService.getCategoryParents(product.getCategory());
            Page<Review> listReviews = reviewService.list3MostRecentReviewsByProduct(product);

            Customer customer = controllerHelper.getAuthenticatedCustomer(request);
            if (customer != null) {
                boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, product.getId());

                if (customerReviewed) {
                    model.addAttribute("customerReviewed", customerReviewed);
                } else {
                    boolean customerCanReview = reviewService.canCustomerReviewProduct(customer, product.getId());
                    model.addAttribute("customerCanReview", customerCanReview);
                }
            }

            model.addAttribute("listReviews", listReviews);
            model.addAttribute("pageTitle", product.getShortName());
            model.addAttribute("product", product);
            model.addAttribute("listCategoryParents", listCategoryParents);

            return "products/product_detail";
        } catch (ProductNotFoundException e) {
            return "error/404";
        }
    }

    @GetMapping("/search")
    public String searchFirstPage(@Param("keyword") String keyword, Model model) {
        return searchByPage(keyword, model, 1);
    }

    @GetMapping("/search/page/{pageNum}")
    public String searchByPage(@Param("keyword") String keyword, Model model,
                               @PathVariable("pageNum") int pageNum) {
        Page<Product> pageProducts = productService.search(keyword, pageNum);
        List<Product> listResult = pageProducts.getContent();

        long startCount = (pageNum - 1) * productService.SEARCH_RESULT_PER_PAGE + 1;
        long endCount = startCount + productService.SEARCH_RESULT_PER_PAGE - 1;

        if (endCount > pageProducts.getTotalElements()) {
            endCount = pageProducts.getTotalElements();
        }

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", pageProducts.getTotalElements());
        model.addAttribute("totalPages", pageProducts.getTotalPages());
        model.addAttribute("pageTitle", keyword + " - Search Result");
        model.addAttribute("keyword", keyword);
        model.addAttribute("listResult", listResult);

        return "products/search_result";
    }
}
