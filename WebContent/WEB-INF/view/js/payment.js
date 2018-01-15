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
    						showPopup('Download started', 'If you are using an iPhone, it should automatically open the ticket. ' +
    								 'If you are using an Android phone, download a third-party wallet app like WalletPass, and then ' +
    								 'clicked on the downloaded .pkpass ticket file to add to wallet. If you are downloading from a laptop, ' +
    								 'you can email the ticket file to your phone. Here is a <a target="_blank" href="https://www.tenorshare.com' +
    								 '/iphone-tips/top-4-ways-to-view-pkpass-files-on-iphone-android-pc-mac.html">link for all the methods to open a .pkpass file</a>');
    						
    					} else {
    						showErrorPopup('Transaction is successful. ' + data['ticketStatus'] + "Please contact support");
    					}
    				} else if (data['status'] == "AlreadyPaid") {
    					$("#ticketId").val(data['ticketId']);
    					$('#downloadTicketModal').modal('toggle');
    				} else {
    					showErrorPopup('Unknown status. ' + data['status'] + "Please contact support");
    				}
    				
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
    showPopup('Download started', 'If you are using an iPhone, it should automatically open the ticket. ' +
			 'If you are using an Android phone, download a third-party wallet app like WalletPass, and then ' +
			 'clicked on the downloaded .pkpass ticket file to add to wallet. If you are downloading from a laptop, ' +
			 'you can email the ticket file to your phone. Here is a <a target="_blank" href="https://www.tenorshare.com' +
			 '/iphone-tips/top-4-ways-to-view-pkpass-files-on-iphone-android-pc-mac.html">link for all the methods to open a .pkpass file</a>');
});