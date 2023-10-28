package com.shopping.admin.report.service;

import com.shopping.admin.report.ReportItem;
import com.shopping.admin.report.ReportType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class AbstractReportService {
    protected DateFormat dateFormatter;

    public List<ReportItem> getReportDataLast7Days(ReportType reportType) {
        return getReportDataLasXDays(7, reportType);
    }

    public List<ReportItem> getReportDataLast28Days(ReportType reportType) {
        return getReportDataLasXDays(28, reportType);
    }

    public List<ReportItem> getReportDataLast6Months(ReportType reportType) {
        return getReportDataLasXMonths(6, reportType);
    }

    public List<ReportItem> getReportDataLastYear(ReportType reportType) {
        return getReportDataLasXMonths(12, reportType);
    }

    protected List<ReportItem> getReportDataLasXDays(int days, ReportType reportType) {
        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -(days - 1));
        Date startTime = calendar.getTime();

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        return getReportDataByDateRangeInternal(startTime, endTime, reportType);
    }

    protected List<ReportItem> getReportDataLasXMonths(int months, ReportType reportType) {
        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -(months - 1));
        Date startTime = calendar.getTime();

        dateFormatter = new SimpleDateFormat("yyyy-MM");
        return getReportDataByDateRangeInternal(startTime, endTime, reportType);
    }

    protected abstract List<ReportItem> getReportDataByDateRangeInternal(Date startTime, Date endTime, ReportType reportType);

    public List<ReportItem> getReportDataByDateRange(Date startTime, Date endTime, ReportType reportType) {
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        return getReportDataByDateRangeInternal(startTime, endTime, reportType);
    }
}
