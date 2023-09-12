package com.shopping.security.oauth;

import com.shopping.common.entity.AuthenticationType;
import com.shopping.common.entity.Customer;
import com.shopping.customer.CustomerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private CustomerService customerService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        CustomerOAuth2User oAuth2User = (CustomerOAuth2User) authentication.getPrincipal();

        String name = oAuth2User.getName();
        String email = oAuth2User.getEmail();
        String countryCode = request.getLocale().getCountry();
        String clientName = oAuth2User.getClientName();

        AuthenticationType authenticationType = getAuthenticationType(clientName);

        Customer customerByEmail = customerService.getCustomerByEmail(email);
        if (customerByEmail == null) {
            customerService.addNewCustomerUponOAuthLogin(name, email, countryCode, authenticationType);
        } else {
            oAuth2User.setFullName(customerByEmail.getFullName());
            customerService.updateAuthenticationType(customerByEmail, authenticationType);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private AuthenticationType getAuthenticationType(String clientName) {
        if (clientName.equals("Google")) {
            return AuthenticationType.GOOGLE;
        } else if (clientName.equals("Facebook")) {
            return AuthenticationType.FACEBOOK;
        }
        return AuthenticationType.DATABASE;
    }
}
