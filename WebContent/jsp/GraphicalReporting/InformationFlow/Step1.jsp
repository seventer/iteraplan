<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<tiles:insertTemplate template="/jsp/commonReporting/ShowSavedQuery.jsp">
	<tiles:putAttribute name="isGraphicalReporting" value="true" />
	<tiles:putAttribute name="selectedSavedQueryId" value="${memBean.graphicalOptions.savedQueryInfo.id}" />
	<tiles:putAttribute name="permissionCreateReports" value="${permissionCreateReports}" />
	<tiles:putAttribute name="permissionSaveReports" value="${permissionSaveReports}" />
	<tiles:putAttribute name="showBuildingBlockType" value="true" />
</tiles:insertTemplate>

<c:if test="${permissionCreateReports == 'true'}">
	<p class="aligned"><fmt:message key="graphicalExport.informationflow.help" /></p>

	<tiles:insertTemplate template="/jsp/GraphicalReporting/GraphicalReportingQueryForm.jsp" flush="true">
		<tiles:putAttribute name="hasPermission" value="${not empty memBean.graphicalOptions.availableBbTypes}" />
		<tiles:putAttribute name="flowAction" value="goToInformationFlowGraphicExportStep2" />
	</tiles:insertTemplate>
</c:if>
