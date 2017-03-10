<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>


<table>
  <tr>
    <td class="leftColumn">
    
      <h4><fmt:message key="role.createRole"/>:</h4>
      
      <%-- Set parameters for RoleForm.jsp --%>
      <c:set var="roleFormAction" value="createRole" scope="request"/>
      <c:set var="formButtonLabelKey" value="role.createRole" scope="request"/>
      <jsp:include flush="true" page="/jsp/roles/RoleForm.jsp"/>
    
    </td>
    <td class="rightColumn">

<h4><fmt:message key="role.existingRoles"/>:</h4>
<table>
  <c:forEach items="${roleList}" var="role" varStatus="status">
  <tr>
    <td>
      <table class="listTable" id="role_table_<c:out value="${status.count}"/>">
        <tr>
          <th>
            <form method="post" action="<c:url value="/roles/post?action=deleteRole&amp;roleId=${role.id}"/>" id="delete_<c:out value="${role.id}"/>">
              <img src="<c:url value="/images/icon_delete_red.gif"/>" onclick="confirmSubmit('<fmt:message key="message.deleteRole"><fmt:param value="${role.roleName}"/></fmt:message>', 'delete_<c:out value="${role.id}"/>');" class="link" title="<fmt:message key="common.delete"/>" id="button_delete_role_<c:out value="${status.count}"/>"/>
            </form>
            <form method="post" action="<c:url value="/roles/post?action=preUpdateRole&amp;roleId=${role.id}"/>" id="edit_<c:out value="${role.id}"/>">
              <img src="<c:url value="/images/icon_edit.gif"/>" onclick="getElementByIdSafe('edit_<c:out value="${role.id}"/>').submit();" class="link" title="<fmt:message key="common.edit"/>" id="button_edit_role_<c:out value="${status.count}"/>"/>
            </form>
          </th>
          <th>
            <span class="bold"><c:out value="${role.roleName}"/></span>
          </th>
        </tr>
        <c:choose>
          <c:when test="${not empty role.users}">
            <c:forEach items="${role.users}" var="user" varStatus="status">
              <tr>
                <td>
                  <c:choose>
                    <c:when test="${status.index == 0}">
                      <fmt:message key="role.users"/>:
                    </c:when>
                    <c:otherwise>
                      &nbsp;
                    </c:otherwise>
                  </c:choose>
                </td>
                <td>
                  <c:out value="${user.loginName}"/>
                </td>
              </tr>
            </c:forEach>
          </c:when>
          <c:otherwise>
            <tr>
              <td>
                <fmt:message key="role.users"/>:
              </td>
              <td>
                -
              </td>
            </tr>
          </c:otherwise>
        </c:choose>
      </table>
    </td>
  </tr>
  </c:forEach>
</table>

    </td>
  </tr>
</table>