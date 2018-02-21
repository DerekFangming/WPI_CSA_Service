$(document).ready(function() {
	openSG(1);
});

function openSG(id) {
	$.ajax({
		type: "GET",
		url: "./get_sg?id=" + id,
        contentType: "application/json",
        dataType: "json",
		success: function (data) {
			if (data['error'] == '') {
				$("#sgTitle").text(data['title']);
				$("#sgSubTitle").text('Last updated by ' + data['ownerName'] + ' on ' + parseDateStr(data['createdAt']));
				
				$("#sgContent").html(processContent(data['content']));
			} else {
				showErrorPopup(data['error']);
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
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


