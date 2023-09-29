package com.shopping.admin.order.controller;

import com.shopping.admin.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderRestController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/orders_shipper/update/{id}/{status}")
    public Response updateOrderStatus(@PathVariable("id") Integer orderId, @PathVariable("status") String status) {
        orderService.updateStatus(orderId, status);
        return new Response(orderId, status);
    }
}

class Response {
    private Integer id;
    private String status;

    public Response(Integer id, String status) {
        this.id = id;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}