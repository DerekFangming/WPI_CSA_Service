function openEvent(id) {
	$('#adminEventEditModal').modal('toggle');
	$.ajax({
		type: "GET",
		url: "../../get_event?id=" + id,
        contentType: "application/json",
        dataType: "json",
		success: function (data) {
			if (data['error'] != '') {
				showErrorPopup(data['error']);
			} else {
				var sTime = new Date(data['startTime']);
				var eTime = new Date(data['endTime']);
				
				$("#eventTitle").val(data['title']);
				$("#eventSTime").val(new Date(sTime.getTime()-sTime.getTimezoneOffset()*60000).toISOString().substring(0,16));
				$("#eventETime").val(new Date(eTime.getTime()-eTime.getTimezoneOffset()*60000).toISOString().substring(0,16));
				$("#eventLocation").val(data['location']);
				$("#eventId").val(id);
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
			showErrorPopup('Unknown error occured. Please contact support');
		}
	});
}

$('#adminEventEditModal').on('hidden.bs.modal', function () {
	$("#eventTitle").val('');
	$("#eventSTime").val('');
	$("#eventETime").val('');
	$("#eventLocation").val('');
	$("#eventId").val('0');
})

function editEvent() {
	var accessToken = getAccessToken();
	var id = parseInt($('#eventId').val());
	startBtnLoading('#updateEventBtn');
	
	var params = {accessToken : accessToken, id : id};
	if ($("#eventTitle").val().trim() != '') {
		params.title = $("#eventTitle").val().trim();
	}
	if ($("#eventSTime").val().trim() != '') {
		params.startTime = new Date($("#eventSTime").val().trim()).toISOString();
	}
	if ($("#eventETime").val().trim() != '') {
		params.endTime = new Date($("#eventETime").val().trim()).toISOString();
	}
	if ($("#eventLocation").val().trim() != '') {
		params.location = $("#eventLocation").val().trim();
	}
	
	$.ajax({
		type: "POST",
		url: "../../update_event_details",
		data: JSON.stringify(params),
        contentType: "application/json",
        dataType: "json",
		success: function (data) {
			stopBtnLoading('#updateEventBtn');
			$('#adminEventEditModal').modal('toggle');
			if (data['error'] != "" ) {
	    		showErrorPopup(data['error']);
	    	} else {
	    		$("#titleLbl" + id).html($("#eventTitle").val().trim());
	    		showPopup('Done', 'Ticket details has been updated');
	    	}
		},
		error: function (jqXHR, textStatus, errorThrown) {
			stopBtnLoading('#updateEventBtn');
			$('#adminEventEditModal').modal('toggle');
			showErrorPopup('Unknown error occured. Please contact support');
		}
	});
}

function setBalance(id) {
	var balance = parseInt($('#balanceIn' + id).val());
	if (isNaN(balance)) {
		showErrorPopup('Balance can only be numbers.');
	} else if (balance < 0 || balance > 500){
		showErrorPopup('Ticket balance has to be in range 0 to 500.');
	} else {
		var accessToken = getAccessToken();
		startBtnLoading('#balanceBtn' + id);
		$.ajax({
			type: "POST",
			url: "../../update_event_balance",
			data: JSON.stringify({accessToken : accessToken, id : id, balance : balance}),
	        contentType: "application/json",
	        dataType: "json",
			success: function (data) {
				stopBtnLoading('#balanceBtn' + id);
				if (data['error'] != "" ) {
		    		showErrorPopup(data['error']);
		    	} else {
		    		$('#balanceIn' + id).val('');
		    		$("#balanceLbl" + id).html($("#balanceLbl" + id).html().split("remaining")[0] + 'remaining ' + balance);
		    		showPopup('Done', 'Ticket remaining count has been updated');
				}
			},
			error: function (jqXHR, textStatus, errorThrown) {
				stopBtnLoading('#balanceBtn' + id);
				showErrorPopup('Unknown error occured. Please contact support');
			}
		});
	}
}

function toggleStatus(id, newStatus) {
	var accessToken = getAccessToken();
	startBtnLoading('#statusBtn' + id);
	$.ajax({
		type: "POST",
		url: "../../update_event_status",
		data: JSON.stringify({accessToken : accessToken, id : id, status : newStatus}),
        contentType: "application/json",
        dataType: "json",
		success: function (data) {
			stopBtnLoading('#statusBtn' + id);
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
			stopBtnLoading('#statusBtn' + id);
			showErrorPopup('Unknown error occured. Please contact support');
		}
	});
}

function openPartiList(id) {
	var accessToken = getAccessToken();
	$('#partiListModal').modal('toggle');
	$.ajax({
		type: "POST",
		url: "../../get_parti_list",
		data: JSON.stringify({accessToken : accessToken, id : id}),
        contentType: "application/json",
        dataType: "json",
		success: function (data) {
			$("#partiLoading").html('');
			if (data['error'] != "" ) {
	    		showErrorPopup(data['error']);
	    	} else {
	    		var partiList = data['partiList'];
	    		var partiHtml = '<thead><tr><th>Name</th><th>Email</th><th>Registered at</th></tr></thead><tbody>';
	    		for (var i = 0; i < partiList.length; i++) {
	    			partiHtml += '<tr><td>' + partiList[i].name + '</td>';
	    			partiHtml += '<td>' + partiList[i].email + '</td><td>' + parseDateStr(partiList[i].regiTime) + '</td></tr> \n';
	    		}
	    		$("#partiTable").html(partiHtml + '</tbody>');
	    		$('#partiTable').DataTable();
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
			showErrorPopup('Unknown error occured. Please contact support');
		}
	});
	
}


$('#partiListModal').on('hidden.bs.modal', function () {
	$("#partiTable").html('');
	$("#partiTable").DataTable().destroy();
	$("#partiLoading").html('<i class="fa fa-refresh fa-5x fa-spin" style="color:black"></i>');
})

