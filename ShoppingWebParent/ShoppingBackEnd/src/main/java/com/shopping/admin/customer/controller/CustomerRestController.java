package com.shopping.admin.customer.controller;

import com.shopping.admin.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerRestController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/customers/check_email_unique")
    public String checkEmailUnique(@Param("id") Integer id ,@Param("email") String email) {
        return customerService.isEmailUnique(id, email) ? "OK" : "Duplicated";
    }
}
