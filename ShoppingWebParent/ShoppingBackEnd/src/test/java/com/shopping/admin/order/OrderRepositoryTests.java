package com.shopping.admin.order;

import com.shopping.admin.order.repository.OrderRepository;
import com.shopping.common.entity.*;
import com.shopping.common.entity.order.*;
import com.shopping.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class OrderRepositoryTests {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateNewOrderWithSingleProduct() {
        Customer customer = entityManager.find(Customer.class, 1);
        Product product = entityManager.find(Product.class, 1);

        Order mainOrder = new Order();
        mainOrder.setCustomer(customer);
        mainOrder.copyAddressFromCustomer();

        mainOrder.setShippingCost(10);
        mainOrder.setProductCost(product.getCost());
        mainOrder.setTax(0);
        mainOrder.setSubtotal(product.getPrice());
        mainOrder.setTotal(product.getPrice() + 10);

        mainOrder.setPaymentMethod(PaymentMethod.COD);
        mainOrder.setStatus(OrderStatus.NEW);
        mainOrder.setOrderTime(new Date());
        mainOrder.setDeliverDate(new Date());
        mainOrder.setDeliverDays(1);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setProduct(product);
        orderDetail.setOrder(mainOrder);
        orderDetail.setProductCost(product.getCost());
        orderDetail.setShippingCost(10);
        orderDetail.setQuantity(1);
        orderDetail.setSubtotal(product.getPrice());
        orderDetail.setUnitPrice(product.getPrice());

        mainOrder.getOrderDetails().add(orderDetail);

        Order savedOrder = orderRepository.save(mainOrder);

        assertThat(savedOrder.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateNewOrderWithMultipleProducts() {
        Customer customer = entityManager.find(Customer.class, 1);
        Product product1 = entityManager.find(Product.class, 3);
        Product product2 = entityManager.find(Product.class, 5);

        Order mainOrder = new Order();
        mainOrder.setOrderTime(new Date());
        mainOrder.setCustomer(customer);
        mainOrder.copyAddressFromCustomer();

        OrderDetail orderDetail1 = new OrderDetail();
        orderDetail1.setProduct(product1);
        orderDetail1.setOrder(mainOrder);
        orderDetail1.setProductCost(product1.getCost());
        orderDetail1.setShippingCost(10);
        orderDetail1.setQuantity(1);
        orderDetail1.setSubtotal(product1.getPrice());
        orderDetail1.setUnitPrice(product1.getPrice());

        OrderDetail orderDetail2 = new OrderDetail();
        orderDetail2.setProduct(product2);
        orderDetail2.setOrder(mainOrder);
        orderDetail2.setProductCost(product2.getCost());
        orderDetail2.setShippingCost(20);
        orderDetail2.setQuantity(2);
        orderDetail2.setSubtotal(product2.getPrice() * 2);
        orderDetail2.setUnitPrice(product2.getPrice());

        mainOrder.getOrderDetails().add(orderDetail1);
        mainOrder.getOrderDetails().add(orderDetail2);

        mainOrder.setShippingCost(30);
        mainOrder.setProductCost(product1.getCost() + product2.getCost());
        mainOrder.setTax(0);
        float subtotal = product1.getPrice() + product2.getPrice() * 2;
        mainOrder.setSubtotal(subtotal);
        mainOrder.setTotal(subtotal + 30);

        mainOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        mainOrder.setStatus(OrderStatus.PACKAGED);
        mainOrder.setDeliverDate(new Date());
        mainOrder.setDeliverDays(3);

        Order savedOrder = orderRepository.save(mainOrder);

        assertThat(savedOrder.getId()).isGreaterThan(0);
    }

    @Test
    public void testListOrders() {
        Iterable<Order> orders = orderRepository.findAll();
        orders.forEach(System.out::println);

        assertThat(orders).hasSizeGreaterThan(0);
    }

    @Test
    public void testGetOrder() {
        Integer orderId = 1;
        Order order = orderRepository.findById(orderId).get();
        System.out.println(order);

        assertThat(order).isNotNull();
    }

    @Test
    public void testUpdateOrderTracks() {
        Integer orderId = 7;
        Order order = orderRepository.findById(orderId).get();

        OrderTrack newTrack = new OrderTrack();
        newTrack.setOrder(order);
        newTrack.setUpdatedTime(new Date());
        newTrack.setStatus(OrderStatus.NEW);
        newTrack.setNotes(OrderStatus.NEW.defaultDescription());

        OrderTrack processingTrack = new OrderTrack();
        processingTrack.setOrder(order);
        processingTrack.setUpdatedTime(new Date());
        processingTrack.setStatus(OrderStatus.PROCESSING);
        processingTrack.setNotes(OrderStatus.PROCESSING.defaultDescription());

        List<OrderTrack> orderTracks = order.getOrderTracks();
        orderTracks.add(newTrack);
        orderTracks.add(processingTrack);

        Order updatedOrder = orderRepository.save(order);

        assertThat(updatedOrder.getOrderTracks()).hasSizeGreaterThan(1);
    }
}
