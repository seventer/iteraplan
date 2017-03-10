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

<%-- Flow start URL --%>
<tiles:useAttribute name="flow_url" ignore="true" /> 

<c:set var="permissionGranted">
	<itera:write name="userContext" property="perms.userHasDialogPermission(${this_dialog})" escapeXml="false" />
</c:set>

<%-- Everyone can Restart --%>
<c:if test="${this_dialog == 'Restart'}">
	<c:set var="permissionGranted" value="true" />
</c:if>

<c:if test="${permissionGranted == 'true'}" >

	<%-- Correct display of deviders --%>
	<c:set var="sectionHasEntries" value="true" scope="request" />
	<c:if test="${requireDevider}">
		<li class="divider" />
		<c:set var="requireDevider" value="false" scope="request" />
	</c:if>
	
	<c:set var="active" value="" />
	<c:forEach items="${guiContext.flowEntries[this_dialog]}" var="entry">
		<c:if test="${guiContext.activeDialog == entry.key}">
			<c:set var="active" value="active" />
			<c:set var="active_title" value="${title_key}" scope="request"/>
			<c:set var="active_url" value="${flow_url}" scope="request"/>
		</c:if>
	</c:forEach>
	
	<c:set var="count" value="${fn:length(guiContext.flowEntries[this_dialog])}"/>
	
	<c:choose>
		<%-- If no flow is open yet, show simple link to start a flow --%>
		<c:when test="${count <= 0}">
 			<li class="<c:out value="${active}"/>">
	            <a href="<c:url value="${flow_url}"></c:url>"
					class="<c:out value="navLink" />"
	                id="<c:out value="menu.${this_dialog}"/>"
	                onclick="<c:out value="${java_script}" />" >
				  <fmt:message key="${title_key}" />
				</a> 
			</li>
			
		</c:when>
		<c:otherwise>
		
			<%-- for each flow entry show a link to the flow --%>
			<c:forEach items="${guiContext.flowEntries[this_dialog]}" var="entry">
				<li class="<c:out value="${active}"/>">
					<a href="<c:url value="${flow_url}"><c:param name="execution" value="${entry.key}" /></c:url>"
				    	class="<c:out value="navLink" />"
				        id="<c:out value="menu.${this_dialog}.${entry.key}"/>" >
			          <fmt:message key="${title_key}" />
					</a>
				</li>
			</c:forEach>
					
		</c:otherwise>
	</c:choose>
</c:if>