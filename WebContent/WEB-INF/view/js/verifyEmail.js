$('.dropdown-menu > div > a').click(function(e){
	e.stopPropagation();
	//$('.dropdown-menu').toggle();
	
	var nameEQ = "access_token=";
    var ca = document.cookie.split(';');
    var accessToken = "";
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) {
        	accessToken = c.substring(nameEQ.length,c.length);
        }
    }
	
    $.ajax({
        type: "POST",
        url: "./send_verification_email",
        data: JSON.stringify({accessToken : accessToken}) ,
        contentType: "application/json",
        dataType: "json",
        success: function(data){
			if (data['error'] == "" ) {
				showPopup('Verification email sent', 'An email has been sent to your mail box with a link to confirm your email. '
						+ 'Please click on the link in 24 hours. Please check your junk folder if you cannot see the email.');
			} else {
				msg = data['error'];
				if (msg.startsWith('Your email is already confirmed')) {
					msg = 'Your email is already confirmed. Please refresh the page.';
				}
				showErrorPopup(msg);
			}
        },
        failure: function(errMsg) {
        	showErrorPopup('Unknown error occured. Please contact support');
        }
    });
	
	
});