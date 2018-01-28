$(document).ready(function() {
	$("#resendEmailBtn").prop('disabled', true);
	var table = $('#userTable').DataTable();
	
	$('#userTable tbody').on('click','tr', function() {
		var row = table.row(this).data();
	    $('#userDispName').text(row[1]);
	    $('#userUsername').text(row[2]);
	    $('#userCreated').text(row[5].substring(0,10));
	    $('#selectedUserId').val(row[0]);
	    
	    if (row[3].includes('0')) {
	    	$("#resendEmailBtn").prop('disabled', false);
	    } else {
	    	$("#resendEmailBtn").prop('disabled', true);
	    }
	});
	
});

$("#resendEmailBtn").click(function(){
    var accessToken = getAccessToken();
    $("#resendEmailBtn").prop('disabled', true);
    
    $.ajax({
        type: "POST",
        url: "../../send_verification_email",
        data: JSON.stringify({accessToken : accessToken, requestedUserId : parseInt($('#selectedUserId').val())}),
        contentType: "application/json",
        dataType: "json",
        success: function(data){
        	$("#resendEmailBtn").prop('disabled', false);
			if (data['error'] == "" ) {
				showPopup('Done', 'Verification email sent for ' + $('#userDispName').text());
			} else {
				showErrorPopup(data['error']);
			}
        },
        failure: function(errMsg) {
        	$("#resendEmailBtn").prop('disabled', false);
        	showErrorPopup('Unknown error occured. Please contact support');
        }
    });
}); 

