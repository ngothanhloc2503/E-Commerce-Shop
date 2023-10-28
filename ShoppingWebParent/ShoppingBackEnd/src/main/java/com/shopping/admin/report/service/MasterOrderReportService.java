package com.shopping.admin.report.service;

import com.shopping.admin.order.repository.OrderRepository;
import com.shopping.admin.report.ReportItem;
import com.shopping.admin.report.ReportType;
import com.shopping.common.entity.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class MasterOrderReportService extends AbstractReportService{
    @Autowired
    private OrderRepository repository;

    protected List<ReportItem> getReportDataByDateRangeInternal(Date startTime, Date endTime, ReportType reportType) {
        List<Order> listOrders = repository.findByOrderTimeBetween(startTime, endTime);

        List<ReportItem> listReportItems = createReportData(startTime, endTime, reportType);
        calculateSalesForReportData(listOrders, listReportItems);

        return listReportItems;
    }

    private void calculateSalesForReportData(List<Order> listOrders, List<ReportItem> listReportItems) {
        for (Order order : listOrders) {
            String orderDateString = dateFormatter.format(order.getOrderTime());

            ReportItem reportItem = new ReportItem(orderDateString);

            int itemIndex = listReportItems.indexOf(reportItem);

            if (itemIndex >= 0) {
                reportItem = listReportItems.get(itemIndex);

                reportItem.addGrossSales(order.getTotal());
                reportItem.addNetSales(order.getSubtotal() - order.getProductCost());
                reportItem.increaseOrdersCount();
            }
        }
    }

    private List<ReportItem> createReportData(Date startTime, Date endTime, ReportType reportType) {
        List<ReportItem> listReportItems = new ArrayList<>();

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(startTime);
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(endTime);

        Date currentDate = startDate.getTime();
        String currentDateString = dateFormatter.format(currentDate);

        listReportItems.add(new ReportItem(currentDateString));

        do {
            if (reportType.equals(ReportType.DAY)) {
                startDate.add(Calendar.DAY_OF_MONTH, 1);
            } else if (reportType.equals(ReportType.MONTH)) {
                startDate.add(Calendar.MONTH, 1);
            }
            currentDate = startDate.getTime();
            currentDateString = dateFormatter.format(currentDate);

            listReportItems.add(new ReportItem(currentDateString));
        } while (startDate.before(endDate));

        return listReportItems;
    }
}
