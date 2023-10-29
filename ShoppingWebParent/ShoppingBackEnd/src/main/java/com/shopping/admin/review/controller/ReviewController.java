package com.shopping.admin.review.controller;

import com.shopping.admin.paging.PagingAndSortingHelper;
import com.shopping.admin.paging.PagingAndSortingParam;
import com.shopping.admin.review.service.ReviewService;
import com.shopping.common.entity.Review;
import com.shopping.common.exception.ReviewNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReviewController {
    private static final String defaultRedirect = "redirect:/reviews/page/1?sortField=id&sortDir=asc";

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/reviews")
    public String listFirstPage() {
        return defaultRedirect;
    }

    @GetMapping("/reviews/page/{pageNum}")
    public String listByPage(
            @PathVariable("pageNum") Integer pageNum,
            @PagingAndSortingParam(listName = "listReviews", moduleURL = "/reviews") PagingAndSortingHelper helper) {
        reviewService.listByPage(pageNum, helper);
        return "reviews/reviews";
    }

    @GetMapping("/reviews/detail/{id}")
    public String reviewDetails(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Review review = reviewService.findById(id);

            model.addAttribute("review", review);

            return "reviews/review_details_modal";
        } catch (ReviewNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());

            return defaultRedirect;
        }
    }

    @GetMapping("/reviews/edit/{id}")
    public String viewEditReview(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {

        try {
            Review review = reviewService.findById(id);

            model.addAttribute("review", review);
            model.addAttribute("pageTitle", "Edit Review (ID: " + id + ")");

            return "reviews/review_form";
        } catch (ReviewNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());

            return defaultRedirect;
        }
    }

    @PostMapping("/reviews/save")
    public String saveReview(Review review, RedirectAttributes redirectAttributes) {
        reviewService.save(review);

        redirectAttributes.addFlashAttribute("message", "The review ID " + review.getId()
                + " has been updated successfully.");

        return defaultRedirect;
    }

    @GetMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            reviewService.delete(id);
            redirectAttributes.addFlashAttribute("message", "The review ID " + id
                    + " has been deleted.");
        } catch (ReviewNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return defaultRedirect;
    }
}
