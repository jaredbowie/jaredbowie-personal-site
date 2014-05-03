$(document).ready(function(){
    var allNoteNames = [];
    $('#add-notes-button').click(function(){
        $('<div class="one-input"><div class="left-label"><label form="notes-chunk"><font class="string">"Note" </font></label></div><div class="right-label"><div class = "one-note-group"><textarea placeholder="Japanese Word" class="notes-japanese-word" name="word"></textarea><textarea placeholder="Furigana" class="notes-furigana-word" name="reading"></textarea><textarea placeholder="English" class="notes-english-explanation" name="english"></textarea></div></div></div>').insertAfter('#notes-section');
    });
    $('#submit-button').click(function(){
        var paragraphText = ':paragraph "' + $('#paragraph').val() + '" ';
        // var notesText =
        $('.one-note-group').children("textarea").each(function(){
            allNoteNames.push(this.value);
        });
        var notesText = ':notes [' + groupThings(allNoteNames) + '] ';
        //var oneNote = $(notesText[1].val());
        var audioPath =  ':audio "' + $('#audio-path').val() + '"';
        // console.log(allNoteNames.length);
        var clojureMapToSend = '{' + paragraphText + notesText + audioPath + '}';
        console.log(clojureMapToSend);
        request = $.ajax({
            url: "card-creator",
            type: "post",
            //dataType: "text",
            data: { data: clojureMapToSend }
        });
        //$.post("card-creator", clojureMapToSend);
    });
});

var groupThings = function(allNoteNames){
    var theReading = "";
    var theWhole = ""
    for (var i=0; i < allNoteNames.length; i+=3) {
        if (allNoteNames.length === 0) {
            theReading = "nil";
        }
        else {
            theReading = allNoteNames[i + 1];
            }
       theWhole = theWhole + '{:word "' + allNoteNames[i] + '" :reading ' + theReading + '" :translation "' + allNoteNames[i + 2] + '"} ';
    }
    return theWhole;
};
