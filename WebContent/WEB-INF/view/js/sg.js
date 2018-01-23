function openSG(id) {
	$.ajax({
		type: "GET",
		url: "./get_sg_article?id=" + id,
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
			$("#ticketSpinner").toggleClass("fa fa-refresh fa-spin");
	    	$("#payButton").toggleClass("disabled");
			showErrorPopup('Unknown error occured. Please contact support');
		}
	});
}

function processContent(content) {
	var parser = new DOMParser();
	var doc = parser.parseFromString(content, "text/html");
	//Background div processing
	var divList = doc.getElementsByTagName('div');
	if (divList.length > 0) {
		var bgColor = divList[0].getAttribute('color');
		divList[0].setAttribute('style', 'background-color:#' + bgColor);
	}
	
	//Image text cell processing
	var imgtxtList = doc.getElementsByTagName('imgtxt');
	if (imgtxtList.length > 0) {
		for (var i=imgtxtList.length - 1; i > -1; i--) {
			var imgtxtDiv = document.createElement('div');
			imgtxtDiv.setAttribute('class', 'row top-buffer');
			var imgtxtInner = '<img src="./images/53.jpg" class="col sg-itImage"><div class="col">';
			imgtxtInner += imgtxtList[i].innerHTML + '</div>';
			imgtxtDiv.innerHTML = imgtxtInner;
			//alert(imgtxtInner);
			imgtxtList[i].parentNode.replaceChild(imgtxtDiv, imgtxtList[i]);
		}
	}
	
	return doc.documentElement.innerHTML;
}

window.onload = openSG(1);

