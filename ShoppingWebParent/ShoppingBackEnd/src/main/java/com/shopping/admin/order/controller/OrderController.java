package com.shopping.admin.order.controller;

import com.shopping.admin.order.OrderNotFoundException;
import com.shopping.admin.order.service.OrderService;
import com.shopping.admin.paging.PagingAndSortingHelper;
import com.shopping.admin.paging.PagingAndSortingParam;
import com.shopping.admin.security.ShoppingUserDetails;
import com.shopping.admin.setting.repository.CountryRepository;
import com.shopping.admin.setting.repository.StateRepository;
import com.shopping.common.entity.Country;
import com.shopping.common.entity.State;
import com.shopping.common.entity.order.Order;
import com.shopping.common.entity.order.OrderDetail;
import com.shopping.common.entity.order.OrderStatus;
import com.shopping.common.entity.order.OrderTrack;
import com.shopping.common.entity.product.Product;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

@Controller
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CountryRepository countryRepository;

    @GetMapping("/orders")
    public String listFirstPage() {
        return "redirect:/orders/page/1?sortField=id&sortDir=asc";
    }

    @GetMapping("/orders/page/{pageNum}")
    public String listByPage(
            @PathVariable("pageNum") Integer pageNum,
            @PagingAndSortingParam(listName = "listOrders", moduleURL = "/orders")PagingAndSortingHelper helper,
            @AuthenticationPrincipal ShoppingUserDetails loggedUser) {
        orderService.listAll(pageNum, helper);

        if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Salesperson")
                && loggedUser.hasRole("Shipper")) {
            return "orders/orders_shipper";
        }

        return "orders/orders";
    }

    @GetMapping("/orders/detail/{id}")
    public String orderDetails(@PathVariable("id") Integer orderId, Model model, RedirectAttributes redirectAttributes,
                               @AuthenticationPrincipal ShoppingUserDetails loggedUser) {
        try {
            Order order = orderService.findById(orderId);

            boolean isVisibleForAdminAndSalesPerson = false;
            if (loggedUser.hasRole("Admin") || loggedUser.hasRole("Salesperson")) {
                isVisibleForAdminAndSalesPerson = true;
            }

            model.addAttribute("isVisibleForAdminAndSalesPerson", isVisibleForAdminAndSalesPerson);
            model.addAttribute("order", order);

            return "orders/order_details_modal";
        } catch (OrderNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/orders/page/1?sortField=id&sortDir=asc";
        }
    }

    @GetMapping("/orders/edit/{id}")
    public String editOrder(@PathVariable("id") Integer orderId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.findById(orderId);

            List<Country> listCountries = orderService.listAllCountries();

            Country country = countryRepository.findByName(order.getCountry());
            List<State> listStates = stateRepository.findByCountryOrderByNameAsc(country);

            model.addAttribute("order", order);
            model.addAttribute("listCountries", listCountries);
            model.addAttribute("listStates", listStates);
            model.addAttribute("pageTitle", "Edit Order (ID: " + orderId + ")");

            return "orders/order_form";
        } catch (OrderNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/orders/page/1?sortField=id&sortDir=asc";
        }
    }

    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            orderService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "The order has been deleted!");
        } catch (OrderNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/orders/page/1?sortField=id&sortDir=asc";
    }

    @PostMapping("/orders/save")
    public String saveOrder(Order order, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String countryName = request.getParameter("countryName");
        order.setCountry(countryName);

        updateProductDetails(order, request);
        updateTrackDetails(order, request);

        orderService.save(order);

        redirectAttributes.addFlashAttribute("message", "The order ID " + order.getId()
                + " has been updated successfully.");

        return "redirect:/orders/page/1?sortField=id&sortDir=asc";
    }

    private void updateTrackDetails(Order order, HttpServletRequest request) {
        String[] trackIds = request.getParameterValues("trackId");
        String[] trackDates = request.getParameterValues("trackDate");
        String[] trackStatuses = request.getParameterValues("trackStatus");
        String[] trackNotes = request.getParameterValues("trackNotes");

        List<OrderTrack> orderTracks = order.getOrderTracks();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

        for (int i = 0; i < trackIds.length; i++) {
            OrderTrack track = new OrderTrack();
            Integer trackId = Integer.parseInt(trackIds[i]);
            if (trackId > 0) {
                track.setId(trackId);
            }

            track.setOrder(order);
            track.setStatus(OrderStatus.valueOf(trackStatuses[i]));
            track.setNotes(trackNotes[i]);
            try {
                track.setUpdatedTime(dateFormatter.parse(trackDates[i]));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            orderTracks.add(track);
        }
    }

    private void updateProductDetails(Order order, HttpServletRequest request) {
        String[] detailIds = request.getParameterValues("detailId");
        String[] productIds = request.getParameterValues("productId");
        String[] productCosts = request.getParameterValues("productDetailCost");
        String[] quantities = request.getParameterValues("quantity");
        String[] productPrices = request.getParameterValues("productPrice");
        String[] productSubtotals = request.getParameterValues("productSubtotal");
        String[] productShipCosts = request.getParameterValues("productShipCost");

        Set<OrderDetail> orderDetails = order.getOrderDetails();
        for (int i = 0; i < detailIds.length; i++) {
            OrderDetail orderDetail = new OrderDetail();
            Integer detailId = Integer.parseInt(detailIds[i]);
            if (detailId > 0) {
                orderDetail.setId(detailId);
            }
            orderDetail.setOrder(order);
            orderDetail.setProduct(new Product(Integer.parseInt(productIds[i])));
            orderDetail.setProductCost(Float.parseFloat(productCosts[i]));
            orderDetail.setQuantity(Integer.parseInt(quantities[i]));
            orderDetail.setUnitPrice(Float.parseFloat(productPrices[i]));
            orderDetail.setSubtotal(Float.parseFloat(productSubtotals[i]));
            orderDetail.setShippingCost(Float.parseFloat(productShipCosts[i]));

            orderDetails.add(orderDetail);
        }
    }
}
