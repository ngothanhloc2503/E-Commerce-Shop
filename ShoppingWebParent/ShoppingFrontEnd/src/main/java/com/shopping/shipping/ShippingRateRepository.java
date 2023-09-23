package com.shopping.shipping;

import com.shopping.common.entity.Country;
import com.shopping.common.entity.ShippingRate;
import org.springframework.data.repository.CrudRepository;

public interface ShippingRateRepository extends CrudRepository<ShippingRate, Integer> {
    public ShippingRate findByCountryAndState(Country country, String state);
}
