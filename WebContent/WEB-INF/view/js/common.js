function showErrorPopup(message) {
	showPopup('Something goes wrong', message);
}

function showPopup(title, message) {
	document.getElementById('popupTitle').innerHTML = title;
	document.getElementById('popupMessage').innerHTML = message;
	$('#popupModal').modal('toggle');
}