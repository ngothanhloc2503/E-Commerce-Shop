package com.shopping.admin.shipping_rate.controller;

import com.shopping.admin.shipping_rate.exception.ShippingRateNotFoundException;
import com.shopping.admin.shipping_rate.service.ShippingRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShippingRateRestController {
    @Autowired
    private ShippingRateService shippingRateService;

    @PostMapping("/get_shipping_cost")
    public String getShippingCost(Integer productId, Integer countryId, String state) throws ShippingRateNotFoundException {
        return String.valueOf(shippingRateService.calculateShippingCost(productId, countryId, state));
    }
}
