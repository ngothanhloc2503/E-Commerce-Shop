package com.shopping.customer;

import com.shopping.Utility;
import com.shopping.common.entity.Customer;
import com.shopping.common.exception.CustomerNotFoundException;
import com.shopping.setting.EmailSettingBag;
import com.shopping.setting.SettingService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.UnsupportedEncodingException;
import java.util.NoSuchElementException;

@Controller
public class ForgotPasswordController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private SettingService settingService;

    @GetMapping("/forgot_password")
    public String showForgotPasswordForm() {
        return "customer/forgot_password_form";
    }

    @PostMapping("/forgot_password")
    public String processRequestForm(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        try {
            String token = customerService.updateResetPasswordToken(email);
            String link = Utility.getSiteURL(request) + "/reset_password?token=" + token;
            sendEmail(email, link);
            model.addAttribute("message", "We have send a reset password link to your email.");
        } catch (CustomerNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
            model.addAttribute("error", "Could not send email!");
        }
        return "customer/forgot_password_form";
    }

    private void sendEmail(String email, String link) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);

        String toAddress = email;
        String subject = "Here's the link to reset your password.";
        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password. "
                + "Click the link below to change your password: </p>"
                + "<p><a href=\"" + link +"\">Change my password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, or you have not made the request.</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettings.getFromAddress(), emailSettings. getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);
    }

    @GetMapping("/reset_password")
    public String showResetPasswordForm(@Param("token") String token, Model model) {
        Customer customer = customerService.getByResetPasswordToken(token);

        if (customer != null) {
            model.addAttribute("token", token);
            return "customer/reset_password_form";

        } else {
            model.addAttribute("pageTitle", "Invalid Token");
            model.addAttribute("message", "Invalid Token!");
            return "message";
        }
    }

    @PostMapping("/reset_password")
    public String processResetForm(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        try {
            customerService.updatePassword(token, password);

            model.addAttribute("pageTitle", "Reset Your Password");
            model.addAttribute("title", "Reset Your Password");
            model.addAttribute("message", "You have successfully changed your password.");
        } catch (CustomerNotFoundException e) {
            model.addAttribute("pageTitle", "Invalid Token");
            model.addAttribute("title", "Reset Your Password");
            model.addAttribute("message", e.getMessage());
        }
        return "message";
    }
}
