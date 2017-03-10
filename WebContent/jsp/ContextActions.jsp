<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<%-- add shortcut to create a new bb --%>
<script type="text/javascript">
    addShortcutForClick("<fmt:message key='search.newElement.shortcut'/>","<fmt:message key='search.newElement.id'/>");
</script>

<c:set var="tabReportingUrl">
	<c:url value="/show/tabularreporting">
		<c:param name="_eventId" value="changeQueryType" />
		<c:param name="bbType" value="${active_title}" />
		<c:param name="selectedBuildingBlock" value="${active_title}" />
	</c:url>
</c:set>

<c:set var="massUpdateUrl">
	<c:url value="/show/massupdate">
		<c:param name="_eventId" value="changeQueryType" />
		<c:param name="bbType" value="${active_title}" />
		<c:param name="selectedBuildingBlock" value="${active_title}" />
	</c:url>
</c:set>

<c:set var="createNewJavascript" value="flowActionRedirect(getRestURIRelativeToMVC(), 'create');"/>
<c:if test="${guiContext.activeDialog != guiContext.activeDialogName}">
	<%-- This is a subflow, we need another url --%>
	<c:set var="createNewJavascript" value="flowActionRedirect('.', 'create');"/>
</c:if>

<c:set var="show" value="collapse" />
<c:set var="icon_to_show" value="icon-chevron-up" />

<c:set var="isCommonEaDataDialog" 
		value="${guiContext.eadataDialogActive && !(guiContext.activeDialogName == 'Overview' || guiContext.activeDialogName == 'Search' || guiContext.activeDialogName == 'BusinessMapping')}" />
<c:set var="isCommonGovernanceDialog"
		value="${guiContext.governanceDialogActive && !(guiContext.activeDialogName == 'SupportingQuery' || guiContext.activeDialogName == 'ConsistencyCheck' || guiContext.activeDialogName == 'ObjectRelatedPermission') || guiContext.activeDialogName == 'AttributeTypeGroup' || guiContext.activeDialogName == 'AttributeType' || guiContext.activeDialogName == 'DateInterval'}" />
<c:set var="isCommonVisualDialog" 
		value="${guiContext.visualDialogActive && !(guiContext.activeDialogName == 'Dashboard') && guiContext.activeDialogName != 'CustomDashboardInstance'  && not empty active_sub_title}" />
<c:set var="isTabularReportOrMassUpdate"
		value="${guiContext.activeDialogName eq 'TabularReporting' || guiContext.activeDialogName eq 'MassUpdate'}" /> 

<c:set var="flow_url" value="/show/${fn:toLowerCase(guiContext.activeDialogName)}" />
<c:url var="closeAllUrl" value="${flow_url}"><c:param name="_eventId" value="closeAll" /></c:url>

<c:if test="${isCommonEaDataDialog || isCommonGovernanceDialog || isCommonVisualDialog || isTabularReportOrMassUpdate}">
	<c:set var="show" value="in" />
	<c:set var="icon_to_show" value="icon-chevron-down" /> 
</c:if>


<div class="accordion" id="contextActionsContainer">
	<div class="accordion-group">
		<div class="accordion-heading">
			<a class="accordion-toggle" data-toggle="collapse" data-parent="#contextActionsContainer" href="#contextActionsAccordion"
						onclick="toggleIcon('contextActionsIcon', 'icon-chevron-down', 'icon-chevron-up');" >
				<i class="icon-cog"></i>
				<fmt:message key="contextMenu.actions" />
				<i style="float: right;" id="contextActionsIcon" class="${icon_to_show}"></i>
			</a>
		</div>
		<div id="contextActionsAccordion" class="accordion-body ${show}">
			<div class="accordion-inner">
				<c:choose>
				<c:when test="${isCommonEaDataDialog}">
					
					<c:set var="type" value="${not empty memBean ? memBean.componentModel.entity.typeOfBuildingBlock.value : bbt.typeOfBuildingBlock.value}" />
					<c:set var="pluraltype" value="${not empty memBean ? memBean.componentModel.entity.typeOfBuildingBlock.pluralValue : bbt.typeOfBuildingBlock.pluralValue}" />
					
					<%-- Permissions --%>
					<c:set var="permissionTabularReporting" value="${userContext.perms.userHasFuncPermTabReporting}" scope="request" />
					<c:set var="permissionMassUpdate" value="${userContext.perms.userHasFuncPermMassUpdate}" scope="request" />
					
					<c:set var="permissionUpdateBbType">
						<itera:write name="userContext" property="perms.userHasBbTypeUpdatePermission(${type})" escapeXml="false" />
					</c:set>
					<c:set var="permissionCreateBbType">
						<itera:write name="userContext" property="perms.userHasBbTypeCreatePermission(${type})" escapeXml="false" />
					</c:set>
					
					
					<ul class="nav nav-list">
						<c:if test="${permissionCreateBbType}">
							<li>
								<fmt:message var="type_to_create" key="${type}" />
								
								<a id="newElementFromSearch" href="#" onclick="${createNewJavascript}">
									<i class="icon-plus"></i>
									<span rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key='search.newElement.tooltip'/>">
										<fmt:message key="contextMenu.actions.createNew.param"><fmt:param value="${type_to_create}" /></fmt:message>
									</span>
								</a>
							</li>
						</c:if>
						<c:if test="${permissionTabularReporting}">
							<li>
								<a id="tabularReportingLink" href="javascript:changeLocation('${tabReportingUrl}');">
									<i class="icon-th-list"></i>
									<fmt:message key="global.report.text" />
								</a>
							</li>
						</c:if>
						<c:if test="${permissionMassUpdate && permissionUpdateBbType}">
							<li>
								<a href="javascript:changeLocation('${massUpdateUrl}');">
									<i class="icon-pencil"></i>
									<fmt:message key="global.mass_updates" />
								</a>
							</li>
						</c:if>
						<c:if test="${guiContext.listableOpenElements}">
							<li>
								<fmt:message var="type_to_close" key="${pluraltype}" />
								
								<a href="<c:out value="${closeAllUrl}" escapeXml="false"/>">
									<i class="icon-remove-circle"></i>
									<fmt:message key="contextMenu.actions.closeAll.param"><fmt:param value="${type_to_close}" /></fmt:message>
								</a>
							</li>
						</c:if>
					</ul>
				</c:when>
				
				<c:when test="${isCommonGovernanceDialog}">
					<ul class="nav nav-list">						
						<li>
							<a id="newElementFromSearch" href="#" onclick="${createNewJavascript}">
								<i class="icon-plus"></i>
								<span rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key='search.newElement.tooltip'/>"><fmt:message key="button.create" /></span>
							</a>
						</li>
						<c:if test="${guiContext.listableOpenElements}">
							<li>
								<a href="<c:out value="${closeAllUrl}" escapeXml="false"/>">
									<i class="icon-remove-circle"></i>
									<fmt:message key="contextMenu.actions.closeAll" />
								</a>
							</li>
						</c:if>
					</ul>	
				</c:when>
				
				<c:when test="${isCommonVisualDialog}">
					<ul class="nav nav-list">						
						<li>
							<a href="javascript:flowAction('close');">
								<i class="icon-repeat"></i>&nbsp;<fmt:message key="button.reset" />
							</a>
						</li>
					</ul>
				</c:when>
				
				<c:when test="${isTabularReportOrMassUpdate}">
					<ul class="nav nav-list">						
						<li>
							<a href="<c:out value="${closeAllUrl}" escapeXml="false"/>">
								<i class="icon-repeat"></i>&nbsp;<fmt:message key="button.reset" />
							</a>
						</li>
					</ul>
				</c:when>
				
				<c:otherwise>
					<fmt:message key="global.contextMenu.actions.empty" />
				</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</div>
