package com.shopping.order;

import com.shopping.Utility;
import com.shopping.common.entity.Customer;
import com.shopping.common.exception.CustomerNotFoundException;
import com.shopping.common.exception.OrderNotFoundException;
import com.shopping.customer.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderRestController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/orders/return")
    public ResponseEntity<?> handleOrderReturnRequest(@RequestBody OrderReturnRequest returnRequest,
            HttpServletRequest servletRequest) {
        System.out.println(returnRequest.getOrderId());
        System.out.println(returnRequest.getReason());
        System.out.println(returnRequest.getNote());

        Customer customer = null;
        try {
             customer = getAuthenticatedCustomer(servletRequest);
        } catch (CustomerNotFoundException e) {
            return new ResponseEntity<>("Authentication required", HttpStatus.BAD_REQUEST);
        }

        try {
            orderService.setOrderReturnRequested(returnRequest, customer);
        } catch (OrderNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new OrderReturnResponse(returnRequest.getOrderId()), HttpStatus.OK);
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String customerEmail = Utility.getEmailOfAuthenticatedCustomer(request);
        if (customerEmail == null) {
            throw new CustomerNotFoundException("No Authenticated Customer");
        }

        return customerService.getCustomerByEmail(customerEmail);
    }
}
