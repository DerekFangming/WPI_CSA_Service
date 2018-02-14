$(document).ready(function() {
	
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
	var classOf = $('#userClassof').val().trim();
	if (classOf.length != 4 && classOf.length != 0) {
		showErrorPopup('Graduation year must be a four digits number');
	} else {
		var dateArr = $('#userBirthday').val().split('-');
		var dateStr = dateArr[1] + '/' + dateArr[2] + '/' + dateArr[0].slice(-2);
		alert(dateStr);
		
		var accessToken = getAccessToken();
		var params = {accessToken : accessToken, name : $('#userName').val().trim(), birthday : dateStr, body : content, coverImage : cover };
		
		if ($('#userOrigAvatarId').val() != 0) {
			imgSrc = './images/' + $('#userOrigAvatarId').val() + '.jpg';
		}
		if ($('#img-picker').children().children('img').length == 0) {
			return 'Please select a cover image on the left.';
		}
		
		$('#profileCancelBtn').fadeOut();
		$.when($(this).fadeOut()).promise().done(function() {
			$('#profileEditBtn').fadeIn();
		});
	}
	
});