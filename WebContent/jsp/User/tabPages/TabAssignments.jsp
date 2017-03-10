<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<%-- User Groups --%>
<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxView.jsp" flush="true">
  <tiles:putAttribute name="path_to_componentModel" value="componentModel.userGroupModel"/>
  <tiles:putAttribute name="header_postfix_edit" value="&nbsp;&nbsp;<sup>1)</sup>"/>
</tiles:insertTemplate>

<%-- Checks if the user is allowed to change the datasource. --%>
<c:set var="isAllowedToChangeDataSource">
  <c:out value="${userContext.perms.userHasFuncPermDatasources}" />
</c:set>

<%-- Checks if the currently logged-in user is selected. --%>
<c:set var="isLoggedInUserSelected">
  <c:out value="${memBean.componentModel.loggedInUserSelected}" />
</c:set>

<%-- Datasources --%>
<c:if test="${isAllowedToChangeDataSource}">
  <tiles:insertTemplate template="/jsp/common/RoutingDatasourceComponentComboboxView.jsp" flush="true">
    <tiles:putAttribute name="pathToComponentModel" value="componentModel.routingDatasourceModel"/>
    <tiles:putAttribute name="loggedInUserSelected" value="${isLoggedInUserSelected}" />
    <tiles:putAttribute name="backingBean" value="memBean" />
  </tiles:insertTemplate>
</c:if>

<c:if test="${componentMode != 'READ'}">
	<br/>
		<sup>1)</sup> <fmt:message key="messages.usersNeedToLoginAgain"/>
  	<br/>
  <c:if test="${isLoggedInUserSelected}">
    <br/>
    	<sup>2)</sup> <fmt:message key="routing.dataSourceChangeNotAllowed"/>
    <br/>
  </c:if>  
</c:if>