$(document).ready(function() {
	var table = $('#feedTable').DataTable({
		"order": [[ 2, "desc" ]]
	});
	
	$('#feedTable tbody').on('click','tr', function() {
		/*var row = table.row(this).data();
	    $('#selectedUserId').val(row[0]);
	    $('#userDispName').text(row[1]);
	    $('#userUsername').text(row[2]);
	    $("#currentRole").html(row[4]);
	    $("#selectedUserRoleId").val(getUserRoleId(row[4]));
	    $('#userCreated').text(row[5].substring(0,10));
	    
	    if (row[3].includes('0')) {
	    	$("#resendEmailConfirmBtn").prop('disabled', false);
	    } else {
	    	$("#resendEmailConfirmBtn").prop('disabled', true);
	    }
	    $("#sendResetPasswordBtn").prop('disabled', false);
		$("#setUserRoleBtn").prop('disabled', true);*/
	});
})

$('#profileEditBtn').click(function(){
	$('#userName').prop('disabled', false);
	$('#userBirthday').prop('disabled', false);
	$('#userClassof').prop('disabled', false);
	$('#userMajor').prop('disabled', false);
	
	$.when($(this).fadeOut()).promise().done(function() {
		$('#profileSaveBtn').fadeIn();
		$('#profileCancelBtn').fadeIn();
	});
	
	$('#userAvatar').replaceWith('<div id="img-picker" class="aspect-fill" ondragover="allowDrop(event)"></div>')
	var imgSrc = '';
	if ($('#userOrigAvatarId').val() != 0) {
		imgSrc = './images/' + $('#userOrigAvatarId').val() + '.jpg';
	}
	$('#img-picker').imagePicker({
		src: imgSrc,
		container: 'life-img-container border',
		iconText: '<p>Click or drop an avatar here</p>'
	});
	$('#userName').focus();
	
	
});

$('#profileCancelBtn').click(function(){
	$('#profileSaveBtn').fadeOut();
	$.when($(this).fadeOut()).promise().done(function() {
		$('#profileEditBtn').fadeIn();
	});
	
	$('#userName').val($('#userOrigName').val());
	$('#userBirthday').val($('#userOrigBirthday').val());
	$('#userClassof').val($('#userOrigClassof').val());
	$('#userMajor').val($('#userOrigMajor').val());
	
	var imgSrc = '/resources/img/defaultAvatar.png';
	if ($('#userOrigAvatarId').val() != 0) {
		imgSrc = './images/' + $('#userOrigAvatarId').val() + '.jpg';
	}
	$('#img-picker').replaceWith('<div class="life-img-container border" id="userAvatar"><img class="aspect-fill" src="' + imgSrc + '"></div>')
	
	$('#userName').prop('disabled', true);
	$('#userBirthday').prop('disabled', true);
	$('#userClassof').prop('disabled', true);
	$('#userMajor').prop('disabled', true);
});

$('#profileSaveBtn').click(function(){
	var name = $('#userName').val().trim();
	var classOf = $('#userClassof').val().trim();
	
	if (name.length == 0) {
		showErrorPopup('Name cannot be empty.');
	} else if (classOf.length != 4 && classOf.length != 0) {
		showErrorPopup('Graduation year must be a four digits number');
	} else {
		var dateArr = $('#userBirthday').val().split('-');
		var dateStr = '';
		if (dateArr.length == 3) {
			dateStr = dateArr[1] + '/' + dateArr[2] + '/' + dateArr[0].slice(-2);
		}
		
		var accessToken = getAccessToken();
		var params = {accessToken : accessToken, name : name, birthday : dateStr,
				year : $('#userClassof').val().trim(), major : $('#userMajor').val().trim() };
		
		if ($('#userOrigAvatarId').val() != 0 && $('#img-picker').children().children('img').length == 0) {
			params.removeAvatarId = $('#userOrigAvatarId').val();
		}
		if ($('#img-picker').children().children('img').length != 0) {
			var avatar = $('#img-picker').children().children('img').attr('src');
			if (!avatar.includes('/images/')) {
				params.avatar = avatar;
			}
		}
		
		
		startBtnLoading('#profileSaveBtn');
		$('#profileCancelBtn').prop('disabled', true);
		
		$.ajax({
	        type: "POST",
	        url: "./save_user_detail",
	        data: JSON.stringify(params),
	        contentType: "application/json",
	        dataType: "json",
	        success: function(data){
	        	stopBtnLoading('#profileSaveBtn');
	        	$('#profileCancelBtn').prop('disabled', false);
				if (data['error'] == "" ) {
					var imgSrc = '/resources/img/defaultAvatar.png';
					if ($('#img-picker').children().children('img').length != 0) {
						imgSrc = $('#img-picker').children().children('img').attr('src');
					}
					if (data['imageId'] != null) {
						$('#userOrigAvatarId').val(data['imageId']);
					}
					
					$('#img-picker').replaceWith('<div class="life-img-container border" id="userAvatar"><img class="aspect-fill" src="' + imgSrc + '"></div>')
					
					$('#userName').prop('disabled', true);
					$('#userBirthday').prop('disabled', true);
					$('#userClassof').prop('disabled', true);
					$('#userMajor').prop('disabled', true);
					
					$('#profileCancelBtn').fadeOut();
		    		$.when($('#profileSaveBtn').fadeOut()).promise().done(function() {
		    			$('#profileEditBtn').fadeIn();
		    		});
				} else {
					showErrorPopup(data['error']);
				}
	        },
	        failure: function(errMsg) {
	        	stopBtnLoading('#profileSaveBtn');
	        	$('#profileCancelBtn').prop('disabled', false);
	        	showErrorPopup('Unknown error occured. Please contact support');
	        }
	    });
	}
	
});
