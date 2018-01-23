function showErrorPopup(message) {
	showPopup('Something goes wrong', message);
}

function showPopup(title, message) {
	document.getElementById('popupTitle').innerHTML = title;
	document.getElementById('popupMessage').innerHTML = message;
	$('#popupModal').modal('toggle');
}

function parseDate(date) {
	document.write((new Date(date)).toLocaleString());
}

function parseDateStr(date) {
	return (new Date(date)).toLocaleString();
}

function getAccessToken() {
	var nameEQ = "accessToken=";
    var ca = document.cookie.split(';');
    var accessToken = "";
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) {
        	return c.substring(nameEQ.length,c.length);
        }
    }
    return accessToken;
}