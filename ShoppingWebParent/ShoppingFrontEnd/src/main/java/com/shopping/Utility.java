package com.shopping;

import com.shopping.security.oauth.CustomerOAuth2User;
import com.shopping.setting.CurrencySettingBag;
import com.shopping.setting.EmailSettingBag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Properties;

public class Utility {
    public static String getSiteURL(HttpServletRequest request) {
        String siteUrl = request.getRequestURL().toString();
        return siteUrl.replace(request.getServletPath(), "");
    }

    public static JavaMailSenderImpl prepareMailSender(EmailSettingBag emailSettings) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(emailSettings.getHost());
        mailSender.setPort(emailSettings.getPort());
        mailSender.setUsername(emailSettings.getUsername());
        mailSender.setPassword(emailSettings.getPassword());

        Properties mailProperties = new Properties();
        mailProperties.setProperty("mail.smtp.auth", emailSettings.getSmtpAuth());
        mailProperties.setProperty("mail.smtp.starttls.enable", emailSettings.getSmtpSecured());

        mailSender.setJavaMailProperties(mailProperties);

        return mailSender;
    }

    public static String getEmailOfAuthenticatedCustomer(HttpServletRequest request) {
        Object principal = request.getUserPrincipal();
        if (principal == null) return null;

        String customerEmail = null;

        if (principal instanceof UsernamePasswordAuthenticationToken
                || principal instanceof RememberMeAuthenticationToken) {
            customerEmail = request.getUserPrincipal().getName();
        } else if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
            CustomerOAuth2User oAuth2User = (CustomerOAuth2User) oauth2Token.getPrincipal();
            customerEmail = oAuth2User.getEmail();
        }

        return customerEmail;
    }

    public static String formatCurrency(float amount, CurrencySettingBag settingBag) {
        String symbol = settingBag.getSymbol();
        String symbolPosition = settingBag.getSymbolPosition();
        String decimalPointType = settingBag.getDecimalPointType();
        String thousandPointType = settingBag.getThousandPointType();
        int decimalDigits = settingBag.getDecimalDigits();


        String pattern = symbolPosition.equals("Before price") ? symbol : "";
        pattern += "###,###";

        if (decimalDigits > 0) {
            pattern += ".";
            for (int i = 0; i < decimalDigits; i++) {
                pattern += "#";
            }
        }

        pattern = symbolPosition.equals("After price") ? symbol : "";

        char thousandSeparator = thousandPointType.equals("POINT") ? '.' : ',';
        char decimalSeparator = decimalPointType.equals("POINT") ? '.' : ',';

        DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
        decimalFormatSymbols.setDecimalSeparator(thousandSeparator);
        decimalFormatSymbols.setGroupingSeparator(decimalSeparator);

        DecimalFormat formatter = new DecimalFormat(pattern, decimalFormatSymbols);

        return formatter.format(amount);
    }
}
