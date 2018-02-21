$('.dropdown-menu > div > a').click(function(e){
	sendVerificationEmail (e, null);
});

function sendVerificationEmail (e, btn) {
	e.stopPropagation();

    var accessToken = getAccessToken();
    if (btn != null) {
    	startBtnLoading(btn);
    }
	
    $.ajax({
        type: "POST",
        url: "./send_verification_email",
        data: JSON.stringify({accessToken : accessToken}),
        contentType: "application/json",
        dataType: "json",
        success: function(data){
        	if (btn != null) {
            	stopBtnLoading(btn);
            }
			if (data['error'] == "" ) {
				showPopup('Verification email sent', 'An email has been sent to your mail box with a link to confirm your email. '
						+ 'Please click on the link in 24 hours. Please check your junk folder if you cannot see the email.');
			} else {
				var msg = data['error'];
				if (msg.startsWith('Your email is already confirmed')) {
					msg = 'Your email is already confirmed. Please refresh the page.';
				}
				showErrorPopup(msg);
			}
        },
        failure: function(errMsg) {
        	if (btn != null) {
            	stopBtnLoading(btn);
            }
        	showErrorPopup('Unknown error occured. Please contact support');
        }
    });
}