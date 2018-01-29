$(document).ready(function() {
	$("#resendEmailConfirmBtn").prop('disabled', true);
	$("#sendResetPasswordBtn").prop('disabled', true);
	var table = $('#userTable').DataTable();
	
	$('#userTable tbody').on('click','tr', function() {
		var row = table.row(this).data();
	    $('#userDispName').text(row[1]);
	    $('#userUsername').text(row[2]);
	    $('#userCreated').text(row[5].substring(0,10));
	    $('#selectedUserId').val(row[0]);
	    
	    if (row[3].includes('0')) {
	    	$("#resendEmailConfirmBtn").prop('disabled', false);
	    } else {
	    	$("#resendEmailConfirmBtn").prop('disabled', true);
	    }
	    $("#sendResetPasswordBtn").prop('disabled', false);
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

