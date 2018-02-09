$("#createArticleBtn").click(function(){
    var accessToken = getAccessToken();
    
    if (accessToken == "") {
    		showErrorPopup('You have to login first before creating articles');
    } else if ($("#userEmailConfirmed").val() != 'true'){
    		showErrorPopup('You have to confirm your email first before creating articles');
    } else {
    		window.location="./new_article";
    }
    
});

function selectType(typeId) {
	$("#searchType").html(getSearchType(typeId));
}

function getSearchType(typeId) {
	if (typeId == 1) {
		return 'All';
	} else if (typeId == 2) {
		return 'Blog';
	} else if (typeId == 3) {
		return 'Trade';
	} else if (typeId == 4) {
		return 'Event';
	} else {
		return 'Unknown';
	}
}

$("#searchArticleBtn").click(function(){
    $("#feedSearchResult").html('');
});

