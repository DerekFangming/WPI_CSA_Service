$(function() {
    $('textarea').froalaEditor({
    	height: 300,
		toolbarButtons: ['bold', 'italic', 'underline', '|', 'color', 'paragraphFormat', 'align', '|', 'insertImage', 'insertTable', '|', 'undo', 'redo', 'spellChecker', 'selectAll', 'clearFormatting', '|', 'print', 'fullscreen', 'html'],//html
		toolbarButtonsMD: ['bold', 'italic', 'underline', '|', 'color', 'paragraphFormat', 'align', '|', 'insertImage', 'insertTable', '|', 'undo', 'redo', 'spellChecker', 'selectAll', 'clearFormatting', '|', 'print', 'fullscreen'],
		toolbarButtonsSM: ['bold', 'italic', 'underline', '|', 'color', 'paragraphFormat', 'align', '-', 'insertImage', 'insertTable', '|', 'undo', 'redo', 'spellChecker', 'selectAll', 'clearFormatting', '|', 'print', 'fullscreen'],
		toolbarButtonsXS: ['bold', 'italic', 'underline', '|', 'color', 'paragraphFormat', 'align', '-', 'insertImage', 'insertTable', '|', 'undo', 'redo', 'spellChecker', 'selectAll', 'clearFormatting', '|', 'print', 'fullscreen'],
		paragraphFormat: {
			H1: 'Heading 1',
			H2: 'Heading 2',
			H3: 'Heading 3',
			H4: 'Heading 4',
			N: 'Paragraph'
		}, 
		imageInsertButtons: ['imageBack', '|', 'imageUpload'],
		imageEditButtons: ['imageReplace', 'imageSize', 'imageRemove'],
		tableEditButtons: ['tableRows', 'tableRemove'],
		tableInsertMaxSize: 1,
		colorsBackground: ['#FFFFFF', '#FFFFFF', '#FFFFFF', '#FFFFFF', '#FFFFFF'],
		quickInsertButtons: ['image']
	});
    
    if($('#editorDefaultText').html() != '') {
    		$('textarea').froalaEditor('html.set', getEditableHTML($('#editorDefaultText').html()));
    }
    
});

function checkContentFormat(content) {
	var parser = new DOMParser();
	var doc = parser.parseFromString(content, "text/html");
	
	if (content.replace(/<\/?[^>]+(>|$)/g, "").length < 30) {
		return 'Content is too short. Please at least have 30 charactors.';
	}
	
	var tableList = doc.getElementsByTagName('table');
	for (var i=tableList.length - 1; i > -1; i--) {
		if (tableList[i].getElementsByTagName('table').length > 0) {
			var tabElement = tableList[i].getElementsByTagName('table')[0];
			tabElement.setAttribute( 'class', 'table table-bordered');
			return 'You cannot have one table inside another. Please remove the following table <br><br>' + tabElement.outerHTML;
		}
		if (tableList[i].getElementsByTagName('IMG').length > 0) {
			var imgElement = tableList[i].getElementsByTagName('IMG')[0];
			imgElement.removeAttribute("style");
			imgElement.setAttribute( 'class', 'aspect-fill');
			return 'You cannot have images inside table. Please remove the following image <br><br>' + imgElement.outerHTML;
		}
	}
	
	var imageList = doc.getElementsByTagName('img');
	for (var i=imageList.length - 1; i > -1; i--) {
		var src = imageList[i].getAttribute('src');
		if (src.toLowerCase().includes('gif')) {
			return 'Gif image is not supported. Please remove the following image <br><br><img class="aspect-fill" src="' + src +'">';
		}
	}
	
	return '';
}

function getAcceptableHTML(content) {
	var parser = new DOMParser();
	var doc = parser.parseFromString(content, "text/html");
	
	//Processing images
	var imgList = doc.getElementsByTagName('img');
	for (var i=imgList.length - 1; i > -1; i--) {
		var img = document.createElement('img');
		img.setAttribute('src', imgList[i].src);
		
		
		if (imgList[i].parentNode.innerHTML.length == imgList[i].outerHTML.length) {
			imgList[i].parentNode.replaceWith(img);
		} else {
			var otherChildren = imgList[i].parentNode.innerHTML.split(imgList[i].outerHTML);
			var newImgStr = '';
			if (otherChildren[0].length != 0) {
				var newChild = document.createElement(imgList[i].parentNode.nodeName);
				newChild.innerHTML = otherChildren[0];
				newImgStr += newChild.outerHTML;
			}
			newImgStr += img.outerHTML;
			if (otherChildren[1].length != 0) {
				var newChild = document.createElement(imgList[i].parentNode.nodeName);
				newChild.innerHTML = otherChildren[1];
				newImgStr += newChild.outerHTML;
			}
			
			imgList[i].parentNode.outerHTML = newImgStr;
		}
		
	}
	
	//Processing tables
	var tabList = doc.getElementsByTagName('table');
	for (var i=tabList.length - 1; i > -1; i--) {
		var tabStr = '<tab>';
		var flag = false;
		for (var j = 0, row; row = tabList[i].rows[j]; j++) {
			if (row.cells.length == 0) {
				continue;
			}
			if (flag) {
				tabStr += '<tbr></tbr>';
			}
			flag = true;
			tabStr += row.cells[0].innerHTML;
		}
		
		if (tabStr == '<tab><br>') {
			tabStr = '';
		} else {
			tabStr += '</tab>';
		}
		tabList[i].parentNode.innerHTML = tabList[i].parentNode.innerHTML.replace(tabList[i].outerHTML, tabStr);
	}
	
	//Processing alignments
	var elms = doc.querySelectorAll('*[style="text-align: right;"]');
	Array.prototype.forEach.call(elms, function(elm) {
		elm.align = 'right';
		elm.removeAttribute("style");
	});
	elms = doc.querySelectorAll('*[style="text-align: center;"]');
	Array.prototype.forEach.call(elms, function(elm) {
		elm.align = 'center';
		elm.removeAttribute("style");
	});
	elms = doc.querySelectorAll('*[style="text-align: justify;"]');
	Array.prototype.forEach.call(elms, function(elm) {
		elm.removeAttribute("style");
	});
	
	//Processing colors
	elms = doc.querySelectorAll('span[style]');
	Array.prototype.forEach.call(elms, function(elm) {
		var font = document.createElement('font');
		font.color = rgb2hex(elm.style.getPropertyValue('color'));
		font.innerHTML = elm.innerHTML;
		elm.outerHTML = font.outerHTML
	});
	
	//Processing links
	var linkList = doc.getElementsByTagName('a');
	for (var i=linkList.length - 1; i > -1; i--) {
		linkList[i].outerHTML = linkList[i].innerHTML;
	}
	
	//Removing the close tag for table new lines
	return doc.body.innerHTML.replace(/<tbr><\/tbr>/g, '<tbr>');
}

function getEditableHTML(content) {
	var parser = new DOMParser();
	var doc = parser.parseFromString(content.replace(/<tbr>/g, '<tbr></tbr>'), "text/html");
	
	//Processing tables
	var tabList = doc.getElementsByTagName('tab');
	for (var i=tabList.length - 1; i > -1; i--) {
		var newTab = '<table style="width: 100%;"><tbody>';
		var rows = tabList[i].innerHTML.split('<tbr></tbr>');
		for (var j=0; j < rows.length; j++) {
			newTab += '<tr><td>' + rows[j] + '</td></tr>';
		}
		newTab += '</tbody></table>';
		tabList[i].outerHTML = newTab;
	}
	
	//Processing font colors
	var fontList = doc.getElementsByTagName('font');
	for (var i=fontList.length - 1; i > -1; i--) {
		
		var span = document.createElement('span');
		span.setAttribute('style', 'color:' + fontList[i].color);
		
		span.innerHTML = fontList[i].innerHTML;
		
		fontList[i].outerHTML = span.outerHTML;
	}
		
	return doc.body.innerHTML;
}

function rgb2hex(rgb) {
    rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
    function hex(x) {
        return ("0" + parseInt(x).toString(16)).slice(-2);
    }
    return "#" + hex(rgb[1]) + hex(rgb[2]) + hex(rgb[3]);
}
