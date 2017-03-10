<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<c:set var="isCommonVisualDialog" 
		value="${guiContext.visualDialogActive && !(guiContext.activeDialogName == 'Dashboard') && !guiContext.activeDialogName == 'CustomDashboardInstance'  && not empty active_sub_title}" />
<c:set var="isTabularReportOrMassUpdate"
		value="${guiContext.activeDialogName eq 'TabularReporting' || guiContext.activeDialogName eq 'MassUpdate'}" /> 

<c:set var="flow_url" value="/show/${fn:toLowerCase(guiContext.activeDialogName)}" />
<c:url var="closeAllUrl" value="${flow_url}"><c:param name="_eventId" value="closeAll" /></c:url>

<ul class="breadcrumb" id="iteraplanBreadcrumb">

	<li>
		<a href="<c:url value='/start/start.do' />">
			&nbsp;<i class="icon-home"></i>&nbsp;
		</a>
	</li>
	
	<c:if test="${userContext.dataSource ne 'MASTER'}">
		<li>
			<c:out value="${userContext.dataSource}" />
		</li>	
	</c:if>
	
	<c:choose>
		<c:when test="${guiContext.eadataDialogActive}">
			<li>
				<span class="divider">/</span>
				<c:choose>
					<c:when test="${userContext.perms.userHasFuncPermOverview}">
						<a href="<c:url value="/overview/init.do" />">
							<fmt:message key="menu.header.eadata" />
						</a>
					</c:when>
					<c:otherwise>
						<a href="#" onclick="return false;">
							<fmt:message key="menu.header.eadata" />
						</a>
					</c:otherwise>
				</c:choose>
			</li>
		</c:when>
		<c:when test="${guiContext.reportDialogActive}">
			<li>
				<span class="divider">/</span>
				<fmt:message key="menu.header.reports" />
			</li>
		</c:when>
		<c:when test="${guiContext.visualDialogActive}">
			<li>
				<span class="divider">/</span>
				<a href="<c:url value="/graphicalreporting/init.do" />">
					<fmt:message key="menu.header.visualisation" />
				</a>
			</li>
			<%-- explicitly point to overview again if the overview dialog is active --%>
			<c:if test="${guiContext.activeDialogName eq 'GraphicalReporting' && empty active_sub_title}"> 
				<li>
					<span class="divider">/</span>
					<a href="<c:url value="/graphicalreporting/init.do" />">
						<fmt:message key="${active_title}"/>
					</a>
				</li>
			</c:if>
		</c:when>
		<c:when test="${guiContext.massDialogActive}">
			<li>
				<span class="divider">/</span>
				<fmt:message key="menu.header.massdata" />
			</li>
		</c:when>
		<c:when test="${guiContext.governanceDialogActive}">
			<li>
				<span class="divider">/</span>
				<fmt:message key="menu.header.governance" />
			</li>
		</c:when>
		<c:when test="${guiContext.adminDialogActive}">
			<li>
				<span class="divider">/</span>
				<fmt:message key="menu.header.administration" />
			</li>
		</c:when>
		<c:otherwise>
			<c:set var="active_title" value="" />
			<%-- Hide breadcrumbs on overview page --%>
			<style>
				#iteraplanBreadcrumb { visibility: hidden; }
			</style>
		</c:otherwise>
	</c:choose>

	
	<c:if test="${not empty active_title && (guiContext.activeDialogName != 'GraphicalReporting')}">
		<li>
			<span class="divider">/</span>
			<a href="<c:url value="${active_url}"/>">
				<c:if test="${guiContext.eadataDialogActive && !(guiContext.activeDialogName == 'Overview' || guiContext.activeDialogName == 'Search' || guiContext.activeDialogName == 'BusinessMapping') }">
					<i class="icon-iteraplan-${fn:toLowerCase(guiContext.activeDialogName)}"></i>
				</c:if>
				<fmt:message key="${active_title}"/>
			</a>
		</li>
	</c:if>

	<c:choose>
		
		<%-- For building blocks, show all elements in the hierarchy --%>
		<c:when test="${guiContext.eadataDialogActive && not empty memBean && guiContext.activeDialogName != 'BusinessMapping'}">
			<itera:hierarchicalList name="memBean" property="componentModel.entity" id="hierarchicalList" />
			<c:forEach var="entity" items="${hierarchicalList}" varStatus="state">
			
				<%-- show the list if there is at least one element, cut off the virtual element unless information systems are being displayed --%>
				<c:if test="${fn:length(hierarchicalList) == 1 || (state.index > 0) || (state.index >= 0 && guiContext.activeDialogName == 'InformationSystem')}">
					<li>
						<span class="divider">/</span>
						<a href="<itera:linkToElement name="entity" type="html" />">
							<c:out value="${entity.nonHierarchicalName}" />
						</a>
					</li>
				</c:if>
			</c:forEach>
		</c:when>
		
		<%-- show active_sub_title if set --%>
		<c:when test="${not empty active_sub_title}">
			<li>
				<span class="divider">/</span>
				<a href="<c:out value="${active_sub_url}"/>">
					<c:out value="${active_sub_title}" />
				</a>
			</li>
		</c:when>
	</c:choose>

	<c:if test="${isCommonVisualDialog || isTabularReportOrMassUpdate}">
		<c:choose>
			<c:when test="${guiContext.visualDialogActive}">
				<a href="javascript:flowAction('close');">
					<i class="icon-repeat"></i>
				</a>
			</c:when>
			<c:otherwise>
				<a href="<c:out value="${closeAllUrl}" escapeXml="false"/>">
					<i class="icon-repeat"></i>
				</a>
			</c:otherwise>
		</c:choose>
	</c:if>
</ul>
