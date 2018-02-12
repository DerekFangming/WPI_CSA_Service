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
	
    var params = {"web": "true"};
    var keyword = $('#searchInput').val().trim();
    if (keyword != '') {params.keyword = keyword;}
    var type = $("#searchType").html();
    if (type != 'All'){params.type = type;}
    
    if (type == 'All' && keyword == '') {
    	currentPageIndex = -1;
    	gotoPage(0); 
    } else {
    	$("#searchArticleBtn").prop('disabled', true);
    	$('#searchInput').prop('disabled', true);
        $("#feedSearchResult").html('<center><i class="fa fa-refresh fa-3x fa-spin" style="color:black"></i></center>' );
        $('#pageIndexNav').hide();
        $('#pageIndex' + currentPageIndex).attr('class', 'page-item');
        
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
    				var result = processFeedList (data['feedList']);
    				$("#feedSearchResult").html(result);
    			}
    		},
    		error: function (jqXHR, textStatus, errorThrown) {
    			$("#searchArticleBtn").prop('disabled', false);
    			$('#searchInput').prop('disabled', false);
    			showErrorPopup('Unknown error occured. Please contact support');
    		}
    	});
    }
    
});

var currentPageIndex = 0;

function gotoPrev() {
	if (currentPageIndex == 0) {
		showErrorPopup('You are already on first page');
	} else {
		gotoPage(currentPageIndex - 1);
	}
}

function gotoNext() {
	if (currentPageIndex == parseInt($('#pageCount').val()) - 1) {
		showErrorPopup('You are already on last page');
	} else {
		gotoPage(currentPageIndex + 1);
	}
}

function gotoPage(pageIndex) {
	if(pageIndex != currentPageIndex) {
		$('#pageIndex' + currentPageIndex).attr('class', 'page-item');
		$('#pageIndex' + pageIndex).attr('class', 'page-item active');
		currentPageIndex = pageIndex;
		
		if (currentPageIndex == 0) {
			$('#prevBtn').attr('class', 'page-item disabled');
		} else {
			$('#prevBtn').attr('class', 'page-item');
		}
		if (currentPageIndex == parseInt($('#pageCount').val()) - 1) {
			$('#nextBtn').attr('class', 'page-item disabled');
		} else {
			$('#nextBtn').attr('class', 'page-item');
		}
		
		$("#feedSearchResult").html('<center><i class="fa fa-refresh fa-3x fa-spin" style="color:black"></i></center>');
		$('#pageIndexNav').hide();
		
		$.ajax({
			type: "GET",
			url: "./get_recent_feeds",
			data: {"page": pageIndex},
	        contentType: "application/json",
	        dataType: "json",
			success: function (data) {
				$('#pageIndexNav').fadeIn();
				if (data['error'] != '') {
					if (data['error'] == 'There are no more feeds.') {
						$("#feedSearchResult").html('<div class="jumbotron my-4"><center><h2 class="display-4">No articles found.</h2><p class="lead">Please try searching with some other keywords or article type.</p></center></div>');
					} else {
						showErrorPopup(data['error']);
						$("#feedSearchResult").html('');
					}
				} else {
					var result = processFeedList (data['feedList']);
					$("#feedSearchResult").html(result);
					
					var pageCount = parseInt($('#pageCount').val());
					var pageCountNew = data['pageCount'];
					if (pageCount != pageCountNew) {
						$('#pageCount').val(pageCountNew);
						if (pageCountNew > pageCount) {
							for (var i = pageCount; i < pageCountNew; i ++) {
								$('#pageIndex' + (i - 1)).after('<li class="page-item" id="pageIndex' + i +
										'"><a class="page-link" href="#" onclick="gotoPage(' + i + ')">' + (i + 1) + '</a></li>');
							}
						} else {
							for (var i = pageCountNew; i < pageCount; i ++) {
								$('#pageIndex' + i).remove();
							}
						}
					}
				}
			},
			error: function (jqXHR, textStatus, errorThrown) {
				$('#pageIndexNav').fadeIn();
				showErrorPopup('Unknown error occured. Please contact support');
			}
		});
		
	}
}

function processFeedList (feedList) {
	var result = '';
	for (var i = 0; i < feedList.length; i++) {
		result += '<div class="card top-buffer"><div class="card-header"><h1 class="mb-0"><a href="./feed?id=';
		result += feedList[i].id;
		result += '">' + feedList[i].title;
		result += '</a></h1><p class="mb-0"><span class="badge badge-secondary">' + feedList[i].type;
		result += '</span><span class="text-muted">&nbsp; Created by <b>' + feedList[i].ownerName;
		result += '</b> on ' + parseDateStr(feedList[i].createdAt) + '</span></p></div>';
		
		result += '<div class="row"><div class="col-lg-6 col-sm-12"><div class="life-img-container"><img class="aspect-fill" src="./images/';
		result += feedList[i].coverImgId || '0';
		result += '.jpg" /></div></div>';
		result += '<div class="col-lg-6 clm-sm-12"><div class="card-body life-desc">';
		result += feedList[i].body.replace(/<\/?[^>]+(>|$)/g, "");
		result += '</div></div></div></div>';	
	}
	return result;
}

