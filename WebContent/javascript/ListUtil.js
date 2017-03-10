/**
 * Moves all selected list elements from one list to another list.
 * @param fromList : Object. Source list
 * @param toList : Object. Target list
 */
function moveSelection(fromList, toList) {
  
  var toMove = [];
  for ( var i = 0; i < fromList.options.length; i++) {
    var tmpOption = fromList.options[i];
    if (tmpOption.selected) {
      var option = new Option(tmpOption.text, tmpOption.value);
      option.id = tmpOption.id;
      toMove.push(option);
      fromList.options[i] = null;
      i--;
    }
  }
  
  var pos = 0;
  for ( var i = 0; i < toMove.length; i++) {
	  if (toList.options.length == 0) {
		  toList.options.add(toMove[i]);
		  continue;
	  }
	  while (pos < toList.options.length
			  && compareTo(toMove[i], toList.options[pos]) >= 0) {
		  pos++;
	  }
	  toList.options.add(toMove[i], pos);
  }
}

/**
 * Moves all selected list elements from one list to another list.
 * Lists are identified by their IDs, referring to multi-select boxes.
 * @param fromListName : String. ID of the source list 
 * @param toListName : String. ID of target list
 */
function moveSelection2(fromListName, toListName) {
  
  var fromList = getElementByIdSafe(fromListName);
  var toList = getElementByIdSafe(toListName);
  
  moveSelection(fromList, toList);
}

function moveSelectionLeftTopExclusive(prefix, topOptionText) {

	var availableList = getElementByIdSafe(prefix + "_available");
	var connectedList = getElementByIdSafe(prefix + "_connected");

	// Check if Top is in the list of selected things
	var bContainsTop = $.grep(availableList.options, function(value, idx) {
		return value.text == topOptionText && value.selected;
	});
	
	// Move whatever the user wanted to
	moveSelection(availableList, connectedList);

	// If top is in the list, remove all other elements
	if (bContainsTop.length > 0) {

		$.each(connectedList.options, function() {
			if (this.text != topOptionText) {
				this.selected = true;
			}
		});
		moveSelection(connectedList, availableList);
	}
	// Else, remove Top
	else {

		selectOptionByName(prefix + "_connected", topOptionText);
		moveSelection(connectedList, availableList);
	}
}

function moveTopLeftIfIsEmpty(prefix, topOptionText) {
		
	// Move the Top Element Left at OnLoad only if Left is empty
	var connectedList = getElementByIdSafe(prefix+"_connected");	
	
	if (connectedList != null && connectedList.options.length < 1) {
	
		selectOptionByName(prefix+"_available", topOptionText); 
		moveSelection2(prefix+"_available", prefix+"_connected");
	}
}

function removeOption(fromListName,toListName,optionText){
	
	var fromList = getElementByIdSafe(fromListName);
	var toList = getElementByIdSafe(toListName);
	
	if(fromList.options.length > 1){
		$.each(fromList.options, function() {
			if(this.text == optionText){
				
				this.selected = true;
				moveSelection(fromList, toList);
				return false;
			}
		});
	}
}

function selectOptionByName(fromListName, optionText){
	
	var fromList = getElementByIdSafe(fromListName);
	// This is a forEach loop that can break
	$.each(fromList.options, function () {
		if(this.text == optionText){
			
			this.selected = true;
			return false;
		}
	});
}

function compareTo(record1, record2) {
    var value1 = record1.text.toLowerCase();
    var value2 = record2.text.toLowerCase();
    if (value1 > value2) {
      return (1);
    }
    if (value1 < value2) {
      return (-1);
    }
    return (0);
}

function sortList(list) {

  // copy options into an array
  var optionsArray = [];
  for ( var loop = 0; loop < list.options.length; loop++) {
    optionsArray[loop] = {
      text :list.options[loop].text,
      value :list.options[loop].value,
      id :list.options[loop].id
    };
  }
  //sort options in ascending order 
  optionsArray.sort(compareTo);

  // copy sorted options from array back to select box
  list.options.length = 0;
  for ( var loopCnt = 0; loopCnt < optionsArray.length; loopCnt++) {
    var option = document.createElement('option');
    option.text = optionsArray[loopCnt].text;
    option.value = optionsArray[loopCnt].value;
    option.id = optionsArray[loopCnt].id;
    list.options.add(option);
  }
  
}

function selectOptions(listName) {
  var list = getElementByIdSafe(listName);
  if (list == null) {
	  return;
  }
	
  list.multiple = true;
  for ( var i = 0; i <= list.length - 1; i++) {
    list.options[i].selected = true;
  }
}

function selectOptionsAll(listName1, listName2) {
	
  var list1 = getElementByIdSafe(listName1);
  var list2 = getElementByIdSafe(listName2);
	
  list1.multiple = true;
  list2.multiple = true;
  
  for ( var i = 0; i <= list1.length - 1; i++) {
	  list1.options[i].selected = true;
  }
  
  for ( var j = 0; j <= list2.length - 1; j++) {
	  list2.options[j].selected = true;
  }
}

function removeOptions(listName) {
	var list = getElementByIdSafe(listName);
	if (list == null) {
		return;
	}
	
	list.options.length = 0;
}