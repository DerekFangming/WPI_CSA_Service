$(document).ready(function() {
	var id = parseInt($('#initialId').val());
	openSG(id);
});

function openSG(id) {
	$('#sgTitle').hide();
	$('#sgHistDropdown').hide();
	$('#sgEditBtn').hide();
	$('#sgLoading').fadeIn();
	$.ajax({
		type: "GET",
		url: "./get_sg?id=" + id,
        contentType: "application/json",
        dataType: "json",
		success: function (data) {
			$('#sgLoading').hide();
			if (data['error'] == '') {
				var lastEditStr = 'Last updated by ' + data['ownerName'] + ' on ' + parseDateStr(data['createdAt']);
				$("#sgTitle").text(data['title']);
				$("#sgSubTitle").text(lastEditStr);
				$("#sgContent").html(processContent(data['content']));
				$('#sgEditBtn').html('Edit');
				$('#sgEditBtn').prop('href', './edit_sg?id=' + id);
				if(data['history']) {
					var dropdownContent = '<a class="dropdown-item" href="#" onclick="openSG(' + id + ')"><small class="text-muted">'
						+ lastEditStr + '</small></a>';
					var histList = data['history'];
					for(var i = 0; i < histList.length; i++) {
						dropdownContent += '<a class="dropdown-item" href="#" onclick="openSGHist(' + histList[i].id + ')"><small class="text-muted">';
						dropdownContent += 'Updated by ' + histList[i].ownerName + ' on ' + parseDateStr(histList[i].createdAt);
						dropdownContent += '</small></a>';
					}
					$('#sgHistDropdown > div').html(dropdownContent);
				} else {
					$('#sgHistDropdown > div').html('<a class="dropdown-item" href="#"><small class="text-muted">No more editing history found</small></a>');
				}
				
				$('#sgTitle').fadeIn();
				$('#sgHistDropdown').fadeIn();
				$('#sgEditBtn').fadeIn();
			} else {
				showErrorPopup(data['error']);
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
			$('#sgLoading').hide();
			showErrorPopup('Unknown error occured. Please contact support');
		}
	});
}

function openSGHist(histId) {
	$('#sgTitle').hide();
	$('#sgHistDropdown').hide();
	$('#sgEditBtn').hide();
	$('#sgLoading').fadeIn();
	$.ajax({
		type: "GET",
		url: "./get_sg_history?historyId=" + histId,
        contentType: "application/json",
        dataType: "json",
		success: function (data) {
			$('#sgLoading').hide();
			if (data['error'] == '') {
				$("#sgTitle").text(data['title']);
				$("#sgSubTitle").text('Updated by ' + data['ownerName'] + ' on ' + parseDateStr(data['createdAt']));
				$("#sgContent").html(processContent(data['content']));
				$('#sgEditBtn').html('Edit latest version');
				
				$('#sgTitle').fadeIn();
				$('#sgHistDropdown').fadeIn();
				$('#sgEditBtn').fadeIn();
			} else {
				showErrorPopup(data['error']);
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
			$('#sgLoading').hide();
			showErrorPopup('Unknown error occured. Please contact support');
		}
	});
}

$('#sgSearchInput').keypress(function (e) {
	if(e.which == 13) {
		$("#searchSGBtn").click();
	}
});

$("#searchSGBtn").click(function(){
	var keyword = $('#sgSearchInput').val().trim();
	startBtnLoading('#searchSGBtn');
	if (keyword == '') {
		$.ajax({
			type: "GET",
			url: "./get_full_sg_menu",
	        contentType: "application/json",
	        dataType: "json",
			success: function (data) {
				stopBtnLoading('#searchSGBtn');
				if (data['error'] == '') {
					$('#sgMenuDiv').html(data['menuList']);
				} else {
					showErrorPopup(data['error']);
				}
			},
			error: function (jqXHR, textStatus, errorThrown) {
				stopBtnLoading('#searchSGBtn');
				showErrorPopup('Unknown error occured. Please contact support');
			}
		});
	} else {
		$.ajax({
			type: "GET",
			url: "./search_sg",
			data: {keyword: keyword},
	        contentType: "application/json",
	        dataType: "json",
			success: function (data) {
				stopBtnLoading('#searchSGBtn');
				if (data['error'] == '') {
					var sgList = data['sgList']
					var result = '<div class="card"><div class="card-header">Survival Guide Menu</div>';
					for (var i = 0; i < sgList.length; i++) {
						result += '<div class="card-body sg-menu-item border-bottom"><a onclick="openSG(';
						result += sgList[i].id;
						result += ');" href="#">';
						result += sgList[i].title;
						result += '</a></div>';
					}
					result += '</div>';
					
					$('#sgMenuDiv').html(result);
				} else {
					if (data['error'] == 'The survival guide article you are looking for does not exist.') {
						$('#sgMenuDiv').html('<div class="jumbotron my-4"><center><h2 class="display-4">No articles found.</h2><p class="lead">Please try searching with some other keywords.</p></center></div>');
					} else {
						showErrorPopup(data['error']);
					}
				}
			},
			error: function (jqXHR, textStatus, errorThrown) {
				stopBtnLoading('#searchSGBtn');
				showErrorPopup('Unknown error occured. Please contact support');
			}
		});
	}
});

$("#createSGBtn").click(function(){
    var accessToken = getAccessToken();
    
    if (accessToken == "") {
    		showErrorPopup('You have to login first before creating articles');
    } else if ($("#userEmailConfirmed").val() != 'true'){
    		showErrorPopup('You have to confirm your email first before creating articles');
    } else {
    		window.location="./new_sg";
    }
    
});


