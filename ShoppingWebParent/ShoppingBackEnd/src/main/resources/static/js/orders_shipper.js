var iconNames = {
    PICKED: 'fa-people-carry',
    SHIPPING: 'fa-shipping-fast',
    DELIVERED: 'fa-box-open',
    RETURNED: 'fa-undo'
}
var confirmText;
var confirmModalDialog;
var yesButton;
var noButton;

$(document).ready(function () {
    confirmText = $("#confirmText");
    confirmModalDialog = $("#confirmModal");
    yesButton = $("#btnYes");
    noButton = $("#btnNo");

    $(".linkUpdateStatus").on("click", function (e) {
        e.preventDefault();
        link = $(this);
        url = link.attr("href");
        showUpdateConfirmation(link);
    })

    addEventHandlerForYesButton();
})

function addEventHandlerForYesButton() {
    yesButton.click(function (e) {
        e.preventDefault();
        sendRequestToUpdateOrderStatus($(this));
    })
}

function sendRequestToUpdateOrderStatus(button) {
    requestUrl = button.attr("href");

    $.ajax({
        type: 'POST',
        url: requestUrl,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function (response) {
        showMessageModal("Order updated successfully");
        updateStatusIconColor(response.id, response.status);
    }).fail(function (err) {
        showMessageModal("Error updating order status");
    })
}


function updateStatusIconColor(orderId, status) {
    link = $("#link" + status + orderId);
    link.replaceWith("<i class='fas " + iconNames[status] + " fa-2x icon-green'></i>");
}

function showUpdateConfirmation(link) {
    noButton.text("No");
    yesButton.show();
    orderId = link.attr("orderId");
    status = link.attr("status");
    yesButton.attr("href", link.attr("href"));

    confirmText.text("Are you sure you want to update status of the order ID #" + orderId + " to " + status);

    confirmModalDialog.modal("show");
}

function showMessageModal(message) {
    noButton.text("Close");
    yesButton.hide();
    confirmText.text(message);
}