var data;
var chartOption;
var totalGrossSales;
var totalNetSales;
var totalItems;

$(document).ready(function () {
    setupButtonEventHandlers("_category", loadSalesReportByDateForCategory);

    $("#salesByCategory").on("click", function (e) {
        $(".btn-sales-by_category").each(function (e) {
            $(this).removeClass("btn-primary").addClass("btn-light");
        })
        $("#btn-sales-by_category").removeClass("btn-light").addClass("btn-primary");
        loadSalesReportByDateForCategory("last_7_days");
    })
})

function loadSalesReportByDateForCategory(period) {
    if (period == "custom") {
        startDate = $("#startDate_category").val();
        endDate = $("#endDate_category").val();
        requestURL = contextPath + "reports/category/" + startDate + "/" + endDate;
    } else {
        requestURL = contextPath + "reports/category/" + period;
    }

     $.get(requestURL, function (responseJSON) {
         prepareChartDataForSalesReportByCategory(responseJSON);
         customizeChartForSalesReportByCategory();
         formatChartData(data, 1, 2);
         drawChartForSalesReportByCategory();
         setSalesAmount(period, "_category", "Total Products");
     })
}

function prepareChartDataForSalesReportByCategory(responseJSON) {
    data = new google.visualization.DataTable();
    data.addColumn('string', 'Category');
    data.addColumn('number', 'Gross Sales');
    data.addColumn('number', 'Net Sales');

    totalGrossSales = 0.0;
    totalNetSales = 0.0;
    totalItems = 0;

    $.each(responseJSON, function (index, reportItem) {
        data.addRows([[reportItem.identifier, reportItem.grossSales, reportItem.netSales]]);
        totalGrossSales += parseFloat(reportItem.grossSales);
        totalNetSales += parseFloat(reportItem.netSales);
        totalItems += parseInt(reportItem.productsCount);
    })
}

function customizeChartForSalesReportByCategory() {
    chartOption = {
        'height': 360,
        legend: {position: 'right'},
    }
}

function drawChartForSalesReportByCategory() {
    var salesChart = new google.visualization.PieChart(document.getElementById('chart_sales_by_category'));
    salesChart.draw(data, chartOption);
}