<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%> 
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="resultPostSelection" value="true" scope="request" />
<c:set var="functionalPermission" value="${userContext.perms.userHasFuncPermGraphReporting}" scope="request" />
<c:set var="permissionCreateReports" value="${userContext.perms.userHasFuncPermGraphReportingCreate}" scope="request" />
<c:set var="permissionSaveReports" value="${userContext.perms.userHasFuncPermGraphReportingFull}" scope="request" />

<%-- adds the shortcut keys to quickly perform actions such as going back, 
     sending a query or generating a graphical report --%>
<script type="text/javascript">
	addMiscShortcuts();

	function removePartReport(index) {
		setHiddenField('partReportIndexToRemove', index);
		flowAction('removeReportPart');
	}
</script>

<c:choose>
<c:when test="${functionalPermission == true}">
	<h1><fmt:message key="graphicalExport.compositeDiagram" /></h1>
	
	<tiles:insertTemplate template="/jsp/commonReporting/ShowSavedQuery.jsp">
		<tiles:putAttribute name="isGraphicalReporting" value="true" />
		<tiles:putAttribute name="selectedSavedQueryId" value="${memBean.compositeOptions.savedQueryInfo.id}" />
		<tiles:putAttribute name="permissionCreateReports" value="${permissionCreateReports}" />
		<tiles:putAttribute name="permissionSaveReports" value="${permissionSaveReports}" />
		<tiles:putAttribute name="showBuildingBlockType" value="false" />
	</tiles:insertTemplate>

	<c:if test="${permissionCreateReports == 'true'}" >
	  	<h3><fmt:message key="graphicalExport.composite.helpDescription" /></h3>
	  	<table class="table table-bordered table-striped table-condensed" id="selectedReportParts">
			<colgroup>
				<col class="col-ico" />
				<col class="col-name-small" />
				<col class="col-name" />
				<col class="col-desc" />
				<col class="col-name-small" />
			</colgroup>
  			<thead>
	  			<tr>
		  			<th colspan="5">
		  				<form:checkbox path="checkAllBox" id="checkAllBox" value="" onclick="checkUnCheckAll(document.getElementsByName('compositeOptions.selectedSavedQueryIds'), this);flowAction('refreshPage');" />						
			  			&nbsp;<fmt:message key="reports.selectAll" />
		  			</th>
	  			</tr>
  			</thead>
  			<tbody>
  			<%-- show selected queries --%>
			<c:forEach var="selectedQuery" varStatus="loop" items="${memBean.selectedPartQueries}">
				<tr>
					<td class="checkboxintable" nowrap="nowrap" align="center" valign="top" id="savedQuery<c:out value='${selectedQuery.id}' />Row">
						<form:checkbox path="compositeOptions.selectedSavedQueryIds" value="${selectedQuery.id}"
								onclick="updateCheckAllBox(document.getElementsByName('compositeOptions.selectedSavedQueryIds'), document.getElementById('checkAllBox'));flowAction('refreshPage');" /> 
					</td>
					<td><fmt:message key="${memBean.typeToMessageKey[selectedQuery.type.value]}" /></td>
					<td><c:out value="${selectedQuery.name}" escapeXml="true" /></td>
					<td><c:out value="${selectedQuery.description}" escapeXml="true" /></td>
		     		<td align="center">
						<img src="<c:url value="/images/SortArrowTop.gif"/>" alt="TOP" class="link"
							onclick="setHiddenField('compositeOptions.movedItem', ${loop.index});setHiddenField('compositeOptions.move', 1);flowAction('refreshPage');" />
						<img src="<c:url value="/images/SortArrowUp.gif"/>" alt="UP" class="link"
							onclick="setHiddenField('compositeOptions.movedItem', ${loop.index});setHiddenField('compositeOptions.move', 2);flowAction('refreshPage');" />
						<img src="<c:url value="/images/SortArrowDown.gif"/>" alt="DOWN" class="link"
							onclick="setHiddenField('compositeOptions.movedItem', ${loop.index});setHiddenField('compositeOptions.move', 3);flowAction('refreshPage');" />
						<img src="<c:url value="/images/SortArrowBottom.gif"/>" alt="BOTTOM" class="link"
							onclick="setHiddenField('compositeOptions.movedItem', ${loop.index});setHiddenField('compositeOptions.move', 4);flowAction('refreshPage');" />
					</td>
				</tr>
			</c:forEach>
  			<%-- show not selected queries --%>
			<c:forEach var="notSelectedQuery" varStatus="loop" items="${memBean.notSelectedPartQueries}">
				<tr>
					<td class="checkboxintable" nowrap="nowrap" align="center" valign="top" id="savedQuery<c:out value='${selectedQuery.id}' />Row">
						<form:checkbox path="compositeOptions.selectedSavedQueryIds" value="${notSelectedQuery.id}"
								onclick="updateCheckAllBox(document.getElementsByName('compositeOptions.selectedSavedQueryIds'), document.getElementById('checkAllBox'));flowAction('refreshPage');" /> 
					</td>
					<td><fmt:message key="${memBean.typeToMessageKey[notSelectedQuery.type.value]}" /></td>
					<td><c:out value="${notSelectedQuery.name}" escapeXml="true" /></td>
					<td><c:out value="${notSelectedQuery.description}" escapeXml="true" /></td>
					<td>&nbsp;</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>

		<input type="button" id="refreshButton" class="link btn"
			value="<fmt:message key="button.refresh" />"
			onclick="flowAction('refreshAvailablePartQueries');" />
		<label class="checkbox">
			<form:checkbox path="compositeOptions.showSavedQueryInfo" id="showSavedQueryInfo" />
			<fmt:message key="graphicalExport.showQueryInfo" />
		</label>
	</c:if>

	<tiles:insertTemplate template="/jsp/GraphicalReporting/RequestButtons.jsp">
		<tiles:putAttribute name="permissionSaveReports" value="${permissionSaveReports}" />
		<tiles:putAttribute name="availableGraphicFormats" value="${memBean.compositeOptions.availableGraphicFormats}" />
		<tiles:putAttribute name="exportFormatPath" value="compositeOptions.selectedGraphicFormat" />
	</tiles:insertTemplate>
	
	<form:hidden path="compositeOptions.movedItem" />
	<form:hidden path="compositeOptions.move" />
</c:when>
<c:otherwise>
  	<tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
</c:otherwise>
</c:choose>