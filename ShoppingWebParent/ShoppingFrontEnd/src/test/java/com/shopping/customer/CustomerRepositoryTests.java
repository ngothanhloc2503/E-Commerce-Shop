package com.shopping.customer;

import com.shopping.common.entity.Country;
import com.shopping.common.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CustomerRepositoryTests {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateCustomer() {
        Integer countryId = 123;
        Country country = entityManager.find(Country.class, countryId);

        Customer customer = new Customer();
        customer.setCountry(country);
        customer.setEmail("ntl@gmail.com");
        customer.setPassword("12345678");
        customer.setFirstName("ngo");
        customer.setLastName("loc");
        customer.setPhoneNumber("0123456789");
        customer.setAddressLine1("123 East Blue");
        customer.setCity("Piece");
        customer.setState("One");
        customer.setPostalCode("12345");
        customer.setCreatedTime(new Date());

        Customer savedCustomer = customerRepository.save(customer);

        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getId()).isGreaterThan(0);
    }

    @Test
    public void testListCustomers() {
        List<Customer> listCustomers = (List<Customer>) customerRepository.findAll();
        listCustomers.forEach(System.out::println);

        assertThat(listCustomers).isNotNull();
        assertThat(listCustomers).size().isGreaterThan(0);
    }

    @Test
    public void testUpdateCustomer() {
        Customer customer = customerRepository.findById(2).get();

        customer.setVerificationCode("a123");
        Customer savedCustomer = customerRepository.save(customer);

        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getVerificationCode()).isEqualTo("a123");
    }

    @Test
    public void testFindByEmail() {
        String email = "ntl@gmail.com";
        Customer customer = customerRepository.findByEmail(email);

        assertThat(customer).isNotNull();
        assertThat(customer.getId()).isGreaterThan(0);
    }

    @Test
    public void testFindByVerificationCode() {
        String code = "a123";
        Customer customer = customerRepository.findByVerificationCode(code);

        assertThat(customer).isNotNull();
        assertThat(customer.getId()).isGreaterThan(0);
    }

    @Test
    public void testEnableCustomer() {
        Integer id = 2;
        customerRepository.enable(id);

        Customer customer = customerRepository.findById(id).get();
        assertThat(customer.isEnabled()).isTrue();
    }
}
