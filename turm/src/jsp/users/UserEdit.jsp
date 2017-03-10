<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>

<h4>
  <fmt:message key="user.editUser">
    <fmt:param value="${createEditUser.loginName}"/>
  </fmt:message>:
</h4>

<%-- Set parameters for UserForm.jsp --%>
<c:set var="userFormAction" value="updateUser" scope="request"/>
<c:set var="formButtonLabelKey" value="common.commit" scope="request"/>
<c:set var="showPasswordFields" value="false" scope="request"/>
<jsp:include flush="true" page="/jsp/users/UserForm.jsp"/>
<input type="button" onclick="window.location='<c:url value="/users"/>'" value="<fmt:message key="common.cancel"/>" id="button_cancelCreateUpdateUser"/>