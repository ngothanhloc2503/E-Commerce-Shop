$(document).ready(function () {
    $(".linkVoteReview").on("click", function (e) {
        e.preventDefault();
        voteReview($(this));
    })
})

function voteReview(currentLink) {
    requestURL = currentLink.attr("href");

    $.ajax({
        type: 'POST',
        url: requestURL,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function (voteResult) {
        console.log(voteResult);

        if (voteResult.successful) {
            $("#modalDialog").on("hide.bs.modal", function (e) {
                updateVoteCountAndIcons(currentLink, voteResult);
            })
        }

        showModalDialog("Vote Review", voteResult.message);
    }).fail(function () {
        showErrorModal("Error voting review.");
    })
}

function updateVoteCountAndIcons(currentLink, voteResult) {
    reviedId = currentLink.attr("reviewId");
    voteUpLink = $("#linkVoteUp-" + reviedId);
    voteDownLink = $("#linkVoteDown-" + reviedId);

    $("#voteCount-" + reviedId).text(voteResult.voteCount + " Votes");

    message = voteResult.message;

    if (message.includes("successfully voted up")) {
        highlightVoteUpIcon(currentLink, voteDownLink);
    } else if (message.includes("successfully voted down")) {
        highlightVoteDownIcon(currentLink, voteUpLink);
    } else if (message.includes("unvoted down")) {
        unHighlightVoteDownIcon(currentLink);
    } else if (message.includes("unvoted up")) {
        unHighlightVoteUpIcon(currentLink);
    }
}

function highlightVoteUpIcon(voteUpLink, voteDownLink) {
    voteUpLink.removeClass("far").addClass("fas");
    voteUpLink.attr("title", "Undo vote up this review");

    voteDownLink.removeClass("fas").addClass("far");
}

function highlightVoteDownIcon(voteDownLink, voteUpLink) {
    voteDownLink.removeClass("far").addClass("fas");
    voteDownLink.attr("title", "Undo vote down this review");

    voteUpLink.removeClass("fas").addClass("far");
}

function unHighlightVoteDownIcon(voteDownLink) {
    voteDownLink.removeClass("fas").addClass("far");
    voteDownLink.attr("title", "Vote down this review");
}

function unHighlightVoteUpIcon(voteUpLink) {
    voteUpLink.removeClass("fas").addClass("far");
    voteUpLink.attr("title", "Vote up this review");
}