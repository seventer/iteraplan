<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<c:set var="editColumn" value="true" scope="request"/>
<c:set var="resultPostSelection" value="false" scope="request" />
<c:set var="functionalPermission" value="${userContext.perms.userHasFuncPermTabReporting}" scope="request" />

<c:choose>
<c:when test="${functionalPermission == true}">
	<tiles:insertTemplate template="/jsp/commonReporting/ManageReportOrMassupdate.jsp">
		<tiles:putAttribute name="showAddColumnButton" value="true" cascade="true" />
		<tiles:putAttribute name="show_nettoexport" value="true" cascade="true" />
	</tiles:insertTemplate>
</c:when>
<c:otherwise>
  	<tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
</c:otherwise>
</c:choose>

