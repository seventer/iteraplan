<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type="text/javascript">
   addTabShortcuts();
</script>

<itera:define id="componentMode" name="memBean" property="componentModel.componentMode" toScope="request" />

<c:set var="functionalPermission" value="${userContext.perms.userHasFuncPermRoles}" scope="request" />

<%-- set a different class for read and write mode --%>
<c:choose>
	  <c:when test="${(componentMode != 'READ')}">
	    <c:set var="componentModeClass" value="write" />
	  </c:when>
	  <c:otherwise>
	      <c:set var="componentModeClass" value="read" />
	  </c:otherwise>
</c:choose>

<div class="${componentModeClass}">
	<c:choose>
		<c:when test="${functionalPermission == true}">
			
			<%--Start construction of page --%>
			<tiles:insertTemplate template="/jsp/common/TransactionBar.jsp" >
				<tiles:putAttribute name="showBuildingBlockCopyButton" value="true"/>
				<tiles:putAttribute name="ignoreWritePermission" value="true" />
			</tiles:insertTemplate>	
			
			<tiles:insertTemplate template="/jsp/common/ElementNameComponentView.jsp" flush="true">
			    <tiles:putAttribute name="path_to_componentModel" value="componentModel.nameModel" />
			  	<tiles:putAttribute name="validate" value="false" />
			</tiles:insertTemplate>
			
			<p>
			  <fmt:message key="manageRoles.iturm.Roles"/>
			  	<a style="cursor:help;" href="<fmt:message key="global.iturm.Link"/>">
			  		<fmt:message key="global.iturm"/>
			  	</a>
			  <fmt:message key="manageRoles.iturm.Roles2"/>
			</p>
			  
			<tiles:insertTemplate template="/jsp/common/ElementDescriptionComponentView.jsp">
				<tiles:putAttribute name="path_to_componentModel" value="componentModel.descriptionModel" />
			</tiles:insertTemplate>
			  
			<%-- Insert the Tabs --%>
			<itera:tabgroup id="ConfigTabs">
				<itera:tab id="TabHierarchy" 		textKey="global.hierarchy" 				page="/jsp/Role/tabPages/TabHierarchy.jsp" />
				<itera:tab id="TabPermissions" 		textKey="manageRoles.tab.permissions" 	page="/jsp/Role/tabPages/TabPermissions.jsp" />
				<itera:tab id="TabPermissionSummary" textKey="global.permissions_summary" 	page="/jsp/Role/tabPages/TabPermissionSummary.jsp" />
			</itera:tabgroup>
			
						<%-- Insert the second TransactionBar for save & cancel at the bottom --%>
			<c:if test="${componentMode != 'READ'}">
				<tiles:insertTemplate template="/jsp/common/TransactionBar.jsp">
					<tiles:putAttribute name="showBuildingBlockCopyButton" value="true"/>
					<tiles:putAttribute name="ignoreWritePermission" value="true" />
				</tiles:insertTemplate>
			</c:if>
		</c:when>
		<c:otherwise>
			<tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
		</c:otherwise>
	</c:choose>
</div>