<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<%-- This JSP checks if the user has permission to see the given dialog. --%>

<%-- The name of the dialog --%>
<tiles:useAttribute name="this_dialog"/>

<%-- The message key for the link to this dialog --%>
<tiles:useAttribute name="title_key"/>

<%-- Search start menu URL --%>
<tiles:useAttribute name="url" /> 

<%-- Allows to explicitly override the permission check that is performed for ${this_dialog} --%>
<tiles:useAttribute name="overridePermissionCheckWith" ignore="true"/> 

<%-- Flow start URL --%>
<tiles:useAttribute name="flow_url" ignore="true" />

<%-- Menu point will never shown as active if this is set to true --%>
<tiles:useAttribute name="showNoActive" ignore="true" />

<%-- Menu point will not shown as active if this is set to true and a subflow is active --%>
<tiles:useAttribute name="showNoActiveIfSubflow" ignore="true" /> 

<c:choose>
	<c:when test="${not empty overridePermissionCheckWith}"> 
		<c:set var="permissionGranted" value="${overridePermissionCheckWith}" />
	</c:when>
	<c:otherwise>
		<c:set var="permissionGranted">
			<itera:write name="userContext" property="perms.userHasDialogPermission(${this_dialog})" escapeXml="false" />
		</c:set>
	</c:otherwise>
</c:choose>

<c:if test="${permissionGranted == 'true'}" >

	<%-- Correct display of deviders --%>
	<c:set var="sectionHasEntries" value="true" scope="request" />
	<c:if test="${requireDevider}">
		<li class="divider" />
		<c:set var="requireDevider" value="false" scope="request" />
	</c:if>

	<c:set var="active" value="" />
	<c:if test="${not showNoActive}">
		<c:if test="${guiContext.activeDialog == this_dialog}">
			<c:set var="active" value="active" />
			<c:set var="active_title" value="${title_key}" scope="request"/>
			<c:set var="active_url" value="${url}" scope="request" />
		</c:if>
		
		<c:forEach items="${guiContext.flowEntries[this_dialog]}" var="entry">
			<c:if test="${guiContext.activeDialog == entry.key}">
				<c:if test="${not showNoActiveIfSubflow}">
					<c:set var="active" value="active" />
				</c:if>
				<c:set var="active_title" value="${title_key}" scope="request" />
				<c:set var="active_url" value="${url}" scope="request" />
				<c:set var="active_sub_title" value="${entry.label}" scope="request" />
				<c:set var="active_sub_url" scope="request" ><c:url value="${flow_url}/${entry.entityId}"><c:param name="execution" value="${entry.key}" /></c:url></c:set>
			</c:if>
		</c:forEach>
	</c:if>
	
		
	<li class="<c:out value="${active}"/>">
		<a href="<c:url value="${url}"/>" 
			class="<c:out value="navLink" />" 
			id="<c:out value="menu.${this_dialog}"/>">	
			<fmt:message key="${title_key}" />
		</a>
	</li>
</c:if>