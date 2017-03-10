<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<%-- Associated super User Groups --%>
<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxView.jsp" flush="true">
  <tiles:putAttribute name="path_to_componentModel" value="componentModel.subUserGroupModel"/>
  <tiles:putAttribute name="header_postfix_edit" value="&nbsp;&nbsp;<sup>1)</sup>"/>
</tiles:insertTemplate>

<%-- Associated Users --%>
<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxView.jsp" flush="true">
  <tiles:putAttribute name="path_to_componentModel" value="componentModel.userModel"/>
  <tiles:putAttribute name="header_postfix_edit" value="&nbsp;&nbsp;<sup>1)</sup>"/>
</tiles:insertTemplate>

<c:if test="${component_mode != 'READ'}">
	<sup>1)</sup> <fmt:message key="messages.usersNeedToLoginAgain"/>
	<br/>
</c:if>