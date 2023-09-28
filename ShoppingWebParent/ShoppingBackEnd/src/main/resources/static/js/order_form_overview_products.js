var fieldProductCost;
var fieldSubtotal;
var fieldShippingCost;
var fieldTax;
var fieldTotal;

$(document).ready(function () {
    fieldProductCost = $("#productCost");
    fieldSubtotal = $("#subtotal");
    fieldShippingCost = $("#shippingCost");
    fieldTax = $("#tax");
    fieldTotal = $("#total");

    formatOrderAmounts();
    formatProductAmounts();

    $("#productList").on("change", ".quantity-input", function (e) {
        updateSubtotalWhenQuantityChanged($(this));
        updateOrderAmounts();
    })
    $("#productList").on("change", ".price-input", function (e) {
        updateSubtotalWhenPriceChanged($(this));
        updateOrderAmounts();
    })
    $("#productList").on("change", ".cost-input", function (e) {
        updateOrderAmounts();
    })
    $("#productList").on("change", ".ship-input", function (e) {
        updateOrderAmounts();
    })
})

function updateOrderAmounts() {
    totalCost = 0.0;

    $(".cost-input").each(function (e) {
        costInputField = $(this);
        rowNumber = costInputField.attr("rowNumber");
        quantityValue = $("#quantity" + rowNumber).val();

        productCost = costInputField.val().replace(",", "");
        totalCost += parseInt(quantityValue) * productCost;
    });

    $("#productCost").val($.number(totalCost, 2));

    orderSubtotal = 0.0;

    $(".subtotal-output").each(function (e) {
        productSubtotal = $(this).val().replace(",", "");
        orderSubtotal += parseFloat(productSubtotal);
    });

    $("#subtotal").val($.number(orderSubtotal, 2));

    shippingCost = 0.0;

    $(".ship-input").each(function (e) {
        productShippingCost = $(this).val().replace(",", "");
        shippingCost += parseFloat(productShippingCost);
    });

    $("#shippingCost").val($.number(shippingCost, 2));

    tax = parseFloat(fieldTax.val().replace(",", ""));
    orderTotal = orderSubtotal + tax + shippingCost;
    $("#total").val($.number(orderTotal, 2));
}

function updateSubtotalWhenPriceChanged(input) {
    priceValue = parseFloat(input.val().replace(",", ""));
    rowNumber = input.attr("rowNumber");
    quantityValue = $("#quantity" + rowNumber).val();
    newSubtotal = parseFloat(quantityValue) * priceValue;
    $("#subtotal" + rowNumber).val($.number(newSubtotal, 2));
}

function updateSubtotalWhenQuantityChanged(input) {
    quantityValue = input.val();
    rowNumber = input.attr("rowNumber");
    priceField = $("#price" + rowNumber);
    priceValue = parseFloat(priceField.val().replace(",", ""));
    newSubtotal = parseFloat(quantityValue) * priceValue;
    $("#subtotal" + rowNumber).val($.number(newSubtotal, 2));
}

function formatProductAmounts() {
    $(".cost-input").each(function (e) {
        formatNumberForField($(this));
    })
    $(".price-input").each(function (e) {
        formatNumberForField($(this));
    })
    $(".subtotal-output").each(function (e) {
        formatNumberForField($(this));
    })
    $(".ship-input").each(function (e) {
        formatNumberForField($(this));
    })
}

function formatOrderAmounts() {
    formatNumberForField(fieldProductCost);
    formatNumberForField(fieldSubtotal);
    formatNumberForField(fieldShippingCost);
    formatNumberForField(fieldTax);
    formatNumberForField(fieldTotal);
}

function formatNumberForField(fieldRef) {
    fieldRef.val($.number(fieldRef.val(), 2));
}

function processFormBeforeSubmit() {
    setCountryName();

    removeThousandSeparatorForField(fieldProductCost);
    removeThousandSeparatorForField(fieldSubtotal);
    removeThousandSeparatorForField(fieldShippingCost);
    removeThousandSeparatorForField(fieldTax);
    removeThousandSeparatorForField(fieldTotal);

    $(".cost-input").each(function (e) {
        removeThousandSeparatorForField($(this));
    });

    $(".price-input").each(function (e) {
        removeThousandSeparatorForField($(this));
    });

    $(".subtotal-output").each(function (e) {
        removeThousandSeparatorForField($(this));
    });

    $(".ship-input").each(function (e) {
        removeThousandSeparatorForField($(this));
    });
}

function removeThousandSeparatorForField(fieldRef) {
    fieldRef.val(fieldRef.val().replace(",", ""));
}

function setCountryName() {
    selectedCountry = $("#country option:selected");
    countryName = selectedCountry.text();
    $("#countryName").val(countryName);
}