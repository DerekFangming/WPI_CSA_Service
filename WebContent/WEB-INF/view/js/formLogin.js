$('#loginForm').submit(function (e) {
	$.ajax({
		type: "POST",
		url: "./web_login",
		data: $("#loginForm").serialize(),
		success: function (data) {
			if (data.startsWith("{")) {
				var obj = $.parseJSON(data);
				showErrorPopup(obj['error']);
			} else {
				if ($('#refreshAfterLogin').length == 0) {
					$('#loginModal').modal('toggle');
					$('#loginNav').replaceWith(data);
					
					$('#userEmailConfirmed').val(data.includes('not') ? 'false' : 'true');
				} else {
					location.reload();
				}
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
			showErrorPopup('Unknown error occured. Please contact support');
		}
	});
	e.preventDefault();
});

$('#registerForm').submit(function (e) {
	var username = document.getElementById("newUsername").value.trim();
	var name = document.getElementById("newName").value.trim();
	var password = document.getElementById("newPassword").value.trim();
	var confirm = document.getElementById("newConfirm").value.trim();
	
	var usernameRegex = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	var passwordRegex = /([0-9].*[a-zA-Z])|([a-zA-Z].*[0-9])/;
	if (!usernameRegex.test(username.toLowerCase())) {
		showErrorPopup('Username must be an email');
	} else if (password != confirm) {
		showErrorPopup('The two passwords you entered are not the same');
	} else if (name == "") {
		showErrorPopup('Name cannot be empty string');
	} else if (password.length < 6) {
		showErrorPopup('Password needs to have more than 6 characters');
	} else if (!passwordRegex.test(password)) {
		showErrorPopup('Password needs to have at least one letter and one number');
	} else {
		$.ajax({
			type: "POST",
			url: "./web_register",
			data: $("#registerForm").serialize(),
			success: function (data) {
				if (data.startsWith("{")) {
					var obj = $.parseJSON(data);
					showErrorPopup(obj['error']);
				} else {
					if ($('#refreshAfterLogin').length == 0) {
						$('#loginModal').modal('toggle');
						$('#loginNav').replaceWith(data);
						showPopup('Verification email sent', 'An email has been sent to your mail box with a link to confirm your email. '
								+ 'Please click on the link in 24 hours. Please check your junk folder if you cannot see the email.');
						
						$('#userEmailConfirmed').val('false');
					} else {
						location.reload();
					}
				}
			},
			error: function (jqXHR, textStatus, errorThrown) {
				showErrorPopup('Unknown error occured. Please contact support');
			}
		});
	}
	
	e.preventDefault();
});

$('#forgetForm').submit(function (e) {
	$('#loginModal').modal('toggle');
	$.ajax({
		type: "POST",
		url: "./send_change_pwd_email",
		contentType: "application/json",
        dataType: "json",
		data: JSON.stringify({email : $('#forgetEmail').val()}),
		success: function (data) {
			if (data['error'] != "" ) {
	    		showErrorPopup(data['error']);
	    	} else {
	    		showPopup('Done', 'An email with password reset link has been sent to your inbox. Please check junk and trash folder if you can\'t find it.');
	    	}
		},
		error: function (jqXHR, textStatus, errorThrown) {
			showErrorPopup('Unknown error occured. Please contact support');
		}
	});
	e.preventDefault();
});
