function selectType(typeId) {
	$("#currentType").html(getFeedTypeText(typeId));
	
	if (typeId == 3) {
		$('#alertMsg').attr("class", 'alert alert-success');
		$('#alertMsg').html('You are posting this article as CSA Official');
		$('#alertMsg').fadeIn();
	} else {
		if ($('#hasAvatar').val() == 'true') {
			$('#alertMsg').fadeOut();
		} else {
			$('#alertMsg').attr("class",  'alert alert-warning');
			$('#alertMsg').html('You do not have an avatar and it will show as the default panda. We strongly recommend you to add an avatar from mobile end before posting articles.');
		}
	}
	
	/*if ($('#currentUserID').val() == $('#selectedUserId').val()) {
		$("#setUserRoleBtn").prop('disabled', true);
	} else if (parseInt($('#currentUserRoleID').val()) > parseInt($('#selectedUserRoleId').val())) {
		$("#setUserRoleBtn").prop('disabled', true)
	} else {
		$("#setUserRoleBtn").prop('disabled', $("#currentRole").html() ==  getUserRoleText(parseInt($('#selectedUserRoleId').val())));
	}*/
}

function getFeedTypeText(typeId) {
	if (typeId == 1) {
		return 'Blog';
	} else if (typeId == 2) {
		return 'Trade';
	} else if (typeId == 3) {
		return 'Event';
	} else {
		return 'Unknown';
	}
}