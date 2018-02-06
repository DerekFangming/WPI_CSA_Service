$(document).ready(function() {
	$('#img-picker').imagePicker({name: 'images'});
})

function selectType(typeId) {
	$("#currentType").html(getFeedTypeText(typeId));
	
	if (typeId == 3) {
		$('#alertMsg').attr("class", 'alert alert-success');
		$('#alertMsg').html('You will be posting this article as CSA Official.');
		$("#eventInput").fadeIn();
	} else {
		if ($('#hasAvatar').val() == 'true') {
			$('#alertMsg').attr("class", 'alert alert-success');
			$('#alertMsg').html('Don\'t forget to check format to make sure your article look good in all platforms.');
		} else {
			$('#alertMsg').attr("class",  'alert alert-warning');
			$('#alertMsg').html('You do not have an avatar and it will show as the default panda. We strongly recommend you to add an avatar from mobile end before posting articles.');
		}
		if ($('#eventInput').length) {
			$("#eventInput").fadeOut();
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

$("#instructionBtn").click(function(){
	var instructions = '<h5>Cover image: <small class="text-muted">Cover image is the image shown along with the title. There must ' +
	'be a cover image for each article. Please choose a landscape image with aspect ratio around 5 x 3. If you use a portrait image, the ' +
	'image will be stretched horizontally and only the center is shown. Please use a relative image for your article.</small></h5><br><h5>' +
	'Article type: <small class="text-muted">Article type will be Blog by default. Change it to Trade if you want to sell something and be ' +
	'sure to leave contact information in the article. CSA members will be able to create article with type Event.</small></h5><br><h5>Title: ' +
	'<small class="text-muted">Please use a short descriptive title. A long title will be shortened on mobile devices, depending on the ' +
	'screen width.</small></h5><br><h5>Content: <small class="text-muted">To change text color, click on the&nbsp;<i class="fa fa-tint">' +
	'</i>&nbsp;button. To change text size, click on the&nbsp;<i class="fa fa-paragraph"></i>&nbsp;button. Images are shown on a full ' +
	'line. Texts are only allowed above or below images, not inline. You do not need to resize images. They will automatically be stretched ' +
	'horizontally to the full line. Please use images with at least 800 * 800 resolution. Table allows only one column, shown as a list. </small></h5>';
	showPopup('Instruction', instructions);
});

//Start of event input modal
$("#eventInputBtn").click(function(){
	$('#eventInputModal').modal('toggle');
});

//Event type toggle
$("#calToggleBtn").click(function(){
	$('#calToggleBtn').attr('class', 'btn btn-secondary');
	$('#ticketToggleBtn').attr('class', 'btn btn-outline-secondary');
	$('#ticketStatusLbl').attr('placeholder', 'You are all set');
});

$("#ticketToggleBtn").click(function(){
	$('#calToggleBtn').attr('class', 'btn btn-outline-secondary');
	$('#ticketToggleBtn').attr('class', 'btn btn-secondary');
	$('#ticketStatusLbl').attr('placeholder', 'Enter following fields');
});

//Event fee toggle
$("#freeTicketToggleBtn").click(function(){
	$('#freeTicketToggleBtn').attr('class', 'btn btn-secondary');
	$('#paidTicketToggleBtn').attr('class', 'btn btn-outline-secondary');
	$('#ticketFeeInput').prop('disabled', true);
	$('#ticketFeeInput').val('');
});

$("#paidTicketToggleBtn").click(function(){
	$('#freeTicketToggleBtn').attr('class', 'btn btn-outline-secondary');
	$('#paidTicketToggleBtn').attr('class', 'btn btn-secondary');
	$('#ticketFeeInput').prop('disabled', false);
});

//Event balance toggle
$("#sellNowToggleBtn").click(function(){
	$('#sellNowToggleBtn').attr('class', 'btn btn-secondary');
	$('#sellLaterToggleBtn').attr('class', 'btn btn-outline-secondary');
});

$("#sellLaterToggleBtn").click(function(){
	$('#sellNowToggleBtn').attr('class', 'btn btn-outline-secondary');
	$('#sellLaterToggleBtn').attr('class', 'btn btn-secondary');
});

function chooseFile(option) {
    $("#ticketImgInput").click();
    $("#ticketImgOption").val(option);
 }

$("#ticketImgInput").change(function() {
    if ($(this).prop('files')[0]) {
        var reader = new FileReader();
        reader.onload = function(e) {
        		if ($("#ticketImgOption").val() == '1') {
        			$("#ticketBGImage").attr('src', e.target.result);
        		} else {
        			$("#ticketThumnImage").attr('src', e.target.result);
        		}
        }
        reader.readAsDataURL($("#ticketImgInput").prop('files')[0]);
    }                
});
