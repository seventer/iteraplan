
	// exchanges options between two option lists.
	function exchangeListEntries(list1, list2) {
		for (var i = 0; i < list1.options.length; i++) {
			var tmpOption = list1.options[i];
			if (tmpOption.selected) {
				var option = new Option(tmpOption.text, tmpOption.value);
				option.id = tmpOption.id;
				list2[list2.length] = option;
				list1.options[i] = null;
				i--;
			}
		}
		sortList(list1);
		sortList(list2);
	}

	// sorts an option list alphabetically.
	function sortList(list) {
		// copy options into an array
		var myOptions = [];
		for (var loop=0; loop<list.options.length; loop++) {
			myOptions[loop] = { optText:list.options[loop].text, optValue:list.options[loop].value, optId:list.options[loop].id };
		}
		myOptions.sort(sortFuncAsc);
		// copy sorted options from array back to select box
		list.options.length = 0;
		for (var loop=0; loop<myOptions.length; loop++) {
			var optObj = document.createElement('option');
			optObj.text = myOptions[loop].optText;
			optObj.value = myOptions[loop].optValue;
			optObj.id = myOptions[loop].optId;
			list.options.add(optObj);
		}
	}

	// sort function - ascending (case-insensitive). See: function sortList(list)
	function sortFuncAsc(record1, record2) {
		var value1 = record1.optText.toLowerCase();
		var value2 = record2.optText.toLowerCase();
		if (value1 > value2) return(1);
		if (value1 < value2) return(-1);
		return(0);
	}

	// selects all options of a select.
	function selectAllOptions(listObj) {
		for (var i = 0; i <= listObj.length - 1; i++) {
			listObj.options[i].selected = true;
		}
	}

	// toggles the css class of an element between visible and hidden
	function toggleLayer(layerId) {
		var el = getElementByIdSafe(layerId);
		el.className=(el.className=='visible')?'hidden':'visible';
	}

	// retrieves an element by id
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
  
	// confirm a submit of a form
	function confirmSubmit(message, formName) {
		var doIt = confirm(message);
		if (doIt) {
			var form = getElementByIdSafe(formName);
			form.submit();
		}
	}
