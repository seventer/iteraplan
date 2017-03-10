/**
 * @author GeekTantra
 * @date 20 September 2009
 * http://www.geektantra.com/2009/09/jquery-live-form-validation/
 * Modified by dsmith: added support for twitter boostrap
 * Modified by Michael Westrich: added real live validation support
 */
$(document).ready(function() {
	$('[onclick*="flowAction(\'save\');"]').each(function() {
    	var onclickAction = $(this).attr('onclick');
    	$(this).attr('onclick', "if($('div.control-group.error > div.controls > span.help-inline').length == 0) {" + onclickAction + "}");
    });
});
(function($) {
    var ValidationErrors = new Array();
    $.fn.validate = function(options){
        options = $.extend({
            expression: "return true;",
            message: "",
            error_message_class: "help-inline",
            error_container_class: "control-group",
            live: true
        }, options);
        var SelfID = $(this).attr("id");
        var unix_time = new Date();
        unix_time = parseInt(unix_time.getTime() / 1000);
        if (!$(this).parents('form:first').attr("id")) {
            $(this).parents('form:first').attr("id", "Form_" + unix_time);
        }
        var FormID = $(this).parents('form:first').attr("id");
        if (!((typeof(ValidationErrors[FormID]) == 'object') && (ValidationErrors[FormID] instanceof Array))) {
            ValidationErrors[FormID] = new Array();
        }
        if (options['live']) {
            if ($(this).find('input').length > 0) {
            	if (validate_field("#" + SelfID, options)) {
                    if (options.callback_success) {
                        options.callback_success(this);
                    }
                } else {
                    if (options.callback_failure) { 
                        options.callback_failure(this);
                    }
                }
                $(this).find('input').bind('keyup blur', function() {
                    if (validate_field("#" + SelfID, options)) {
                        if (options.callback_success) {
                            options.callback_success(this);
                        }
                    } else {
                        if (options.callback_failure) { 
                            options.callback_failure(this);
                        }
                    }
                });
            } else {
            	validate_field(this);
                $(this).bind('keyup blur', function() {
                    validate_field(this);
                });
            }
        }
        $(this).parents("form").submit(function() {

            if (validate_field('#' + SelfID)) {
				
				$("#" + SelfID + options['error_message_class']).remove();
				if ($(id).next("." + options['error_message_class']).length == 0) {
					$('.' + options['error_container_class']).removeClass('error');
				}
			
                return true;
			} else {
                return false;
			}
        });
        function validate_field(id) {
            var self = $(id).attr("id");
            var expression = 'function Validate(){' + options['expression'].replace(/VAL/g, '$(\'#' + self + '\').val()') + '} Validate()';
            var validation_state = eval(expression);
            if (!validation_state) {
                if ($(id).next("#" + self + options['error_message_class']).length == 0) {
                    $(id).after('<span class="' + options['error_message_class'] + '" id="' + self + options['error_message_class'] + '">' + options['message'] + '</span>');
                    $($(id).parents("div .control-group")[0]).addClass("error");
					//$(id).parents("div .control-group").addClass("error");
                }
                if (ValidationErrors[FormID].join("|").search(id) == -1) {
                    ValidationErrors[FormID].push(id);
                }
                return false;
            } else {
                for (var i = 0; i < ValidationErrors[FormID].length; i++) {
                    if (ValidationErrors[FormID][i] == id) { 
                        ValidationErrors[FormID].splice(i, 1);
                    }
                }
	            $("#" + self + options['error_message_class']).remove();
                if ($(id).next("." + options['error_message_class']).length == 0) {
                	$($(id).parents("." + options['error_container_class'])[0]).removeClass('error');
                	//$(id).parents("." + options['error_container_class']).removeClass('error');
                }
                return true;
            }
        }
    };
    $.fn.validated = function(callback) {
        $(this).each(function(){
            if (this.tagName == "FORM") {
                $(this).submit(function() {
                    if (ValidationErrors[$(this).attr("id")].length == 0) {
                        callback();
                    }
					return false;
                });
            }
        });
    };
})($);