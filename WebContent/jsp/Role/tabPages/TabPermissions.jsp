<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<%-- Associated building block types --%>
<tiles:insertTemplate template="/jsp/Role/PermissionMatrixView.jsp" flush="true">
  <tiles:putAttribute name="path_to_componentModel" value="componentModel.bbTypeModel"/>
</tiles:insertTemplate>

<%-- Associated functional permissions --%>
<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxViewRoles.jsp" flush="true">
  <tiles:putAttribute name="path_to_componentModel" value="componentModel.functionalPermissionsModel"/>
  <tiles:putAttribute name="isLinked" value="false" />
  <tiles:putAttribute name="header_postfix_edit" value="&nbsp;<sup>1)</sup>"/>
</tiles:insertTemplate>

<%-- Associated permissions for attribute type groups --%>
<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxView.jsp" flush="true">
  <tiles:putAttribute name="path_to_componentModel" value="componentModel.permissionAtgModel"/>
  <tiles:putAttribute name="header_postfix_edit" value="&nbsp;<sup>1)</sup>"/>
</tiles:insertTemplate>

<c:if test="${componentMode == 'EDIT'}">
  <sup>1)</sup>&nbsp;&ndash;&nbsp;<fmt:message key="messages.usersNeedToLoginAgain"/>
</c:if>