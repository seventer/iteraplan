<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<%-- Associated Roles --%>
<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxView.jsp" flush="true">
  <tiles:putAttribute name="path_to_componentModel" value="componentModel.subRoleModel"/>
  <tiles:putAttribute name="header_postfix_edit" value="&nbsp;&nbsp;1)"/>
</tiles:insertTemplate>

<c:if test="${componentMode == 'EDIT'}">
	1) <fmt:message key="messages.usersNeedToLoginAgain"/>
</c:if>