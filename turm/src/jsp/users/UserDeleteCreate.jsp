<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>

<table>
  <tr>
    <td class="leftColumn">
      <h4><fmt:message key="user.createUser"/>:</h4>
      
      <%-- Set parameters for UserForm.jsp --%>
      <c:set var="userFormAction" value="createUser" scope="request"/>
      <c:set var="formButtonLabelKey" value="user.createUser" scope="request"/>
      <c:set var="showPasswordFields" value="${not isPasswordMgmtDisabled}" scope="request"/>
      <jsp:include flush="true" page="/jsp/users/UserForm.jsp"/>
    </td>
    <td class="rightColumn">
      <h4><fmt:message key="user.existingUsers"/>:</h4>
      <table>
        <c:forEach items="${userList}" var="user" varStatus="status">
        <tr>
          <td>
            <table class="listTable" id="user_table_<c:out value="${status.count}"/>">
              <tr>
                <th>
                  <form method="post" action="<c:url value="/users/post?action=deleteUser&amp;userId=${user.id}"/>" id="delete_<c:out value="${user.id}"/>">
                    <img src="<c:url value="/images/icon_delete_red.gif"/>" onclick="confirmSubmit('<fmt:message key="message.deleteUser"><fmt:param value="${user.loginName}"/></fmt:message>', 'delete_<c:out value="${user.id}"/>');" class="link" title="<fmt:message key="common.delete"/>" id="button_delete_user_<c:out value="${status.count}"/>"/>
                  </form>
                  <form method="post" action="<c:url value="/users/post?action=preUpdateUser&amp;userId=${user.id}"/>" id="edit_<c:out value="${user.id}"/>">
                    <img src="<c:url value="/images/icon_edit.gif"/>" onclick="getElementByIdSafe('edit_<c:out value="${user.id}"/>').submit();" class="link" title="<fmt:message key="common.edit"/>" id="button_edit_user_<c:out value="${status.count}"/>"/>
                  </form>
				  <form method="post" action="<c:url value="/password/post?action=changePassword&amp;userId=${user.id}"/>" id="changePass_<c:out value="${user.id}"/>">
                    <img src="<c:url value="/images/icon_changePassword.gif"/>" onclick="getElementByIdSafe('changePass_<c:out value="${user.id}"/>').submit();" class="link" title="<fmt:message key="password.changePassword"/>" id="button_change_pass_<c:out value="${status.count}"/>"/>
                  </form>
                </th>
                <th>
                  <span class="bold"><c:out value="${user.loginName}"/></span>
                </th>
              </tr>
              <tr>
                <td>
                  <fmt:message key="user.name"/>:
                </td>
                <td>
                  <c:out value="${user.firstName} ${user.lastName}"/>            
                </td>
              </tr>
              <c:choose>
                <c:when test="${not empty user.roles}">
                  <c:forEach items="${user.roles}" var="role" varStatus="status">
                    <tr>
                      <td>
                        <c:choose>
                          <c:when test="${status.index == 0}">
                            <fmt:message key="user.roles"/>:
                          </c:when>
                          <c:otherwise>
                            &nbsp;
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td>
                        <c:out value="${role.roleName}"/>
                      </td>
                    </tr>
                  </c:forEach>
                </c:when>
                <c:otherwise>
                  <tr>
                    <td>
                      <fmt:message key="user.roles"/>:
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