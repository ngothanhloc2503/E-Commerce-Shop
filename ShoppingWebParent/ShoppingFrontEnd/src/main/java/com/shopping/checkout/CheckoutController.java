package com.shopping.checkout;

import com.shopping.Utility;
import com.shopping.address.AddressService;
import com.shopping.cart.ShoppingCartService;
import com.shopping.checkout.paypal.PayPalApiException;
import com.shopping.checkout.paypal.PayPalService;
import com.shopping.common.entity.Address;
import com.shopping.common.entity.CartItem;
import com.shopping.common.entity.Customer;
import com.shopping.common.entity.ShippingRate;
import com.shopping.common.entity.order.Order;
import com.shopping.common.entity.order.PaymentMethod;
import com.shopping.customer.CustomerService;
import com.shopping.order.OrderService;
import com.shopping.setting.CurrencySettingBag;
import com.shopping.setting.EmailSettingBag;
import com.shopping.setting.PaymentSettingBag;
import com.shopping.setting.SettingService;
import com.shopping.shipping.ShippingRateService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
public class CheckoutController {
    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ShippingRateService shippingRateService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private PayPalService payPalService;

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);

        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate shippingRate = null;

        if (defaultAddress != null) {
            model.addAttribute("shippingAddress", defaultAddress.toString());
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } else {
            model.addAttribute("shippingAddress", customer.toString());
            shippingRate = shippingRateService.getShippingRateForCustomer(customer);
        }

        if (shippingRate == null) {
            return "redirect:/cart";
        }

        List<CartItem> cartItems = shoppingCartService.listCartItems(customer);
        CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);

        String currencyCode = settingService.getCurrencyCode();
        PaymentSettingBag paymentSettings = settingService.getPaymentSettings();
        String paypalClientId = paymentSettings.getClientID();

        model.addAttribute("paypalClientId", paypalClientId);
        model.addAttribute("customer", customer);
        model.addAttribute("currencyCode", currencyCode);
        model.addAttribute("checkoutInfo", checkoutInfo);
        model.addAttribute("cartItems", cartItems);

        return "checkout/checkout";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request){
        String customerEmail = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.getCustomerByEmail(customerEmail);
    }

    @PostMapping("/place_order")
    public String placeOrder(HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        String paymentType = request.getParameter("paymentMethod");
        PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentType);

        Customer customer = getAuthenticatedCustomer(request);

        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate shippingRate = null;

        if (defaultAddress != null) {
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } else {
            shippingRate = shippingRateService.getShippingRateForCustomer(customer);
        }

        List<CartItem> cartItems = shoppingCartService.listCartItems(customer);
        CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);

        Order order = orderService.createOrder(customer, defaultAddress, cartItems, paymentMethod, checkoutInfo);
        shoppingCartService.deleteByCustomer(customer);
        sendOrderConfirmationEmail(request, order);

        return "checkout/order_completed";
    }

    private void sendOrderConfirmationEmail(HttpServletRequest request, Order order) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
        mailSender.setDefaultEncoding("utf-8");

        String toAddress = order.getCustomer().getEmail();
        String subject = emailSettings.getOrderConfirmationSubject();
        String content = emailSettings.getOrderConfirmationContent();

        subject.replace("[[orderId]]", String.valueOf(order.getId()));

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        DateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss E, dd MMM yyyy");
        String orderTime = dateFormatter.format(order.getOrderTime());

        CurrencySettingBag currencySettings = settingService.getCurrencySettings();
        String total = Utility.formatCurrency(order.getTotal(), currencySettings);

        content = content.replace("[[name]]", order.getCustomer().getFullName());
        content = content.replace("[[orderId]]", String.valueOf(order.getId()));
        content = content.replace("[[orderTime]]", orderTime);
        content = content.replace("[[shippingAddress]]", order.getAddress());
        content = content.replace("[[total]]", total);
        content = content.replace("[[paymentMethod]]", order.getPaymentMethod().toString());

        helper.setText(content, true);
        mailSender.send(mimeMessage);
    }

    @PostMapping("/process_paypal_order")
    public String processPayPalOrder(HttpServletRequest request, Model model)
            throws MessagingException, UnsupportedEncodingException {
        String orderId = request.getParameter("orderId");

        String pageTitle = "Checkout Failure";
        String message = null;
        try {
            if (payPalService.validateOrder(orderId)) {
                return placeOrder(request);
            } else {
                message = "ERROR: Transaction could not be completed because order information is invalid!";
            }
        } catch (PayPalApiException e) {
            message = "ERROR: Transaction failed due to error: " + e.getMessage();
        }

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("message", message);

        return "message";
    }
}
