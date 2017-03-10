<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<script type="text/javascript">
   addTabShortcuts();
</script>

<itera:define id="componentMode" name="memBean" property="componentModel.componentMode" toScope="session"/>  

<c:set var="functionalPermissionUser" value="${userContext.perms.userHasFuncPermUsers}" scope="request" />
<c:set var="permissionLoggedInUser" value="${memBean.componentModel.loggedInUserSelected == true}" scope="request" />
<c:set var="functionalPermission" value="${(functionalPermissionUser == true) || (permissionLoggedInUser == true)}" scope="request" />

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
				<tiles:putAttribute name="userHasFuncPermission" value="${functionalPermissionUser}" />
			</tiles:insertTemplate>
			
			<tiles:insertTemplate template="/jsp/common/ElementNameComponentView.jsp">
				<tiles:putAttribute name="path_to_componentModel" value="componentModel.loginNameModel" />
				<tiles:putAttribute name="prefixKey" value="manageUser.loginName" />
			</tiles:insertTemplate>
			
			<div class="manage">
				<fmt:message key="manageUser.iturm.Users" />
				<a class="cursor" href="<fmt:message key="global.iturm.Link"/>"><fmt:message
					key="global.iturm" /></a><fmt:message key="manageUser.iturm.Users2" />
			</div>
			
			<%-- Insert Table for elementComponent properties --%>
			<tiles:insertTemplate template="/jsp/common/StringComponentInputView.jsp">
				<tiles:putAttribute name="path_to_componentModel" value="componentModel.firstNameModel" />
			</tiles:insertTemplate>
			
			<tiles:insertTemplate template="/jsp/common/StringComponentInputView.jsp">
				<tiles:putAttribute name="path_to_componentModel" value="componentModel.lastNameModel" />
			</tiles:insertTemplate>

			<tiles:insertTemplate template="/jsp/common/StringComponentInputView.jsp">
				<tiles:putAttribute name="path_to_componentModel" value="componentModel.emailModel" />
			</tiles:insertTemplate>
			
			<%-- Insert the Tabs --%>
			<itera:tabgroup id="ConfigTabs">
				<itera:tab id="TabAssignments" 			textKey="button.assignments" 			page="/jsp/User/tabPages/TabAssignments.jsp" />
				<itera:tab id="TabPermissionSummary" 	textKey="global.permissions_summary" 	page="/jsp/User/tabPages/TabPermissionSummary.jsp" />
			</itera:tabgroup>
			
			<%-- Insert the second TransactionBar for save & cancel at the bottom --%>
			<c:if test="${componentMode != 'READ'}">
				<tiles:insertTemplate template="/jsp/common/TransactionBar.jsp">
					<tiles:putAttribute name="showBuildingBlockCopyButton" value="true"/>
					<tiles:putAttribute name="ignoreWritePermission" value="true" />
					<tiles:putAttribute name="userHasFuncPermission" value="${functionalPermissionUser}" />
				</tiles:insertTemplate>
			</c:if>
		</c:when>
		<c:otherwise>
			<tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
		</c:otherwise>
	</c:choose>
</div>