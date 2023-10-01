package com.shopping.order;

import com.shopping.Utility;
import com.shopping.common.entity.Customer;
import com.shopping.common.entity.order.Order;
import com.shopping.customer.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/orders")
    public String listFirstPage() {
        return "redirect:/orders/page/1?sortField=id&sortDir=asc";
    }

    @GetMapping("/orders/page/{pageNum}")
    public String listByPage(Model model, @PathVariable("pageNum") Integer pageNum,
                             String sortField, String sortDir, String keyword,
                             HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);

        Page<Order> page = orderService.listAll(customer.getId(), pageNum, sortField, sortDir, keyword);
        List<Order> listOrders = page.getContent();

        int startCount = (pageNum - 1) * orderService.ORDER_PER_PAGE + 1;
        int endCount = startCount + orderService.ORDER_PER_PAGE - 1;

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("listOrders", listOrders);
        model.addAttribute("moduleURL", "/orders");

        return "orders/orders";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request){
        String customerEmail = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.getCustomerByEmail(customerEmail);
    }

    @GetMapping("/orders/detail/{id}")
    public String orderDetails(@PathVariable("id") Integer orderId, Model model,
                               HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);

        Order order = orderService.getOrder(orderId, customer);

        model.addAttribute("order", order);

        return "orders/order_details_modal";
    }

}
