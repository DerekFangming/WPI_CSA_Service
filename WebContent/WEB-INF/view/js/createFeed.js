$(document).ready(function() {
	$('#img-picker').imagePicker();
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
		startBtnLoading('#submitBtn');
		
		var params = {accessToken : accessToken, title : title, type : type, body : content, coverImage : cover };
		
		if ($("#currentType").html() == 'Event') {
			error = checkEventFormat();
			if (error != '' ) {
				showErrorPopup(error);
				return;
			} else {
				params.eventTitle = $("#eventTitle").val().trim();
				params.eventDesc = $("#eventDesc").val().trim();
				params.eventStartTime = new Date($("#eventSTime").val().trim()).toISOString();
				params.eventEndTime = new Date($("#eventETime").val().trim()).toISOString();
				params.eventLocation = $("#eventLocation").val().trim();
				
				
				if ($('#calToggleBtn').attr('class').includes('btn-outline-secondary')) {//ticket also needed
					if ($('#ticketFeeInput').val() == '') {
						params.ticketFee = 0.0;
					} else {
						params.ticketFee = parseFloat($('#ticketFeeInput').val());
					}
					params.ticketActive = $('#sellNowToggleBtn').attr('class').includes('btn-secondary');
					params.ticketBalance = parseInt($('#ticketBalInput').val());
					params.ticketBgImage = $('#ticketBGImage').attr('src');
					params.ticketThumbImage = $('#ticketThumnImage').attr('src');
				}
				
			}
		}
	    
	    $.ajax({
	        type: "POST",
	        url: "./create_feed",
	        data: JSON.stringify(params),
	        contentType: "application/json",
	        dataType: "json",
	        success: function(data){
	        	stopBtnLoading('#submitBtn');
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
	        	stopBtnLoading('#submitBtn');
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
	$("#ticketDetailSec").fadeOut();
});

$("#ticketToggleBtn").click(function(){
	$('#calToggleBtn').attr('class', 'btn btn-outline-secondary');
	$('#ticketToggleBtn').attr('class', 'btn btn-secondary');
	$('#ticketStatusLbl').attr('placeholder', 'Enter following fields');
	$("#ticketDetailSec").fadeIn();
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

$('#eventInputModal').on('hidden.bs.modal', function () {
	if ($('#eventTitle').val().trim() != '' || $('#eventDesc').val().trim() != '' || $('#eventSTime').val().trim() != '' || $('#eventETime').val().trim() != '' ||
			$('#eventLocation').val().trim() != '') {
		$('#eventInputBtn').html('Edit event');
		$('#eventTitlePreview').val($('#eventTitle').val() == '' ? 'Title not enterred yet' : $('#eventTitle').val());
	} else {
		$('#eventInputBtn').html('Add an event');
		$('#eventTitlePreview').val('');
	}
})

function saveEvent(){
	var error = checkEventFormat();
	if (error != '' ) {
		showErrorPopup(error);
	} else {
		$('#eventInputModal').modal('toggle');
	}
}


function checkEventFormat() {
	if ($('#eventTitle').val().trim().length == 0) {
		return 'Please enter event title.';
	}
	if ($('#eventDesc').val().trim().length == 0) {
		return 'Please enter the description for this event. This will be shown in Calendar if user adds this event.';
	}
	if ($('#eventSTime').val().trim().length == 0) {
		return 'Please enter the start time for this event.';
	}
	if ($('#eventETime').val().trim().length == 0) {
		return 'Please enter the end time for this event.';
	}
	if ($('#eventLocation').val().trim().length == 0) {
		return 'Please enter the location for this event';
	}
	
	//Check if needs to sell ticket
	if ($('#calToggleBtn').attr('class').includes('btn-outline-secondary')) {
		if($('#ticketBalInput').val().trim().length == 0) {
			return 'Please enter ticket balance. This will be the total amount to ticket can be sold from all platforms. This can be updated later.';
		}
		var attr = $('#ticketBGImage').attr('src');
		if(typeof attr == typeof undefined || attr == false) {
			return 'Please select a ticket background image. Note that the image will be blurred and we recommand a dark colorred image for better effect.';
		}
		attr = $('#ticketThumnImage').attr('src');
		if(typeof attr == typeof undefined || attr == false) {
			return 'Please select a ticket thumbnail image.';
		}
	}
	
	
	return '';
}

