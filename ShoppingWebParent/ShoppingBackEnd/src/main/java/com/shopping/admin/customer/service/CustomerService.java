package com.shopping.admin.customer.service;

import com.shopping.admin.customer.repository.CustomerRepository;
import com.shopping.admin.paging.PagingAndSortingHelper;
import com.shopping.common.entity.Brand;
import com.shopping.common.entity.Customer;
import com.shopping.common.exception.CustomerNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class CustomerService {
    public static int CUSTOMERS_PER_PAGE = 10;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder encoder;

    public List<Customer> listAll() {
        return (List<Customer>) customerRepository.findAll();
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, CUSTOMERS_PER_PAGE, customerRepository);
    }

    public Customer findById(Integer id) throws CustomerNotFoundException {
        try {
            return customerRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new CustomerNotFoundException("Could not find any customer with ID " + id);
        }
    }

    public boolean isEmailUnique(Integer id, String email) {
        Customer customer = customerRepository.findByEmail(email);

        if (customer != null && id != customer.getId()) {
            return false;
        }
        return true;
    }

    public void save(Customer customer) {
        Customer customerInDB = customerRepository.findById(customer.getId()).get();

        if (customer.getPassword().isBlank()) {
            customer.setPassword(customerInDB.getPassword());
        } else {
            encodePassword(customer);
        }

        customer.setCreatedTime(customerInDB.getCreatedTime());
        customer.setEnabled(customerInDB.isEnabled());

        customerRepository.save(customer);
    }

    private void encodePassword(Customer customer) {
        String encode = encoder.encode(customer.getPassword());
        customer.setPassword(encode);
    }

    public void deleteById(Integer id) {
        customerRepository.deleteById(id);
    }

    public void updateCustomerEnabledStatus(Integer id, boolean enabled) {
        customerRepository.updateCustomerEnabledStatus(id, enabled);
    }
}
