if (jQuery) (function($) {
	var colorPickerColorIndex = 0;
	
	$.extend($.fn, {
		colorPicker: function(opt) {
			var options = (!opt) ? {} : opt;
			if (options.colors == undefined) options.colors = Array("#123456", "#234567", "#345678", "#456789", "#567890");
			options.currentColor = this.val();
			// Set a default color if no color selected
			if ($.inArray(options.currentColor, options.colors) == -1) {
				options.currentColor = options.colors[colorPickerColorIndex];
				colorPickerColorIndex = ++colorPickerColorIndex % options.colors.length;
				this.val(options.currentColor);
			}
			options.inputField = this;
			options.id = this.attr('id').replace(/\W/g,"__");
			this.hide();
			
			// Create HTML for color picker
			myHtml = '<div id="colorPicker_'+options.id+'" class="optionChooser smallColorPicker_button_back">';
			myHtml +='<div id="colorPicker_selection_'+options.id+'" class="smallColorPicker_button_color" style="background-color:'+options.currentColor+'" />';
			myHtml += '<div id="colorPicker_panel_'+options.id+'" class="colorPanel" style="display:none"><ul>';
			for (i in options.colors) {
				myHtml += '<li><div style="background-color: '+options.colors[i]+'" /></li>';
			}
			myHtml += '</ul></div></div>';

			this.after(myHtml);
			
			// Handle click on color picker button
			$("#colorPicker_"+options.id).click(function() {
				$("#colorPicker_panel_"+options.id).toggle();
				$("#colorPicker_panel_"+options.id+" a").each(function() {
					if ($("div", this).css("background-color") == $("#show_color_"+options.id).css("background-color")) {
						$(this).addClass("selected");
					}
				});
			});
			
			// Handle click on color in color panel
			$("#colorPicker_panel_"+options.id+" ul li div").click(function() {
				$("#colorPicker_panel_"+options.id+" ul li.selected").removeClass("selected"); // Remove class "selected" from previous selection
				$(this).parent().addClass("selected"); // Add class "selected" to current selection
				$("#colorPicker_selection_"+options.id).css("background-color", $(this).css("background-color"));
				$("#colorPicker_panel_"+options.id).hide(); // Hide panel
				options.inputField.val($(this).css("background-color"));
				return false;
			});
			
			return this; // allows chaining
		}
	});
})(jQuery);
