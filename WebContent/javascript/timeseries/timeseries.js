function initTimeseriesDialog(attrId) {
	createHiddenField('currentTimeseriesAttributeId', attrId);
	$('#timeseriesDialog').empty();
	timeseriesDialogUpdate();
}

function timeseriesDialogUpdate() {
	$.ajax({
		url : flowExecutionUrl+"&_eventId=timeseriesUpdate&ajaxSource=true",
		type : "post",
		data : $('form:first').serialize(),
		success : function(data) {
			$('#timeseriesDialog').html(data);
			datepickerInit();
		}
	});
}

function triggerTimeseriesEntryAction(position, action) {
	createHiddenField('currentTimeseriesComponentModel.action', action);
	createHiddenField('currentTimeseriesComponentModel.position', position);
	timeseriesDialogUpdate();
	setHiddenField('currentTimeseriesComponentModel.action', '');
	setHiddenField('currentTimeseriesComponentModel.position', '');
}

function closeTimeseriesDialogAndUpdate(componentMode) {
	if (componentMode != 'READ') {
		var newEntryComplete = $("#newTimeseriesEntryComponentModel_date").val() != ''&& $("#newTimeseriesEntryComponentModel_value").val() != '';
		if (newEntryComplete) {
			createHiddenField('currentTimeseriesComponentModel.action', 'add');
		}
		flowAction('update');
	}
}

function getDateRegex(dateFormat) {
	return dateFormat.replace('dd','[0-3]?[0-9]').replace('mm', '[0-1]?[0-9]').replace(/y/g,'[0-9]');
}
