package com.shopping.admin.order;

import com.shopping.admin.order.repository.OrderDetailRepository;
import com.shopping.common.entity.order.OrderDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class OrderDetailRepositoryTests {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Test
    public void testFindWithCategoryAndTimeBetween() throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormatter.parse("2021-08-01");
        Date endTime = dateFormatter.parse("2021-08-31");

        List<OrderDetail> listOrderDetails = orderDetailRepository.findWithCategoryAndTimeBetween(startTime, endTime);
        for (OrderDetail detail : listOrderDetails) {
            System.out.printf("%-30s | %d | %.2f | %.2f | %.2f \n", detail.getProduct().getCategory().getName(),
                    detail.getQuantity(), detail.getProductCost(), detail.getShippingCost(), detail.getSubtotal());
        }

        assertThat(listOrderDetails.size()).isGreaterThan(0);
    }

    @Test
    public void testFindWithProductAndTimeBetween() throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormatter.parse("2021-08-01");
        Date endTime = dateFormatter.parse("2021-08-31");

        List<OrderDetail> listOrderDetails = orderDetailRepository.findWithProductAndTimeBetween(startTime, endTime);
        for (OrderDetail detail : listOrderDetails) {
            System.out.printf("%d | %s | %.2f | %.2f | %.2f \n", detail.getQuantity(),
                    detail.getProduct().getName(), detail.getProductCost(),
                    detail.getShippingCost(), detail.getSubtotal());
        }

        assertThat(listOrderDetails.size()).isGreaterThan(0);
    }
}
