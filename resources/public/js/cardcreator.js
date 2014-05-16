$(document).ready(function(){
 //   console.log(uniqueNumber());
    $('div.add-deck').click(function(){
        var deckName = prompt("Deck Name");
        if(deckName != null){
            request = $.ajax({
                url: "card-creator/new-deck",
                type: "post",
                dataType: "text",
                data: {
                    deckname: deckName },
                success: function(r){
                    console.log(r);
                    location.reload();
                }
            });
        }
    });


    $('div.add-card').click(function(){
        resetCard();
       });


    $('#card-creator').on('keyup', '.notes-japanese-word', function(){
        setTimeout(function(){
            highlightWords();
        }, 100);
        });

/// making words highlight as they're written
    $('#card-creator').on('focus', '.notes-japanese-word', function(){
        highlightWords();
    });

    $('#card-creator').on('focusout', '.notes-japanese-word', function(){
        unHighlightWords();
    });

    $('div.card-name').click(function(){
        console.log('add-card');
    });

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
        var deckDelete = ' <div class="delete-deck"><a href="#"><font class="para1">(</font><font class="functionbuiltin">Delete Deck</font><font class="para1">)</font></a></div>';
        var deckName = $(this).attr('deck-name');
        var deckNameAreaInput = $('div').find('#deck-name-area-input');
        $(deckNameAreaInput).attr('deckid', deckId);
        $(deckNameAreaInput).text(deckName);
        $(deckNameAreaInput).append(deckDelete);
        $(".card-name").remove();
        resetCard();
        returnCards(deckId);
    });


// delete deck button
    $("#deck-name-area-input").on("click", "div.delete-deck", function(){
        var deckIdToDelete = $('#deck-name-area-input').attr('deckid');
        request = $.ajax({
            url: "card-creator/delete-deck",
            type: "post",
            dataType: "text",
            data: {
                deckid: deckIdToDelete },
            success: function(r){
                location.reload();
                console.log(r);
            }
        });
    });

//click on one card
    $('#cards-list').on("click", ".card-name", function(){
        //console.log("hi");
        resetCard();
        var cardIdToUse = $(this).attr('id');
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
    var cardId = $('#one-card-id').attr('cardid');
    if(typeof cardId === 'undefined'){
        var cardId = false;
    };
    var deckId = deckIdExists();
    if(typeof deckId === 'undefined'){
        alert("Must Select a Deck");
    }
    else {
  //var deckId = $('#deck-name-area-input').attr();
        console.log(deckId);
        var allNoteNames = [];
        var oneCardObj = new Object();
        oneCardObj['paragraph'] =  $('#paragraph').text();
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
                 location.reload();
                 console.log(r);
             }
         });
    };
};


var highlightWords = function(){
    var currentFocus = $(document.activeElement).filter(':focus');
    var currentFocusId = $(currentFocus).attr('id');
    var notesJapaneseWord = $("textarea[id='" + currentFocusId + "']").val();
    console.log(notesJapaneseWord + '-focus jword');
          // basically just rest the form
    var paragraphVal = $('#paragraph').text();
    $("#paragraph").text("");
    // get the japanese word again
    // if japanese word isn't empty
    if (typeof notesJapaneseWord != 'undefined'){
        // take the current paragraph and make the focus-word the current japaense word
        var newWordWithFontColor = '<div class="focus-word">' + notesJapaneseWord + '</div><!-- closing focus-word -->';
        // replace the japanese word in the paragraph with the new colored word
        // regex to match all
        var notesJapaneseWordRegEx = new RegExp(notesJapaneseWord, 'g');
        var newParagraphVal = paragraphVal.replace(notesJapaneseWordRegEx, newWordWithFontColor);
        console.log('newparagraphval=' + newParagraphVal);
        // add the new text to the paragraph
        $('#paragraph').append(newParagraphVal);
        //console.log(notesJapaneseWord);
    }
    else {
        unHighlightWords(paragraphVal);
    }
};

var unHighlightWords = function(paragraphVal){
   // console.log('unhighlight');
    //var paragraphVal = $('#paragraph').text();
    //console.log('unhlight para val' + paragraphVal)
   // $("#paragraph").text("");
    $('#paragraph').text(paragraphVal);
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
    var cardNameAreaInput = $('div').find('#card-name-area-input');
   // console.log('returneOneCard');
    request = $.ajax({
        url: "card-creator/return-one-card",
        type: "get",
        data: {
            deckid: deckId,
            cardid: cardId },
        success: function(r){
            var cardObject = jQuery.parseJSON(r);
            $("#paragraph").text(cardObject["paragraph"]);
            $('#audio-path').val(cardObject["audio-path"]);
            $("#font-color").val(cardObject["font-color"]);
            $(cardNameAreaInput).text(cardObject["paragraph"].substring(0,10));
            noteInsert(cardObject["notes"]);
        }
    });
};

var resetCard = function(){
        //console.log("hi");
    var cardNameAreaInput = $('div').find('#card-name-area-input');
    $(cardNameAreaInput).text("");
    $("#paragraph").text("");
    $('#audio-path').val("");
    var notes = $("div[class2='one-note-line']");
    $(notes).remove();
    $("#font-color").val("#0000ff");
    $("#one-card-id").removeAttr("cardid");
};


// this is for new notes
var addOneNote = function(){
    var uniqueId = uniqueNumber();
    var string1 = '<div class="one-input" class2="one-note-line"><div class="left-label"><button class="add-rem-button" type="button" buttontype="delete-one-note-button">Remove Note</button></div><div class="right-label"><div class="one-note-group"><textarea placeholder="Japanese Word" class="notes-japanese-word" id="';
    var string2 = '" name="word"></textarea><textarea placeholder="Furigana" class="notes-furigana-word" id="';
    var string3 = '" name="reading"></textarea><textarea placeholder="English" class="notes-english-explanation" id="';
    var string4 = '" name="english"></textarea></div></div></div>';
    var completeStringWithId = string1 + uniqueId + string2 + uniqueId + string3 + uniqueId + string4;
    $(completeStringWithId).insertAfter('#notes-section');
};

//insert notes with the strings from an array
// this is for editing cards when you have to place new notes in
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

var uniqueNumber = function(){
    var min = 100000000;
    var max = 999999999;;
    // and the formula is:
    var random = Math.floor(Math.random() * (max - min + 1)) + min;
    return random;
};
