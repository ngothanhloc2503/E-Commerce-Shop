package com.shopping.customer;

import com.shopping.common.entity.AuthenticationType;
import com.shopping.common.entity.Country;
import com.shopping.common.entity.Customer;
import com.shopping.common.exception.CustomerNotFoundException;
import com.shopping.setting.CountryRepository;
import jakarta.transaction.Transactional;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Customer findById(Integer id) {
        return customerRepository.findById(id).get();
    }

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public boolean isEmailUnique(String email) {
        Customer customer = customerRepository.findByEmail(email);
        return (customer == null);
    }

    public void registerCustomer(Customer customer) {
        encodePassword(customer);
        customer.setEnabled(false);
        customer.setCreatedTime(new Date());
        customer.setAuthenticationType(AuthenticationType.DATABASE);

        String randomCode = RandomString.make(64);
        customer.setVerificationCode(randomCode);

        customerRepository.save(customer);
    }

    public void update(Customer customer) {
        Customer customerInDB = customerRepository.findById(customer.getId()).get();

        if (customerInDB.getAuthenticationType().equals(AuthenticationType.DATABASE)) {
            if (customer.getPassword().isEmpty()) {
                customer.setPassword(customerInDB.getPassword());
            } else {
                encodePassword(customer);
            }
        } else {
            customer.setPassword(customerInDB.getPassword());
        }

        customer.setCreatedTime(customerInDB.getCreatedTime());
        customer.setEnabled(customerInDB.isEnabled());
        customer.setVerificationCode(customerInDB.getVerificationCode());
        customer.setAuthenticationType(customerInDB.getAuthenticationType());
        customer.setResetPasswordToken(customerInDB.getResetPasswordToken());

        customerRepository.save(customer);
    }

    private void encodePassword(Customer customer) {
        String encode = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encode);
    }

    public boolean verify(String verificationCode) {
        Customer customer = customerRepository.findByVerificationCode(verificationCode);
        if (customer == null || customer.isEnabled()) {
            return false;
        } else {
            customerRepository.enable(customer.getId());
            return true;
        }
    }

    public void updateAuthenticationType(Customer customer, AuthenticationType type) {
        if (!customer.getAuthenticationType().equals(type)) {
            customerRepository.updateAuthenticationType(customer.getId(), type);
        }
    }

    public void addNewCustomerUponOAuthLogin(String name, String email, String countryCode, AuthenticationType authenticationType) {
        Customer customer = new Customer();
        customer.setEmail(email);

        setName(customer, name);

        customer.setLastName("");
        customer.setEnabled(true);
        customer.setCreatedTime(new Date());
        customer.setAuthenticationType(authenticationType);
        customer.setPassword("");
        customer.setAddressLine1("");
        customer.setCity("");
        customer.setState("");
        customer.setPhoneNumber("");
        customer.setPostalCode("");
        customer.setCountry(countryRepository.findByCode(countryCode));

        customerRepository.save(customer);
    }

    private void setName(Customer customer, String name) {
        String[] nameArray = name.split(" ");
        if (nameArray.length < 2) {
            customer.setFirstName(name);
            customer.setLastName("");
        } else {
            String firstName = nameArray[0];
            customer.setFirstName(firstName);

            String lastName = name.substring(firstName.length() + 1);
            customer.setLastName(lastName);
        }
    }

    public String updateResetPasswordToken(String email) throws CustomerNotFoundException {
        Customer customer = customerRepository.findByEmail(email);
        if (customer != null) {
            String token = RandomString.make(30);
            customer.setResetPasswordToken(token);
            customerRepository.save(customer);
            return token;
        } else {
            throw new CustomerNotFoundException("Could not find any customer with the email " + email);
        }
    }

    public Customer getByResetPasswordToken(String token) {
        return customerRepository.findByResetPasswordToken(token);
    }

    public void updatePassword(String token, String password) throws CustomerNotFoundException {
        Customer customer = customerRepository.findByResetPasswordToken(token);
        if (customer != null) {
            customer.setPassword(password);
            encodePassword(customer);
            customer.setResetPasswordToken(null);
            customerRepository.save(customer);
        } else {
            throw new CustomerNotFoundException("No customer found: invalid token!");
        }
    }
}
