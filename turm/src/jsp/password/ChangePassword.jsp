<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<c:choose>
  <c:when test="${isPasswordMgmtDisabled}">
    <%-- output nothing. The exception has been printed already --%>
  </c:when>
  <c:when test="${passwordWasChanged}">

    <p><fmt:message key="message.automaticForward" /></p>
    <a href="<c:out value="${referrerUrl}"/>"><fmt:message
      key="common.continue" /></a>
    <script type="text/javascript">
    <!--
      setTimeout(function() {
        location.href = '<c:out value="${referrerUrl}"/>';
      }, 4000);
    // --></script>

  </c:when>
  <c:otherwise>

    <br />
    <br />
    <c:if test="${not empty passwordChangeReason}">
      <fmt:message key="${passwordChangeReason}" />
    </c:if>
    <fmt:message key="message.enterNewPassword" />
    <br />
    <br />

    <form method="POST" action="<c:url value='/password'/>"
      id="password_change_form">
    <table>
      <tr>
        <td align="right"><fmt:message key="login.loginName" />:</td>
        <td align="left"><input type="text" name="loginName"
          value="<c:out value="${userChangePassword}"/>"
          style="width: 130px;" id="text_login_name"></td>
      </tr>
      	<c:if test="${passwordChangedExternal ne 'false'}">
      	 <tr>
	        <td align="right"><fmt:message key="user.oldPassword" />:</td>
	        <td align="left"><input type="password" name="oldPassword"
          	style="width: 130px;" id="text_oldPassword"></td>
      	</tr>
      	</c:if>
      <tr>
        <td align="right"><fmt:message key="user.newPassword" /> :</td>
        <td align="left"><input type="password" name="password"
          style="width: 130px;" id="text_password"></td>
      </tr>
      <tr>
        <td align="right"><fmt:message key="user.passwordRepeated" />
        :</td>
        <td align="left"><input type="password"
          name="passwordRepeat" style="width: 130px;"
          id="text_passwordRepeated"></td>
      </tr>
      <tr>
        <td colspan="2" align="center"><br />
        <button type="submit" name="submit" id="button_submit"><fmt:message
          key="common.commit" /></button>
        </td>
      </tr>
    </table>
    </form>

    <script type="text/javascript">
      var field = getElementByIdSafe('text_login_name');
      // window.onload=field.focus();
    </script>

  </c:otherwise>
</c:choose>

<jsp:include flush="true" page="/jsp/CommonBottom.jsp" />