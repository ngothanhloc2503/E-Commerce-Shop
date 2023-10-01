var returnModal;
var modalTitle;
var fieldNote;
var orderId;
var divReason;
var divMessage;
var firstBtn;
var secondBtn;

$(document).ready(function () {
    returnModal = $("#returnOrderModal");
    modalTitle = $("#returnOrderModalTitle");
    fieldNote = $("#returnNote");
    divReason = $("#divReason");
    divMessage = $("#divMessage");
    firstBtn = $("#firstBtn");
    secondBtn = $("#secondBtn");

    handleReturnOrderLink();
})

function handleReturnOrderLink() {
    $(".linkReturnOrder").on("click", function (e) {
        e.preventDefault();
        showReturnModalDialog($(this));
    })
}

function showReturnModalDialog(link) {
    divMessage.hide();
    divReason.show();
    firstBtn.show();
    secondBtn.text("Cancel");
    fieldNote.val("");

    orderId = link.attr("orderId");
    returnModal.modal("show");
    modalTitle.text("Return Order ID #" + orderId);
}

function showMessageModal(message) {
    divReason.hide();
    firstBtn.hide();
    secondBtn.text("Close");
    divMessage.text(message);

    divMessage.show();
}

function submitReturnOrderForm() {
    reason = $("input[name='reason']:checked").val();
    note = fieldNote.val();

    sendReturnOrderRequest(reason, note);
    return false;
}

function sendReturnOrderRequest(reason, note) {
    requestURL = contextPath + "orders/return";
    requestBody = {orderId: orderId, reason: reason, note: note};

    $.ajax({
        type: 'POST',
        url: requestURL,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(requestBody),
        contentType: 'application/json'
    }).done(function (returnResponse) {
        showMessageModal("Return request has been sent");
        updateStatusTextAndHideReturnButton(orderId);
    }).fail(function (err) {
        showMessageModal(err.responseText);
    })
}

function updateStatusTextAndHideReturnButton(orderId) {
    $(".textOrderStatus" + orderId).each(function (index) {
        $(this).text("RETURN_REQUESTED");
    })

    $(".linkReturn" + orderId).each(function (index) {
        $(this).hide();
    })
}