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

<tiles:useAttribute name="rest_mapping"/>	

<tiles:useAttribute name="subflow_base_id"/>

<tiles:useAttribute name="queryType"/>

<tiles:useAttribute name="queryType2" ignore="true"/>

<%-- Flow start URL --%>
<c:set var="flow_url" value="${rest_mapping}${subflow_base_id}" /> 

<c:set var="subflowId" value="${subflow_base_id}/start" />

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
		<c:if test="${guiContext.activeDialog == entry.key && entry.flowId == subflowId}">
			<c:set var="active" value="active" />
			<c:set var="active_sub_title" scope="request" ><fmt:message key="${title_key}"/></c:set>
			<c:set var="active_sub_url" scope="request" ><c:url value="${flow_url}"><c:param name="execution" value="${entry.key}" /></c:url></c:set>
		</c:if>
	</c:forEach>
	
	<script type="text/javascript">
	//<![CDATA[
	    var ${queryType}_Loaded = false;
	//]]>
	</script>
	<li class="dropdown <c:out value="${active}"/>" onmouseover="
				if(!${queryType}_Loaded) {
					loadSavedQueries('${queryType}', '${queryType}_dropdown', '<c:url value="/show/${subflow_base_id}" />');
					<c:if test="${not empty queryType2}">loadSavedQueries('${queryType2}', '${queryType}_dropdown', '<c:url value="/show/${subflow_base_id}" />');</c:if>
				}
				${queryType}_Loaded = true;
			">
		
		<c:set var="flowWasHitAlready" value="false" />
		<c:forEach items="${guiContext.flowEntries[this_dialog]}" var="entry">
			<c:if test="${not flowWasHitAlready && entry.flowId == subflowId}">
				<c:set var="flowWasHitAlready" value="true" />
				
				<a href="<c:url value="${flow_url}"><c:param name="execution" value="${entry.key}" /></c:url>"
			    	class="navLink"
			        id="<c:out value="menu.${title_key}"/>" >
		          <fmt:message key="${title_key}" />
				</a>
			</c:if>
		</c:forEach>
		
		<%-- If no flow is open yet, show simple link to start a flow --%>
		<c:if test="${flowWasHitAlready == false}">
			<a href="<c:url value="${flow_url}"></c:url>"
				class="navLink"
				id="<c:out value="menu.${title_key}"/>"
				onclick="<c:out value="${java_script}" />" >
		  		<fmt:message key="${title_key}" />
			</a> 
		</c:if>
		
		<ul class="dropdown-menu" id="${queryType}_dropdown">
			<%-- content will be loaded with ajax --%>
		</ul>
	</li>
	
</c:if>