// Submitted flag to avoid double submits when using hot keys (see ITERAPLAN-1736)
// Using hot keys which click buttons/links triggering the flowAction function in their onclick handler
// for some reason causes the flow action to be executed twice.
submitted = false;

function flowAction(action){
	// only execute if there wasn't a recent submit
	if (!submitted) {
		submitted = true;
		createHiddenField('_eventId', action);
		// save the current scroll coordinates
		saveScrollCoordinates();
		document.forms[0].submit();
		
		// reset the submitted flag to false after a second
		// (necessary for example for diagram creation where flowActions don't trigger a page refresh)
		setTimeout(function() {
			submitted = false;
		}, 1000);
	}
}

function changeAttributeWithResets() {
	document.forms[0].submit();
}

function flowActionRedirect(url, action) {
	document.forms[0].action = url;
	flowAction(action);
}

function flowActionWithID(action, id) {
	changeLocation(id.toString()+"?_eventId="+action);
}

/**
 * Returns the REST-URI relative from the current active MVC-URL. For example if
 * the current url would be
 * "http://localhost/iteraplan/informationsystem/init.do" this would return
 * "../show/informationsystem". Used for New-Buttin in MVC-Pages only.
 */
function getRestURIRelativeToMVC() {
	pathArray = window.location.pathname.split( '/' );
	return "../show/"+pathArray[pathArray.length-2]+"/";
}

function getParentURI() {
	uri = window.location.toString();
	return uri.substring(0, uri.lastIndexOf("/")+1);
}

function confirmChangeLocation(functionName, locationOrCallBack, args) {
	functionName(locationOrCallBack, args);
}

function CreateBookmarkLink(title, url) {

	if (window.sidebar) { // Mozilla Firefox Bookmark
		window.sidebar.addPanel(title, url,"");
	} else if( window.external ) { // IE Favorite
		window.external.AddFavorite( url, title); }
	else if(window.opera && window.print) { // Opera Hotlist
		return true; }
 }

function changeLocation(url){
	location.href=url;
}

function submitForm(action, method) {
	self.document.forms[0].action = action;
	if(method) {
		self.document.forms[0].method = method;
	}
	self.document.forms[0].submit();
	return true;
}

function triggerAttributeValueAction(path_to_componentModel, valueIndex, action) {
  	setHiddenField(path_to_componentModel + '.selectedPosition', valueIndex); 
  	setHiddenField(path_to_componentModel + '.action', action); 
  	flowAction('update');
}


/**
 * Note that this function differs from
 * confirmDeletionOfAttributeValueAssignments(), which is does also have more
 * Parameters. However, JS doesn't support function overloading. We choose this
 * subtly different method name to be used for Web Flow pages.
 * 
 * @param hiddenField
 * @param value
 * @param callback function to be execute after the confirmed
 * @param askBeforeDelete
 * @param confirmationMessage
 * @return true if user confirmed and form was submitted
 */
function confirmDeleteAttributeValueAssignment(hiddenField, value, callback, askBeforeDelete, confirmationHeader, confirmationMessage){
	if (askBeforeDelete != null && askBeforeDelete == 'true') {
		showConfirmDialog(confirmationHeader, confirmationMessage, function(){
			setHiddenField(hiddenField, value);
			callback();
		});
	} else {
		setHiddenField(hiddenField, value);
		callback();
	}
}

var awaitingResponse = false;

function createHiddenField(name, value){
	// if the hiddenField already exists overwrite it!
	var oldHiddenField = getElementByIdSafe(name);
	if(oldHiddenField) {
		if(oldHiddenField.tagName == 'INPUT') {
			oldHiddenField.value = value;
		} else {
			alert("Tried to set hidden field that is not an input but already exists! Please Report this!");
		}
	}
	// otherwise create a new one
	else {
		var field = document.createElement("input");
		field.setAttribute("type", "hidden");
		field.setAttribute("name", name);
		field.setAttribute("value", value);
		field.setAttribute("id", name);
		self.document.forms[0].appendChild(field);
		return field;
	}
}

function setHiddenField(hiddenField, value) {
	self.document.forms[0].elements[hiddenField].value = value;
	return false;
}

/**
 * Enables a checkbox if a superordinate option is selected, e.g. a radio
 * button. If the superordinate option becomes disable, e.g. because an other
 * radio button has been selected, the checkbox is disabled again.
 * 
 * @param option
 *            <input> object which has a <code>checked</code> property. Its
 *            state determines the enable/disable state of the checkbox
 * @param checkboxName
 *            Name property value of the checkbox to control. Note: This
 *            parameter should be turned into something more generic as soon as
 *            more than a few checkbox are to be handled.
 * @return void
 */
function enableCheckboxBySuperOption(option, checkboxName) {
	var checkbox = self.document.forms[0].elements[checkboxName];        

	// update checkbox field
	if (option.checked == true) {
		checkbox.disabled = "";
	} else {
		checkbox.disabled = "disabled";
		checkbox.checked = false;
	}
}

function changePortfolioType() {
	if(self.document.forms[0].elements["portfolioOptions.portfolioType"].value == 'xy') {
		getElementByIdSafe("xy").className = "visible";
		getElementByIdSafe("quad").className = "hidden";
	} else {
		getElementByIdSafe("xy").className = "hidden";
		getElementByIdSafe("quad").className = "visible";          
	}
}

function changeColor(o) 
{ 
	var o2 = o.options[o.options.selectedIndex]; 
	if (o2) { 
		o.style.backgroundColor = o2.style.backgroundColor;
	}
}

function copyValue(sourceField, targetField) {
	if ((self.document.forms[0].elements[sourceField] != null) && (self.document.forms[0].elements[targetField] != null) 
			&& (self.document.forms[0].elements[targetField].value == "")) {
		self.document.forms[0].elements[targetField].value = self.document.forms[0].elements[sourceField].value;
	}
}

function enableFieldBySecondFieldValue(firstFieldName, secondFieldName, fieldValue) {
	if ((self.document.forms[0].elements[firstFieldName] != null) && (self.document.forms[0].elements[secondFieldName]
	                                                                                                  != null)) {
		if (self.document.forms[0].elements[secondFieldName].value == fieldValue) {
			self.document.forms[0].elements[firstFieldName].style.visibility = "visible";
		} else {
			self.document.forms[0].elements[firstFieldName].style.visibility = "hidden";
		}
	}
}

function switchRadioButton(name, index) {
	self.document.getElementsByName(name)[index].checked = "checked";
}

function keypress_handler(e) {
	var keycode;

	if (!e) {
		e = window.event;
	}

	var srce   = e.srcElement
	? e.srcElement
			: e.target;
	var eltype = (srce)
	? srce.type
			: 'undefined';

	if (e.which) {
		keycode = e.which;
	} else if (e.keyCode) {
		keycode = e.keyCode;
	}

	return (keycode != 13 || eltype == 'textarea');
}


document.onkeypress = keypress_handler; 

/**
 * Toggles a table layer (hide/show)
 * @param id
 * @usedFrom jsp\commonReporting\AdvancedFunctions.jsp
 */
function toggleLayer(layerId) {
	var el = getElementByIdSafe(layerId);
	el.className = (el.className == 'visibleRows') ? 'hiddenRows' : 'visibleRows';
}
/**
 * Toggles a div layer (hide/show)
 * @param id
 * @usedFrom jsp\commonReporting\DynamicForm.jsp
 */
function toggleDivLayer(layerId) {
	var el = getElementByIdSafe(layerId);
	el.className = (el.className == 'visible') ? 'hidden' : 'visible';
}

function toggleImage(pic1, pic2, elementId) {
	var el = getElementByIdSafe(elementId);
	var sub = el.src.substring(el.src.length - pic1.length);
	el.src = (sub == pic1) ? pic2 : pic1;
}

/**
 * changes the icon class of an <i> element. If this element has class1 then it will be
 * changed to class2. If this element has class2 then it will be changed to class1.
 * @param elementId the id of the <i> element.
 * @param class1 first possible class for this element
 * @param class2 second possible class for this element
 */
function toggleIcon(elementId, class1, class2) {
	var icon = $('#' + elementId);
	if(icon.attr('class') == class2) {
		icon.attr('class', class1);
	} else {
		icon.attr('class', class2);
	}
}

function doUnloadProcessing() {
	awaitingResponse = false;
}

function allChecked(fields) {
	if (fields.length > 0) {
		for (var i = 0; i < fields.length; i++) {
			if (!fields[i].checked && fields[i].type == 'checkbox') {
				return false;
			}
		}
	}
	else {
		if (!fields.checked && fields.type == 'checkbox') {
			return false;
		}
	}
	return true;
}

function getCheckBoxesByNamePattern(checkBoxNamePattern1, checkBoxNamePattern2) {
	var notDoneYet = true;
	var count = 0;
	var checkBoxesToCheck = [];
	while (notDoneYet) {      
		var checkBoxName = checkBoxNamePattern1+count+checkBoxNamePattern2;
		var checkBoxes = document.getElementsByName(checkBoxName);
		if (checkBoxes != null && checkBoxes.length > 0) {
			for (var i = 0; i < checkBoxes.length; i++) {
				if (checkBoxes[i].type == 'checkbox') {
					checkBoxesToCheck[checkBoxesToCheck.length] = checkBoxes[i];
				}
			}
		}
		else {
			notDoneYet = false;
		}
		count++;
	}
	return checkBoxesToCheck;      
}

function checkUnCheckAllByPattern(checkBoxNamePattern1, checkBoxNamePattern2, checkAllBox) {
	var checkBoxesToCheck = getCheckBoxesByNamePattern(checkBoxNamePattern1, checkBoxNamePattern2);
	checkUnCheckAll(checkBoxesToCheck, checkAllBox);
}

function checkUnCheckAll(checkboxes, checkAllBox) {
	var checkOn = true;
	if (allChecked(checkboxes) && checkAllBox && checkAllBox.checked == false) {
		checkOn = false;
	}
	for (var i = 0; i < checkboxes.length; i++) {
		if (checkboxes[i].type == 'checkbox') {
			checkboxes[i].checked = checkOn;
		}
	}
}

/**
 * Sets the given checkAllBox dependent on the status of "child"-boxes given by their name pattern prefix+"number"+suffix
 * If all child-boxes are checked, the checkAll-box is set to 'checked', too, otherwise it is unchecked.
 * @param childBoxesNamePattern1 String: prefix of child-checkbox names
 * @param childBoxesNamePattern2 String: suffix of child-checkbox names
 * @param checkAllBoxId id of the "checkAll"-checkbox
 */
function updateCheckAllBoxByNamePattern(childBoxesNamePattern1, childBoxesNamePattern2, checkAllBoxId) {
	var checkAllBox = document.getElementById(checkAllBoxId);
	var childBoxes = getCheckBoxesByNamePattern(childBoxesNamePattern1, childBoxesNamePattern2);
	updateCheckAllBox(childBoxes, checkAllBox);
}

/**
 * Sets the given checkAllBox dependent on the "child"-boxes' status
 * If all child-boxes are checked, the checkAll-box is set to 'checked', too, otherwise it is unchecked.
 * @param childBoxes array of document-elements
 * @param checkAllBox checkbox-element
 */
function updateCheckAllBox(childBoxes, checkAllBox) {
	if (allChecked(childBoxes)) {
		checkAllBox.checked = true;
	} else {
		checkAllBox.checked = false;
	}
}

function unCheckCheckBox(checkboxId) {
		$('#' + checkboxId).prop('checked', false);
}

function addLinkToDescription(link_title_id, link_value_text_id,link_value_file_id, description_id) {
	var link_value = getElementByIdSafe(link_value_text_id).value;
	var link_value_file=getElementByIdSafe(link_value_file_id).value;
	
	link_type = 'URL';    // default assumption
	
	if (link_value == "" && link_value_file == "") {
		return;
	}
	else if (link_value ==""){
		link_value=link_value_file;
		link_type = 'File';
	}
	var link_title = getElementByIdSafe(link_title_id).value;
	if (link_title == "") {
		link_title = link_value;
	}
	
	link_value = convertLinkToHref(link_value, link_type);
	var description = getElementByIdSafe(description_id);
	description.value = description.value + '\n[[' + link_title + '>>' + link_value + ']]';
	// scroll to the bottom, where the link was just inserted
	description.scrollTop = description.scrollHeight;
}

function convertLinkToHref(link, link_type) {
	rExp = / /g;
	link = link.replace(rExp,'%20');
	rExp = /\\/g;
	link = link.replace(rExp,'/');
	if(link_type == 'File'){
		link = 'file:///' + link;
	}
	else if (link_type = 'URL')
	{
	    var linktmp = link.toLowerCase();

		if (linktmp.indexOf('http://') == -1 &&
				linktmp.indexOf('https://') == -1 &&
				linktmp.indexOf('ftp://') == -1) {
			link = 'http://' + link;
		}
	}
	return link;
}


function getElementByIdSafe(elementId) {
	if (document.getElementById) {
		return document.getElementById(elementId);
	}
	else if (document.all) {
		return document.all[elementId];
	}
	else if (document.layers) {
		return document.layers[elementId];
	}
}

function getElementsByClass(node,searchClass,tag) {
	var classElements = new Array();
	var els = node.getElementsByTagName(tag);
	var elsLen = els.length;
	var pattern = new RegExp(searchClass);
	for (var i = 0, j = 0; i < elsLen; i++) {
		if ( pattern.test(els[i].className) ) {
			classElements[j] = els[i];
			j++;
		}
	}

	return classElements;
}

function onEnterClickButton(id, event) {
	var keycode;
	if (event) {
		if(event.keyCode) {
			keycode = event.keyCode;
		}
		else {
			keycode = event.which;
		}
	}
	else {
		return true;
	}
	
	// ENTER = 13
	if (keycode == 13) {
		// stop submitting the form
		if(event.preventDefault) {
			event.preventDefault();
		}
		if(event.stopPropagation) {
			event.stopPropagation();
		}
		
		// click the button instead of the submit
		getElementByIdSafe(id).click();
		return false;
		
	}
	else {
	   return true;
	}
}


// The following javascript is used by AddRemoveEnumAVs.jsp. It is included here
// so that it is rendered only once.
function displayDescription(atId, atValueId, extended_html_id) {
	var numberOfAV = $('#' + extended_html_id + '_descriptionOutput' + atId).children().length;
	var start = 0;
	var step = 1;
	// iterate over all descriptions and set them to hidden
    // increment depends on browser type
    if (navigator.appName.indexOf("Netscape") != -1){
    	start = 1;
    	step = 2;
    }
    if (navigator.appName.indexOf("Microsoft") != -1){
    	start = 0;
    	step = 1;
    }    
    for (var i = start; i < numberOfAV; i += step){
    	$('#' + extended_html_id + '_descriptionOutput' + atId).children().eq(i).attr('class', 'hidden');
    }
    // if no element is selected or the element has no description, don't show
	// the borders
    if (atValueId == ''){
    	$('#' + extended_html_id + '_descriptionOutput' + atId).attr('class', 'nameintable');  
	}    
    else if ($('#' + extended_html_id + '_enumAVdescription' + atValueId) == null){
    	$('#' + extended_html_id + '_descriptionOutput' + atId).attr('class', 'nameintable');	      
    }
    else {
    	$('#' + extended_html_id + '_descriptionOutput' + atId).attr('class', 'border nameintable');
	    $('#' + extended_html_id + '_enumAVdescription' + atValueId).attr('class', 'visible nameintable');
    }
  }

// Cross Browser Event Handling
function addEvent(obj, eventType, func, useCaption) {
  if (obj.addEventListener) {
    obj.addEventListener(eventType, func, useCaption);
    return true;
  } else if (obj.attachEvent) {
    var retVal = obj.attachEvent("on"+eventType, func);
    return retVal;
  } else {
    return false;
  }
}

function toggleButton(buttonId) {
	var button = $('#' + buttonId);

	// toggle button
	if (button.attr('disabled')) {
		button.attr('disabled', false);
	} else {
		button.attr('disabled', true);
	}
}

//Add CSS Class ".borderHighlighted" to current this element
function setBorderHighlighted() {
	$(this).addClass("borderHighlighted");
}

// This will decorate frontend elements. When their value gets changed, their
// style will change to make the unsaved value noticeable for the user.
function decorateTextInputsOnKeyPress() {
	// get all text-inputs that are not of the filter class and decorate them
	$("input[type='text']").each(function() {
		if($(this).attr('class') != 'filter') {
			$(this).change(setBorderHighlighted);
		}
	});
	
	// get all dropdowns and decorate them
	$('select').each(function() {
		$(this).change(setBorderHighlighted);
	});	
	
	// get all textareas and decorate them
	$('textarea').each(function() {
		$(this).change(setBorderHighlighted);
	});
}

function getCheckedRadioValue()
{
   return $('input:radio:checked').first().val();
}   

/**
 * Shows a modal Dialog (Popup) for the attribute details. 
 * Parameters are variable but must follow the convention: 
 * first param is used as the title,
 * rest of the params are in key-value order
 */
function showTipDataDialogGen(par) {
	var midStr = '<br />';
    	
	var tip = "";
	for (var i = 1; i < par.length; i++) {
		if(par[i]) {
			tip = tip+'<b>'+ par[i] + ':</b> ' + par[i+1] + midStr;
		} 
		else {
			tip = tip+midStr;
		}
	    i++;
	}
	
	showPopupDialog(par[0], tip);
}

/**
 * Creates and appends a popover for the attribute details to the element with the given id. 
 * Parameters are variable but must follow the convention: 
 * first param is used as the title,
 * rest of the params are in key-value order
 */
function showTipDataPopoverGen(id, par, options) {
	var midStr = '<br />';
    	
	var tip = "";
	for (var i = 1; i < par.length; i++) {
		if (par[i]) {
			tip = tip + '<b>' + par[i] + ':</b> ' + par[i+1] + midStr;
		} 
		else {
			tip = tip + midStr;
		}
	    i++;
	}
	
	createAndAppendPopover(id, par[0], tip, options);
}

function showTipDataDialog(varArgs) {
	showTipDataDialogGen(arguments);
}

function showTipDataDialogArr(arr) {
	showTipDataDialogGen(arr);
}

/**
 * Shows a modal Dialog (Popup) for a Link message.  
 * @param id
 * @return
 */
function showTipLinkDialog(title, messageD, url) {
	var midStr = '<br />';
    	
	var tip = '<b><a href="' + url + '">' + messageD + ':</a></b>' + midStr 
			+ '<input type="text" name="url" id="bookmark" style="width:400px" onfocus="this.select()" value="' + url + '">' + midStr;
	
	showPopupDialog(title, tip);
}

/**
 * A generic function, shows a modal dialog (Popup). 
 * @param title the title of the modal dialog
 * @param content the content of the modal dialog
 */
function showPopupDialog(title, content) {

	if($('#modalFooterContainer').hasClass('hide')){
		$('#modalFooterContainer').removeClass('hide');
		$('#modalFooterCancel').addClass('hide');
	}
	
	$('#modalDialogTitle').html(title);
	$('#modalDialogContent').html(content);
	$('#modalDialog').modal('toggle');
}

/**
 * A generic function, creates and appends a popover to the element with the given id.
 * @param id the id of the element the popover is appended to 
 * @param title the title of the modal dialog
 * @param content the content of the modal dialog
 * @param options options for the popover dialog
 */
function createAndAppendPopover(id, title, content, options) {
//	console.log(content);
//	$('#' + id).attr(
//        'data-content', function() {
//        	return content;
//        }
//	);
//	$('#' + id).attr('data-placement', 'right');
//	$('#' + id).attr('data-trigger', 'hover');
	var opt = options;
	if (!opt) {
		opt = {};
	}
	if (!opt.trigger) {
		opt.trigger = 'focus';
	}
	if (!opt.placement) {
		opt.placement = 'right';
	}
	opt.html= 'true';
	opt.container = 'body';
	opt.title = function() { return title; };
	opt.content = function() { return content; };
	opt.delay = { show: '200', hide: '400' };
	$('#' + id).popover(opt).popover('toggle');
}

/**
 * A generic function, shows a modal dialog (Confirm). 
 * @param title the title of the modal dialog
 * @param content the content of the modal dialog
 * @param callback function or href-link that will be called, if OK was clicked
 */
function showConfirmDialog(title, content, callback) {
	
	if($('#modalFooterContainer').hasClass('hide')){
		$('#modalFooterContainer').removeClass('hide');
	}
	if($('#modalFooterCancel').hasClass('hide')){
		$('#modalFooterCancel').removeClass('hide');
	}
	
	/* Unbind old handlers first in order to avoid executing multiple callbacks (and i.e. deleting saved queries by accident!) */  
	if(jQuery.isFunction(callback)){
		$('#modalFooterOK').unbind('click').click(function(){
			callback();
		});
	} else {
		$('#modalFooterOK').unbind('click').click(function(){
			changeLocation(callback);
		});
	}
	
	$('#modalDialogTitle').html(title);
	$('#modalDialogContent').html(content);
	$('#modalDialog').modal('toggle');
}

function loadAttributeTypeDescription(elem_id, id, pos, url, text, avs, ord, options) {
	$.ajax({
    	url : url,
    	cache : false,
    	data : {
      	  'id': id,
      	  'avs': avs
    	},
    	traditional : true,
    	dataType : "json",
    	success : function(data){
    		text.splice(pos, 0, data[0]);
    		for (var k = 1; k < data.length; k++) {
    			text.splice(ord[k-1]+k, 0, data[k]);
    		}
    		showTipDataPopoverGen(elem_id, text, options);
    	},
    	error : function(error) {
            alert(error);
        }
	});
}

function sortByColumn(col) {
	createHiddenField('colSortIndex', col);
	setHiddenField('pageStart', 0);
	setHiddenField('nextPageToShow', true);
	setHiddenField('previousPage', 'first');
	setHiddenField('nextPage', '');
	
	self.document.forms[0].submit();	
}

//Make table headers stick to menu on scrolling
//If "table" (header) reaches "stickToElem" => show "captionBar"
function setStickyTableCaptions(tableId, stickToElemId, captionBarId) {
	var fadeDelay = 300;
	
	var theader = $('#' + tableId + ' thead');
	var thcells = $('tr:first th', theader);
	var elemToStick = $('#' + stickToElemId);
	var stickyElem = $('#' + captionBarId);
	
	var theaderTop = theader.offset().top;
	var magicNumber = 3;
	var stickyTop = (elemToStick.offset().top + elemToStick.height()*2) - $(window).scrollTop() - magicNumber;
		
	$(window).scroll(function(){
 		if ($(this).scrollTop() > (theaderTop-stickyTop - ($.browser.msie ? magicNumber : 0) )) {
			if (stickyElem.hasClass('unvis')) {
				stickyElem.removeClass('unvis');
				stickyElem.addClass('vis');
				stickyElem.css({
					top: stickyTop,
					width: theader.width()
				});
				thcells.each(function(index) {
					var aDiv = $('#' + captionBarId + '_' + (index+1));
					aDiv.css({
						left: $(this).offset().left,
						width: $(this).width(),
						position: 'fixed'
					}); 
				});
			}
		}
		else {
			if (stickyElem.hasClass('vis')) {
				stickyElem.removeClass('vis');
				stickyElem.addClass('unvis');
			}
		}
	});
}

/**
 * Escapes the most common special characters in JavaScript. The implementation follows
 * StringEscapeUtils.escapeJavaScript from the Apache Common library 
 * @param string the string to be escaped
 */
function escapeJavaScript(string) {
	var result = string;
	
	if (result) {
		result = result.replace(/\\/g, '\\\\');
		result = result.replace(/'/g, '\\\'');
		result = result.replace(/"/g, '&quot;');
	}
	
	return result;
}


/**
 * Spezial function for the dashboard.
 * After successful loading, this function hide the loading image with the given id.
 * 
 * @param imageId
 */
function onGraphicLoaded(imageId) {
	
	$('#' + imageId).css('visibility', 'hidden');
	$('#' + imageId).css('display', 'none');
		
	// The following isn't done in IE6, because it results in half the page being blanked-out for some reason
	if (navigator.userAgent.indexOf("MSIE 6.0") === -1) {
		// Remove the Loading Graphic, so the Preview Gaphic's vertical location is not lowered by it 
		$('#' + imageId).text('');
	}
}

/**
 * Spezial function for the dashboard.
 * After successful loading, this function hide the loading image with the given id.
 * 
 * @param imageId
 */
function onSvgGraphicLoaded(imageId, iframe, errorMessage) {
	var doc= 'contentDocument' in iframe? iframe.contentDocument : iframe.contentWindow.document;
	if (doc.getElementsByTagName("svg").length > 0) {
	    var svg = doc.getElementsByTagName("svg")[0];
	}
    
    if (typeof(svg) != 'undefined' && svg != null) {
    	iframe.style.height= svg.getAttribute("height") + 'px';
    	iframe.style.width= svg.getAttribute("width") + 'px';
    	
    	$('#' + imageId).css('visibility', 'hidden');
    	$('#' + imageId).css('display', 'none');
    		
    	// The following isn't done in IE6, because it results in half the page being blanked-out for some reason
    	if (navigator.userAgent.indexOf("MSIE 6.0") === -1) {
    		// Remove the Loading Graphic, so the Preview Gaphic's vertical location is not lowered by it 
    		$('#' + imageId).text('');
    	}
    	
    } else {
    	// Failure: Show failure message in Loading element (HTML possible) 
		iframe.style.visibility='hidden';
		iframe.style.display='none';
		$('#' + imageId).text(errorMessage);
		$('#' + imageId).css('visibility', 'visible');
		$('#' + imageId).attr('class', 'feedbackPanelERROR');
		onGraphicLoadingError(null, null);
    }
}

/**
 * Spezial error function for the dashboard.
 * In case of an error, when call an url (loading an image). 
 * 
 * This function hide the loading image and show the global error message.
 * 
 * @param imageId
 */
function onGraphicLoadingError(imageId, errorMessage){
	
	if (imageId != null) {
		$('#' + imageId).text(errorMessage);
		$('#' + imageId).css('visibility', 'visible');
		$('#' + imageId).css('display', 'block');
		$('#' + imageId).attr('class', 'feedbackPanelERROR');
	}
	
	$('#dashboardErrorFeedbackPanel').css('visibility', 'visible');
	$('#dashboardErrorFeedbackPanel').css('display', 'block');
}

/**
 * Loading function for FireFox.
 * For loading images in the dashbord, we need an error handling.
 * In most cases there is no error handling supported for the tag object.
 * 
 * This Script is optimized for FireFox. In other browsers the images are not show. 
 * So we need an special way to load an image and to handle loading status.
 * 
 * This function call the given url, check the status and write the result into the object with the given id.
 * 
 * @param id
 * @param urlWithParameters
 * @param errorMessage
 */
function loadAction(id, urlWithParameters, errorMessage) {

	// element where to display the diagramm; one of image, object
	var graphic = $('#object' + id);
	
	// element where we show Loading, and potential error messages; always plain html
	var graphicLoading = $('#' + id);
	
	// XHR used to handle potential errors
	var xhrArgs = {
		url : urlWithParameters,
		dataType : "text",
		cache: false,
		complete : function(jqXHR) {
				
			switch (jqXHR.status) {
			case 200:
                // Success... inject the data into the object tag 
				graphic.html(jqXHR.responseText);	

				// hide the loading image
				onGraphicLoaded(id);
				
				// debug output
				// alert(jqXHR.responseText);
				break;
			
			default:
				// Failure: Show failure message in Loading element (HTML possible) 
				graphic.css('visibility', 'hidden');
				graphic.css('display', 'none');
				graphicLoading.text(errorMessage + ": Statuscode " + jqXHR.status);
				graphicLoading.css('visibility', 'visible');
				graphicLoading.attr('class', 'feedbackPanelERROR');
				onGraphicLoadingError(null,null);
			}
		}
	};
	
	// Call the asynchronous xhrGet with above defined params
	var deferred = $.ajax(xhrArgs);	    		
}

function triggerNettoDownload(format) {
	var hfield = createHiddenField('format', format);
	self.document.forms[0].submit();
	hfield.parentNode.removeChild(hfield);
}

function triggerNettoDownloadFlow(format) {
	var hfield = createHiddenField('format', format);
	flowAction('nettoExport');
	hfield.parentNode.removeChild(hfield);
}