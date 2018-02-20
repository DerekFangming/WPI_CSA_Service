$(document).ready(function() {
	var parser = new DOMParser();
	var doc = parser.parseFromString($('#menuList').html(), "text/html");
	
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
		aList[i].outerHTML = newItem;
	}
	$('#menuList').html(doc.body.innerHTML);
	$('#menuList').fadeIn();
});

function before(elm, menuId) {
	var prevSelection = $("#menuList").find(".btn-success");
	prevSelection.attr('class', 'btn btn-outline-secondary');
	var prevPlaceholder = $("#menuList").find(".bg-success");
	prevPlaceholder.remove();
	
	$(elm).attr('class', 'btn btn-success');
	var appendDiv = '<div class="card-body sg-menu-item border-bottom bg-success text-white">Your new article & menu will appear here.</div>';
	var parentMenu = $(elm).parent().parent();
	parentMenu.before(appendDiv);
	
	$('#relLocSelection').val('before' + menuId);
}

function after(elm, menuId) {
	var prevSelection = $("#menuList").find(".btn-success");
	prevSelection.attr('class', 'btn btn-outline-secondary');
	var prevPlaceholder = $("#menuList").find(".bg-success");
	prevPlaceholder.remove();
	
	$(elm).attr('class', 'btn btn-success');
	var appendDiv = '<div class="card-body sg-menu-item border-bottom bg-success text-white">Your new article & menu will appear here.</div>';
	var parentMenu = $(elm).parent().parent();
	if (parentMenu.next().length) {
		if (parentMenu.next().attr('class').includes('card-collapse')) {
			parentMenu.next().after(appendDiv);
		} else {
			parentMenu.after(appendDiv);
		}
	} else {
		parentMenu.after(appendDiv);
	}
	
	$('#relLocSelection').val('after' + menuId);
}

function selectSgType(typeId) {
	if (typeId == 1) {
		$('#currentType').html('Article');
		$('#title').attr('placeholder', 'Enter the name of the new article');
		$('#alertMsg').html('Don\'t forget to check format to make sure your article looks good in all platforms.');
		$('#articleTitleDiv').fadeOut();
	} else {
		$('#currentType').html('Menu');
		$('#title').attr('placeholder', 'Enter the name of the new menu');
		$('#alertMsg').html('Your article will be created as first article under your new menu.');
		$('#articleTitleDiv').fadeIn();
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
	/*if ($('#relLocSelection').val() == '') {
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
	}*/
	return checkContentFormat($('textarea').froalaEditor('html.get', true));
}

$("#submitBtn").click(function(){
	var content = getAcceptableHTML($('textarea').froalaEditor('html.get', true));
	alert(content);
});
