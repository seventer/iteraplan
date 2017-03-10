<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<script type='text/javascript' src="<c:url value="/dwr/interface/BuildingBlockTypeService.js" />"></script>

<c:set var="functionalPermission">
 	<c:set var="technicalComponent" value="technicalComponent.singular" />
	<itera:write name="userContext"
		property="perms.userHasBbTypeFunctionalPermission(${technicalComponent})" escapeXml="false" />
</c:set>

<c:choose>
<c:when test="${functionalPermission == true}">
<c:set var="createPermissionType">
	<itera:write name="userContext"
		property="perms.userHasBbTypeCreatePermission(${technicalComponent})" escapeXml="false" />
</c:set>
<tiles:insertTemplate
	template="/jsp/common/SearchFormAndResults.jsp">
	<tiles:putAttribute name="entitySearchKey" value="technicalComponentRelease.search.heading" />
	<tiles:putAttribute name="entityNamePluralKey" value="technicalComponentRelease.plural" />
	<tiles:putAttribute name="searchCriteria" value="${dialogMemory.criteria}" />
	<tiles:putAttribute name="resultColumnDefinitions" value="${dialogMemory.tableState.visibleColumnDefinitions}" />
	<tiles:putAttribute name="createEnabled" value="${createPermissionType}" />
	<tiles:putAttribute name="subscribable_type" value="true" />
	<tiles:putAttribute name="bbTypeHtmlId" value="technicalcomponent" />
	<tiles:putAttribute name="addManageShortcuts" value="true" />
</tiles:insertTemplate>
</c:when>
<c:otherwise>
  <tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
</c:otherwise>
</c:choose>