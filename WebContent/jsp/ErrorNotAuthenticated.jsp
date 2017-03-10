<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>

<%
  // effectively disable browser caching, enforce complete roundtrip on every reload 
  response.setHeader("Expires", "Tue, 15 Nov 1994 12:45:26 GMT");
  response.setHeader("Last-Modified", "Tue, 15 Nov 1994 12:45:26 GMT");
  response.setHeader("Pragma", "no-cache");
  response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
  response.addHeader("Cache-Control", "post-check=0, pre-check=0");
%>
<spring:eval var="buildVersion" expression="@applicationProperties.getProperty('build.version')" />


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
   
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta http-equiv="refresh" content="<%=session.getMaxInactiveInterval()-10 %>" />
    <meta http-equiv="expires" content="0" />
    <meta http-equiv="pragma" content="no-cache" />
    <meta http-equiv="cache-control" content="no-cache" />
    <meta http-equiv="X-UA-Compatible" content="IE=8,9,10" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        
    <link rel="icon"          href="<c:url value="/images/favicon.ico"/>" type="image/x-icon" />
    <link rel="shortcut icon" href="<c:url value="/images/favicon.ico"/>" type="image/x-icon" />
	<link href="<c:url value="/ui/login.cxx" />" media="screen" type="text/css" rel="stylesheet" />		
    
    <title>
      <fmt:message key="global.madeby" /> - <fmt:message key="global.applicationname" />
    </title>
    
   
  </head> 
  
<body>

  <div id="outerbox">
   <div id="innerbox">
     <div id="logo">
       <img id="iteraplan_header_image" border="0" src="<c:url value="/images/blank.gif"/>" alt="iteraplan Logo"/>
     </div>
     <h1><fmt:message key="global.applicationname" />
          <c:out value=" " />
        ${buildVersion}
     </h1>
     <p class="errorMsg"><fmt:message key="errors.authentication.failed" /></p>
     
     <div id="links">
       <table style ="width: 100%">
         <tr>
         <td align="left">  
           <div id="iteratecLink" class = "Link">
          
             <a href="<fmt:message key ="login.link.iteratec.url" />">
              <fmt:message key ="login.link.iteratec.text" />
           </a>
           </div>
       </td>       
         <td align="right">
           <div id="iteraplanLink" class="Link">          
          <a href="<fmt:message key ="login.link.iteraplan.url" />">
              <fmt:message key ="login.link.iteraplan.text" />
            </a>
          </div>
        </td>
        </tr>
       </table>
     </div> 
   </div> 
   <!-- innerbox -->
  </div> 
</body>
</html>