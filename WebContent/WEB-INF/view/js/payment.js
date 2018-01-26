$('#payButton').on('click', function (e) {
    e.preventDefault();
    
    var fee = parseFloat($("#ticketFee").val());
    
    if ($('#userEmailAddr').length == 0) {
    	showErrorPopup('You must login first to get ticket. Note that to get free ticket, you have to login using @wpi email');
    } else if ($('#userEmailConfirmed').val() != 'true') {
    	showErrorPopup('Please verify your email first');
    } else if (fee > 0) {
    	$("#ticketSpinner").toggleClass("fa fa-refresh fa-spin");
    	$("#payButton").toggleClass("disabled");
    	var accessToken = getAccessToken();
    var eventId = parseInt($('#eventId').val());
    	
    	$.ajax({
    		type: "POST",
    		url: "./check_payment_status",
    		data: JSON.stringify({accessToken : accessToken, type : 'Event', id : eventId}),
            contentType: "application/json",
            dataType: "json",
    		success: function (data) {
    			$("#ticketSpinner").toggleClass("fa fa-refresh fa-spin");
    	    	$("#payButton").toggleClass("disabled");
    	    	if (data['error'] != "" ) {
    	    		showErrorPopup(data['error']);
    	    	} else if (data['status'] == "AlreadyPaid") {
    	    		$("#ticketId").val(data['ticketId']);
    				$('#downloadTicketModal').modal('toggle');
    	    	} else if (data['status'] == "NotExist" || data['status'] == "Rejected") {
    	    		$('#paymentModal').modal('toggle');
    	        	var button = document.querySelector('#submitPaymentButton');
    	        	braintree.dropin.create({
    	        		authorization: 'sandbox_bk8pdqf3_wnbj3bx4nwmtyz77',
    	        		container: '#dropinContainer',
    	        		paypal: {
    	        			flow: 'vault'
    	        		}
    	        	}, function (createErr, instance) {
    	        		button.addEventListener('click', function () {
    	        			instance.requestPaymentMethod(function (err, payload) {
    	        				if (err != null) {
    	        					showErrorPopup(err == 'DropinError: No payment method is available.' ? 'Please select a payment method' : err);
    	        				} else {
    	        					makePaymentRequest(fee, payload.type, payload.nonce)
    	        				}
    	        			});
    	        		});
    	        	});
    	    	} else {
    				showErrorPopup('Unknown status. ' + data['status'] + "Please contact support");
    			}
    		},
    		error: function (jqXHR, textStatus, errorThrown) {
    			$("#ticketSpinner").toggleClass("fa fa-refresh fa-spin");
    	    	$("#payButton").toggleClass("disabled");
    			showErrorPopup('Unknown error occured. Please contact support');
    		}
    	});
    	
    	
    } else if (fee == 0) {
    	if (!$('#userEmailAddr').val().toLowerCase().endsWith("@wpi.edu")) {
        	showErrorPopup('To get free ticket, you have to login using @wpi email');
        } else {
        	makePaymentRequest(fee, null, null);
        }
    } else {
    	
    }
    
});

function makePaymentRequest(fee, method, nonce) {
	$("#ticketSpinner").toggleClass("fa fa-refresh fa-spin");
	$("#payButton").toggleClass("disabled");
	var accessToken = getAccessToken();
    var eventId = parseInt($('#eventId').val());
	
	var params = {accessToken : accessToken, type : 'Event', id : eventId, amount : fee , web : true};
	if (method != null) {
		params.method = method;
		params.nonce = nonce == null ? 'Unknown' : nonce;
	}
	
	$.ajax({
		type: "POST",
		url: "./make_payment",
		data: JSON.stringify(params),
        contentType: "application/json",
        dataType: "json",
		success: function (data) {
			$("#ticketSpinner").toggleClass("fa fa-refresh fa-spin");
	    	$("#payButton").toggleClass("disabled");
	    	if (data['error'] != "" ) {
	    		showErrorPopup(data['error']);
	    	} else if (data['status'] == "Done") {
	    		window.location="./download_ticket?accessToken=" + accessToken + "&id=" + data['ticketId'];
				showPopup('Download started', 'If you are using an iPhone, it should automatically open the ticket. If you are using an ' +
						 'Android phone, download a third-party wallet app like WalletPass, and then clicked on the downloaded .pkpass ' +
						 'ticket file to add to wallet. If you are downloading from a laptop, you can email the ticket file to your phone. ' +
						 'Here is a <a target="_blank" href="https://www.tenorshare.com/iphone-tips/top-4-ways-to-view-pkpass-files-on-' +
						 'iphone-android-pc-mac.html">link for all the methods to open a .pkpass file</a>');
	    	} else if (data['status'] == "AlreadyPaid") {
	    		$("#ticketId").val(data['ticketId']);
				$('#downloadTicketModal').modal('toggle');
	    	} else {
				showErrorPopup('Unknown status. ' + data['status'] + "Please contact support");
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
			$("#ticketSpinner").toggleClass("fa fa-refresh fa-spin");
	    	$("#payButton").toggleClass("disabled");
			showErrorPopup('Unknown error occured. Please contact support');
		}
	});
}

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