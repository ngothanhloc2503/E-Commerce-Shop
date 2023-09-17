package com.shopping.address;

import com.shopping.common.entity.Address;
import com.shopping.common.entity.Country;
import com.shopping.common.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class AddressRepositoryTests {
    @Autowired
    private AddressRepository addressRepository;

    @Test
    public void testAddNew() {
        Integer customerId = 5;
        Integer countryId = 234; // USA

        Address newAddress = new Address();
        newAddress.setCustomer(new Customer(customerId));
        newAddress.setCountry(new Country(countryId));
        newAddress.setFirstName("Tobie");
        newAddress.setLastName("Abel");
        newAddress.setPhoneNumber("19094644165");
        newAddress.setAddressLine1("4213 Gordon Street");
        newAddress.setAddressLine2("Novak Building");
        newAddress.setCity("Chino");
        newAddress.setState("California");
        newAddress.setPostalCode("91710");

        Address savedAddress = addressRepository.save(newAddress);

        assertThat(savedAddress).isNotNull();
        assertThat(savedAddress.getId()).isGreaterThan(0);
    }

    @Test
    public void testFindByCustomer() {
        Integer customerId = 5;
        List<Address> listAddresses = addressRepository.findByCustomer(new Customer(customerId));
        listAddresses.forEach(System.out::println);

        assertThat(listAddresses.size()).isGreaterThan(0);
    }

    @Test
    public void testFindByIdAndCustomer() {
        Integer addressId = 1;
        Integer customerId = 5;

        Address address = addressRepository.findByIdAndCustomer(addressId, customerId);
        System.out.println(address);

        assertThat(address).isNotNull();
    }

    @Test
    public void testUpdate() {
        Integer addressId = 2;
//        String phoneNumber = "646-232-3932";

        Address address = addressRepository.findById(addressId).get();
//        address.setPhoneNumber(phoneNumber);
        address.setDefaultForShipping(true);

        Address updatedAddress = addressRepository.save(address);
//        assertThat(updatedAddress.getPhoneNumber()).isEqualTo(phoneNumber);
    }
}
