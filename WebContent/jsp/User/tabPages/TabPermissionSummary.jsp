<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<c:if test="${memBean.componentModel.componentMode != 'READ'}">
  <fmt:message key="messages.permissionSummaryEdit"/>
</c:if>

<c:if test="${memBean.componentModel.componentMode == 'READ'}">

  <%-- Aggregated user groups --%>
  <tiles:insertTemplate template="/jsp/common/ManyAssociationSetReadOnlyComponentView.jsp" flush="true">
    <tiles:putAttribute name="path_to_componentModel" value="componentModel.aggregatedUserGroupModel"/>
  </tiles:insertTemplate>
  
  <%-- Aggregated permissions building block instances --%>
  <tiles:insertTemplate template="/jsp/common/ManyAssociationSetReadOnlyComponentView.jsp" flush="true">
    <tiles:putAttribute name="path_to_componentModel" value="componentModel.aggregatedBuildingBlockModel"/>
  </tiles:insertTemplate>
  
</c:if>
