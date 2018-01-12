$('#loginForm').submit(function (e) {
	$.ajax({
		type: "POST",
		url: "/web_login",
		data: $("#loginForm").serialize(),
		success: function (data) {
			if (data.startsWith("{")) {
				var obj = $.parseJSON(data);
				document.getElementById('popupMessage').innerHTML = obj['error'];
				$('#popupModal').modal('toggle');
			} else {
				$('#loginModal').modal('toggle');
				$('#loginNav').replaceWith(data);
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
			document.getElementById('popupMessage').innerHTML = 'Unknown error occured. Please contact support';
			$('#popupModal').modal('toggle');
		}
	});
	e.preventDefault();
});