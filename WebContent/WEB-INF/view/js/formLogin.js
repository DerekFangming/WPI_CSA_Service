$('#loginForm').submit(function (e) {
	$.ajax({
		type: "POST",
		url: "/web_login",
		data: $("#loginForm").serialize(),
		success: function (data) {
			$('#loginModal').modal('toggle');
			$('#loginNav').replaceWith(data);
		},
		error: function (jqXHR, textStatus, errorThrown) {
			alert(jqXHR.status);
		}
	});
	e.preventDefault();
});