package com.shopping.admin.order.service;

import com.shopping.admin.order.OrderNotFoundException;
import com.shopping.admin.order.repository.OrderRepository;
import com.shopping.admin.paging.PagingAndSortingHelper;
import com.shopping.admin.setting.repository.CountryRepository;
import com.shopping.common.entity.Country;
import com.shopping.common.entity.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderService {
    public static final int ORDERS_PER_PAGE = 10;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CountryRepository countryRepository;

    public List<Order> listAll() {
        return (List<Order>) orderRepository.findAll();
    }

    public void listAll(int pageNum, PagingAndSortingHelper helper) {
        String sortField = helper.getSortField();
        String sortDir = helper.getSortDir();
        String keyword = helper.getKeyword();

        Sort sort = null;

        if ("destination".equals(sortField)) {
            sort = Sort.by("country").and(Sort.by("state")).and(Sort.by("city"));
        } else {
            sort = Sort.by(sortField);
        }

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);

        Page<Order> page = null;

        if (keyword != null) {
            page = orderRepository.findAll(keyword, pageable);
        } else {
            page = orderRepository.findAll(pageable);
        }

        helper.updateModelAttributes(pageNum, page);
    }

    public Order findById (Integer id) throws OrderNotFoundException {
        try {
            return orderRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new OrderNotFoundException("Could not find any order with ID " + id);
        }
    }

    public void deleteById(Integer id) throws OrderNotFoundException {
        try {
            orderRepository.findById(id).get();
            orderRepository.deleteById(id);
        } catch (NoSuchElementException e) {
            throw new OrderNotFoundException("Could not find any order with ID " + id);
        }
    }

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public void save(Order orderInForm) {
        Order orderInDB = orderRepository.findById(orderInForm.getId()).get();

        orderInForm.setOrderTime(orderInDB.getOrderTime());
        orderInForm.setCustomer(orderInDB.getCustomer());

        orderRepository.save(orderInForm);
    }
}

