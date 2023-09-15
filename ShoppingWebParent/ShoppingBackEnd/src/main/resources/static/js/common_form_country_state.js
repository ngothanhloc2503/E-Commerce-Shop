var dropdownStatesByCountry;

$(document).ready(function () {
    dropdownStatesByCountry = $("#state");

    $("#country").on("change", function () {
        loadStatesByCountry();
    })
})

function loadStatesByCountry() {
    selectedCountryId = $("#country option:selected").val();
    url = contextPath + "states/list_by_country/" + selectedCountryId;

    $.get(url, function (responseJson) {
        dropdownStatesByCountry.empty();

        $.each(responseJson, function (index, state) {
            $("<option>").val(state.name).text(state.name).appendTo(dropdownStatesByCountry);
        })
    }).done(function () {

    }).fail(function () {
        showErrorModal("ERROR: Could not connect to server or server encountered an error!");
    })
}