<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<fmt:setLocale value="${turmLocale}" scope="session" />
<fmt:setBundle basename='ApplicationResources' scope="request" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=iso8859-1" />
    <title><fmt:message key="app.name"/></title>
    <link rel="icon"          href="<c:url value="/images/favicon.ico"/>" type="image/x-icon" />
    <link rel="shortcut icon" href="<c:url value="/images/favicon.ico"/>" type="image/x-icon" />
    <link rel="STYLESHEET" href="<c:url value="/css/turm.css"/>" type="text/css" />
    <link rel="STYLESHEET" href="<c:url value="/css/tabs.css"/>" type="text/css" />
    <script type="text/javascript" src="<c:url value="/javascript/turm.js"/>"></script>
  </head>
  <body>

  <div class="header">
    <form action="<c:out value="${servletURI}"/>" method="get">
      <select name="turmLocale" onchange="submit();">
        <option value="de" <c:if test="${turmLocale == 'de'}">selected</c:if> id="option_lang_de"><fmt:message key="common.lang.de"/></option>
        <option value="en" <c:if test="${turmLocale == 'en'}">selected</c:if> id="option_lang_en"><fmt:message key="common.lang.en"/></option>
      </select>
    </form>
    <c:if test="${not logoutIsDeactivated}">
      |
      <a href="<c:url value="/j_turm_logout"/>" id="logout_link"><fmt:message key="common.logout"/></a>
    </c:if>
  </div>
  
  <div class="title"><fmt:message key="app.name"/> - <fmt:message key="app.description"/></div>  

  <%-- Tabs. Define userTabClass or roleTabClass as 'selected', depending on which menu is active.  --%>  
  <c:if test="${not tabsAreDeactivated}">
  <c:set var="passwordChangedExternal" value="false" scope="session"/>
    <ul class="tabnav">
      <li class="<c:out value="${userTabClass}"/>"><a href="<c:url value="/users"/>" id="link_user_tab"><fmt:message key="user.title"/></a></li>
      <li class="<c:out value="${roleTabClass}"/>"><a href="<c:url value="/roles"/>" id="link_role_tab"><fmt:message key="role.title"/></a></li>
      <li class="<c:out value="${changePassTabClass}"/>"><a href="<c:url value="/password"/>" id="link_changePass_tab" ><fmt:message key="password.title"/></a></li>
    </ul>
  </c:if>
  
  <div class="content">
  
    <%-- Print TurmExceptions if present --%>
    <c:if test="${not empty turmExceptions}">
      <div class="errors">
        <c:forEach items="${turmExceptions}" var="exception" varStatus="status">
          <fmt:message key="${exception.message}">
            <c:forEach items="${exception.messageParams}" var="parameter">
              <fmt:param value="${parameter}"/>
            </c:forEach>
          </fmt:message>
          <c:if test="${not empty exception.cause}">
            <span class="jsLink" onclick="toggleLayer('exception_<c:out value="${status.count}"/>');">
              <fmt:message key="common.more"/>
            </span>
            <br/>
            <div class="hidden" id="exception_<c:out value="${status.count}"/>">      
              <c:out value="${exception.cause.class.name}"/>: <c:out value="${exception.cause.localizedMessage}"/><br/>
              <c:forEach items="${exception.cause.stackTrace}" var="stackLineToPrint">
                <c:out value="${stackLineToPrint}"/>
                <br/>
              </c:forEach>
            </div>
          </c:if>          
        </c:forEach>
      </div>
    </c:if>
    
    <%-- Print TurmMessages if present --%>
    <c:if test="${not empty turmMessages}">
      <div class="messages">
        <c:forEach items="${turmMessages}" var="message">
          <fmt:message key="${message.messageKey}">
            <c:forEach items="${message.parameters}" var="parameter">
              <fmt:param value="${parameter}"/>
            </c:forEach>
          </fmt:message>
        </c:forEach>
      </div>
    </c:if>