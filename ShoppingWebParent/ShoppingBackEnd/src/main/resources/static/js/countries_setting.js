var btnLoadCountries;
var dropdownCountries;
var btnAddCountry;
var btnUpdateCountry;
var btnDeleteCountry;
var labelCountryName;
var fieldCountryName;
var fieldCountryCode;

$(document).ready(function () {
    btnLoadCountries = $("#btnLoadCountries");
    dropdownCountries = $("#dropdownCountries");
    btnAddCountry = $("#btnAddCountry");
    btnUpdateCountry = $("#btnUpdateCountry");
    btnDeleteCountry = $("#btnDeleteCountry");
    labelCountryName = $("#labelCountryName");
    fieldCountryName = $("#fieldCountryName");
    fieldCountryCode = $("#fieldCountryCode");

    btnLoadCountries.click(function () {
        loadCountries();
        changeFormStateToNewCountry();
    })

    dropdownCountries.on("change", function () {
        changeFormStateToSelectedCountry();
    })

    btnAddCountry.click(function () {
        if (btnAddCountry.val() == "Add") {
            addCountry();
        } else {
            changeFormStateToNewCountry();
        }
    })

    btnUpdateCountry.click(function () {
        updateCountry();
    })

    btnDeleteCountry.click(function () {
        deleteCountry();
    })
})

function deleteCountry() {
    optionValue = dropdownCountries.val();
    countryId = optionValue.split("-")[0];
    url = contextPath + "countries/delete/" + countryId;

    $.ajax({
        type: 'DELETE',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function () {
        $("#dropdownCountries option:selected").remove();
        changeFormStateToNewCountry();
        showToast("The country has been deleted.");
    }).fail(function () {
        showToast("ERROR: Could not connect to server or server encountered an error!");
    })
}
function updateCountry() {
    if (!validateFormCountry()) return;

    url = contextPath + "countries/save";
    countryName = fieldCountryName.val();
    countryCode = fieldCountryCode.val();

    countryId = dropdownCountries.val().split("-")[0];

    jsonData = {id: countryId, name: countryName, code: countryCode};

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json',
    }).done(function (countryId) {
        $("#dropdownCountries option:selected").val(countryId + "-" + countryCode);
        $("#dropdownCountries option:selected").text(countryName);
        showToast("The country has been updated.");

        changeFormStateToNewCountry();
    }).fail(function () {
        showToast("ERROR: Could not connect to server or server encountered an error!");
    })
}

function validateFormCountry() {
    formCountry = document.getElementById("formCountry");
    if (formCountry.checkValidity()) {
        formCountry.reportValidity();
        return false;
    }
    return true;
}

function addCountry() {
    if (!validateFormCountry()) return;

    url = contextPath + "countries/save";
    countryName = fieldCountryName.val();
    countryCode = fieldCountryCode.val();
    jsonData = {name: countryName, code: countryCode};

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json',
    }).done(function (countryId) {
        selectNewlyAddedCountry(countryId, countryCode, countryName);
        showToast("The new country has been added.");
    }).fail(function () {
        showToast("ERROR: Could not connect to server or server encountered an error!");
    })
}

function selectNewlyAddedCountry(countryId, countryCode, countryName) {
    optionValue = countryId + "-" + countryCode;
    $("<option>").val(optionValue).text(countryName).appendTo(dropdownCountries);

    $("#dropdownCountries option[value='" + optionValue + "']").prop("selected", true);
    fieldCountryName.val("").focus();
    fieldCountryCode.val("");
}

function changeFormStateToNewCountry() {
    btnAddCountry.prop("value", "Add");
    btnUpdateCountry.prop("disabled", true);
    btnDeleteCountry.prop("disabled", true);
    labelCountryName.text("Country Name:");
    fieldCountryName.val("").focus();
    fieldCountryCode.val("");
}

function changeFormStateToSelectedCountry() {
    btnAddCountry.prop("value", "New");
    btnUpdateCountry.prop("disabled", false);
    btnDeleteCountry.prop("disabled", false);

    labelCountryName.text("Selected Country:")
    selectedCountryName = $("#dropdownCountries option:selected").text();
    fieldCountryName.val(selectedCountryName);

    optionValue = dropdownCountries.val().split("-")[1];
    fieldCountryCode.val(optionValue);
}

function loadCountries() {
    url = contextPath + "countries/list";

    $.get(url, function (responseJson) {
        dropdownCountries.empty();

        $.each(responseJson, function (index, country) {
            optionValue = country.id + "-" + country.code;
            $("<option>").val(optionValue).text(country.name).appendTo(dropdownCountries);
        })
    }).done(function () {
        btnLoadCountries.val("Refresh Country List")
        showToast("All countries have been loaded!");
    }).fail(function () {
        showToast("ERROR: Could not connect to server or server encountered an error!");
    })
}

function showToast(message) {
    $("#toastMessage").text(message);
    $(".toast").toast('show');
}