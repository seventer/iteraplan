<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="resultPostSelection" value="true" scope="request" />
<c:set var="functionalPermission" value="${userContext.perms.userHasFuncPermGraphReporting}" scope="request" />
<c:set var="permissionCreateReports" value="${userContext.perms.userHasFuncPermGraphReportingCreate}" scope="request" />
<c:set var="permissionSaveReports" value="${userContext.perms.userHasFuncPermGraphReportingFull}" scope="request" />

<h1><fmt:message key="graphicalExport.pieBarDiagram" /></h1>

<tiles:insertTemplate template="/jsp/commonReporting/ShowSavedQuery.jsp">
	<tiles:putAttribute name="isGraphicalReporting" value="true" />
	<tiles:putAttribute name="selectedSavedQueryId" value="${memBean.graphicalOptions.savedQueryInfo.id}" />
 	<tiles:putAttribute name="permissionCreateReports" value="${permissionCreateReports}" />
	<tiles:putAttribute name="permissionSaveReports" value="${permissionSaveReports}" />
	<tiles:putAttribute name="showBuildingBlockType" value="true" />
</tiles:insertTemplate>

<c:if test="${permissionCreateReports == 'true'}">
	<p class="aligned"><fmt:message key="graphicalExport.pieBar.helpDiagramType" /></p>
	<form:select path="graphicalOptions.diagramType" onchange="flowAction('changeDiagramType');">
	  <c:forEach var="type" items="${memBean.graphicalOptions.diagramType.values}">
	    <form:option value="${type}">
	    	<fmt:message key="${type.value}" />
	    </form:option>
	  </c:forEach>
	</form:select>
	
	<hr />
	
	<p class="aligned"><fmt:message key="graphicalExport.pieBar.helpContentType" /></p>
	<form:select path="graphicalOptions.selectedBbType" onchange="flowAction('changeQueryType');">
	  <c:forEach var="available" items="${memBean.graphicalOptions.availableBbTypes}">
	    <form:option value="${available}"><fmt:message key="${available}" /></form:option>
	  </c:forEach>
	</form:select>
	
	<hr />
	
	<fmt:message var="chosenContentType" key="${memBean.graphicalOptions.selectedBbType}"/>
	<p class="aligned">
		<fmt:message key="graphicalExport.cluster.helpStep1"> 
			<fmt:param value="${chosenContentType}" />
		</fmt:message>
	</p>

	<tiles:insertTemplate template="/jsp/GraphicalReporting/GraphicalReportingQueryForm.jsp" flush="true">
		<tiles:putAttribute name="hasPermission" value="${not empty memBean.graphicalOptions.availableBbTypes}"/>
		<tiles:putAttribute name="flowAction" value="goToPieBarExportStep2"/>
	</tiles:insertTemplate>
</c:if>