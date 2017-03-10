<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
  
<c:set var="functionalPermission" value="${userContext.perms.userHasFuncPermAuditLog}" scope="request" />

<c:choose>
	<c:when test="${functionalPermission == true}">
		<tiles:insertTemplate template="/jsp/Miscellaneous/DownloadAuditLogfile.jsp" >
		  <tiles:putAttribute name="title">
		    <fmt:message key="global.madeby" /> - <fmt:message key="global.applicationname" />
		  </tiles:putAttribute>
		</tiles:insertTemplate>
	</c:when>	
	<c:otherwise>
		<tiles:insertTemplate template="/jsp/Miscellaneous/AccessDenied.jsp">
	  	  <tiles:putAttribute name="title">
	        <fmt:message key="global.madeby" /> - <fmt:message key="global.applicationname" />
	      </tiles:putAttribute>
	   </tiles:insertTemplate>
	</c:otherwise>	
</c:choose>
