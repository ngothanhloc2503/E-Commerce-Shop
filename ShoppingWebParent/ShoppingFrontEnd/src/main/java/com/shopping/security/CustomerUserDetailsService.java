package com.shopping.security;

import com.shopping.common.entity.Customer;
import com.shopping.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(email);

        if (customer != null) {
            return new CustomerUserDetails(customer);
        }

        throw new UsernameNotFoundException("Could not find customer with email: " + email);
    }
}
