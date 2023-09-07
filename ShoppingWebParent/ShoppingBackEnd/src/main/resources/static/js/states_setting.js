var dropdownCountriesForStates;
var dropdownStatesByCountry;
var btnLoadCountriesForStates;
var btnAddState;
var btnUpdateState;
var btnDeleteState;
var labelStateName;
var fieldStateName;
$(document).ready(function () {
    dropdownCountriesForStates = $("#dropdownCountriesForStates");
    dropdownStatesByCountry = $("#dropdownStatesByCountry");
    btnLoadCountriesForStates = $("#btnLoadCountriesForStates");
    btnAddState = $("#btnAddState");
    btnUpdateState = $("#btnUpdateState");
    btnDeleteState = $("#btnDeleteState");
    labelStateName = $("#labelStateName");
    fieldStateName = $("#fieldStateName");

    btnLoadCountriesForStates.click(function () {
        loadCountriesForStates();
        dropdownStatesByCountry.empty();
        changeFormStateToNewState();
    })

    dropdownCountriesForStates.on("change", function () {
        loadStatesByCountry();
        changeFormStateToNewState();
    })

    dropdownStatesByCountry.on("change", function () {
        changeFormStateToSelectedState();
    })

    btnAddState.click(function () {
        if (btnAddState.val() == "Add") {
            addState();
        } else {
            changeFormStateToNewState();
        }
    })

    btnUpdateState.click(function () {
        updateState();
    })

    btnDeleteState.click(function () {
        deleteState();
    })
})

function deleteState() {
    stateId = dropdownStatesByCountry.val();
    url = contextPath + "states/delete/" + stateId;

    $.ajax({
        type: 'DELETE',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function () {
        $("#dropdownStatesByCountry option:selected").remove();
        changeFormStateToNewState();
        showToast("The state has been deleted!");
    }).fail(function () {
        showToast("ERROR: Could not connect to server or server encountered an error!");
    })
}

function updateState() {
    if (!validateFormState()) return;

    url = contextPath + "states/save";
    stateId = dropdownStatesByCountry.val();
    stateName = fieldStateName.val();
    selectedCountry = $("#dropdownCountriesForStates option:selected");
    countryId = selectedCountry.val();
    countryName = selectedCountry.text();

    jsonData = {id: stateId, name: stateName, country: {id: countryId, name: countryName}};

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json',
    }).done(function (stateId) {
        $("#dropdownStatesByCountry option:selected").val(stateId);
        $("#dropdownStatesByCountry option:selected").text(stateName);
        showToast("The state has been updated.");

        changeFormStateToNewState();
    }).fail(function () {
        showToast("ERROR: Could not connect to server or server encountered an error!");
    })
}

function validateFormState() {
    formState = document.getElementById("formState");
    if (formState.checkValidity()) {
        formState.reportValidity();
        return false;
    }
    return true;
}

function addState() {
    if (!validateFormState()) return;

    url = contextPath + "states/save";
    stateName = fieldStateName.val();
    selectedCountry = $("#dropdownCountriesForStates option:selected");
    countryId = selectedCountry.val();
    countryName = selectedCountry.text();

    jsonData = {name: stateName, country: {id: countryId, name: countryName}};


    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json',
    }).done(function (stateId) {
        selectNewlyAddedState(stateId, stateName);
        showToast("The new state has been added.");
    }).fail(function () {
        showToast("ERROR: Could not connect to server or server encountered an error!");
    })
}

function selectNewlyAddedState(stateId, stateName) {
    $("<option>").val(stateId).text(stateName).appendTo(dropdownStatesByCountry);

    $("#dropdownStatesByCountry option[value='" + stateId + "']").prop("selected", true);
    fieldStateName.val("").focus();
}

function changeFormStateToNewState() {
    btnAddState.prop("value", "Add");
    btnUpdateState.prop("disabled", true);
    btnDeleteState.prop("disabled", true);
    labelStateName.text("State Name:");
    fieldStateName.val("");
}

function changeFormStateToSelectedState() {
    btnAddState.prop("value", "New");
    btnUpdateState.prop("disabled", false);
    btnDeleteState.prop("disabled", false);

    labelCountryName.text("Selected State:")
    selectedStateName = $("#dropdownStatesByCountry option:selected").text();
    fieldStateName.val(selectedStateName);
}

function loadStatesByCountry() {
    selectedCountryId = dropdownCountriesForStates.val();
    url = contextPath + "states/list_by_country/" + selectedCountryId;

    $.get(url, function (responseJson) {
        dropdownStatesByCountry.empty();

        $.each(responseJson, function (index, state) {
            $("<option>").val(state.id).text(state.name).appendTo(dropdownStatesByCountry);
        })
    }).done(function () {
        showToast("All states have been loaded!");
    }).fail(function () {
        showToast("ERROR: Could not connect to server or server encountered an error!");
    })
}

function loadCountriesForStates() {
    url = contextPath + "countries/list";

    $.get(url, function (responseJson) {
        dropdownCountriesForStates.empty();

        $.each(responseJson, function (index, country) {
            $("<option>").val(country.id).text(country.name).appendTo(dropdownCountriesForStates);
        })
    }).done(function () {
        btnLoadCountriesForStates.val("Refresh Country List")
        showToast("All countries have been loaded!");
    }).fail(function () {
        showToast("ERROR: Could not connect to server or server encountered an error!");
    })
}