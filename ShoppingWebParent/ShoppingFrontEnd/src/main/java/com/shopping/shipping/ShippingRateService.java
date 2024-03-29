package com.shopping.shipping;

import com.shopping.common.entity.Address;
import com.shopping.common.entity.Country;
import com.shopping.common.entity.Customer;
import com.shopping.common.entity.ShippingRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShippingRateService {
    @Autowired
    private ShippingRateRepository shippingRateRepository;

    public ShippingRate getShippingRateForCustomer(Customer customer) {
        String state = customer.getState();

        if (state == null || state.isEmpty()) {
            state = customer.getCity();
        }

        return shippingRateRepository.findByCountryAndState(customer.getCountry(), state);
    }

    public ShippingRate getShippingRateForAddress(Address address) {
        String state = address.getState();

        if (state == null || state.isEmpty()) {
            state = address.getCity();
        }

        return shippingRateRepository.findByCountryAndState(address.getCountry(), state);
    }
}
