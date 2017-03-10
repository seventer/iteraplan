<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="chosenContentType">
  <fmt:message>
    <itera:write name="memBean" property="graphicalOptions.selectedBbType" escapeXml="false" />
  </fmt:message>
</c:set>

<tiles:insertTemplate template="/jsp/commonReporting/ShowSavedQuery.jsp">
	<tiles:putAttribute name="isGraphicalReporting" value="true" />
	<tiles:putAttribute name="selectedSavedQueryId" value="${memBean.graphicalOptions.savedQueryInfo.id}" />
	<tiles:putAttribute name="permissionCreateReports" value="${permissionCreateReports}" />
	<tiles:putAttribute name="permissionSaveReports" value="${permissionSaveReports}" />
	<tiles:putAttribute name="showBuildingBlockType" value="true" />
</tiles:insertTemplate>

<c:if test="${permissionCreateReports == 'true'}" >
	<p class="aligned"><fmt:message key="graphicalExport.masterplan.helpContentType"/></p>
	
	<form:select path="graphicalOptions.selectedBbType" onchange="flowAction('selectMasterplanType');">
	
	  <itera:define id="availableTypes_array" name="memBean" property="graphicalOptions.availableBbTypes" />
	  <c:forEach items="${availableTypes_array}" var="available" varStatus="countStatus">
	    <form:option value="${available}">
	      <fmt:message key="${available}" />
	    </form:option>
	  </c:forEach>
	</form:select>
	
	<hr />
	
	<p class="aligned">
		<fmt:message key="graphicalExport.masterplan.helpStep1">
			<fmt:param>${chosenContentType}</fmt:param>
		</fmt:message>
	</p>
	
	<tiles:insertTemplate template="/jsp/GraphicalReporting/GraphicalReportingQueryForm.jsp" flush="true">
		<tiles:putAttribute name="hasPermission" value="${not empty availableTypes_array}"/>
		<tiles:putAttribute name="flowAction" value="goToMasterplanExportStep2"/>
	</tiles:insertTemplate>
</c:if>
	