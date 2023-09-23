package com.shopping.order;

import com.shopping.checkout.CheckoutInfo;
import com.shopping.common.entity.Address;
import com.shopping.common.entity.CartItem;
import com.shopping.common.entity.Customer;
import com.shopping.common.entity.order.Order;
import com.shopping.common.entity.order.OrderDetail;
import com.shopping.common.entity.order.OrderStatus;
import com.shopping.common.entity.order.PaymentMethod;
import com.shopping.common.entity.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {
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

        return orderRepository.save(order);
    }
}
