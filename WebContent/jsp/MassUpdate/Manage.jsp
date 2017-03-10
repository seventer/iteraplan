<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<c:set var="massUpdateMode" value="true" scope="request" />
<c:set target="${memBean}" property="massUpdateMode" value="true" />
<c:set var="functionalPermission" value="${userContext.perms.userHasFuncPermMassUpdate}" scope="request" />

<c:choose>
<c:when test="${functionalPermission == true}">
<tiles:insertTemplate template="/jsp/commonReporting/ManageReportOrMassupdate.jsp" />
</c:when>
<c:otherwise>
  <tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
</c:otherwise>
</c:choose>