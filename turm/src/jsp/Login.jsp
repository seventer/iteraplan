<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>

<fmt:setLocale value="${turmLocale}" scope="session" />
<fmt:setBundle basename='ApplicationResources' scope="request" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
       "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="expires" content="0" />
	<meta http-equiv="pragma" content="no-cache" />
	<meta http-equiv="cache-control" content="no-cache" />
	<title>
	  <fmt:message key="app.name"/>
	</title>
	<link rel="icon" href="<c:url value="/images/favicon.ico"/>" type="image/x-icon" />
	<link rel="shortcut icon" href="<c:url value="/images/favicon.ico"/>" type="image/x-icon" />	
	<style type="text/css">
	 #outerbox {
	   margin:			15% auto;
	   width:			300px;
	   border-width:	1px; 
	   border-style:	solid; 
	   border-color:	rgb(0, 0, 100);
	   padding:			6px; 
	 }
	 
	 #innerbox {
	   background-color: rgb(245, 245, 245); 
	   border-width:	1px; 
	   border-style:	solid; 
	   border-color:	rgb(204, 204, 204); 
	   padding:			1px;
	 }
	 #buttons {
	   text-align:		center;
	   line-height:		120%;
	   margin:			2em auto;
	 }
	 #failurebox {
	   border-width:	1px; 
	   border-style:	solid; 
	   border-color:	rgb(0, 0, 100);
	   width:			100%; 
	   padding:			2px; 
	   margin:			5 auto;
	 }
	 body {
	   font-family:		Verdana, Arial, Helvetica, sans-serif; 
	   line-height:		120%;
	   font-size:		7pt;
	 }
	 h1 {
	   text-align:		center;
	   margin:			1.5em auto;
	   font-size:		11pt;
	   font-weight:		normal;
	 }
	 .label {
	   text-align:		right;
	 }
	 .input {
	   text-align:		left;
	   width:			140px;
	 }
	 p.errorMsg {
	   color:			red;
	   font-weight:		bold;
	   text-align: 		center;
	 }
	 #changePassLink {
	   text-align:		right;
	   line-height:		120%;
	   padding-right:	1pt;
	 }
	</style>
</head>
<body onload="javascript:document.getElementById('login_form').j_username.focus()">
<div id="outerbox">
 <div id="innerbox">
   <h1><fmt:message key="app.name"/></h1>
   <form method="POST" action="<c:url value='/j_turm_security_check'/>" id="login_form" name="login_form" >
     <table style="width: 100%;">
       <tr>
         <td class="label"><label for="j_username">
           <fmt:message key="login.loginName"/>:</label>
         </td>
         <td><input class="input" type="text" name="j_username" id="j_username" ></td>
       </tr>
       
       <tr>
         <td class="label"><label for="j_password">
           <fmt:message key="login.password"/>:</label>
         </td>
         <td><input class="input" type="password" name="j_password" id="j_password"></td>
       </tr>
     </table>
     <div id="buttons">
       <input type="submit" name="login_button" value="<fmt:message key="login.do"/>" />
     </div>
     <div id="changePassLink">
       <a href="<c:url value="/password"/>" id="logout_link" style="color: #0000FF;" title="<fmt:message key="password.hint"/>"><fmt:message key="password.changePassword"/></a>
     </div>
   </form>
   <c:if test="${not empty param.errorKey}">
     <p class="errorMsg"><fmt:message key="${param.errorKey}"/></p>
   </c:if>
 </div> <!-- innerbox -->
</div>
</body>
</html>
