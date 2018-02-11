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

$('#searchInput').keypress(function (e) {
	if(e.which == 13) {
		$("#searchArticleBtn").click();
	}
});

$("#searchArticleBtn").click(function(){
	$("#searchArticleBtn").prop('disabled', true);
	$('#searchInput').prop('disabled', true);
    $("#feedSearchResult").html('<center><i class="fa fa-refresh fa-3x fa-spin" style="color:black"></i></center>' );
    var params = {"web": "true"};
    var keyword = $('#searchInput').val().trim();
    if (keyword != '') {params.keyword = keyword;}
    var type = $("#searchType").html();
    if (type != 'All'){params.type = type;}
    
    $.ajax({
		type: "GET",
		url: "./search_feed",
		data: params,
        contentType: "application/json",
        dataType: "json",
		success: function (data) {
			$("#searchArticleBtn").prop('disabled', false);
			$('#searchInput').prop('disabled', false);
			if (data['error'] != '') {
				if (data['error'] == 'There are no more feeds.') {
					$("#feedSearchResult").html('<div class="jumbotron my-4"><center><h2 class="display-4">No articles found.</h2><p class="lead">Please try searching with some other keywords or article type.</p></center></div>');
				} else {
					showErrorPopup(data['error']);
					$("#feedSearchResult").html('');
				}
			} else {
				var feedList = data['feedList'];
				var result = '';
				for (var i = 0; i < feedList.length; i++) {
					result += '<div class="card top-buffer"><div class="card-header"><h1><a href="./feed?id=';
					result += feedList[i].id;
					result += '">' + feedList[i].title;
					result += '</a></h1></div>';
					
					result += '<div class="row"><div class="col-lg-6 col-sm-12"><div class="life-img-container"><img class="aspect-fill" src="./images/';
					result += feedList[i].coverImgId;
					result += '.jpg" /></div></div>';
					result += '<div class="col-lg-6 clm-sm-12"><div class="card-body life-desc">';
					result += feedList[i].body.replace(/<\/?[^>]+(>|$)/g, "");
					result += '</div></div></div></div>';
					
				}
				$("#feedSearchResult").html(result);
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
			$("#searchArticleBtn").prop('disabled', false);
			$('#searchInput').prop('disabled', false);
			showErrorPopup('Unknown error occured. Please contact support');
		}
	});
});

