$(document).ready(function() {
	$('#img-picker').imagePicker({name: 'images'});
})

function selectType(typeId) {
	$("#currentType").html(getFeedTypeText(typeId));
	
	if (typeId == 3) {
		$('#alertMsg').attr("class", 'alert alert-success');
		$('#alertMsg').html('You are posting this article as CSA Official');
	} else {
		if ($('#hasAvatar').val() == 'true') {
			$('#alertMsg').attr("class", 'alert alert-success');
			$('#alertMsg').html('Don\'t forget to check format to make sure your article look good in all platforms.');
		} else {
			$('#alertMsg').attr("class",  'alert alert-warning');
			$('#alertMsg').html('You do not have an avatar and it will show as the default panda. We strongly recommend you to add an avatar from mobile end before posting articles.');
		}
	}

}

function getFeedTypeText(typeId) {
	if (typeId == 1) {
		return 'Blog';
	} else if (typeId == 2) {
		return 'Trade';
	} else if (typeId == 3) {
		return 'Event';
	} else {
		return 'Unknown';
	}
}

$("#checkFormatBtn").click(function(){
	var error = checkFormat();
	if (error == '' ) {
		showPopup('<span style="color:green"> No errors found<i class="fa fa-check"></i></span>', 'The format has no problems and should look good in all platforms.');
	} else {
		showErrorPopup(error);
	}
});



$("#submitBtn").click(function(){
	var error = checkFormat();
	if (error != '' ) {
		showErrorPopup(error);
	} else {
		var cover = $('#img-picker').children().children('img').attr('src');
		var title = $('#title').val().trim();
		var type = $("#currentType").html();
		var content = getAcceptableHTML($('textarea').froalaEditor('html.get', true));
		var accessToken = getAccessToken();
		$("#submitBtn").prop('disabled', true);
		$("#submitSpinner").show();
	    
	    $.ajax({
	        type: "POST",
	        url: "./create_feed",
	        data: JSON.stringify({accessToken : accessToken, title : title, type : type, body : content, coverImage : cover }),
	        contentType: "application/json",
	        dataType: "json",
	        success: function(data){
	        	$("#submitBtn").prop('disabled', false);
	        	$("#submitSpinner").hide();
				if (data['error'] == "" ) {
					showPopup('Done', 'Article created. You will be redirected in 5 seconds.');
					window.setTimeout(function(){
						window.location.href = "./";
					}, 5000);
				} else {
					showErrorPopup(data['error']);
				}
	        },
	        failure: function(errMsg) {
	        	$("#submitBtn").prop('disabled', false);
	        	$("#submitSpinner").hide();
	        	showErrorPopup('Unknown error occured. Please contact support');
	        }
	    });
	    
	}

    
});


function checkFormat () {
	if ($('#img-picker').children().children('img').length == 0) {
		return 'Please select a cover image on the left.';
	}
	
	if ($('#title').val().trim().length == 0) {
		return 'Please enter the title.';
	}
	return checkContentFormat($('textarea').froalaEditor('html.get', true));
}

