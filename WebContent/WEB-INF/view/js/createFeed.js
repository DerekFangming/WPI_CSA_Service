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
	
	/*if ($('#currentUserID').val() == $('#selectedUserId').val()) {
		$("#setUserRoleBtn").prop('disabled', true);
	} else if (parseInt($('#currentUserRoleID').val()) > parseInt($('#selectedUserRoleId').val())) {
		$("#setUserRoleBtn").prop('disabled', true)
	} else {
		$("#setUserRoleBtn").prop('disabled', $("#currentRole").html() ==  getUserRoleText(parseInt($('#selectedUserRoleId').val())));
	}*/
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
	var error = checkContentFormat($('textarea').froalaEditor('html.get', true));
	if (error == '' ) {
		showPopup('No errors found', 'The format has no problems and should look good in all platforms.');
	} else {
		showErrorPopup(error);
	}
});



$("#submitBtn").click(function(){
	var error = checkContentFormat($('textarea').froalaEditor('html.get', true));
	if (error != '' ) {
		showErrorPopup(error);
	} else {
		//var accessToken = getAccessToken();
		alert(getAcceptableHTML($('textarea').froalaEditor('html.get', true)));
	}
	
    
    /*$("#resendEmailConfirmBtn").prop('disabled', true);
    
    $.ajax({
        type: "POST",
        url: "../../send_verification_email",
        data: JSON.stringify({accessToken : accessToken, requestedUserId : parseInt($('#selectedUserId').val())}),
        contentType: "application/json",
        dataType: "json",
        success: function(data){
        	$("#resendEmailConfirmBtn").prop('disabled', false);
			if (data['error'] == "" ) {
				showPopup('Done', 'Verification email sent for ' + $('#userDispName').text());
			} else {
				showErrorPopup(data['error']);
			}
        },
        failure: function(errMsg) {
        	$("#resendEmailConfirmBtn").prop('disabled', false);
        	showErrorPopup('Unknown error occured. Please contact support');
        }
    });*/
    
    
});

var dragTimer;
$('#img-picker').on('dragover', function(e) {
	var dt = e.originalEvent.dataTransfer;
	if (dt.types && (dt.types.indexOf ? dt.types.indexOf('Files') != -1 : dt.types.contains('Files'))) {
		$('#img-picker').attr('style', 'background: #DFDFDF;');
		window.clearTimeout(dragTimer);
	}
});

$('#img-picker').on('dragleave', function(e) {
	dragTimer = window.setTimeout(function() {
		$('#img-picker').attr('style', 'background: white;');
	}, 25);
});

function allowDrop(ev) {
	if ($('#img-picker').children().children('img').length == 0) {
		ev.preventDefault();
	}
}

