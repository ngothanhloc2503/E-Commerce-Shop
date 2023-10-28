var MILLISECONDS_A_DAY = 24 * 60 * 60 * 1000;

function setupButtonEventHandlers(reportType, callbackFunction) {

    $(".btn-sales-by" + reportType).on("click", function () {

        $(".btn-sales-by" + reportType).each(function (e) {
            $(this).removeClass("btn-primary").addClass("btn-light");
        })
        $(this).removeClass("btn-light").addClass("btn-primary");

        period = $(this).attr("period");
        if (period) {
            callbackFunction(period);
            $("#divCustomDateRange" + reportType).addClass("d-none");
        } else {
            $("#divCustomDateRange" + reportType).removeClass("d-none");
        }
    });

    initCustomDateRange(reportType);

    $("#btnViewReportByDateRange" + reportType).on("click", function (e) {
        validateDateRange(reportType, callbackFunction);
    })
}

function validateDateRange(reportType, callbackFunction) {
    days = calculateDays(reportType);

    startDateField = document.getElementById('startDate' + reportType);
    startDateField.setCustomValidity("");

    if (days >= 7 && days <= 30) {
        callbackFunction("custom");
    } else {
        startDateField.setCustomValidity("Dates must be in the range of 7..30 days");
        startDateField.reportValidity();
    }
}

function calculateDays(reportType) {
    startDateField = document.getElementById('startDate' + reportType);
    endDateField = document.getElementById('endDate' + reportType);
    startDate = startDateField.valueAsDate;
    endDate = endDateField.valueAsDate;

    differenceInMilliseconds = endDate - startDate;
    return differenceInMilliseconds / MILLISECONDS_A_DAY;
}

function initCustomDateRange(reportType) {
    startDateField = document.getElementById('startDate' + reportType);
    endDateField = document.getElementById('endDate' + reportType);
    toDate = new Date();
    endDateField.valueAsDate = toDate;

    fromDate = new Date();
    fromDate.setDate(toDate.getDate() - 30);
    startDateField.valueAsDate = fromDate;
}

function formatCurrency(amount) {
    formattedAmount = $.number(amount, decimalDigits, decimalPointType, thousandsPointType);
    return prefixCurrencySymbol + formattedAmount + suffixCurrencySymbol;
}

function getChartTitle(period) {
    if (period == "last_7_days") return "Sales in Last 7 Days";
    if (period == "last_28_days") return "Sales in Last 28 Days";
    if (period == "last_6_months") return "Sales in Last 6 Months";
    if (period == "last_year") return "Sales in Last Year";
    if (period == "custom") return "Custom Date Range";
    return "";
}

function getDenominator(period, reportType) {
    if (period == "last_7_days") return 7;
    if (period == "last_28_days") return 28;
    if (period == "last_6_months") return 6;
    if (period == "last_6_months") return 12;
    if (period == "custom") return calculateDays(reportType);
    return 1;
}

function setSalesAmount(period, reportType, labelTotalItems) {
    $("#textTotalGrossSales" + reportType).text(formatCurrency(totalGrossSales));
    $("#textTotalNetSales" + reportType).text(formatCurrency(totalNetSales));

    denominator = getDenominator(period, reportType);

    $("#textAvgGrossSales" + reportType).text(formatCurrency(totalGrossSales / denominator));
    $("#textAvgNetSales" + reportType).text(formatCurrency(totalNetSales / denominator));
    $("#labelTotalItems" + reportType).text(labelTotalItems);
    $("#textTotalOrders" + reportType).text(totalItems);
}

function formatChartData(data, columnIndex1, columnIndex2) {
    var formatter = new google.visualization.NumberFormat({
        prefix: prefixCurrencySymbol,
        suffix: suffixCurrencySymbol,
        decimalSymbol: decimalPointType,
        groupingSymbol: thousandsPointType,
        factionDigits: decimalDigits
    });

    formatter.format(data, columnIndex1);
    formatter.format(data, columnIndex2);
}