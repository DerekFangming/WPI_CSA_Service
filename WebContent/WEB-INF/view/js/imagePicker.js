(function ( $ ) {
 
    $.fn.imagePicker = function( options ) {
        var settings = $.extend({
            name: "",
            class: "btn btn-default btn-block",
            icon: "Not used"
        }, options );
        
        return this.each(function() {
            $(this).html(create_btn(this, settings));
        });
 
    };
 
    function create_btn(that, settings) {
        var picker_btn_icon = $('<div class="text-center" style="margin-top:100px;"><img src="/resources/img/plus.png"><p>Click or drop an image here</p></div>');
        var picker_btn_input = $('<input type="file" accept="image/jpeg, image/jpg, image/png" name="'+settings.name+'" />');
        var picker_btn = $('<div class="'+settings.class+' img-upload-btn life-img-container"></div>')
            .append(picker_btn_icon)
            .append(picker_btn_input);
            
        picker_btn_input.change(function() {
            if ($(this).prop('files')[0]) {
                var reader = new FileReader();
                reader.onload = function(e) {
                    var preview = create_preview(that, e.target.result, settings);
                    $(that).html(preview);
                }
                reader.readAsDataURL(picker_btn_input.prop('files')[0]);
            }                
        });

        return picker_btn
    };
    
    function create_preview(that, src, settings) {
    	if (!src.startsWith('data:image')) {
    		showErrorPopup('Please select an image.');
    		return create_btn(that, settings);
    	} else if (!src.startsWith('data:image/jpeg') && !src.startsWith('data:image/jpg') && !src.startsWith('data:image/png')) {
    		showErrorPopup('Supported image formats are jpg, jpeg and png.');
    		return create_btn(that, settings);
    	}
    	
        var picker_preview_image = $('<img src="'+src+'" class="aspect-fill" />');
        var picker_preview_remove = $('<button class="btn btn-primary" style="margin-top:-160px;">Remove</button>');
        var picker_preview = $('<div class="life-img-container text-center"></div>')
            .append(picker_preview_image)
            .append(picker_preview_remove);

        picker_preview_remove.click(function() {
            var btn = create_btn(that, settings);
            $(that).html(btn);
        });
        
        return picker_preview;
    };
    
}( jQuery ));

var dragTimer;
$('#img-picker').on('dragover', function(e) {
	var dt = e.originalEvent.dataTransfer;
	if (dt.types && (dt.types.indexOf ? dt.types.indexOf('Files') != -1 : dt.types.contains('Files'))) {
		$('#img-picker').attr('style', 'background: #DFDFDF;');
		window.clearTimeout(dragTimer);
	}
});

$('#img-picker').on('dragleave', function(e) {
	dragTimer = window.setTimeout(function() {
		$('#img-picker').attr('style', 'background: white;');
	}, 25);
});

function allowDrop(ev) {
	if ($('#img-picker').children().children('img').length == 0) {
		ev.preventDefault();
	}
}
