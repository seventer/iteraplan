<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<c:set var="resultPostSelection" value="true" scope="request" />
<c:set var="functionalPermission" value="${userContext.perms.userHasFuncPermGraphReporting}" scope="request" />
<c:set var="permissionCreateReports" value="${userContext.perms.userHasFuncPermGraphReportingCreate}" scope="request" />
<c:set var="permissionSaveReports" value="${userContext.perms.userHasFuncPermGraphReportingFull}" scope="request" />

<c:set var="dialogStep" value="${memBean.graphicalOptions.dialogStep}" />

<c:set var="chosenContentType">
  <fmt:message>
    <itera:write name="memBean" property="graphicalOptions.selectedBbType" escapeXml="false" />
  </fmt:message>
</c:set>

<c:set var="timeseriesAttributesActive" value="${memBean.graphicalOptions.timeseriesAttributesActive}"/>

<c:choose>
<c:when test="${functionalPermission == true}">
	<%-- adds the shortcut keys to quickly perform actions such as going back, 
	     sending a query or generating a graphical report --%>
	     
	<script type="text/javascript">
		addMiscShortcuts();
	</script>
 	
    <c:choose>
    <c:when test="${timeseriesAttributesActive == true}">
		<tiles:insertTemplate template="/jsp/GraphicalReporting/DiagramBreadcrumb.jsp" flush="true">
			<tiles:putListAttribute name="state_keys">
				<tiles:addAttribute value="graphicalExport.line.breadcrumbs.chooseElements" />
				<tiles:addAttribute value="graphicalExport.line.breadcrumbs.configuration" />
			</tiles:putListAttribute>
			<tiles:putListAttribute name="state_events">
				<tiles:addAttribute value="goToLineExportStep1" />
				<tiles:addAttribute value="goToLineExportStep2" />
			</tiles:putListAttribute>
			<tiles:putAttribute name="current_state_index" value="${dialogStep - 1}" />
		</tiles:insertTemplate>
	
		<h1><fmt:message key="graphicalExport.lineDiagram"/></h1>

		<c:choose>
		<c:when test="${dialogStep == 1}">
			<tiles:insertTemplate template="Step1.jsp" />
		</c:when>
		<c:when test="${(dialogStep == 2) && (permissionCreateReports == true)}">
    		<tiles:insertTemplate template="Step2.jsp" />
		</c:when>
		<c:otherwise>
  			<tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
  		</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<div class="alert alert-error">
			<h3><fmt:message key="graphicalExport.line.Error.Title" /></h3>
			<p><fmt:message key="graphicalExport.line.Error.Content" /></p>
		</div>
	</c:otherwise>
	</c:choose>	
</c:when>
<c:otherwise>
	<h1>false</h1>
  	<tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
</c:otherwise>
</c:choose>
