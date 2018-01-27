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
			$("#statusBtn" + id).prop('disabled', false);
			if (data['error'] != "" ) {
	    		showErrorPopup(data['error']);
	    	} else {
	    		$("#titleLbl" + id).html($("#eventTitle").val().trim());
	    		showPopup('Done', 'Ticket details has been updated');
	    	}
		},
		error: function (jqXHR, textStatus, errorThrown) {
			$("#statusBtn" + id).prop('disabled', false);
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
		$("#balanceBtn" + id).prop('disabled', true);
		$.ajax({
			type: "POST",
			url: "../../update_event_balance",
			data: JSON.stringify({accessToken : accessToken, id : id, balance : balance}),
	        contentType: "application/json",
	        dataType: "json",
			success: function (data) {
				$("#balanceBtn" + id).prop('disabled', false);
				if (data['error'] != "" ) {
		    		showErrorPopup(data['error']);
		    	} else {
		    		$("#balanceLbl" + id).html($("#balanceLbl" + id).html().split("remaining")[0] + 'remaining ' + balance);
		    		showPopup('Done', 'Ticket remaining count has been updated');
				}
			},
			error: function (jqXHR, textStatus, errorThrown) {
				$("#balanceBtn" + id).prop('disabled', false);
				showErrorPopup('Unknown error occured. Please contact support');
			}
		});
	}
}

function toggleStatus(id, newStatus) {
	var accessToken = getAccessToken();
	$("#statusBtn" + id).prop('disabled', true);
	$.ajax({
		type: "POST",
		url: "../../update_event_status",
		data: JSON.stringify({accessToken : accessToken, id : id, status : newStatus}),
        contentType: "application/json",
        dataType: "json",
		success: function (data) {
			$("#statusBtn" + id).prop('disabled', false);
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
			$("#statusBtn" + id).prop('disabled', false);
			showErrorPopup('Unknown error occured. Please contact support');
		}
	});
}

function openPartiList(id) {
	$('#partiListModal').modal('toggle');
	
}
	
	