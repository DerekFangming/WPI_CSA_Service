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
				$('#loginModal').modal('toggle');
				$('#loginNav').replaceWith(data);
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
					document.getElementById('popupTitle').innerHTML = 'Something goes wrong';
					document.getElementById('popupMessage').innerHTML = obj['error'];
					$('#popupModal').modal('toggle');
				} else {
					$('#loginModal').modal('toggle');
					$('#loginNav').replaceWith(data);
				}
			},
			error: function (jqXHR, textStatus, errorThrown) {
				document.getElementById('popupTitle').innerHTML = 'Something goes wrong';
				document.getElementById('popupMessage').innerHTML = 'Unknown error occured. Please contact support';
				$('#popupModal').modal('toggle');
			}
		});
	}
	
	e.preventDefault();
});
