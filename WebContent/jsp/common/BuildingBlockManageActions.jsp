<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<tiles:useAttribute name="bb" />
<tiles:useAttribute name="updatePermissionType" />
<tiles:useAttribute name="createPermissionType" />
<tiles:useAttribute name="deletePermissionType" />

<itera:checkBbInstancePermission2 name="bb" property="owningUserEntities" result="writePermissionInstance" userContext="userContext" />

<c:if test="${writePermissionInstance and (bb.identityString != '-')}">
	<c:set var="link">
		<itera:linkToElement name="bb" type="html" />
	</c:set>

	<c:if test="${updatePermissionType}">
		<a href="javascript:changeLocation('${link}?_eventId=edit');" title="<fmt:message key="button.edit" />"> <i class="icon-pencil"></i>
		</a>
	</c:if>
	<c:if test="${createPermissionType}">
		<a href="javascript:changeLocation('${link}?_eventId=copyBB');" title="<fmt:message key="button.copy" />"> <i class="icon-share"></i>
		</a>
	</c:if>
	<c:if test="${deletePermissionType}">
		<a href="javascript:confirmDeleteBuildingBlocks(function(){changeLocation('${link}?_eventId=delete')});"
			title="<fmt:message key="button.delete" />"> <i class="icon-trash"></i>
		</a>
	</c:if>
</c:if>