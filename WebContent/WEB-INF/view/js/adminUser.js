$(document).ready(function() {
	$("#resendEmailConfirmBtn").prop('disabled', true);
	$("#sendResetPasswordBtn").prop('disabled', true);
	$("#setUserRoleBtn").prop('disabled', true);
	var table = $('#userTable').DataTable();
	
	$('#userTable tbody').on('click','tr', function() {
		var row = table.row(this).data();
	    $('#selectedUserId').val(row[0]);
	    $('#userDispName').text(row[1]);
	    $('#userUsername').text(row[2]);
	    $("#currentRole").html(row[4]);
	    $("#selectedUserRole").val(row[4]);
	    $('#userCreated').text(row[5].substring(0,10));
	    
	    if (row[3].includes('0')) {
	    	$("#resendEmailConfirmBtn").prop('disabled', false);
	    } else {
	    	$("#resendEmailConfirmBtn").prop('disabled', true);
	    }
	    $("#sendResetPasswordBtn").prop('disabled', false);
		$("#setUserRoleBtn").prop('disabled', true);
	});
	
});

$("#resendEmailConfirmBtn").click(function(){
    var accessToken = getAccessToken();
    $("#resendEmailConfirmBtn").prop('disabled', true);
    
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
    });
}); 

$("#sendResetPasswordBtn").click(function(){
    var accessToken = getAccessToken();
    $("#sendResetPasswordBtn").prop('disabled', true);
    
    $.ajax({
        type: "POST",
        url: "../../send_change_pwd_email",
        data: JSON.stringify({accessToken : accessToken, email : $('#userUsername').text()}),
        contentType: "application/json",
        dataType: "json",
        success: function(data){
        	$("#sendResetPasswordBtn").prop('disabled', false);
			if (data['error'] == "" ) {
				showPopup('Done', 'Password reset email sent for ' + $('#userDispName').text());
			} else if (data['error'].startsWith('You have to confirm your email before changing password.')) {
				showErrorPopup('The user has to confirm his email first before resetting password. The account confirmation email has been sent.');
			} else {
				showErrorPopup(data['error']);
			}
        },
        failure: function(errMsg) {
        	$("#sendResetPasswordBtn").prop('disabled', false);
        	showErrorPopup('Unknown error occured. Please contact support');
        }
    });
}); 

function selectRole(roleId) {
	if (roleId == 1) {
		$("#currentRole").html("System Admin");
	} else if (roleId == 2) {
		$("#currentRole").html("Site Admin");
	} else {
		$("#currentRole").html("User");
	}
	
	var selectedUserRoleId = 10;
	if ($("#selectedUserRole").val() == "System Admin") {
		selectedUserRoleId = 1;
	} else if ($("#selectedUserRole").val() == "Site Admin") {
		selectedUserRoleId = 2;
	}
	
	alert(parseInt($('#currentUserRoleID').val()));
	alert(selectedUserRoleId);
	alert($("#selectedUserRole").val());
	
	if ($('#currentUserID').val() == $('#selectedUserId').val()) {
		$("#setUserRoleBtn").prop('disabled', true);
	} else if (parseInt($('#currentUserRoleID').val()) > selectedUserRoleId) {
		$("#setUserRoleBtn").prop('disabled', true)
	} else {
		$("#setUserRoleBtn").prop('disabled', $("#currentRole").html() ==  $("#selectedUserRole").val());
	}
}

$("#setUserRoleBtn").click(function(){
	if ($("#selectedUserRole").val() == "0") {
		showErrorPopup('Please select an user first.');
	} else if ( $("#currentRole").html() ==  $("#selectedUserRole").val()) {
		showErrorPopup('You didn\'t change the user\'s role. Nothing to save.');
	} else if ($('#currentUserID').val() == $('#selectedUserId').val()){
		showErrorPopup('You cannot set your own role');
	} else {
		var newRoleId = 10;
		if ($("#currentRole").html() == "System Admin") {
			newRoleId = 1;
		} else if ($("#currentRole").html() == "Site Admin") {
			newRoleId = 2;
		}
		var currentUserRoleId = parseInt($('#currentUserRoleID').val())
		var accessToken = getAccessToken();
	    $("#setUserRoleBtn").prop('disabled', true);
	    
	    $.ajax({
	        type: "POST",
	        url: "../../update_user_role",
	        data: JSON.stringify({accessToken : accessToken, roleId : newRoleId, requestedUserId : parseInt($('#selectedUserId').val())}),
	        contentType: "application/json",
	        dataType: "json",
	        success: function(data){
	        	$("#setUserRoleBtn").prop('disabled', false);
				if (data['error'] == "" ) {
					showPopup('Done', 'An email has been sent to ' + $('#userDispName').text() + ' with this role change, and related instructions, if any.');
				} else {
					showErrorPopup(data['error']);
				}
	        },
	        failure: function(errMsg) {
	        	$("#setUserRoleBtn").prop('disabled', false);
	        	showErrorPopup('Unknown error occured. Please contact support');
	        }
	    });
	}
    
}); 

