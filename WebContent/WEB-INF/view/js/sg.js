function openSG(id) {
	alert(id);
	$.ajax({
		type: "GET",
		url: "./get_sg_article?id=" + id,
        contentType: "application/json",
        dataType: "json",
		success: function (data) {
			alert(data['title']);
		},
		error: function (jqXHR, textStatus, errorThrown) {
			$("#ticketSpinner").toggleClass("fa fa-refresh fa-spin");
	    	$("#payButton").toggleClass("disabled");
			showErrorPopup('Unknown error occured. Please contact support');
		}
	});
}