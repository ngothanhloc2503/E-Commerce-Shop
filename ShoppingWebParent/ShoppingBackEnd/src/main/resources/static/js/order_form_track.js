var trackRecordCount;

$(document).ready(function () {
    trackRecordCount = $(".hiddenTrackId").length;

    $("#track").on("click", "#linkAddTrack", function (e) {
        e.preventDefault();
        generateTrackCode();
    });

    $("#trackList").on("change", ".dropDownStatus", function(e) {
        dropDownList = $(this);
        rowNumber = dropDownList.attr("rowNumber");
        selectedOption = $("option:selected", dropDownList);

        defaultNote = selectedOption.attr("defaultDescription");
        $("#trackNote" + rowNumber).text(defaultNote);
    });

    $("#trackList").on("click", ".linkRemoveTrack", function (e) {
        e.preventDefault();
        removeTrack($(this));
        updateTrackCountNumbers();
    })
})

function removeTrack(link) {
    rowNumber = link.attr('rowNumber');
    $("#rowTrack" + rowNumber).remove();
}

function updateTrackCountNumbers() {
    $(".divCountTrack").each(function (index, element) {
        element.innerHTML = "" + (index + 1);
    });
}

function generateTrackCode() {
    nextCount = trackRecordCount + 1;
    trackRecordCount++;
    rowId = "rowTrack" + nextCount;
    trackNoteId = "trackNote" + nextCount;
    currentDateTime = formatCurrentDateTime();

    htmlCode = `
        <div class="row border rounded p-2 mb-2" id="${rowId}">
            <input type="hidden" name="trackId" class="hiddenTrackId" value="0">
            <div class="col-2">
                <div class="divCountTrack">${nextCount}</div>

                <a class="fa-solid fa-trash icon-dark fa-lg linkRemoveTrack" href=""
                   rowNumber="${nextCount}"
                   title="Delete this track" style="text-decoration:none"></a>
            </div>

            <div class="col">
                <div class="row">
                    <table>
                        <tr>
                            <td style="width: 10%">Time:</td>
                            <td>
                                <input type="datetime-local" name="trackDate"
                                       value="${currentDateTime}" class="form-control"
                                       required style="max-width: 300px" />
                            </td>
                        </tr>
                        <tr>
                            <td>Status:</td>
                            <td>
                                <select name="trackStatus" class="form-select dropDownStatus" required 
                                        style="max-width: 150px" rowNumber="${nextCount}">
    `;

    htmlCode += $("#trackStatusOptions").clone().html();

    htmlCode += `
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <td>Notes:</td>
                            <td>
                                <textarea rows="2" cols="10" class="form-control"
                                          name="trackNotes" style="max-width: 300px"
                                          id="${trackNoteId}" required></textarea>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
    `;

    $("#trackList").append(htmlCode);
}

function formatCurrentDateTime() {
    date = new Date();
    year = date.getFullYear();
    month = date.getMonth() + 1;
    day = date.getDate();
    hour = date.getHours();
    minute = date.getMinutes();
    second = date.getSeconds();

    if (month < 10) month = "0" + month;
    if (day < 10) day = "0" + day;

    if (hour < 10) hour = "0" + hour;
    if (minute < 10) minute = "0" + minute;
    if (second < 10) second = "0" + second;

    return year + "-" + month + "-" + day + "T" + hour + ":" + minute + ":" + second;

}