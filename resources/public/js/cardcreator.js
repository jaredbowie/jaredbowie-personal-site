$(document).ready(function(){
    $('#add-notes-button').click(function(){
        //var test = $('div').find('#one-card-id');
        var cardId = $('#one-card-id').attr('cardid');
        console.log(cardId);
        addOneNote();
    });

    $('#submit-button').click(function(){
        saveCard();
        //$.post("card-creator", clojureMapToSend);
    });

//click on deck display it's cards
    $('.deck-name').click(function(){
        var deckId = $(this).attr('id'); // grabs deckid from the deck div you click on
      //  console.log(deckId);
        var deckName = $(this).attr('deck-name');
        var deckNameAreaInput = $('div').find('#deck-name-area-input');
        $(deckNameAreaInput).attr('deckid', deckId);
        $(deckNameAreaInput).text(deckName);
        $(".card-name").remove();
        resetCard();
        returnCards(deckId);
    });

//click on one card
    $('#cards-list').on("click", ".card-name", function(){
        //console.log("hi");
        resetCard();
        var cardIdToUse = $(this).attr('id');
        console.log(cardIdToUse);
        $("#one-card-id").attr('cardid', cardIdToUse);
        var cardId = $(this).attr('id');
        var deckId = $(this).attr('deck');
        returnOneCard(cardId, deckId);
     });

    $('#reset-button').click(function(){
        //console.log(testVar);
        resetCard();
    });

    // remove notes
    $('#card-creator').on('click', "button[buttontype='delete-one-note-button']", function(){
        //var theParents = $(this).closest('.one-note-group');
        var theNext = $(this).parent().parent();
        //var theChildrens = theParents.closest('.one-note-group');
        theNext.remove();
        console.log(theNext);
    });
});

var deckIdExists = function(){
    var deckNameAreaInput = $('div').find('#deck-name-area-input');
    return $(deckNameAreaInput).attr('deckid');
};

var saveCard = function(){

    var deckId = deckIdExists();
    if(typeof deckId === 'undefined'){
        alert("Must Select a Deck");
    }
    else {
  //var deckId = $('#deck-name-area-input').attr();
        console.log(deckId);
        var allNoteNames = [];
        var oneCardObj = new Object();
        oneCardObj['paragraph'] =  $('#paragraph').val();
        $('.one-note-group').children("textarea").each(function(){
            allNoteNames.push(this.value);
        });
        oneCardObj['notes'] = groupThings(allNoteNames);
        // console.log(oneCardObj.notes);
        oneCardObj['audio-path'] = $('#audio-path').val();
        oneCardObj['font-color'] = $('#font-color').val();
        var jsonStringToSend = JSON.stringify(oneCardObj);
        console.log(jsonStringToSend);
        console.log(cardId);
        request = $.ajax({
             url: "card-creator/save-card",
             type: "post",
             dataType: "text",
             data: {
                 cardid: cardId,
                 deckid: deckId,
                 onecardmap: jsonStringToSend },
             success: function(r){
                 console.log(r);
             }
         });
    };
};

// gather notes info
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
        tempObject.japanese=allNoteNames[i];
        tempObject.reading = theReading;
        tempObject.english = allNoteNames[i + 2];
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
   // console.log('returneOneCard');
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
    var notes = $("div[class2='one-note-line']");
    $(notes).remove();
    $("#font-color").val("#0000ff");
    $("#one-card-id").removeAttr("cardid");
};

var addOneNote = function(){
    $('<div class="one-input" class2="one-note-line"><div class="left-label"><button class="add-rem-button" type="button" buttontype="delete-one-note-button">Remove Note</button></div><div class="right-label"><div class="one-note-group"><textarea placeholder="Japanese Word" class="notes-japanese-word" name="word"></textarea><textarea placeholder="Furigana" class="notes-furigana-word" name="reading"></textarea><textarea placeholder="English" class="notes-english-explanation" name="english"></textarea></div></div></div>').insertAfter('#notes-section');
};

//insert notes with the strings from an array
var noteInsert = function(noteArray){
    var string1 = '<div class="one-input" class2="one-note-line"><div class="left-label"><button class="add-rem-button" type="button" buttontype="delete-one-note-button">Remove Note</button></div><div class="right-label"><div class="one-note-group"><textarea placeholder="Japanese Word" class="notes-japanese-word" name="word">';
    var string2 = '</textarea><textarea placeholder="Furigana" class="notes-furigana-word" name="reading">';
    var string3 = '</textarea><textarea placeholder="English" class="notes-english-explanation" name="english">';
    var string4 = '</textarea></div></div></div>';
   // console.log(noteArray[0]["japanese"]);
    for (var i=0; i < noteArray.length; i++) {
        $(string1 + noteArray[i]["japanese"] + string2 + noteArray[i]["english"] + string3 + noteArray[i]["reading"] + string4).insertAfter('#notes-section');
        }
};
