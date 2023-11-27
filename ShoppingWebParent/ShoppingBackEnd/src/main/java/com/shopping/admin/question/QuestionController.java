package com.shopping.admin.question;

import com.shopping.Utility;
import com.shopping.admin.paging.PagingAndSortingHelper;
import com.shopping.admin.paging.PagingAndSortingParam;
import com.shopping.admin.user.service.UserService;
import com.shopping.common.entity.Question;
import com.shopping.common.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class QuestionController {
    private static final String defaultRedirect = "redirect:/questions/page/1?sortField=id&sortDir=asc";

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @GetMapping("/questions")
    public String listFirstPage() {
        return defaultRedirect;
    }

    @GetMapping("/questions/page/{pageNum}")
    public String listByPage(
            @PathVariable("pageNum") int pageNum,
            @PagingAndSortingParam(listName = "listQuestions", moduleURL = "/questions") PagingAndSortingHelper helper) {
        questionService.listByPage(pageNum, helper);
        return "questions/questions";
    }

    @GetMapping("/questions/detail/{id}")
    public String reviewDetails(@PathVariable("id") Integer id, Model model) {
        Question question = questionService.findById(id);

        model.addAttribute("question", question);

        return "questions/question_details_modal";
    }

    @GetMapping("/questions/delete/{id}")
    public String deleteQuestion(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        questionService.delete(id);
        redirectAttributes.addFlashAttribute("message", "The question ID " + id
                + " has been deleted.");

        return defaultRedirect;
    }

    @GetMapping("/questions/{id}/approved/{status}")
    public String updateQuestionApprovalStatus(@PathVariable("id") Integer id,
                                               @PathVariable("status") boolean status,
                                               RedirectAttributes redirectAttributes) {
        questionService.updateQuestionApprovalStatus(id, status);
        String approval = status ? "approved" : "disapproved";
        String message = "The customer ID " + id + " has been " + approval;
        redirectAttributes.addFlashAttribute("message", message);

        return defaultRedirect;
    }

    @GetMapping("/questions/edit/{id}")
    public String viewEditReview(@PathVariable("id") Integer id, Model model) {

        Question question = questionService.findById(id);

        model.addAttribute("question", question);
        model.addAttribute("pageTitle", "Edit Question (ID: " + id + ")");

        return "questions/question_form";
    }

    @PostMapping("/questions/save")
    public String saveReview(Question question, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        questionService.save(question, user);

        redirectAttributes.addFlashAttribute("message", "The question ID " + question.getId()
                + " has been updated successfully.");

        return defaultRedirect;
    }

    public User getAuthenticatedUser(HttpServletRequest request){
        String userEmail = Utility.getEmailOfAuthenticatedCustomer(request);
        return userService.getUserByEmail(userEmail);
    }
}
