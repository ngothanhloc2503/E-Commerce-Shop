package com.shopping.admin.shipping_rate.service;

import com.shopping.admin.paging.PagingAndSortingHelper;
import com.shopping.admin.setting.repository.CountryRepository;
import com.shopping.admin.shipping_rate.exception.ShippingRateAlreadyExistException;
import com.shopping.admin.shipping_rate.exception.ShippingRateNotFoundException;
import com.shopping.admin.shipping_rate.repository.ShippingRateRepository;
import com.shopping.common.entity.ShippingRate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class ShippingRateService {
    public static final int SHIPPING_RATES_PER_PAGE = 10;

    @Autowired
    private ShippingRateRepository shippingRateRepository;

    @Autowired
    private CountryRepository countryRepository;

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, SHIPPING_RATES_PER_PAGE, shippingRateRepository);
    }

    public ShippingRate get(Integer shippingRateId) throws ShippingRateNotFoundException {
        ShippingRate shippingRate = shippingRateRepository.findById(shippingRateId).get();

        if (shippingRate == null) {
            throw new ShippingRateNotFoundException("Not found shipping rate with ID " + shippingRateId);
        }
        return shippingRate;
    }

    public void save(ShippingRate shippingRate) throws ShippingRateAlreadyExistException {
        ShippingRate shippingRateInDB = shippingRateRepository.findByCountryAndState(
                shippingRate.getCountry().getId(), shippingRate.getState());
        boolean foundExistingRateInNewMode = shippingRate.getId() == null && shippingRateInDB != null;
        boolean foundDifferentExistingRateInEditMode = shippingRate.getId() != null && shippingRateInDB != null
                    && shippingRate.getId() != shippingRateInDB.getId();

        if (foundExistingRateInNewMode || foundDifferentExistingRateInEditMode) {
            throw new ShippingRateAlreadyExistException("There's already a rate for destination "
                + shippingRate.getCountry().getName() + ", " + shippingRate.getState());
        }
        shippingRateRepository.save(shippingRate);
    }

    public void delete(Integer id) throws ShippingRateNotFoundException {
        Optional<ShippingRate> shippingRate = shippingRateRepository.findById(id);

        if (!shippingRate.isPresent()) {
            throw new ShippingRateNotFoundException("Not found shipping rate with ID " + id);
        }

        shippingRateRepository.deleteById(id);
    }

    public void updateCODSupport(Integer id, boolean supported) throws ShippingRateNotFoundException {
        Optional<ShippingRate> shippingRate = shippingRateRepository.findById(id);

        if (!shippingRate.isPresent()) {
            throw new ShippingRateNotFoundException("Not found shipping rate with ID " + id);
        }
        ShippingRate shippingRateInDB = shippingRate.get();
        shippingRateInDB.setCodSupported(supported);

        shippingRateRepository.save(shippingRateInDB);
    }
}
