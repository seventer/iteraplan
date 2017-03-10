<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<c:set var="resultPostSelection" value="true" scope="request" />
<c:set var="functionalPermission" value="${userContext.perms.userHasFuncPermGraphReporting}" scope="request" />
<c:set var="permissionCreateReports" value="${userContext.perms.userHasFuncPermGraphReportingCreate}" scope="request" />
<c:set var="permissionSaveReports" value="${userContext.perms.userHasFuncPermGraphReportingFull}" scope="request" />

<c:set var="dialogStep" value="${memBean.graphicalOptions.dialogStep}" />
<c:set var="iteraQlEnabled" value="${memBean.graphicalOptions.iteraQlEnabled}" />

<h1><fmt:message key="graphicalExport.matrixDiagram" /></h1>

<c:choose>
	<c:when test="${functionalPermission == true}">
		<c:choose>
			<c:when test="${iteraQlEnabled}">
				<tiles:insertTemplate template="/jsp/commonReporting/ShowSavedQuery.jsp">
					<tiles:putAttribute name="isGraphicalReporting" value="true" />
					<tiles:putAttribute name="selectedSavedQueryId" value="${memBean.graphicalOptions.savedQueryInfo.id}" />
					<tiles:putAttribute name="permissionCreateReports" value="${permissionCreateReports}" />
					<tiles:putAttribute name="permissionSaveReports" value="${permissionSaveReports}" />
					<tiles:putAttribute name="showBuildingBlockType" value="false" />
				</tiles:insertTemplate>
				<c:if test="${permissionCreateReports == 'true'}" >
					<tiles:insertTemplate template="Configuration.jsp" />
					<tiles:insertTemplate template="/jsp/GraphicalReporting/RequestButtons.jsp">
						<tiles:putAttribute name="permissionSaveReports" value="${permissionSaveReports}" />
						<tiles:putAttribute name="availableGraphicFormats" value="${memBean.graphicalOptions.availableGraphicFormats}" />
						<tiles:putAttribute name="exportFormatPath" value="graphicalOptions.selectedGraphicFormat" />
					</tiles:insertTemplate>
				</c:if>
			</c:when>
			<c:otherwise>
				<div class="alert"><p><fmt:message key="ITERAQL_NOT_ENABLED" /></p></div>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
	 	<tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
	</c:otherwise>
</c:choose>