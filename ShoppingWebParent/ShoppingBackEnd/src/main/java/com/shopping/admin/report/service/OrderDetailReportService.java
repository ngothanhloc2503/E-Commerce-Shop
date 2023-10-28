package com.shopping.admin.report.service;

import com.shopping.admin.order.repository.OrderDetailRepository;
import com.shopping.admin.report.ReportItem;
import com.shopping.admin.report.ReportType;
import com.shopping.common.entity.order.Order;
import com.shopping.common.entity.order.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderDetailReportService extends AbstractReportService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    protected List<ReportItem> getReportDataByDateRangeInternal(Date startTime, Date endTime, ReportType reportType) {
        List<OrderDetail> listOrderDetails = null;

        if (reportType.equals(ReportType.CATEGORY)) {
            listOrderDetails = orderDetailRepository.findWithCategoryAndTimeBetween(startTime, endTime);
        } else if (reportType.equals(ReportType.PRODUCT)) {
            listOrderDetails = orderDetailRepository.findWithProductAndTimeBetween(startTime, endTime);
        }

        List<ReportItem> listReportItems = new ArrayList<>();

        for (OrderDetail order : listOrderDetails) {
            String identifier = "";
            if (reportType.equals(ReportType.CATEGORY)) {
                identifier = order.getProduct().getCategory().getName();
            } else if (reportType.equals(ReportType.PRODUCT)) {
                identifier = order.getProduct().getShortName();
            }
            ReportItem item = new ReportItem(identifier);

            float grossSales = order.getSubtotal() + order.getShippingCost();
            float netSales = order.getSubtotal() + order.getShippingCost();

            int itemIndex = listReportItems.indexOf(item);
            if (itemIndex >= 0) {
                item = listReportItems.get(itemIndex);

                item.addGrossSales(grossSales);
                item.addNetSales(netSales);
                item.increaseProductsCount(order.getQuantity());
            } else {
                listReportItems.add(new ReportItem(identifier, grossSales, netSales, order.getQuantity()));
            }
        }

        return listReportItems;
    }
}
