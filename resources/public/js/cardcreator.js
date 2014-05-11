$(document).ready(function(){
    $('#add-notes-button').click(function(){
        addOneNote();
    });

    $('#submit-button').click(function(){
        var allNoteNames = [];
        var oneCardObj = new Object();
        oneCardObj.paragraph =  $('#paragraph').val();
        // var notesText =
        $('.one-note-group').children("textarea").each(function(){
            //console.log(this.value);
            allNoteNames.push(this.value);
        });
        oneCardObj.notes = groupThings(allNoteNames);
        console.log(oneCardObj.notes);
        //var oneNote = $(notesText[1].val());
        oneCardObj.audio = $('#audio-path').val();
        // console.log(allNoteNames.length);
        var jsonStringToSend = JSON.stringify(oneCardObj);
        console.log(jsonStringToSend);
        request = $.ajax({
            url: "card-creator",
            type: "post",
            //dataType: "text",
            data: { onecard: jsonStringToSend }
        });
        //$.post("card-creator", clojureMapToSend);
    });

    $('.deck-name').click(function(){
        //console.log("hi");
        $(".card-name").remove();
        var deckId = $(this).attr('id');
        returnCards(deckId);
        });

    $('#cards-list').on("click", ".card-name", function(){
        //console.log("hi");
        var cardId = $(this).attr('id');
        var deckId = $(this).attr('deck');
        returnOneCard(cardId, deckId);
     });

    $('#reset-button').click(function(){
        resetCard()
    });
});

var groupThings = function(allNoteNames){
    var theReading = "";
    var theWhole = [];
    var tempObject = new Object();
    for (var i=0; i < allNoteNames.length; i+=3) {
        if (allNoteNames.length === 0) {
            theReading = "nil";
        }
        else {
            theReading = allNoteNames[i + 1];
            }
        tempObject.word=allNoteNames[i];
        tempObject.reading = theReading;
        tempObject.translation = allNoteNames[i + 2];
        theWhole.push(tempObject);
    }
    return theWhole;
};

var returnCards = function(deckId){
    request = $.ajax({
        url: "card-creator/return-cards",
        type: "get",
        data: {
            deckid: deckId },
        success: function(r){
            $("#font-cards-list").append(r);
        }
    });
};

var returnOneCard = function(cardId, deckId){
    resetCard();
    request = $.ajax({
        url: "card-creator/return-one-card",
        type: "get",
        data: {
            deckid: deckId,
            cardid: cardId },
        success: function(r){
            var cardObject = jQuery.parseJSON(r);
            $("#paragraph").val(cardObject["paragraph"]);
            $('#audio-path').val(cardObject["audio-path"]);
            $("#font-color").val(cardObject["font-color"]);
            noteInsert(cardObject["notes"]);
        }
    });
};

var resetCard = function(){
        //console.log("hi");
        $("#paragraph").val("");
        $('#audio-path').val("");
        $(".input-note").remove();
        $("#font-color").val("#0000ff");
    };

var addOneNote = function(){
    $('<div class="one-input input-note"><div class="left-label"><label form="notes-chunk"><font class="string">"Note" </font></label></div><div class="right-label"><div class = "one-note-group"><textarea placeholder="Japanese Word" class="notes-japanese-word" name="word"></textarea><textarea placeholder="Furigana" class="notes-furigana-word" name="reading"></textarea><textarea placeholder="English" class="notes-english-explanation" name="english"></textarea></div></div></div>').insertAfter('#notes-section');
};

var noteInsert = function(noteArray){
    var string1 = '<div class="one-input input-note"><div class="left-label"><label form="notes-chunk"><font class="string">"Note" </font></label></div><div class="right-label"><div class = "one-note-group"><textarea placeholder="Japanese Word" class="notes-japanese-word" name="word">';
    var string2 = '</textarea><textarea placeholder="Furigana" class="notes-furigana-word" name="reading">';
    var string3 = '</textarea><textarea placeholder="English" class="notes-english-explanation" name="english">';
    var string4 = '</textarea></div></div></div>';
    console.log(noteArray[0]["japanese"]);
    for (var i=0; i < noteArray.length; i++) {
        $(string1 + noteArray[i]["japanese"] + string2 + noteArray[i]["english"] + string3 + noteArray[i]["reading"] + string4).insertAfter('#notes-section');
        }
};
