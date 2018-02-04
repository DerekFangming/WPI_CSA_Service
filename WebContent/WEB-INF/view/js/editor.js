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
		imageInsertButtons: ['imageBack', '|', 'imageUpload', 'imageByURL'],
		imageEditButtons: ['imageReplace', 'imageSize', 'imageRemove'],
		tableEditButtons: ['tableRows', 'tableRemove'],
		tableInsertMaxSize: 1,
		colorsBackground: ['#FFFFFF', '#FFFFFF', '#FFFFFF', '#FFFFFF', '#FFFFFF'],
		quickInsertButtons: ['image']
	});
});

function checkContentFormat(content) {
	var parser = new DOMParser();
	var doc = parser.parseFromString(content, "text/html");
	
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
		var src = imageList[i].src;
		if (src.toLowerCase().includes('gif')) {
			return 'Gif image is not supported. Please remove the following image <br><br><img class="aspect-fill" src="' + src +'">';
		}
	}
	
	return '';
}

function getAcceptableHTML(content) {
	var parser = new DOMParser();
	var doc = parser.parseFromString(content, "text/html");
	
	var imgList = doc.getElementsByTagName('img');
	for (var i=imgList.length - 1; i > -1; i--) {
		var img = document.createElement('img');
		img.setAttribute('src', imgList[i].src);
		
		var subStyle = imgList[i].style.cssText.split(';')
		for (var j in subStyle) {
			if (subStyle[j].trim().startsWith('width')) {
				img.setAttribute('width', subStyle[j].replace("width", "").replace(":", "").replace("px", "").split(".")[0].trim());
			} else if (subStyle[j].trim().startsWith('height')) {
				img.setAttribute('height', subStyle[j].replace("height", "").replace(":", "").replace("px", "").split(".")[0].trim());
			}
		}
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
	
	return doc.body.innerHTML;
}
