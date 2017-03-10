<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%-- 
  The path to the component model that provides the values for this page. 
--%>
<tiles:useAttribute name="path_to_componentModel" />

<%-- Set the variables for the table header. --%>
<itera:define id="componentModel" name="memBean" property="${path_to_componentModel}" />

<c:set var="numberOfPermissionTypes" value="${fn:length(componentModel.columnHeaderKeys)}"/>

<div id="PermissionMatrixViewModul" class="row-fluid module">
	<div class="module-heading">
		<fmt:message key="${componentModel.tableHeaderKey}"/>
	</div>
	<div class="row-fluid">
		<div class="module-body-table">
			<div class="row-fluid">
				<table class="table table-striped table-condensed tableInModule">
					<colgroup>
						<col style="width:20%;" />
					</colgroup>
					<colgroup>
						<col style="width: ${80/numberOfPermissionTypes}%;" span="${numberOfPermissionTypes}" />
					</colgroup>
					<thead>
						<tr>
							<th />
							<c:forEach items="${componentModel.columnHeaderKeys}" var="columnHeaderKey">
								<th>
									<fmt:message key="${columnHeaderKey}" />
								</th>
							</c:forEach>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${componentModel.rows}" var="row" varStatus="rowStatus">
							<tr>
								<td>
									<fmt:message key="${row.htmlId}" />
								</td>
								<c:set var="className" value=""/>
								<c:set var="onclick" value=""/>
								<c:if test="${row.firstRequiredForOthers}">
									<c:set var="className" value="firstRequiredForOthers"/>
									<c:set var="onclick" value="firstRequiredForOthers(${rowStatus.index});"/>
								</c:if>
								<c:forEach items="${row.cells}" var="cell" varStatus="cellStatus">
									<c:set var="onclick2" value=""/>
									<c:if test="${cellStatus.index == 0}">
										<c:set var="onclick2" value="checkAllIfFirst(${rowStatus.index});"/>
									</c:if>
									<td align="center">
										<c:choose>
											<c:when test="${componentMode != 'READ'}">
												<form:checkbox class="${className} permissionRowElement_${rowStatus.index} permissionColumnElement_${cellStatus.index}"
													path="${path_to_componentModel}.rows[${rowStatus.index}].cells[${cellStatus.index}].value" onclick="${onclick} ${onclick2}" />
											</c:when>
											<c:when test="${cell.value}">
			  	  								&#10003;
											</c:when>
										</c:choose>
									</td>
								</c:forEach>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>

<c:if test="${componentMode != 'READ'}">
	<script type="text/javascript">
	// <![CDATA[
	    var numberOfRows = ${fn:length(componentModel.rows)};
	    var firstTimeClicked = new Array(numberOfRows);
	    
	    $(document).ready(function() {
	    	for (var i = 0; i < numberOfRows; i++){
	    		firstTimeClicked[i] = false;

	    		// build the class name to first chechbox in a row
	    		var className = ".permissionRowElement_" + i;
				var firstCheckBox = $(className + ".permissionColumnElement_0").first();

				// read the value of the onclick attribute in the first chechbox in the row "i"
				var onClickValue = $(firstCheckBox).attr('onclick');

				// deselect the checkboxes that can not be selected
				// only those which have the function "firstRequiredForOthers" as onclick-value
	    		if (onClickValue.indexOf('firstRequiredForOthers') >=0 ){
	    			firstRequiredForOthers(i);
				};
	    	 };
	    });
	    
		function firstRequiredForOthers(index) {
			var className = ".permissionRowElement_" + index;
			var firstCheckBox = $(className + ".permissionColumnElement_0").first();
			if (firstCheckBox.prop('checked')) {
				$(className).each(function() {
					$(this).attr('disabled', false);
				});
			} else {
				$(className).each(function() {
					$(this).prop('checked', false);
					$(this).attr('disabled', true);
				});
				firstCheckBox.attr('disabled', false);
			};
		}
		
		function checkAllIfFirst (index) {
			var className = ".permissionRowElement_" + index;
			var firstCheckBox = $(className + ".permissionColumnElement_0").first();
			if (firstCheckBox.prop('checked') && !firstTimeClicked[index]){
				$(className).each(function() {
					$(this).prop('checked', true);
				});
			};
			firstTimeClicked[index] = true;
		};
	
	// ]]>
	</script>
</c:if>