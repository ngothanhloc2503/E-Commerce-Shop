package com.shopping.admin.report.controller;

import com.shopping.admin.report.ReportItem;
import com.shopping.admin.report.ReportType;
import com.shopping.admin.report.service.MasterOrderReportService;
import com.shopping.admin.report.service.OrderDetailReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
public class ReportRestController {
    @Autowired
    private MasterOrderReportService masterOrderReportService;

    @Autowired
    private OrderDetailReportService orderDetailReportService;

    @GetMapping("/reports/sales_by_date/{period}")
    public List<ReportItem> getReportDataByDatePeriod(@PathVariable("period") String period) {
        switch (period) {
            case "last_7_days":
                return masterOrderReportService.getReportDataLast7Days(ReportType.DAY);

            case "last_28_days":
                return masterOrderReportService.getReportDataLast28Days(ReportType.DAY);

            case "last_6_months":
                return masterOrderReportService.getReportDataLast6Months(ReportType.MONTH);

            case "last_year":
                return masterOrderReportService.getReportDataLastYear(ReportType.MONTH);

            default:
                return masterOrderReportService.getReportDataLast7Days(ReportType.DAY);
        }
    }

    @GetMapping("/reports/sales_by_date/{startDate}/{endDate}")
    public List<ReportItem> getReportDataByDateRange(@PathVariable("startDate") String startDate,
                                                      @PathVariable("endDate") String endDate) throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormatter.parse(startDate);
        Date endTime = dateFormatter.parse(endDate);

        return masterOrderReportService.getReportDataByDateRange(startTime, endTime, ReportType.DAY);
    }

    @GetMapping("/reports/{groupBy}/{period}")
    public List<ReportItem> getReportDataByCategoryOrProduct(@PathVariable("groupBy") String groupBy,
                                                             @PathVariable("period") String period) {
        ReportType reportType = ReportType.valueOf(groupBy.toUpperCase());
        switch (period) {
            case "last_7_days":
                return orderDetailReportService.getReportDataLast7Days(reportType);

            case "last_28_days":
                return orderDetailReportService.getReportDataLast28Days(reportType);

            case "last_6_months":
                return orderDetailReportService.getReportDataLast6Months(reportType);

            case "last_year":
                return orderDetailReportService.getReportDataLastYear(reportType);

            default:
                return orderDetailReportService.getReportDataLast7Days(reportType);
        }
    }

    @GetMapping("/reports/{groupBy}/{startDate}/{endDate}")
    public List<ReportItem> getReportDataByCategoryOrProductDateRange(@PathVariable("groupBy") String groupBy,
                                                      @PathVariable("startDate") String startDate,
                                                      @PathVariable("endDate") String endDate) throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormatter.parse(startDate);
        Date endTime = dateFormatter.parse(endDate);

        ReportType reportType = ReportType.valueOf(groupBy.toUpperCase());

        return orderDetailReportService.getReportDataByDateRange(startTime, endTime, reportType);
    }
}
