<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<script type="text/javascript">
   addTabShortcuts();
</script>

<c:set var="componentMode" value="${memBean.componentModel.componentMode}" scope="session" />  
<c:set var="functionalPermission" value="${userContext.perms.userHasFuncPermUserGroups}" scope="request" />

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
		<tiles:insertTemplate template="/jsp/common/TransactionBar.jsp">
			<tiles:putAttribute name="showBuildingBlockCopyButton" value="true"/>
			<tiles:putAttribute name="ignoreWritePermission" value="true" />
		</tiles:insertTemplate>
		
		<tiles:insertTemplate template="/jsp/common/ElementNameComponentView.jsp">
			<tiles:putAttribute name="path_to_componentModel" value="componentModel.nameModel" />
		</tiles:insertTemplate>
	
		<tiles:insertTemplate template="/jsp/common/ElementDescriptionComponentView.jsp">
			<tiles:putAttribute name="path_to_componentModel" value="componentModel.descriptionModel" />
		</tiles:insertTemplate>
		
		<%-- Insert the Tabs --%>
		<itera:tabgroup id="ConfigTabs">
			<itera:tab id="TabAssignments" 			textKey="button.assignments" 			page="/jsp/UserGroup/tabPages/TabAssignments.jsp" />
			<itera:tab id="TabPermissionSummary" 	textKey="global.permissions_summary" 	page="/jsp/UserGroup/tabPages/TabPermissionSummary.jsp" />
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