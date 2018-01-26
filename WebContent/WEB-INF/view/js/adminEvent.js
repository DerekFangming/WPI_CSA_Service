function editEvent(id) {
	alert(id);
}

function toggleStatus(id, newStatus) {
	var accessToken = getAccessToken();
	$("#statusBtn" + id).toggleClass("disabled");
	$.ajax({
		type: "POST",
		url: "../../update_event_status",
		data: JSON.stringify({accessToken : accessToken, id : id, status : newStatus}),
        contentType: "application/json",
        dataType: "json",
		success: function (data) {
			$("#statusBtn" + id).attr("class", newStatus ? "btn btn-danger" : "btn btn-success");
			$("#statusBtn" + id).attr("onclick", 'toggleStatus(' + id + ',' + !newStatus +');');
			//$("#statusBtn" + id).attr("data-toggle", (newStatus ? 'tooltip-soldout' : 'tooltip-selling')); this is not working
			$("#statusBtn" + id).html(newStatus ? '<i class="fa fa-lock"></i>&nbsp;Mark  as  Sold  Out' : '<i class="fa fa-unlock"></i>&nbsp;Start selling tickets');
			$("#statusLbl" + id).html('Status: ' + (newStatus ? 'Selling' : 'Sold out'));
			if (data['error'] != "" ) {
		    		showErrorPopup(data['error']);
		    	} else {
		    		showPopup('Done', 'Ticket status has been updated');
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
			$("#statusBtn" + id).toggleClass("disabled");
			showErrorPopup('Unknown error occured. Please contact support');
		}
	});
}

function dummy() {
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
			$("#ticketSpinner").toggleClass("fa fa-refresh fa-spin");
	    	$("#payButton").toggleClass("disabled");
			showErrorPopup('Unknown error occured. Please contact support');
		}
	});
}
	
	