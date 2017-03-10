<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<c:set var="userLink">
	<itera:linkToElement name="dialogMemory" property="author" type="html" />
</c:set>
<fmt:message key="DATE_FORMAT_LONG" var="dateFormat"/>
<c:set var="functionalPermission" value="${userContext.perms.userHasFuncPermGraphReporting}" scope="request" />
    
<c:choose>
	<c:when test="${functionalPermission}">
		<div class="row-fluid">
			<h1><fmt:message key="customDashboard.title" />: ${dialogMemory.customDashboardInstance.name}</h1>
			
			<fmt:message var="bbTPlural" key="${dialogMemory.customDashboardInstance.query.resultBbType.typeOfBuildingBlock.pluralValue}" />
			<tiles:insertTemplate template="/jsp/GraphicalReporting/ShowSelectedElements.jsp">
				<tiles:putAttribute name="messageKey" value="graphicalExport.pieBar.showChosenElements" />
				<tiles:putAttribute name="messageKeyArg" value="${bbTPlural}" />
				<tiles:putAttribute name="collection" value="queryResults" />
				<tiles:putAttribute name="field" value="identityString" />
				<tiles:putAttribute name="memory" value="dialogMemory" />
			</tiles:insertTemplate>
			
			<div class="well">
				<itera:dashboard name="dialogMemory"
					refIdProperty="customDashboardInstance.query.id"
					property="customDashboardInstance.template.content"
					userAgent="${header['User-Agent']}" breaksAndSpaces="true" />
			</div>
			<div class="row-fluid"><fmt:message key="customDashboard.author" />: <a href="${userLink}" ><c:out value="${dialogMemory.customDashboardInstance.author}" /></a></div>
			<div class="row-fluid"><fmt:message key="customDashboard.creationTime" />: <fmt:formatDate value="${dialogMemory.customDashboardInstance.creationTime}" pattern="${dateFormat}" /></div>
		</div>
	</c:when>
	<c:otherwise>
  		<tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
	</c:otherwise>
</c:choose>