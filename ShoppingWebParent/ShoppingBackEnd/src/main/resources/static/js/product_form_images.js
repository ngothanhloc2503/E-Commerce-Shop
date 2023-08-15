var extraImagesCount = 0;

$(document).ready(function () {
    $("input[name='extraImage']").each(function (index) {
        extraImagesCount++;

        $(this).change(function () {
            if (!checkFileSize(this)) {
                return;
            }
            showExtraImageThumbnail(this, index);
        })
    });
});

function showExtraImageThumbnail(fileInput, index) {
    if (!checkFileSize(fileInput)) {
        return;
    }

    var file = fileInput.files[0];
    var reader = new FileReader();
    reader.onload = function (e) {
        $("#extraThumbnail" + index).attr("src", e.target.result);
    }
    console.log(index);
    reader.readAsDataURL(file);

    if (index >= extraImagesCount - 1) { // check Next Extra Image Section generated?
        addNextExtraImageSection(index + 1);
    }
}

function addNextExtraImageSection(index) {
    htmlExtraImage = `
        <div class="col border m-3 p-2" id="divExtraImage${index}">
            <div id="extraImageHeader${index}">
                <label>Extra Images #${index + 1}:</label>
            </div>
            <div class="m-2">
                <img src="${defaultImageThumbnailSrc}" id="extraThumbnail${index}" alt="Extra image #${index + 1} preview" 
                     class="img-fluid" style="width: 400px">
            </div>
            <div>
                <input type="file" name="extraImage"
                       onchange="showExtraImageThumbnail(this, ${index})"
                       accept="images/png, images/jpg, images/jpeg">
            </div>
        </div>
    `;

    htmlLinkRemove = `
        <a class="btn fas fa-times-circle fa-2x icon-dark float-end" 
        href="javascript:removeExtraImage(${index - 1})" title="Remove this image"></a>
    `;

    $("#divProductImages").append(htmlExtraImage);
    $("#extraImageHeader" + (index - 1)).append(htmlLinkRemove);
    extraImagesCount++;
}

function removeExtraImage(index) {
    $("#divExtraImage" + index).remove();
}
