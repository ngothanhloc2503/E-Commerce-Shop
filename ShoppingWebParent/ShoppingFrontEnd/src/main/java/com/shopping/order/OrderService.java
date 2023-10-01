package com.shopping.order;

import com.shopping.checkout.CheckoutInfo;
import com.shopping.common.entity.Address;
import com.shopping.common.entity.CartItem;
import com.shopping.common.entity.Customer;
import com.shopping.common.entity.order.*;
import com.shopping.common.entity.product.Product;
import com.shopping.common.exception.OrderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {
    public static final int ORDER_PER_PAGE = 5;

    @Autowired
    private OrderRepository orderRepository;

    public Order createOrder(Customer customer, Address address, List<CartItem> cartItems, PaymentMethod paymentMethod,
                             CheckoutInfo checkoutInfo) {
        Order order = new Order();
        order.setOrderTime(new Date());

        if (paymentMethod.equals(PaymentMethod.PAYPAL)) {
            order.setStatus(OrderStatus.PAID);
        } else {
            order.setStatus(OrderStatus.NEW);
        }

        order.setCustomer(customer);
        order.setProductCost(checkoutInfo.getProductCost());
        order.setSubtotal(checkoutInfo.getProductTotal());
        order.setShippingCost(checkoutInfo.getShippingCostTotal());
        order.setTax(0.0f);
        order.setTotal(checkoutInfo.getPaymentTotal());
        order.setPaymentMethod(paymentMethod);
        order.setDeliverDays(checkoutInfo.getDeliverDays());
        order.setDeliverDate(checkoutInfo.getDeliverDate());

        if (address == null) {
            order.copyAddressFromCustomer();
        } else {
            order.copyAddressFromAddress(address);
        }

        Set<OrderDetail> orderDetails = order.getOrderDetails();
        for (CartItem item : cartItems) {
            Product product = item.getProduct();

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setUnitPrice(product.getDiscountPrice());
            orderDetail.setProductCost(product.getCost() * item.getQuantity());
            orderDetail.setSubtotal(item.getSubTotal());
            orderDetail.setShippingCost(item.getShippingCost());

            orderDetails.add(orderDetail);
        }

        OrderTrack newTrack = new OrderTrack();
        newTrack.setOrder(order);
        newTrack.setStatus(OrderStatus.NEW);
        newTrack.setNotes(OrderStatus.NEW.defaultDescription());
        newTrack.setUpdatedTime(new Date());

        order.getOrderTracks().add(newTrack);

        return orderRepository.save(order);
    }

    public Page<Order> listAll(Integer customerId, int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, ORDER_PER_PAGE, sort);

        if (keyword != null) {
            return orderRepository.findAll(customerId, keyword, pageable);
        }

        return orderRepository.findAll(customerId, pageable);
    }

    public Order getOrder(Integer orderId, Customer customer) {
        return orderRepository.findByIdAndCustomer(orderId, customer);
    }

    public void setOrderReturnRequested(OrderReturnRequest request, Customer customer) throws OrderNotFoundException {
        Order order = orderRepository.findByIdAndCustomer(request.getOrderId(), customer);
        if (order == null) {
            throw new OrderNotFoundException("Could not find any order with ID " + request.getOrderId());
        }

        if (order.isReturnRequested() || order.isReturned()) return;

        OrderTrack track = new OrderTrack();
        track.setOrder(order);
        track.setUpdatedTime(new Date());
        track.setStatus(OrderStatus.RETURN_REQUESTED);

        String notes = "Reason: " + request.getReason();
        if (!"".equals(request.getNote())) {
            notes += ". " + request.getNote();
        }
        track.setNotes(notes);

        order.getOrderTracks().add(track);
        order.setStatus(OrderStatus.RETURN_REQUESTED);

        orderRepository.save(order);
    }
}
