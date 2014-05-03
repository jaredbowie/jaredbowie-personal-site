$(document).ready(function(){
   $("#data-edit-button,#long-data-to-edit").attr('disabled','disabled');
    $('#data-edit-button').click(function(){
        var theLong = $('#long-data-to-edit').val();
        getBlogPost(theLong);
        });
    $('#save-post-button').click(function(){
        var theTitle = $('textarea#blog-title').val();
        var theBody = $('textarea#blog-content').val();
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

var addBlogPost = function(title, body, longdate, checkboxyesorno){  /// if no longdate then new
    var postObject = new Object();
    postObject.title = title;
    postObject.body = body;
    postObject.longdate = longdate;
    var jsonString = JSON.stringify(postObject);
    console.log(jsonString);
//request = $
    };

var getBlogPost = function(longdate){
    request = $.ajax({
        url: "edit-post",
        type: "get",
        //dataType: "text",
        data: { longdate: longdate },
        success: function(r) {
             console.log(request.responseText);
        }
    });


};
