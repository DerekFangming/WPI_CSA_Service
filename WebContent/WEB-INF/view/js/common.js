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

function linkify(inputText) {
    var replacedText, replacePattern1, replacePattern2, replacePattern3;

    //URLs starting with http://, https://, or ftp://
    replacePattern1 = /(\b(https?|ftp):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/gim;
    replacedText = inputText.replace(replacePattern1, '<a href="$1" target="_blank">$1</a>');

    //URLs starting with "www." (without // before it, or it'd re-link the ones done above).
    replacePattern2 = /(^|[^\/])(www\.[\S]+(\b|$))/gim;
    replacedText = replacedText.replace(replacePattern2, '$1<a href="http://$2" target="_blank">$2</a>');

    //Change email addresses to mailto:: links.
    replacePattern3 = /(([a-zA-Z0-9\-\_\.])+@[a-zA-Z\_]+?(\.[a-zA-Z]{2,6})+)/gim;
    replacedText = replacedText.replace(replacePattern3, '<a href="mailto:$1">$1</a>');

    return replacedText;
}

function validateInputNumber(evt) {
	var theEvent = evt || window.event;
	var key = theEvent.keyCode || theEvent.which;
	key = String.fromCharCode( key );
	var regex = /[0-9]|\./;
	if( !regex.test(key) ) {
		theEvent.returnValue = false;
		if(theEvent.preventDefault) theEvent.preventDefault();
	}
}

function formalizeFeeAmount(input) {
	if (input.length == 0) {
		return '0.00';
	} else {
		var list = input.split('.');
		if (list.length == 1) {
			return input + '.00';
		} else {
			var ending = list[1].length == 0 ? '00' : list[1].length == 1 ? '0' : '';
			return list[0] + '.' + list[1].substring(0, 2) + ending;
		}
	}
}

function startBtnLoading(btn) {
	$(btn).prop('disabled', true);
	$(btn).prepend('<i class="fa fa-refresh fa-spin mr-1"></i>');
}

function stopBtnLoading(btn) {
	$(btn).prop('disabled', false);
	$(btn).find('i').first().remove();
}