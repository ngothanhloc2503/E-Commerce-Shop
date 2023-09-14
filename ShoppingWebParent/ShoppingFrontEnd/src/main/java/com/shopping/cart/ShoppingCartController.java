package com.shopping.cart;

import com.shopping.Utility;
import com.shopping.common.entity.CartItem;
import com.shopping.common.entity.Customer;
import com.shopping.common.exception.CustomerNotFoundException;
import com.shopping.customer.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService cartItemService;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/cart")
    public String viewCart(Model model, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        List<CartItem> cartItems = cartItemService.listCartItems(customer);

        float estimatedTotal = 0.0F;
        for (CartItem item : cartItems) {
            estimatedTotal += item.getSubTotal();
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("estimatedTotal", estimatedTotal);

        return "cart/shopping_cart";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request){
        String customerEmail = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.getCustomerByEmail(customerEmail);
    }
}
