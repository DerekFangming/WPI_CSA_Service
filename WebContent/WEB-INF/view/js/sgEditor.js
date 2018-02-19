$(document).ready(function() {
	var parser = new DOMParser();
	var doc = parser.parseFromString($('#menuList').html().replace(/openSG\(/g, 'pickSG(this, '), "text/html");
	
	//Processing tables
	var aList = doc.getElementsByTagName('a');
	for (var i=aList.length - 1; i > -1; i--) {
		if (aList[i].hasAttribute('data-toggle')) {
			var menuId = aList[i].href.split('#collapse')[1];
			var newA = '<a onclick="pickSG(this, ' + menuId + ');" class="ml-2" href="#">Select this</a>'
			aList[i].outerHTML = aList[i].outerHTML + newA;
		}
	}
	$('#menuList').html(doc.body.innerHTML);
});

function selectSgType(typeId) {
	if (typeId == 1) {
		$('#sgType').html('an article');
		$('#menuCalledLbl').fadeOut();
		$('#menuNameIn').fadeOut();
	} else {
		$('#sgType').html('a menu item');
		$('#menuCalledLbl').fadeIn();
		$('#menuNameIn').fadeIn();
	}
}

function selectRelLoc(typeId) {
	if (typeId == 1) {
		$('#sgRelLoc').html('after');
	} else {
		$('#sgRelLoc').html('before');
	}
}

function toggleMenuList() {
	$('#locPickerModal').modal('toggle');
}

function pickSG(elm, menuId) {
	var clicked = $(elm.closest('div')).parent();
	alert(clicked.outerHTML);
	//alert($(elm).parent().outerHTML);
	//$('#locPickerModal').modal('toggle');
	//findBeforeAndAfter($('#menuList > div').children('div').eq(1), menuId);
}

$('a').click(function(){
    alert('perform action here');
});

function findBeforeAndAfter(node, menuId) {
	var before;
	node.children('div').each(function(i, obj) {
		before = obj;
		if(obj.attr('class').includes('card-body')) {
			var a = obj.children('a').first();
			var current = a.attr('onclick').replace('pickSG(', '').replace(');', '');
		}
	});
}