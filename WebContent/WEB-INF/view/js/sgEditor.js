$(document).ready(function() {
	var parser = new DOMParser();
	var doc = parser.parseFromString($('#menuList').html(), "text/html");
	var edittingTitle = $('#title').val();
	
	//Processing tables
	var aList = doc.getElementsByTagName('a');
	for (var i=aList.length - 1; i > -1; i--) {
		var newItem;
		var menuId;
		if (!aList[i].hasAttribute('data-toggle')) {
			newItem = '<div class="one-line">' + aList[i].innerHTML + '</div>';
			menuId = $(aList[i]).attr('onclick').replace('openSG\(', '').replace(');', '');
		} else {
			newItem = aList[i].outerHTML;
			menuId = $(aList[i]).attr('href').replace('#collapse', '');
		}
		newItem += '<div class="btn-group btn-group-sm ml-3" role="group" style="margin-top: -10px;"><button type="button" '
			+ 'class="btn btn-outline-secondary" onclick="before(this, ' + menuId + ')">Before this</button><button type="button" '
			+ 'class="btn btn-outline-secondary" onclick="after(this, ' + menuId + ')">After this</button></div>';
		if (edittingTitle != '') {
			if (aList[i].innerHTML == edittingTitle) {
				$(aList[i]).parent().addClass('bg-primary');
				newItem = '<div class="one-line text-white">' + aList[i].innerHTML + '<span class="ml-3">Currently editting</span></div>';
			}
		}
		aList[i].outerHTML = newItem;
	}
	
	//Adding reset selection button
	var headerList = doc.getElementsByClassName('card-header');
	for (var i = 0; i <headerList.length; i++) {
		if (headerList[i].innerHTML == 'Survival Guide Menu') {
			headerList[i].innerHTML = headerList[i].innerHTML
				+ '<button type="button" class="btn btn-outline-secondary btn-sm ml-3" onclick="clearSelection()">Clear selection</button>';
			break;
		}
	}
	
	$('#menuList').html(doc.body.innerHTML);
	$('#menuList').fadeIn();
	
	var template = '<div class="colorpicker dropdown-menu">' +
	'<div class="colorpicker-saturation"><i><b></b></i></div>' +
	'<div class="colorpicker-hue"><i></i></div>' +
	'<div class="colorpicker-color"><div /></div>' +
	'<div class="colorpicker-selectors"></div>' + '</div>';
	
	$('#colorPicker').colorpicker({ "template": template }).on('changeColor', function(ev) {
		var color = $(this).colorpicker('getValue', '#ffffff');
		$('#colorPicker > button').css('background-color', color);
		$('#colorPicker > button').css('color', color);
		$('#bgColor').val(color);
	});
});

function before(elm, menuId) {
	var prevSelection = $("#menuList").find(".btn-success");
	prevSelection.attr('class', 'btn btn-outline-secondary');
	var prevPlaceholder = $("#menuList").find(".bg-success");
	prevPlaceholder.remove();
	
	var message = $('#sgId').val() == '' ? 'Your new article & menu will appear here.' : 'You will be moving the article to this place.';
	$(elm).attr('class', 'btn btn-success');
	var appendDiv = '<div class="card-body sg-menu-item border-bottom bg-success text-white">' + message + '</div>';
	var parentMenu = $(elm).parent().parent();
	if (parentMenu.prev().length && parentMenu.prev().attr('class').includes('bg-primary')) {
		//Moving the article to the original location
		$('#relLocSelection').val('');
	} else {
		parentMenu.before(appendDiv);
		$('#relLocSelection').val('before' + menuId);
	}
}

function after(elm, menuId) {
	var prevSelection = $("#menuList").find(".btn-success");
	prevSelection.attr('class', 'btn btn-outline-secondary');
	var prevPlaceholder = $("#menuList").find(".bg-success");
	prevPlaceholder.remove();
	
	var message = $('#sgId').val() == '' ? 'Your new article & menu will appear here.' : 'You will be moving the article to this place.';
	$(elm).attr('class', 'btn btn-success');
	var appendDiv = '<div class="card-body sg-menu-item border-bottom bg-success text-white">' + message + '</div>';
	var parentMenu = $(elm).parent().parent();
	if (parentMenu.next().length) {
		if (parentMenu.next().attr('class').includes('bg-primary')){
			//Moving the article to the original location
			$('#relLocSelection').val('');
		} else if (parentMenu.next().attr('class').includes('card-collapse')) {
			if (parentMenu.next().next().attr('class').includes('bg-primary')){
				$('#relLocSelection').val('');
			} else {
				parentMenu.next().after(appendDiv);
				$('#relLocSelection').val('after' + menuId);
			}
		} else {
			parentMenu.after(appendDiv);
			$('#relLocSelection').val('after' + menuId);
		}
	} else {
		parentMenu.after(appendDiv);
		$('#relLocSelection').val('after' + menuId);
	}
	
	
}

function clearSelection() {
	var prevSelection = $("#menuList").find(".btn-success");
	prevSelection.attr('class', 'btn btn-outline-secondary');
	var prevPlaceholder = $("#menuList").find(".bg-success");
	prevPlaceholder.remove();
	$('#relLocSelection').val('');
}

function selectSgType(typeId) {
	if (typeId == 1) {
		$('#currentType').html('Article');
		$('#title').attr('placeholder', 'Enter the name of the new article');
		$('#alertMsg').html('Don\'t forget to check format to make sure your article looks good in all platforms.');
		$('#articleTitleDiv').fadeOut();
		$('#colorPicker').fadeOut();
	} else {
		$('#currentType').html('Menu');
		$('#title').attr('placeholder', 'Enter the name of the new menu');
		$('#alertMsg').html('Your article will be created as first article under your new menu.');
		$('#articleTitleDiv').fadeIn();
		$('#colorPicker').fadeIn();
	}
}

$("#checkFormatBtn").click(function(){
	var error = checkFormat();
	if (error == '' ) {
		showPopup('<span style="color:green"> No errors found<i class="fa fa-check"></i></span>', 'The format has no problems and should looks good in all platforms.');
	} else {
		showErrorPopup(error);
	}
});

function checkFormat () {
	if ($('#sgId').val() == '' && $('#relLocSelection').val() == '') {
		return 'Please select a location to place the article & menu by clicking on one of the \'Before this\' or \'After this\' button';
	}
	if ($('#currentType').html() == 'Article') {
		if ($('#title').val().trim().length == 0) {
			return 'Please enter the title of the article.';
		}
	} else {
		if ($('#title').val().trim().length == 0) {
			return 'Please enter the title of the menu.';
		}
		if ($('#articleTitle').val().trim().length == 0) {
			return 'Please enter the title of the article.';
		}
		if ($('#bgColor').val().trim() == '') {
			return 'Please select the background color for the article title by clicking on the <em>Background</em> button on the right side of the title text box.';
		}
	}
	return checkContentFormat($('textarea').froalaEditor('html.get', true));
}

$("#instructionBtn").click(function(){
	var instructions = '<h5>Location: <small class="text-muted">Click on one of the <em>Before this</em> or <em>After this</em> button to place '
		+ 'the new article or menu, and a green place holder will appear. You can close & open menus to see if the location is what you want or '
		+ 'not.</small></h5><br><h5>Article & menu name: <small class="text-muted">If you want to create a new article, it will show up at the '
		+ 'location you choosed. If you want to create a new menu, a new text box will show up asking for article name. The article will then be '
		+ 'places as the first article under the new menu.</small></h5><br><h5>Content: <small class="text-muted">To change text color, click on '
		+ 'the&nbsp;<i class="fa fa-tint"></i>&nbsp;button. To change text size, click on the&nbsp;<i class="fa fa-paragraph"></i>&nbsp;button. '
		+ 'Images are shown on a full line. Texts are only allowed above or below images, not inline. You do not need to resize images. They will '
		+ 'automatically be stretched horizontally to the full line. Please use images with at least 800 * 800 resolution. For table, if there is '
		+ 'only one column, it will be parsed as a list and no images & inner tables are allowed inside. If there are two columns, it will be '
		+ 'parsed into image text inline mode, where the left cell has to have an image and the right cell must not have an image. Tables with '
		+ 'other numbers of columns are not allowed. </small></h5><br><h5>Background: <small class="text-muted">This is the background color for '
		+ 'the title of each article. If you just want to create a new article, the background color will be automatically set to the same color '
		+ 'from it\'s neighbour articles. If you want to create a new menu, you will then need to pick a color for the new article. Note that '
		+ 'this color will be used by all other articles if they are added around your new article. Please also choose a dark color since the '
		+ 'title color will be white.</small></h5>';
	showPopup('Instruction', instructions);
});

$("#submitBtn").click(function(){
	var error = checkFormat();
	if (error != '' ) {
		showErrorPopup(error);
	} else {
		var accessToken = getAccessToken();
		var locStr = $('#relLocSelection').val();
		var placeAfter = locStr.includes('after');
		var relLoc = parseInt(locStr.replace('before', '').replace('after', ''));
		var params = {accessToken : accessToken, relId : relLoc, placeAfter: placeAfter};
		
		var title = $('#title').val().trim();
		var content = getAcceptableHTML($('textarea').froalaEditor('html.get', true));
		if ($('#currentType').html() == 'Article') {
			params.title = title;
			content = '<div><br><br><br><h1><big><big><font color=#FFFFFF>' + title + '</font></big></big></h1></div>' + content;
			params.content = content;
		} else {
			params.menuTitle = title;
			var articleTitle = $('#articleTitle').val().trim();
			var color = $('#bgColor').val().trim().replace('#', '');
			params.title = articleTitle;
			content = '<div color="' + color + '"><br><br><br><h1><big><big><font color=#FFFFFF>' + title + '</font></big></big></h1></div>' + content;
			params.content = content;
		}
		startBtnLoading('#submitBtn');
		$.ajax({
	        type: "POST",
	        url: "./create_sg",
	        data: JSON.stringify(params),
	        contentType: "application/json",
	        dataType: "json",
	        success: function(data){
	        		stopBtnLoading('#submitBtn');
				if (data['error'] == "" ) {
					window.location.href = "./refresh_sg_menu";
				} else {
					showErrorPopup(data['error']);
				}
	        },
	        failure: function(errMsg) {
		        	stopBtnLoading('#submitBtn');
		        	showErrorPopup('Unknown error occured. Please contact support');
	        }
	    });
		
	}
});

$("#saveChangeBtn").click(function(){
	var error = checkFormat();
	if (error != '' ) {
		showErrorPopup(error);
	} else {
		var currentElm = $("#menuList").find(".bg-primary");
		
		if ($('#relLocSelection').val() != '' && !currentElm.next().length && !currentElm.prev().length) {
			showConfirmPopup('This article is the only one in the parent menu. If you move this article to other place, '
					+'the parent menu will be automatically deleted and this cannot be undone. ', 'saveChanges()');
		} else {
			saveChanges();
		}
	}
});

function saveChanges() {
	var accessToken = getAccessToken();
	var params = {accessToken : accessToken, id : parseInt($('#sgId').val())};
	
	var title = $('#title').val().trim();
	var content = getAcceptableHTML($('textarea').froalaEditor('html.get', true));
	//Adding back the original div tag
	if ($('#editorDefaultText').html().includes('div')) {
		content = $('#editorDefaultText').html().split('<\/div>')[0] + '</div>' + content;
	}
	
	if (title != $('#origTitle').val().trim()) {
		params.title = title;
	}

	if (content != $('#editorDefaultText').html().replace(/<\/tbr>/g, '')) {
		params.content = content;
	}
	
	var locStr = $('#relLocSelection').val();
	if (locStr.trim() != '') {
		params.placeAfter = locStr.includes('after');
		params.relId = parseInt(locStr.replace('before', '').replace('after', ''));
	}
	/*alert(content);
	alert($('#editorDefaultText').html().replace(/<\/tbr>/g, ''));*/
	if (Object.keys(params).length == 2) {
		showErrorPopup('Nothing is changed. Please update something before saving');
	} else {
		loadingConfirmPopup();
		startBtnLoading('#saveChangeBtn');
		$.ajax({
	        type: "POST",
	        url: "./update_sg",
	        data: JSON.stringify(params),
	        contentType: "application/json",
	        dataType: "json",
	        success: function(data){
	        		stopBtnLoading('#saveChangeBtn');
	        		hideAndStopLoadingConfirmPopup();
				if (data['error'] == "" ) {
					//window.location.href = "./feed?id=" + $('#feedId').val();
					alert('done');
				} else {
					showErrorPopup(data['error']);
				}
	        },
	        failure: function(errMsg) {
		        	stopBtnLoading('#saveChangeBtn');
		    		hideAndStopLoadingConfirmPopup();
		        	showErrorPopup('Unknown error occured. Please contact support');
	        }
	    });
	}
}
