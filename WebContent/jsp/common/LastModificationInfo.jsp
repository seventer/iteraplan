<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<c:if test="${(functionalPermission == true) and (lastmodificationLogging == true)}">
    <br />
	<c:if test="${not empty memBean.componentModel}">
	    <c:set var="link">
          <itera:linkToElement name="memBean" property="componentModel.lastModificationUserByLoginName" type="html" />
        </c:set>
	   	<fmt:message key="global.lastmodification" />: 
	   	<a href="${link}" ><c:out value="${memBean.componentModel.lastModificationUser}" /></a>
	   	<fmt:message key="DATE_FORMAT_LONG" var="dateFormat"/>
	   	<fmt:formatDate value="${memBean.componentModel.lastModificationTime}" pattern="${dateFormat}" />
	</c:if>
</c:if>
