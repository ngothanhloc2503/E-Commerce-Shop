package com.shopping.review.controller;

import com.shopping.Utility;
import com.shopping.common.entity.Customer;
import com.shopping.common.entity.Review;
import com.shopping.common.exception.ReviewNotFoundException;
import com.shopping.customer.CustomerService;
import com.shopping.review.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ReviewController {
    private static final String defaultRedirect = "redirect:/reviews/page/1?sortField=id&sortDir=asc";

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/reviews")
    public String listFirstPage() {
        return defaultRedirect;
    }

    @GetMapping("/reviews/page/{pageNum}")
    public String listByPage(Model model, @PathVariable("pageNum") Integer pageNum,
                             String sortField, String sortDir, String keyword,
                             HttpServletRequest request) {

        Customer customer = getAuthenticatedCustomer(request);

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
            Customer customer = getAuthenticatedCustomer(request);
            Review review = reviewService.getByIdAndCustomer(id, customer.getId());

            model.addAttribute("review", review);

            return "reviews/review_details_modal";
        } catch (ReviewNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());

            return defaultRedirect;
        }
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request){
        String customerEmail = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.getCustomerByEmail(customerEmail);
    }
}
