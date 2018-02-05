function openSG(id) {
	$.ajax({
		type: "GET",
		url: "./get_sg_article?id=" + id,
        contentType: "application/json",
        dataType: "json",
		success: function (data) {
			if (data['error'] == '') {
				$("#sgTitle").text(data['title']);
				$("#sgSubTitle").text('Last updated by ' + data['ownerName'] + ' on ' + parseDateStr(data['createdAt']));
				
				$("#sgContent").html(processContent(data['content']));
			} else {
				showErrorPopup(data['error']);
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
			showErrorPopup('Unknown error occured. Please contact support');
		}
	});
}



window.onload = openSG(1);

