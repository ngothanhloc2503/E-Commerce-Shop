package com.shopping.order;

import com.shopping.ControllerHelper;
import com.shopping.common.entity.Customer;
import com.shopping.common.entity.order.Order;
import com.shopping.common.entity.order.OrderDetail;
import com.shopping.common.entity.product.Product;
import com.shopping.review.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Iterator;
import java.util.List;

@Controller
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ControllerHelper controllerHelper;

    @GetMapping("/orders")
    public String listFirstPage() {
        return "redirect:/orders/page/1?sortField=id&sortDir=asc";
    }

    @GetMapping("/orders/page/{pageNum}")
    public String listByPage(Model model, @PathVariable("pageNum") Integer pageNum,
                             String sortField, String sortDir, String keyword,
                             HttpServletRequest request) {
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);

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

    @GetMapping("/orders/detail/{id}")
    public String orderDetails(@PathVariable("id") Integer orderId, Model model,
                               HttpServletRequest request) {
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        Order order = orderService.getOrder(orderId, customer);

        setProductReviewableStatus(customer, order);

        model.addAttribute("order", order);

        return "orders/order_details_modal";
    }

    private void setProductReviewableStatus(Customer customer, Order order) {
        Iterator<OrderDetail> iterator = order.getOrderDetails().iterator();
        while (iterator.hasNext()) {
            OrderDetail orderDetail = iterator.next();
            Product product = orderDetail.getProduct();

            boolean didCustomerReviewProduct = reviewService.didCustomerReviewProduct(customer, product.getId());
            product.setReviewedByCustomer(didCustomerReviewProduct);

            if (!didCustomerReviewProduct) {
                product.setCustomerCanReview(reviewService.canCustomerReviewProduct(customer, product.getId()));
            }
        }
    }

}
