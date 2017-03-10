<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>

<c:set var="functionalPermission" value="${userContext.perms.userHasFuncPermOverview}" scope="request" />
<c:set var="header_key" value="global.overview" />

<c:set var="numberOfColumns" value="1" />
<c:set var="columnSize" value="100%" />
<c:if test="${fn:length(dialogMemory.overviewLists.lists) > 1 }">
	<c:set var="numberOfColumns" value="2" />
	<c:set var="columnSize" value="50%" />
</c:if>
<c:if test="${fn:length(dialogMemory.overviewLists.lists) > 4 }">
	<c:set var="numberOfColumns" value="3" />
	<c:set var="columnSize" value="33%" />
</c:if>

<div id="OverviewHeadingContainer" class="row-fluid module">
	<div class="module-heading">
	    <fmt:message key="${header_key}" /> - <span style="font-weight:normal"><fmt:message key="global.overview.subheader" /></span>
	</div>
</div>

<c:choose>
	<c:when test="${functionalPermission == true}">
			<c:forEach items="${dialogMemory.overviewLists.lists}" var="overviewList" varStatus="listsCount">
				<c:if test="${(listsCount.index) % numberOfColumns == 0}">
					<div class="row-fluid">
				</c:if>
				<div class="span4">
					<tiles:insertTemplate template="/jsp/Overview/OverviewListModel.jsp" flush="true">
						<tiles:putAttribute name="overviewList" value="${overviewList}" />
					</tiles:insertTemplate>
				</div>
				<c:if test="${(listsCount.index) % numberOfColumns == numberOfColumns - 1}">
					</div>
				</c:if>
			</c:forEach>
		
		<span class="helpText">1. <fmt:message key="global.overview.footnote.informationSystem" /></span> <br>
		<span class="helpText">2. <fmt:message key="global.overview.footnote.technicalComponent" /></span>
	</c:when>
	<c:otherwise>
		<tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
	</c:otherwise>
</c:choose>