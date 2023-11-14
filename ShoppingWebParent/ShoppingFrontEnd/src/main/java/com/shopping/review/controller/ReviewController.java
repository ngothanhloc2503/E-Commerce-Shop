package com.shopping.review.controller;

import com.shopping.ControllerHelper;
import com.shopping.Utility;
import com.shopping.common.entity.Customer;
import com.shopping.common.entity.Review;
import com.shopping.common.entity.product.Product;
import com.shopping.common.exception.ProductNotFoundException;
import com.shopping.common.exception.ReviewNotFoundException;
import com.shopping.customer.CustomerService;
import com.shopping.product.ProductRepository;
import com.shopping.product.ProductService;
import com.shopping.review.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ReviewController {
    private static final String defaultRedirect = "redirect:/reviews/page/1?sortField=id&sortDir=asc";

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ControllerHelper controllerHelper;

    @Autowired
    private ProductService productService;

    @GetMapping("/reviews")
    public String listFirstPage() {
        return defaultRedirect;
    }

    @GetMapping("/reviews/page/{pageNum}")
    public String listByPage(Model model, @PathVariable("pageNum") Integer pageNum,
                             String sortField, String sortDir, String keyword,
                             HttpServletRequest request) {

        Customer customer = controllerHelper.getAuthenticatedCustomer(request);

        Page<Review> page = reviewService.listByCustomerByPage(customer.getId(), pageNum, sortField, sortDir, keyword);
        List<Review> listReviews = page.getContent();

        int startCount = (pageNum - 1) * reviewService.REVIEW_PER_PAGE + 1;
        int endCount = startCount + reviewService.REVIEW_PER_PAGE - 1;

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("listReviews", listReviews);
        model.addAttribute("moduleURL", "/reviews");

        return "reviews/reviews";
    }

    @GetMapping("/reviews/detail/{id}")
    public String reviewDetails(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes,
                                HttpServletRequest request) {
        try {
            Customer customer = controllerHelper.getAuthenticatedCustomer(request);
            Review review = reviewService.getByIdAndCustomer(id, customer.getId());

            model.addAttribute("review", review);

            return "reviews/review_details_modal";
        } catch (ReviewNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());

            return defaultRedirect;
        }
    }

    @GetMapping("/ratings/{productAlias}")
    public String listProductByFirstPage(@PathVariable("productAlias") String productAlias, Model model) {
        return listProductByPage(productAlias, model, 1, "reviewTime", "desc");
    }

    @GetMapping("/ratings/{productAlias}/page/{pageNum}")
    public String listProductByPage(@PathVariable("productAlias") String productAlias, Model model,
                                    @PathVariable("pageNum") int pageNum, String sortField, String sortDir) {
        Product product = null;
        try {
            product = productService.getProduct(productAlias);
        } catch (ProductNotFoundException e) {
            return "error/404";
        }

        Page<Review> page = reviewService.listByProduct(product, pageNum, sortField, sortDir);
        List<Review> listReviews = page.getContent();

        int startCount = (pageNum - 1) * reviewService.REVIEW_PER_PAGE + 1;
        int endCount = startCount + reviewService.REVIEW_PER_PAGE - 1;

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("product", product);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("listReviews", listReviews);

        return "reviews/reviews_product";
    }

    @GetMapping("/write_review/product/{productId}")
    public String showReviewForm(@PathVariable("productId") Integer productId, Model model,
                                 HttpServletRequest request) {
        Review review = new Review();
        Product product = null;

        try {
            product = productService.getProduct(productId);
        } catch (ProductNotFoundException e) {
            return "error/404";
        }

        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, product.getId());

        if (customerReviewed) {
            model.addAttribute("customerReviewed", customerReviewed);
        } else {
            boolean customerCanReview = reviewService.canCustomerReviewProduct(customer, product.getId());
            if (customerCanReview) {
                model.addAttribute("customerCanReview", customerCanReview);
            } else {
                model.addAttribute("NoReviewPermission", true);
            }
        }

        model.addAttribute("product", product);
        model.addAttribute("review", review);

        return "reviews/review_form";
    }

    @PostMapping("/post_review")
    public String saveReview(Model model, Review review, Integer productId, HttpServletRequest request) {
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        Product product = null;

        try {
            product = productService.getProduct(productId);
        } catch (ProductNotFoundException e) {
            return "error/404";
        }

        boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, product.getId());

        review.setCustomer(customer);
        review.setProduct(product);

        Review savedReview = reviewService.save(review);

        model.addAttribute("review", savedReview);

        return "reviews/review_done";
    }
}
