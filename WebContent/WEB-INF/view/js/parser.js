function processContent(content) {
	var parser = new DOMParser();
	var doc = parser.parseFromString(content, "text/html");
	
	//Background color processing
	var divList = doc.getElementsByTagName('div');
	if (divList.length > 0) {
		var bgColor = divList[0].getAttribute('color');
		divList[0].setAttribute('style', 'background-color:#' + bgColor);
	}
	
	//Image cell processing (This has to happen before imgtxt or txtimg)
	var imgList = doc.getElementsByTagName('img');
	for (var i=imgList.length - 1; i > -1; i--) {
		var imgDiv = document.createElement('div');
		imgDiv.setAttribute('class', 'row top-buffer');
		var imgId = imgList[i].src.replace('WCImage_', '');
		var imgInner = '<div class="col"><img src="/images/' + imgId + '.jpg" class="aspect-fill"></div>';
		imgDiv.innerHTML = imgInner;
		imgList[i].parentNode.replaceChild(imgDiv, imgList[i]);
	}
	
	//Image text cell processing
	var imgtxtList = doc.getElementsByTagName('imgtxt');
	for (var i=imgtxtList.length - 1; i > -1; i--) {
		var imgtxtDiv = document.createElement('div');
		imgtxtDiv.setAttribute('class', 'row top-buffer');
		var imgtxtInner = '<div class="col imgtxt-img-container"><img class="fit-width" src="./images/53.jpg"></div><div class="col col-center">';
		imgtxtInner += '<div class="card-body">' + imgtxtList[i].innerHTML + '</div></div>';
		imgtxtDiv.innerHTML = imgtxtInner;
		imgtxtList[i].parentNode.replaceChild(imgtxtDiv, imgtxtList[i]);
	}
	
	//Table cell processing
	var tabList = doc.getElementsByTagName('tab');
	for (var i=tabList.length - 1; i > -1; i--) {
		var tabDiv = document.createElement('div');
		tabDiv.setAttribute('class', 'card');
		var tabInner = '<ul class="list-group list-group-flush">';
		var cellList = tabList[i].innerHTML.split("<tbr>");
		for (var c in cellList) {
			tabInner += '<li class="list-group-item" style="display:inline">' + cellList[c].replace(/<br>/gi, ' <br> ') + '</li>'
		}
		tabInner += '</ul>';
		tabDiv.innerHTML = tabInner;
		tabList[i].parentNode.replaceChild(tabDiv, tabList[i]);
	}
	
	return linkify(doc.documentElement.innerHTML);
}