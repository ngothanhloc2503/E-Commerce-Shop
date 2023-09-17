package com.shopping.address;

import com.shopping.common.entity.Address;
import com.shopping.common.entity.Customer;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;

    public List<Address> listAddressBook(Customer customer) {
        return addressRepository.findByCustomer(customer);
    }

    public void save(Address address) {
        addressRepository.save(address);
    }

    public Address get(Integer id, Integer customerId) {
        return addressRepository.findByIdAndCustomer(id, customerId);
    }

    public void delete(Integer id, Integer customerId) {
        addressRepository.deleteByIdAndCustomer(id, customerId);
    }

    public void setDefault(Integer addressId, Integer customerId) {
        if (addressId > 0) {
            addressRepository.setDefaultAddress(addressId);
        }

        addressRepository.setNonDefaultForOthers(addressId, customerId);

    }
}
