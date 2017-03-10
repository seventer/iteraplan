<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>

<c:set var="roleTabClass" value="selected" scope="request"/>
<jsp:include flush="true" page="/jsp/CommonTop.jsp"/>

<c:choose>
  <c:when test="${not empty createEditRole.id}">
    <jsp:include flush="true" page="/jsp/roles/RoleEdit.jsp"/>
  </c:when>
  <c:otherwise>
    <jsp:include flush="true" page="/jsp/roles/RoleDeleteCreate.jsp"/>
  </c:otherwise>
</c:choose>

<jsp:include flush="true" page="/jsp/CommonBottom.jsp"/>
