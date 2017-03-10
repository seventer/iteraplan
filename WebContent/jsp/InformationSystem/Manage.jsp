<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<tiles:useAttribute name="doCopyAll" ignore="true" />

<script type='text/javascript' src="<c:url value="/dwr/interface/InformationSystemReleaseService.js" />"></script>

<script type="text/javascript">
   addTabShortcuts();
</script>

<c:set var="componentMode" value="${memBean.componentModel.componentMode}" scope="request" />

<c:set var="functionalPermission" scope="request">
	<c:set var="informationSystem" value="informationSystem.singular" />
	<itera:write name="userContext"
		property="perms.userHasBbTypeFunctionalPermission(${informationSystem})" escapeXml="false" />
</c:set>

<%-- set a different class for read and write mode --%>
<c:choose>
	  <c:when test="${(componentMode != 'READ')}">
	    <c:set var="componentModeClass" value="write" />
	  </c:when>
	  <c:otherwise>
	      <c:set var="componentModeClass" value="read" />
	  </c:otherwise>
</c:choose>

<div class="${componentModeClass}">
	<c:choose>
		<c:when test="${functionalPermission == true}">
			
			<%--Start construction of page --%>
			<tiles:insertTemplate template="/jsp/common/TransactionBar.jsp">
				<tiles:putAttribute name="showNewReleaseButton" value="true" />
				<tiles:putAttribute name="showBuildingBlockCopyButton" value ="true" />
				<tiles:putAttribute name="showSealButtons" value="true" />
				<tiles:putAttribute name="service_class" value="InformationSystemReleaseService" />
				<tiles:putAttribute name="subscribed_element" value="${memBean.componentModel}" />
				<tiles:putAttribute name="element_id" value="${id}" />
				<tiles:putAttribute name="buildingBlockAffectedByDelete" value="true" />
			</tiles:insertTemplate>
			
			<tiles:insertTemplate template="/jsp/common/ManageReleasename.jsp">
				<tiles:putAttribute name="icon_suffix" value="informationsystem" />
				<tiles:putAttribute name="overviewMode" value="true" />
				<tiles:putAttribute name="element_html_id" value="${memBean.componentModel.releaseNameModel.elementHtmlId}" />
				<tiles:putAttribute name="release_html_id" value="${memBean.componentModel.releaseNameModel.releaseHtmlId}" />
				<tiles:putAttribute name="doCopyAll" value="${doCopyAll}" />
			</tiles:insertTemplate>
		
			<tiles:insertTemplate template="/jsp/common/ElementDescriptionComponentView.jsp">
				<tiles:putAttribute name="path_to_componentModel" value="componentModel.descriptionModel" />
			</tiles:insertTemplate>
			
			<%-- Insert Table for elementComponent properties --%>
			<div id="MainAttributesModule" class="row-fluid module" style="border:0;">
				<div class="row-fluid">
					<div class="module-body">
						<div class="row-fluid">
							<tiles:insertTemplate template="/jsp/common/RuntimePeriodComponentView.jsp">
								<tiles:putAttribute name="path_to_componentModel" value="componentModel.runtimePeriodModel" />
								<tiles:putAttribute name="path_to_componentModelName" value="componentModel.releaseNameModel" />
							</tiles:insertTemplate>
							
							<tiles:insertTemplate template="/jsp/common/PersistentEnumComponentComboboxView.jsp">
								<tiles:putAttribute name="path_to_componentModel" value="componentModel.statusModel" />
							</tiles:insertTemplate>

							<tiles:insertTemplate template="/jsp/common/OneAssociationComponentComboboxView.jsp">
								<tiles:putAttribute name="path_to_componentModel" value="componentModel.parentModel" />
								<tiles:putAttribute name="dynamically_loaded" value="true" />
							</tiles:insertTemplate>
						</div>
						<div class="row-fluid">
							<tiles:insertTemplate template="/jsp/common/attributes/AttributesComponentView.jsp">
								<tiles:putAttribute name="path_to_componentModel" value="componentModel.toplevelAttributeModel" />
								<tiles:putAttribute name="showHeaderTable" value="false" />
								<tiles:putAttribute name="showATGHeaderTable" value="false" />
								<tiles:putAttribute name="single_col" value="true" />
							</tiles:insertTemplate>
						</div>
					</div>
				</div>
			</div>
			
			<%-- Insert the Tabs --%>
			<itera:tabgroup id="ConfigTabs" maxTabCountPerLine="5">
				<itera:tab id="TabHierarchy" 	textKey="global.hierarchy" 		page="/jsp/InformationSystem/tabPages/TabHierarchy.jsp" />
				<itera:tab id="TabAssignments" 	textKey="button.assignments" 	page="/jsp/InformationSystem/tabPages/TabAssignments.jsp" />
				<itera:tab id="TabAttributes" 	textKey="global.attributes" 	page="/jsp/common/TabAttributes.jsp" />
				<itera:tab id="TabPermissions" 	textKey="global.permissions" 	page="/jsp/common/TabPermissions.jsp" />
				<c:if test="${componentMode == 'READ'}">	
					<itera:tab id="FastExport"  textKey="global.fastExport" 	page="/jsp/InformationSystem/tabPages/TabFastExportInformationSystem.jsp" />
				</c:if>
				<c:if test="${componentMode == 'READ' && isHistoryEnabled}">
					<itera:tab id="TabLocalHistory" textKey="global.history"    page="/jsp/InformationSystem/tabPages/TabHistory.jsp"/>
				</c:if> 
			</itera:tabgroup>
			
			<%-- Insert the second TransactionBar for save & cancel at the bottom --%>
			<c:if test="${componentMode != 'READ'}">
				<tiles:insertTemplate template="/jsp/common/TransactionBar.jsp">
					<tiles:putAttribute name="showNewReleaseButton" value="true" />
					<tiles:putAttribute name="showBuildingBlockCopyButton" value ="true" />
					<tiles:putAttribute name="showSealButtons" value="true" />
					<tiles:putAttribute name="service_class" value="InformationSystemReleaseService" />
					<tiles:putAttribute name="subscribed_element" value="${memBean.componentModel}" />
					<tiles:putAttribute name="element_id" value="${id}" />
				</tiles:insertTemplate>
			</c:if>
		</c:when>
		<c:otherwise>
			<tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
		</c:otherwise>
	</c:choose>
</div>