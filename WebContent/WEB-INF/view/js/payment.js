$('#payButton').on('click', function (e) {
    e.preventDefault();
    
    var eventId = parseInt($('#eventId').val());
    
    if ($('#userEmailAddr').length == 0) {
    	showErrorPopup('You must login first to get ticket. Note that to get free ticket, you have to login using @wpi email');
    } else if (!$('#userEmailAddr').val().toLowerCase().endsWith("@wpi.edu")) {
    	showErrorPopup('To get free ticket, you have to login using @wpi email');
    } else if ($('#userEmailConfirmed').val() != 'true') {
    	showErrorPopup('Please verify your email first');
    } else {
    	$("#ticketSpinner").toggleClass("fa fa-refresh fa-spin");
    	$("#payButton").toggleClass("disabled");
    	var accessToken = getAccessToken();
    	
    	$.ajax({
    		type: "POST",
    		url: "./make_payment",
    		data: JSON.stringify({accessToken : accessToken, type : 'Event', id : eventId, amount : 0 , web : true}),
            contentType: "application/json",
            dataType: "json",
    		success: function (data) {
    			$("#ticketSpinner").toggleClass("fa fa-refresh fa-spin");
    	    	$("#payButton").toggleClass("disabled");
    			if (data['error'] == "" ) {
    				if (data['status'] == 'ok'){
    					if (data['ticketStatus'] == 'ok'){
    						window.location="./download_ticket?accessToken=" + accessToken + "&id=" + data['ticketId'];
    					} else {
    						showErrorPopup('Transaction is successful. ' + data['ticketStatus'] + "Please contact support");
    					}
    				} else if (data['status'] == "AlreadyPaid") {
    					$("#ticketId").val(data['ticketId']);
    					$('#downloadTicketModal').modal('toggle');
    				} else {
    					showErrorPopup('Unknown status. ' + data['status'] + "Please contact support");
    				}
    				
    				var status = data['status'];
    				var ticketStatus = data['ticketStatus'];
    				var ticketId = data['ticketId'];
    				var ticketStr = data['ticket'];
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
    
});

$('#downloadTicketButton').on('click', function (e) {
    e.preventDefault();
    
    var ticketId = parseInt($("#ticketId").val());
    var accessToken = getAccessToken();
    window.location="./download_ticket?accessToken=" + accessToken + "&id=" + $("#ticketId").val();
    
});