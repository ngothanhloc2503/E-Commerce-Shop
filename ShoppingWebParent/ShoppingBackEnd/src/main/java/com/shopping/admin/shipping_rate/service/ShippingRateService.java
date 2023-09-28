package com.shopping.admin.shipping_rate.service;

import com.shopping.admin.paging.PagingAndSortingHelper;
import com.shopping.admin.product.repository.ProductRepository;
import com.shopping.admin.setting.repository.CountryRepository;
import com.shopping.admin.shipping_rate.exception.ShippingRateAlreadyExistException;
import com.shopping.admin.shipping_rate.exception.ShippingRateNotFoundException;
import com.shopping.admin.shipping_rate.repository.ShippingRateRepository;
import com.shopping.common.entity.ShippingRate;
import com.shopping.common.entity.product.Product;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class ShippingRateService {
    public static final int SHIPPING_RATES_PER_PAGE = 10;
    private static final int DIM_DIVISOR = 139;

    @Autowired
    private ShippingRateRepository shippingRateRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private ProductRepository productRepository;

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

    public float calculateShippingCost(Integer productId, Integer countryId, String state) throws ShippingRateNotFoundException {
        ShippingRate shippingRate = shippingRateRepository.findByCountryAndState(countryId, state);

        if (shippingRate == null) {
            throw new ShippingRateNotFoundException("No shipping rate found for the given destination. You have to "
                    + "enter shipping cost manually");
        }

        Product product = productRepository.findById(productId).get();

        float dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;
        float finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;

        return finalWeight * shippingRate.getRate();
    }
}
