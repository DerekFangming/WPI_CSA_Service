$(function() {
	var htmlOption = $('#editorHTMLOption').val() == 'true' ? 'html' : '';
	var tabSize = $('#allowImgTxt').val() == 'true' ? 2 : 1;
	
    $('textarea').froalaEditor({
    	height: 300,
		toolbarButtons: ['bold', 'italic', 'underline', '|', 'color', 'paragraphFormat', 'align', '|', 'insertImage', 'insertTable', '|', 'undo', 'redo', 'spellChecker', 'selectAll', 'clearFormatting', '|', 'print', 'fullscreen', htmlOption],
		toolbarButtonsMD: ['bold', 'italic', 'underline', '|', 'color', 'paragraphFormat', 'align', '|', 'insertImage', 'insertTable', '|', 'undo', 'redo', 'spellChecker', 'selectAll', 'clearFormatting', '|', 'print', 'fullscreen', htmlOption],
		toolbarButtonsSM: ['bold', 'italic', 'underline', '|', 'color', 'paragraphFormat', 'align', '-', 'insertImage', 'insertTable', '|', 'undo', 'redo', 'spellChecker', 'selectAll', 'clearFormatting', '|', 'print', 'fullscreen', htmlOption],
		toolbarButtonsXS: ['bold', 'italic', 'underline', '|', 'color', 'paragraphFormat', 'align', '-', 'insertImage', 'insertTable', '|', 'undo', 'redo', 'spellChecker', 'selectAll', 'clearFormatting', '|', 'print', 'fullscreen', htmlOption],
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
		tableInsertMaxSize: tabSize,
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
	
	/*if (content.replace(/<\/?[^>]+(>|$)/g, "").length < 30) {
		return 'Content is too short. Please at least have 30 charactors.';
	}*/
	
	var tableList = doc.getElementsByTagName('table');
	for (var i=tableList.length - 1; i > -1; i--) {
		if (tableList[i].getElementsByTagName('table').length > 0) {
			var tabElement = tableList[i].getElementsByTagName('table')[0];
			tabElement.setAttribute( 'class', 'table table-bordered');
			return 'You cannot have one table inside another. Please remove the following table <br><br>' + tabElement.outerHTML;
		}
		if ($('#allowImgTxt').val() == 'true') {
			for (var j = 0, row; row = tableList[i].rows[j]; j++) {
				if (row.cells.length == 1) {
					if (row.cells[0].getElementsByTagName('img').length > 0) {
						var imgElement = row.cells[0].getElementsByTagName('img')[0];
						imgElement.removeAttribute("style");
						imgElement.setAttribute( 'class', 'aspect-fill');
						return 'You cannot have images inside table. Please remove the following image <br><br>' + imgElement.outerHTML;
					}
				} else if (row.cells.length == 2) {
					if (row.cells[0].getElementsByTagName('img').length != 1) {
						return 'You need to place one and only one image inside the left table cell for image inline mode. Please refer to Instruction.<br>'
						+ 'Please modify the following table cell <br><br><div class="border" style="width:100%">' + row.cells[0].outerHTML + '</div>';
					}
					if (row.cells[1].getElementsByTagName('img').length > 0) {
						return 'You cannot have images inside the right table cell for image inline mode. Please refer to Instruction.<br>'
						+ 'Please modify the following table cell <br><br><div class="border" style="width:100%">' + row.cells[1].outerHTML + '</div>';
					}
				} else {
					return 'Invalid table column count.';
				}
			}
		} else {
			if (tableList[i].getElementsByTagName('img').length > 0) {
				var imgElement = tableList[i].getElementsByTagName('img')[0];
				imgElement.removeAttribute("style");
				imgElement.setAttribute( 'class', 'aspect-fill');
				return 'You cannot have images inside table. Please remove the following image <br><br>' + imgElement.outerHTML;
			} else if (tableList[i].rows[0].cells.length != 1) {
				return 'Invalid table column count.';
			}
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
			if (imgList[i].src.includes('WCImage_')) {
				imgList[i].parentNode.replaceWith(imgList[i]);
			} else {
				imgList[i].parentNode.replaceWith(img);
			}
		} else {
			var otherChildren = imgList[i].parentNode.innerHTML.split(imgList[i].outerHTML);
			var newImgStr = '';
			if (otherChildren[0].length != 0) {
				var newChild = document.createElement(imgList[i].parentNode.nodeName);
				newChild.innerHTML = otherChildren[0];
				newImgStr += newChild.outerHTML;
			}
			if (imgList[i].src.includes('WCImage_')) {
				newImgStr += imgList[i].outerHTML;
			} else {
				newImgStr += img.outerHTML;
			}
			
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
		var parseToTab = false;
		if ($('#allowImgTxt').val() == 'true') {
			if (tabList[i].rows[0].cells.length == 0) {
				continue;
			} else if (tabList[i].rows[0].cells.length == 1) {
				parseToTab = true;
			} else {
				//parse to imgtxt. Read only the first two cells regardlessly
				var ImgTxtStr = '<imgtxt>';
				var flag = false;
				//TODO
			}
		} else {
			parseToTab = true;
		}
		
		//parse to tab. Read only the first cell regardlessly
		if (parseToTab) {
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
	
	//Processing alignments in tables (in div)
	var divList = doc.getElementsByTagName('div');
	for (var i=divList.length - 1; i > -1; i--) {
		var paragraph = document.createElement('p');
		if (divList[i].hasAttribute('align')) {
			paragraph.setAttribute('align', divList[i].align);
		}
		paragraph.innerHTML = divList[i].innerHTML;
		divList[i].outerHTML = paragraph.outerHTML;
	}
	
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
	var doc = parser.parseFromString(content.replace(/<\/tbr>/g, '').replace(/<tbr>/g, '<tbr></tbr>'), "text/html");
	
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
