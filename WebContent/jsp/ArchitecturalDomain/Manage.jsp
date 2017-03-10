<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<script type='text/javascript' src="<c:url value="/dwr/interface/ArchitecturalDomainService.js" />"></script>

<script type="text/javascript">
   addTabShortcuts();
</script>

<c:set var="componentMode" value="${memBean.componentModel.componentMode}" scope="request" />
<c:set var="virtualElementSelected" value="${memBean.componentModel.nameModel.virtualElementSelected}" scope="request" />

<c:set var="functionalPermission" scope="request">
	<c:set var="architecturalDomain" value="architecturalDomain.singular" />
	<itera:write name="userContext"
		property="perms.userHasBbTypeFunctionalPermission(${architecturalDomain})" escapeXml="false" />
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
			
			<%-- Insert the TransactionBar --%>
			<tiles:insertTemplate template="/jsp/common/TransactionBar.jsp">
				<tiles:putAttribute name="showBuildingBlockCopyButton" value="true"/>
				<tiles:putAttribute name="service_class" value="ArchitecturalDomainService" />
				<tiles:putAttribute name="subscribed_element" value="${memBean.componentModel}" />
				<tiles:putAttribute name="element_id" value="${id}" />
				<tiles:putAttribute name="buildingBlockAffectedByDelete" value="true" />
			</tiles:insertTemplate>
			
			<%-- Both, Name and Description of Component Model must be grayed out,
				if the VirtualElement is selected --%>
			<tiles:insertTemplate template="/jsp/common/ElementNameComponentView.jsp" flush="true">
				<tiles:putAttribute name="icon_suffix" value="architecturaldomain" />
				<tiles:putAttribute name="path_to_componentModel" value="componentModel.nameModel" />
				<tiles:putAttribute name="virtualElementSelected" value="${virtualElementSelected}" />
				<tiles:putAttribute name="validate" value="true" />
			</tiles:insertTemplate>
			
			<tiles:insertTemplate template="/jsp/common/ElementDescriptionComponentView.jsp">
				<tiles:putAttribute name="path_to_componentModel" value="componentModel.descriptionModel" />
				<tiles:putAttribute name="virtualElementSelected" value="${virtualElementSelected}" />
			</tiles:insertTemplate>
			
			<tiles:insertTemplate template="/jsp/common/attributes/AttributesComponentView.jsp">
				<tiles:putAttribute name="path_to_componentModel" value="componentModel.toplevelAttributeModel" />
				<tiles:putAttribute name="showHeaderTable" value="false" />
				<tiles:putAttribute name="showATGHeaderTable" value="false" />
				<tiles:putAttribute name="single_col" value="true" />
			</tiles:insertTemplate>
				
			<%-- Insert the Tabs --%>
			<itera:tabgroup id="ConfigTabs">
				<itera:tab id="TabHierarchy" 	textKey="global.hierarchy"		page="/jsp/common/TabHierarchyChildrenWithParent.jsp" />
				<itera:tab id="TabAssignments" 	textKey="button.assignments"	page="/jsp/ArchitecturalDomain/tabPages/TabAssignments.jsp" />
				<itera:tab id="TabAttributes" 	textKey="global.attributes" 	page="/jsp/common/TabAttributes.jsp" />
				<itera:tab id="TabPermissions" 	textKey="global.permissions" 	page="/jsp/common/TabPermissions.jsp" />
				<c:if test="${componentMode == 'READ' && isHistoryEnabled}">
					<itera:tab id="TabLocalHistory" textKey="global.history"    page="/jsp/ArchitecturalDomain/tabPages/TabHistory.jsp"/>
				</c:if> 
			</itera:tabgroup>
			
			<%-- Insert the second TransactionBar for save & cancel at the bottom --%>
			<c:if test="${componentMode != 'READ'}">
				<tiles:insertTemplate template="/jsp/common/TransactionBar.jsp">
					<tiles:putAttribute name="showBuildingBlockCopyButton" value="true"/>
					<tiles:putAttribute name="service_class" value="ArchitecturalDomainService" />
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