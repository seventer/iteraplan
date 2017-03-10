<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<%--

This JSP is used as a 'tile' which expects certain attributes to be set in the request:

 * userFormAction: The name of the action to post when submitting the form.
 * formButtonLabelKey: The message key for the submit button.
 * showPasswordFields: Show the password fields. Must be set to true or false.
 
--%>


<form action="<c:url value="/users/post?action=${userFormAction}"/>" method="post" id="form_user_createUpdate">
<table class="default">
  <tr>
    <td><fmt:message key="user.login"/>:
    </td>
    <td><input type="text" name="loginName" value="<c:out value="${createEditUser.loginName}"/>" class="text" id="textfield_user_loginName"/>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="user.firstName"/>:
    </td>
    <td><input type="text" name="firstName" value="<c:out value="${createEditUser.firstName}"/>" class="text" id="textfield_user_firstName"/>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="user.lastName"/>:
    </td>
    <td><input type="text" name="lastName" value="<c:out value="${createEditUser.lastName}"/>" class="text" id="textfield_user_lastName"/>
    </td>
  </tr>
  <c:if test="${showPasswordFields}">
    <tr>
      <td><fmt:message key="user.password"/>:
      </td>
      <td><input type="password" name="password" class="text" id="textfield_user_password"/>
      </td>
    </tr>
    <tr>
      <td><fmt:message key="user.passwordRepeated"/>:
      </td>
      <td><input type="password" name="passwordRepeat" class="text" id="textfield_user_passwordRepeat"/>
      </td>
    </tr>
  </c:if>
  <tr>
    <td><fmt:message key="user.roles"/>:
    </td>
    <td>&nbsp;
    </td>
  </tr>
  <tr>
    <td colspan="2">
      <table>
        <tr>
          <td>
            <select size="10" name="roleIds" multiple="true" class="addRemove">
              <c:forEach items="${connectedRoleList}" var="role" varStatus="roleStatus">                
                <option value="<c:out value="${role.id}"/>" id="option_user_addedRole_<c:out value="${roleStatus.count}"/>">
                  <c:out value="${role.roleName}"/>
                </option>
              </c:forEach>
            </select>
          </td>
          <td>
             <p>
                <button type="button" onclick="exchangeListEntries(this.form.availableRoles,this.form.roleIds)" id="button_addRole">&lt;-- <fmt:message key="common.add"/></button>
              </p>
              <p>
                <button type="button" onclick="exchangeListEntries(this.form.roleIds,this.form.availableRoles)" id="button_removeRole"><fmt:message key="common.remove"/> --&gt;</button>
              </p>
          </td>
          <td>
            <select size="10" name="availableRoles" multiple="true" class="addRemove">
              <c:forEach items="${availableRoleList}" var="role" varStatus="roleStatus">
                <option value="<c:out value="${role.id}"/>" id="option_user_availableRole_<c:out value="${roleStatus.count}"/>">
                  <c:out value="${role.roleName}"/>
                </option>
              </c:forEach>
            </select>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
  <button type="submit" onclick="selectAllOptions(this.form.roleIds);" id="button_createUpdateUser"><fmt:message key="${formButtonLabelKey}"/></button>
</form>
