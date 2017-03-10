<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="resultFormatCount" value="${fn:length(memBean.availableResultFormats)}"/>

<%--
		@UsedFor 	tabularReporting: Dropdown to change the result format.
		@UsedFrom	/commonReporting/GeneralQueryForm.jsp
		@Note		
 --%>

<%-- commented since no longer needed with new add column format 
<script type="text/javascript"> 
//<![CDATA[
 function columnSelectionDisabled(selectReport,selectColumn,addColumn){	
	 selectReport = getElementByIdSafe(selectReport);
	 selectColumn = getElementByIdSafe(selectColumn);
	 addColumn = getElementByIdSafe(addColumn);
   for(i=0;i<selectReport.options.length;i++) {
		if (selectReport.options[i].selected) {
			if (selectReport.options[i] != "reports_exportHTML") {
				selectColumn.disabled = true;
				addColumn.style.visibility = "hidden";
				break;
			} else {
				selectColumn.disabled = false;
				addColumn.style.visibility = "visible";
			}
		}
	} 
 }
</script>
--%>
<c:if test="${resultFormatCount > 1}">
	<div id="TabularReportingResultFormat" class="row-fluid module">
		<div class="row-fluid">
			<div class="module-body">
				<div class="row-fluid">
					<fmt:message key="reports.resultFormat" />
					&nbsp;
					<form:select path="tabularOptions.resultFormat" onchange="flowAction('refreshReport');">
				    	<c:forEach var="availableResultFormat" items="${memBean.availableResultFormats}">
				        	<c:if test="${availableResultFormat.visible}">
				            	<form:option value="${availableResultFormat.presentationKey}">
				                	<fmt:message key="${availableResultFormat.presentationKey}" />
				              	</form:option>
				            </c:if>
				        </c:forEach>
			     	</form:select>
			     	
<%-- commented since no longer needed with new add column behavior (button) 
					<script type="text/javascript"> 
						addEvent(window, 'load', function() {
							columnSelectionDisabled('tabularOptions.resultFormat','selectedNewColumn','addSelectColumn');
						}, false);
					</script>
--%>
				</div>
				<c:if test="${(memBean.tabularOptions.resultFormat eq 'reports_export_Excel2003') || (memBean.tabularOptions.resultFormat eq 'reports_export_Excel2007')}">
					<div class="row-fluid">
						<fmt:message key="reports.template" />
						&nbsp;
						<form:select path="tabularOptions.resultFormatTemplate" cssStyle="width:50em;" >
				        	<c:forEach var="availableTemplate" items="${memBean.tabularOptions.availableResultFormatTemplates}">
				            	<c:if test="${availableTemplate.visible}">
				            		<form:option value="${availableTemplate.presentationKey}"/>
				            	</c:if>
				        	</c:forEach>
			     		</form:select>
					</div>
				</c:if>
			</div>
		</div>
	</div>
</c:if>