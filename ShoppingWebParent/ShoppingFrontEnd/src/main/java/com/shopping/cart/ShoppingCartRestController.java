package com.shopping.cart;

import com.shopping.Utility;
import com.shopping.common.entity.Customer;
import com.shopping.common.exception.CustomerNotFoundException;
import com.shopping.customer.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
public class ShoppingCartRestController {
    @Autowired
    private ShoppingCartService cartService;

    @Autowired
    private CustomerService customerService;

    @PostMapping("/cart/add/{productId}/{quantity}")
    public String addProductToCart(@PathVariable("productId") Integer productId,
                                   @PathVariable("quantity") Integer quantity,
                                   HttpServletRequest request, Model model) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            Integer updatedQuantity = cartService.addProduct(productId, quantity, customer);

            return updatedQuantity + " item(s) of this product were added to your shopping cart!";
        } catch (CustomerNotFoundException e) {
            return "You must login to add this product to cart";
        } catch (ShoppingCartException e) {
            return e.getMessage();
        }
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String customerEmail = Utility.getEmailOfAuthenticatedCustomer(request);
        if (customerEmail == null) {
            throw new CustomerNotFoundException("No authenticated customer");
        }

        return customerService.getCustomerByEmail(customerEmail);
    }

    @PostMapping("/cart/update/{productId}/{quantity}")
    public String updateProductToCart(@PathVariable("productId") Integer productId,
                                      @PathVariable("quantity") Integer quantity,
                                      HttpServletRequest request, Model model) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            float subtotal = cartService.updateQuantity(productId, quantity, customer);

            return String.valueOf(subtotal);
        } catch (CustomerNotFoundException e) {
            return "You must login to change quantity of product.";
        }
    }

    @DeleteMapping("/cart/remove/{productId}")
    public String removeProduct(@PathVariable("productId") Integer productId,
                                HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            cartService.removeProduct(productId, customer);

            return "The product has been removed from your shopping cart.";
        } catch (CustomerNotFoundException e) {
            return "You must login to remove product.";
        }
    }
}
