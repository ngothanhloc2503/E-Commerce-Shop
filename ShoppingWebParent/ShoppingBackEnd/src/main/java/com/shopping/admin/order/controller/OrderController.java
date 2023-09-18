package com.shopping.admin.order.controller;

import com.shopping.admin.order.OrderNotFoundException;
import com.shopping.admin.order.service.OrderService;
import com.shopping.admin.paging.PagingAndSortingHelper;
import com.shopping.admin.paging.PagingAndSortingParam;
import com.shopping.common.entity.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/orders")
    public String listFirstPage() {
        return "redirect:/orders/page/1?sortField=id&sortDir=asc";
    }

    @GetMapping("/orders/page/{pageNum}")
    public String listByPage(
            @PathVariable("pageNum") Integer pageNum,
            @PagingAndSortingParam(listName = "listOrders", moduleURL = "/orders")PagingAndSortingHelper helper) {
        orderService.listAll(pageNum, helper);

        return "orders/orders";
    }

    @GetMapping("/orders/detail/{id}")
    public String orderDetails(@PathVariable("id") Integer orderId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.findById(orderId);

            model.addAttribute("order", order);

            return "orders/order_details_modal";
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
}
