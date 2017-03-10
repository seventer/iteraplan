/*
  This code was originally developed by Justin Whitford.
  See his article at: http://www.whitford.id.au/webmonkey/code/dropdown.php
 
  Usage:
    filterList(pattern, list)
    pattern: 
      a string of zero or more characters by which to filter the list
    list: 
      reference to a form object of type, select

  Example:
  <form name="yourForm">
    <input type="text" name="yourTextField"
       onchange="filtery(this.value,this.form.yourSelect)">
    <select name="yourSelect">
      <option></option>
      <option value="Australia">Australia</option>
       .......
*/
function filterList(pattern, list){
	var myList = getElementByIdSafe(list);
	var numSeparators = 0;
	var noMatches = false;
	var selectedIndex = 0;
	var selectedString = false;		
  
  	// If the passed-in list hasn't already been backed up, we'll do that now.
  	if (!myList.bak){
		// We're going to attach an array to the select object
    	// where we'll keep a backup of the original drop-down list.
    	myList.bak = new Array();
    	for (var n = 0; n < myList.length; n++){
      		var option = myList[n];
			myList.bak[myList.bak.length] = new Array(option.value, option.text);
    	}
  	}

  	// We're going to iterate through the backed up drop-down list. 
  	// If an item matches, it is added to the list of matches. 
  	// If not, then it is added to the list of non matches.
  	var backupListLength = myList.bak.length;
  	var match = new Array();
  	for (var n = 0; n < backupListLength; n++){
    	// Count the number of separation strings.
    	var backupElement = myList.bak[n];
		if(backupElement[1].charAt(0) == '<') {
    		numSeparators++;		
    	}
    	
    	if(backupElement[1].toLowerCase().indexOf(pattern.toLowerCase())!=-1){
      		match[match.length] = new Array(backupElement[0], backupElement[1]);
    	}
    	else{    		
    		// Keep the separation strings.
    		if(backupElement[1].charAt(0) == '<') {
    			match[match.length] = new Array(backupElement[0], backupElement[1]);
    		}
    	}
  	}
  	
  	// If there are only separators contained in the array
  	// of matches, disable the select box.
  	var matchesLength = match.length; 
  	if(matchesLength == numSeparators) {
  		noMatches = true;
  	}
  
  	// Disable drop-down list.
  	if(matchesLength == 0 || noMatches) {
    	myList.length = 1;
    	myList[0].value = -1;
    	myList[0].text = "-";
    	myList.disabled = true;
  	}
  	else {
    
    	// Set the length of the select box to the length of 
  		// the array of matching elements. That way, only
  		// matching elements are displayed.
  		myList.innerHTML = "";
    	
    	// Enable the select box just in case it was disabled.
    	myList.disabled = false;
    
    	// Now we completely rewrite the select box's options.
    	// We write in only the matches.
    	var fragment = document.createDocumentFragment();
    	for (var n = 0; n < matchesLength; n++){
    		var matchData = match[n];
    		var option = document.createElement('option');
            var text = document.createTextNode(matchData[1]);
            option.appendChild(text);
            option.setAttribute('value',matchData[0]);
            fragment.appendChild(option);
    		
      		// Remove styles in case the select box contains 
      		// separation strings.
    		option.style.color = '#000000';
    		option.style.fontWeight = 'normal';
      
      		// Restore the styles for separation strings.
      		if(matchData[1].charAt(0) == '<') {
      			//selectedIndex = n;
      			option.style.fontWeight = 'bold';
      			option.style.color = '#555555';
      		}
      		else { 
      			// Remember the first occurence of a non-separating string.
      			if(!selectedString) {
      				selectedIndex = n;
      				selectedString = true;
      			}
      		}
    	}
    	myList.appendChild(fragment);
  	}

  	// Finally, we make the 1st item selected - 
  	// this makes sure that the matching options are
  	// immediately apparent.
  	myList.selectedIndex = selectedIndex;
}