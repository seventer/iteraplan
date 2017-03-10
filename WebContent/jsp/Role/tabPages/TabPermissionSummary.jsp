<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<c:if test="${memBean.componentModel.componentMode == 'EDIT'}">
  <fmt:message key="messages.permissionSummaryEdit"/>
</c:if>

<c:if test="${memBean.componentModel.componentMode == 'READ'}">

  <%-- Aggregated child roles --%>
  <tiles:insertTemplate template="/jsp/common/ManyAssociationSetReadOnlyComponentView.jsp" flush="true">
    <tiles:putAttribute name="path_to_componentModel" value="componentModel.aggregatedSubRoleModel"/>
  </tiles:insertTemplate>
  
  <%-- Aggregated permissions to manage building block types --%>
  <tiles:insertTemplate template="/jsp/Role/PermissionMatrixView.jsp" flush="true">
    <tiles:putAttribute name="path_to_componentModel" value="componentModel.aggregatedBbTypeModel"/>
  </tiles:insertTemplate>
  
  <%-- Aggregated functional permissions --%>
  <tiles:insertTemplate template="/jsp/common/ManyAssociationSetReadOnlyComponentView.jsp" flush="true">
    <tiles:putAttribute name="path_to_componentModel" value="componentModel.aggregatedFunctionalPermissionModel"/>
    <tiles:putAttribute name="isLinked" value="false" />
  </tiles:insertTemplate>

  <%-- Aggregated permissions to read and/or write attribute type groups --%>
  <tiles:insertTemplate template="/jsp/common/ManyAssociationSetReadOnlyComponentView.jsp" flush="true">
    <tiles:putAttribute name="path_to_componentModel" value="componentModel.aggregatedPermissionAtgModel"/>
  </tiles:insertTemplate>
</c:if>
