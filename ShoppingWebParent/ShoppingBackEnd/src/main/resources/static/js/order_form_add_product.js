$(document).ready(function () {
    $("#products").on("click", "#linkAddProduct", function (e) {
        e.preventDefault();
        link = $(this);
        url = link.attr("href");

        $("#addProductModal").on("shown.bs.modal", function () {
            $(this).find("iframe").attr("src", url);
        })

        $("#addProductModal").modal('show');
    })
})

function addProduct(productId, productName) {
    $("#addProductModal").modal('hide');

    getShippingCost(productId);
}

function getShippingCost(productId) {
    selectedCountry = $("#country option:selected");
    countryId = selectedCountry.val();
    state = $("#state").val();

    if (state == null) {
        state = $("#city").val();
    }

    requestUrl = contextPath + "get_shipping_cost";
    params = {productId: productId, countryId: countryId, state: state};

    $.ajax({
        type: 'POST',
        url: requestUrl,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: params
    }).done(function (shippingCost) {
        getProductInfo(productId, shippingCost);
    }).fail(function (err) {
        showWarningModal(err.responseJSON.message);
        shippingCost = 0.0;
        getProductInfo(productId, shippingCost);
    })
}

function getProductInfo(productId, shippingCost) {
    requestUrl = contextPath + "products/get/" + productId;

    $.get(requestUrl, function(productJson) {
        productName = productJson.name;
        productImagePath = contextPath.substring(0, contextPath.length - 1) + productJson.imagePath;
        productPrice = $.number(productJson.price, 2);
        productCost = $.number(productJson.cost, 2);
        shippingCost = $.number(shippingCost, 2);

        htmlCode = generateProductCode(productId, shippingCost, productName, productImagePath, productPrice, productCost);
        $("#productList").append(htmlCode);
        updateOrderAmounts();
    }).fail(function (err) {
        showWarningModal(err.responseJSON.message);
    })
}

function generateProductCode(productId, shippingCost, productName, productMainImagePath, productPrice, productCost) {
    nextCount = $(".hiddenProductId").length + 1;
    quantityId = "quantity" + nextCount;
    priceId = "price" + nextCount;
    subtotalId = "subtotal" + nextCount;
    rowId = "row" + nextCount;

    htmlCode = `
    <div class="border rounded p-2 mb-2" id="${rowId}">
        <input type="hidden" name="detailId" value="0">
        <input type="hidden" name="productId" value="${productId}" class="hiddenProductId">
        <div class="row">
            <div class="col-1">
                <div class="divCount">&nbsp;${nextCount}</div>
                <a class="fa-solid fa-trash icon-dark fa-lg linkRemove" href=""
                   rowNumber="${nextCount}"
                   title="Delete this product" style="text-decoration:none"></a>
            </div>
            <div class="col">
                <img src="${productMainImagePath}" style="max-height: 200px" class="img-fluid">
            </div>
        </div>
        <div class="row m-2">
            <b>${productName}</b>
        </div>
        <div class="row m-2">
            <table>
                <tr>
                    <td style="width: 10%">Product Cost:</td>
                    <td>
                        <input type="text" class="form-control cost-input" style="max-width: 150px"
                               name="productDetailCost"
                               rowNumber="${nextCount}"
                               value="${productCost}" required>
                    </td>
                </tr>
                <tr>
                    <td>Quantity:</td>
                    <td>
                        <input type="number" step="1" min="1" max="5" class="form-control quantity-input"
                               name="quantity"
                               rowNumber="${nextCount}" id="${quantityId}"
                               style="max-width: 150px" value="1" required>
                    </td>
                </tr>
                <tr>
                    <td>Unit Price:</td>
                    <td>
                        <input type="text" class="form-control price-input" style="max-width: 150px"
                               name="productPrice"
                               rowNumber="${nextCount}"
                               id="${priceId}"
                               value="${productPrice}" required>
                    </td>
                </tr>
                <tr>
                    <td>Subtotal:</td>
                    <td>
                        <input type="text" class="form-control subtotal-output" style="max-width: 150px"
                               name="productSubtotal"
                               rowNumber="${nextCount}"
                               id="${subtotalId}"
                               value="${productPrice}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>Shipping Cost:</td>
                    <td>
                        <input type="text" class="form-control ship-input" style="max-width: 150px"
                               name="productShipCost"
                               value="${shippingCost}" required>
                    </td>
                </tr>
            </table>
        </div>
    </div>
    `;

    return htmlCode;
}

function isProductAlreadyAdded(productId) {
    productExist = false;

    $(".hiddenProductId").each(function (e) {
        aProductId = $(this).val();
        if (productId == aProductId) {
            productExist = true;

        }
    })

    return productExist;
}