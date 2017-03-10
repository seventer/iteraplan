<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxView.jsp" flush="true">
  <tiles:putAttribute name="path_to_componentModel" value="componentModel.owningUserEntityModel" />
  <c:choose>
    <c:when test="${componentMode == 'READ'}">
      <tiles:putAttribute name="path_to_connected_elements" value="componentModel.owningUserEntityModel.aggregatedOwningUserEntities" />
      <tiles:putAttribute name="header_postfix_read">
        (<fmt:message key="global.aggregated" />)
      </tiles:putAttribute>
      <tiles:putAttribute name="info_message_key_empty">
        <fmt:message key="instancePermission.no_restriction" />
      </tiles:putAttribute>
    </c:when>
    <c:otherwise>
      <c:if test="${not userContext.perms.userHasFuncPermGrantInstancePerm}">
        <tiles:putAttribute name="force_component_mode" value="READ" />
        <tiles:putAttribute name="path_to_connected_elements" value="componentModel.owningUserEntityModel.aggregatedOwningUserEntities" />
        <tiles:putAttribute name="info_message_key">
          <fmt:message key="messages.noGrantInstancePermission">
          	<fmt:param><fmt:message key="role.permission.GrantExplicitPermission" /></fmt:param>
          </fmt:message>
        </tiles:putAttribute>     
      </c:if>
    </c:otherwise>
  </c:choose>
</tiles:insertTemplate>