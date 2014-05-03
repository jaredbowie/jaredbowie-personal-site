$(document).ready(function(){
    $("#data-edit-button,#long-data-to-edit").attr('disabled','disabled');
    $('#data-edit-button').click(function(){
        var theLong = $('#long-data-to-edit').val();
        var jsonMapBodyTitle = getBlogPost(theLong);
        //console.log("docready" + jsonMapBodyTitle);
        //httpResponseToTextArea(jsonMapBodyTitle);
        });
    $('button#save-post-button').click(function(){
        var theTitle = $('textarea#blog-title').val();
        var theBody = $('textarea#blog-content').val();
        var theLong = $('textarea#long-data-to-edit').val();
        var checkboxStatus = $('#new-post-check').is(':checked');
        console.log(checkboxStatus);
        if (theTitle != '' & theBody != '') {
            if (checkboxStatus === false) {
                if (theLong === '') {
                    alert('If new post check new post, otherwise fill in post name');
                }
                else { //checkbox is not checked and the long has a number (presumably)
                    addBlogPost(theTitle, theBody, theLong);
                    }
            }
            else { // checkbox is checked long can't have a value now
                addBlogPost(theTitle, theBody, '');
                }
            }
            else { // theTitle or theBody is empty
                alert('title or body empty');
            }
        });

    $('#new-post-check').click(function(){
        if (this.checked) {
            $("#data-edit-button,#long-data-to-edit").attr('disabled','disabled');
            $("#long-data-to-edit").val('');
            }
        else {
            $("#data-edit-button,#long-data-to-edit").removeAttr('disabled','disabled');
        }
        });
});

var addBlogPost = function(title, body, longdate){  /// if no longdate then new
    var postObject = new Object();
    postObject.title = title;
    postObject.body = body;
    postObject.longdate = longdate;
    var jsonString = JSON.stringify(postObject);
    $.ajax({
        url: "add-post",
        type: "post",
        //dataType: "text",
        data: { blogpost: jsonString }
       /* success: function(r) {
            //console.log("getBlogPost" + r);
            httpResponseToTextArea(r);
        }*/
    });
};

var getBlogPost = function(longdate){
    $.ajax({
        url: "edit-post",
        type: "get",
        //dataType: "text",
        data: { longdate: longdate },
        success: function(r) {
            //console.log("getBlogPost" + r);
            httpResponseToTextArea(r);
        }
    });
};

var httpResponseToTextArea = function(titleBodyMap){
    var jsonObjTitleBodyMap = $.parseJSON(titleBodyMap);
    $('textarea#blog-title').val(jsonObjTitleBodyMap.posttitle);
    $('textarea#blog-content').val(jsonObjTitleBodyMap.postcontent);
};
