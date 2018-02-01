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
        var picker_btn_icon = $('<div class="text-center" style="margin-top:100px;"><i class="fa fa-plus fa-3x"></i><p>Click or drop an image here</p></div>');
        var picker_btn_input = $('<input type="file" name="'+settings.name+'" />');
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