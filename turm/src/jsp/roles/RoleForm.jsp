<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<%--

This JSP is used as a 'tile' which expects certain attributes to be set in the request:

 * roleFormAction: The name of the action to post when submitting the form.
 * formButtonLabelKey: The message key for the submit button.
 
--%>


<form action="<c:url value="/roles/post?action=${roleFormAction}"/>" method="post">
<table class="default">
  <tr>
    <td><fmt:message key="role.roleName"/>:
    </td>
    <td><input type="text" name="roleName" value="<c:out value="${createEditRole.roleName}"/>" class="text" id="textfield_role_roleName"/>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="role.users"/>:
    </td>
    <td>&nbsp;
    </td>
  </tr>
  <tr>
    <td colspan="2">
      <table>
        <tr>
          <td>
            <select size="10" name="userIds" multiple="true" class="addRemove">
              <c:forEach items="${connectedUserList}" var="user" varStatus="userStatus">
                <option value="<c:out value="${user.id}"/>" id="option_role_addedUser_<c:out value="${userStatus.count}"/>">
                  <c:out value="${user.loginName}"/>
                </option>
              </c:forEach>
            </select>
          </td>
          <td>
             <p>
                <button type="button" onclick="exchangeListEntries(this.form.availableUsers,this.form.userIds)" id="button_addUser">&lt;-- <fmt:message key="common.add"/></button>
              </p>
              <p>
                <button type="button" onclick="exchangeListEntries(this.form.userIds,this.form.availableUsers)" id="button_removeUser"><fmt:message key="common.remove"/> --&gt;</button>
              </p>
          </td>
          <td>
            <select size="10" name="availableUsers" multiple="true" class="addRemove">
              <c:forEach items="${availableUserList}" var="user" varStatus="userStatus">
                <option value="<c:out value="${user.id}"/>" id="option_role_availableUser_<c:out value="${userStatus.count}"/>">
                  <c:out value="${user.loginName}"/>
                </option>
              </c:forEach>
            </select>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
  <button type="submit" onclick="selectAllOptions(this.form.userIds);" id="button_createUpdateRole"><fmt:message key="${formButtonLabelKey}"/></button>
</form>
