package com.shopping.security;

import com.shopping.common.entity.AuthenticationType;
import com.shopping.common.entity.Customer;
import com.shopping.customer.CustomerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DatabaseLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    private CustomerService customerService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        CustomerUserDetails customerUserDetails = (CustomerUserDetails) authentication.getPrincipal();

        Customer customer = customerUserDetails.getCustomer();

        customerService.updateAuthenticationType(customer, AuthenticationType.DATABASE);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
