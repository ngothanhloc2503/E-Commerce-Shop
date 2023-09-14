var decimalSeparator;
var thousandsSeparator;
$(document).ready(function () {
    decimalSeparator = decimalPointType == 'COMMA' ? ',' : '.';
    thousandsSeparator = thousandsPointType == 'COMMA' ? ',' : '.';

    $(".linkMinus").on("click", function (e) {
        e.preventDefault($(this));
        decreaseQuantity($(this));
    })

    $(".linkPlus").on("click", function (e) {
        e.preventDefault($(this));
        increaseQuantity($(this));
    })

    $(".linkRemove").on("click", function (e) {
        e.preventDefault();
        removeProduct($(this));
    })
})

function decreaseQuantity(link) {
    productId = link.attr("pid");
    quantityInput = $("#quantity" + productId);
    newQuantity = parseInt(quantityInput.val()) - 1;

    if (newQuantity > 0) {
        quantityInput.val(newQuantity);
        updateQuantity(productId, newQuantity);
    } else {
        showWarningModal("Minimum quantity is 1!");
    }
}

function increaseQuantity(link) {
    productId = link.attr("pid");
    quantityInput = $("#quantity" + productId);
    newQuantity = parseInt(quantityInput.val()) + 1;

    if (newQuantity <= 5) {
        quantityInput.val(newQuantity);
        updateQuantity(productId, newQuantity);
    } else {
        showWarningModal("Maximum quantity is 5!");
    }
}

function updateQuantity(productId, quantity) {
    url = contextPath + "cart/update/" + productId + "/" + quantity;

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function (updatedSubtotal) {
        updateSubtotal(updatedSubtotal, productId);
        updateTotal();
    }).fail(function () {
        showErrorModal("Error while updating product to shopping cart.");
    })
}

function updateSubtotal(updatedSubtotal, productId) {
    $("#subtotal" + productId).text(formatCurrency(updatedSubtotal));
}

function updateTotal() {
    total = 0.0;
    productCount = 0;

    $(".subtotal").each(function (index, element) {
        productCount++;
        total += parseFloat(clearCurrencyFormat(element.innerHTML));
    })

    if (productCount < 1) {
        showEmptyShoppingCart();
    } else {
        $("#total").text(formatCurrency(total));
    }
}

function showEmptyShoppingCart() {
    $("#sectionTotal").hide();
    $("#sectionEmptyCartMessage").removeClass("d-none");
}

function removeProduct(link) {
    url = link.attr("href");

    $.ajax({
        type: 'DELETE',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function (response) {
        rowNumber = link.attr("rowNumber");
        removeProductHTML(rowNumber);
        updateTotal();
        updateCountNumber();

        showModalDialog("Shopping cart", response);
    }).fail(function () {
        showErrorModal("Error while removing product to shopping cart.");
    })
}

function removeProductHTML(rowNumber) {
    $("#row" + rowNumber).remove();
    $("#blankLine" + rowNumber).remove();
}

function updateCountNumber() {
    $(".divCount").each(function (index, element) {
        element.innerHTML = "" + (index + 1);
    })
}

function formatCurrency(amount) {
    return $.number(amount, decimalDigits, decimalSeparator, thousandsSeparator);
}

function clearCurrencyFormat(numberString) {
    result = numberString.replaceAll(thousandsSeparator, "");
    return result.replaceAll(decimalSeparator, ".");
}