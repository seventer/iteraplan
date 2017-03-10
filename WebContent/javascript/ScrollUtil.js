function resetScrollCoordinates() {
	setHiddenField('pagePositionXId', 0);
	setHiddenField('pagePositionYId', 0);
}

function saveScrollCoordinates() {
	var scrOfX = 0, scrOfY = 0;
	if( typeof( window.pageYOffset ) == 'number' ) {
		scrOfY = window.pageYOffset;
		scrOfX = window.pageXOffset;
	} 
	else if( document.body && ( document.body.scrollLeft || document.body.scrollTop ) ) {
		scrOfY = document.body.scrollTop;
		scrOfX = document.body.scrollLeft;
	} 
	else if( document.documentElement &&
			( document.documentElement.scrollLeft || document.documentElement.scrollTop ) ) {
		scrOfY = document.documentElement.scrollTop;
		scrOfX = document.documentElement.scrollLeft;
	}
	setHiddenField('pagePositionXId', scrOfX);
	setHiddenField('pagePositionYId', scrOfY);
	return false;
}

function scrollToCoordinates() {
	var scrOfX = getElementByIdSafe('pagePositionXId').value;
	var scrOfY = getElementByIdSafe('pagePositionYId').value;
	window.scrollTo(scrOfX, scrOfY);
}