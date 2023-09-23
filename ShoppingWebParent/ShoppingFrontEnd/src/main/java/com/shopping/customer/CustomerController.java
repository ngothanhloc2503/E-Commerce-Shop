package com.shopping.customer;

import com.shopping.Utility;
import com.shopping.common.entity.Country;
import com.shopping.common.entity.Customer;
import com.shopping.common.entity.State;
import com.shopping.security.CustomerUserDetails;
import com.shopping.security.oauth.CustomerOAuth2User;
import com.shopping.setting.CountryRepository;
import com.shopping.setting.EmailSettingBag;
import com.shopping.setting.SettingService;
import com.shopping.setting.StateRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private StateRepository stateRepository;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        List<Country> listCountries = customerService.listAllCountries();

        model.addAttribute("pageTitle", "Customer Registration");
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("customer", new Customer());

        return "register/register_form";
    }

    @PostMapping("/create_customer")
    public String createCustomer(Customer customer, Model model, HttpServletRequest request)
            throws MessagingException, UnsupportedEncodingException {
        customerService.registerCustomer(customer);
        sendVerificationEmail(request, customer);

        model.addAttribute("pageTitle", "Registration Succeeded!");

        return "register/register_success";
    }

    private void sendVerificationEmail(HttpServletRequest request, Customer customer)
            throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);

        String toAddress = customer.getEmail();
        String subject = emailSettings.getCustomerVerifySubject();
        String content = emailSettings.getCustomerVerifyContent();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettings.getFromAddress(), emailSettings. getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", customer.getFullName());

        String verifyURL = Utility.getSiteURL(request) + "/verify?code=" + customer.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);
    }

    @GetMapping("/verify")
    public String verifyAccount(@Param("code") String code, Model model) {
        boolean verified = customerService.verify(code);

        return "register/" + (verified ? "verify_success" : "verify_fail");
    }

    @GetMapping("/account_details")
    public String viewAccountDetails(Model model,
                                     HttpServletRequest request) {
        String customerEmail = Utility.getEmailOfAuthenticatedCustomer(request);

        Customer customer = customerService.getCustomerByEmail(customerEmail);
        List<Country> listCountries = countryRepository.findAllByOrderByNameAsc();
        List<State> listStates = stateRepository.findByCountryOrderByNameAsc(customer.getCountry());

        model.addAttribute("customer", customer);
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("listStates", listStates);

        return "customer/account_form";
    }

    @PostMapping("/account_details/save")
    public String saveDetails(Customer customer, RedirectAttributes redirectAttributes,
                              HttpServletRequest request){
        customerService.update(customer);
        redirectAttributes.addFlashAttribute("message", "Your account details have been updated.");

        updateNameForAuthenticatedCustomer(customer, request);

        String redirectOption = request.getParameter("redirect");
        String redirectURL = "redirect:/account_details";

        if ("address_book".equals(redirectOption)) {
            redirectURL = "redirect:/address_book";
        } else if ("cart".equals(redirectOption)) {
            redirectURL = "redirect:/cart";
        } else if ("checkout".equals(redirectOption)) {
            redirectURL = "redirect:/address_book?redirect=checkout";
        }

        return redirectURL;
    }

    private void updateNameForAuthenticatedCustomer(Customer customer, HttpServletRequest request) {
        Object principal = request.getUserPrincipal();

        if (principal instanceof UsernamePasswordAuthenticationToken
                || principal instanceof RememberMeAuthenticationToken) {
            CustomerUserDetails userDetails = getCustomerUserDetailsObject(principal);
            Customer authenticatedCustomer = userDetails.getCustomer();
            authenticatedCustomer.setFirstName(customer.getFirstName());
            authenticatedCustomer.setLastName(customer.getLastName());

        } else if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
            CustomerOAuth2User oAuth2User = (CustomerOAuth2User) oauth2Token.getPrincipal();
            oAuth2User.setFullName(customer.getFullName());
        }
    }

    private CustomerUserDetails getCustomerUserDetailsObject(Object principal) {
        CustomerUserDetails customerUserDetails = null;
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
            customerUserDetails = (CustomerUserDetails) token.getPrincipal();
        } else if (principal instanceof RememberMeAuthenticationToken) {
            RememberMeAuthenticationToken token = (RememberMeAuthenticationToken) principal;
            customerUserDetails = (CustomerUserDetails) token.getPrincipal();
        }

        return customerUserDetails;
    }
}
