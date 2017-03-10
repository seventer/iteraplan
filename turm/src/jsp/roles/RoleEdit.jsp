<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>

<h4>
  <fmt:message key="role.editRole">
    <fmt:param value="${createEditRole.roleName}"/>
  </fmt:message>:
</h4>

<%-- Set parameters for RoleForm.jsp --%>
<c:set var="roleFormAction" value="updateRole" scope="request"/>
<c:set var="formButtonLabelKey" value="common.commit" scope="request"/>
<jsp:include flush="true" page="/jsp/roles/RoleForm.jsp"/>
<input type="button" onclick="window.location='<c:url value="/roles"/>'" value="<fmt:message key="common.cancel"/>" id="button_cancelCreateUpdateRole"/>