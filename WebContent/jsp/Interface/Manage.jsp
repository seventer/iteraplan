<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type="text/javascript">
   addTabShortcuts();
</script>

<itera:define id="componentMode" name="memBean" property="componentModel.componentMode" toScope="session"/>
 
<c:set var="functionalPermission" scope="request">
	<c:set var="interfaceNumerus" value="interface.singular" />
	<itera:write name="userContext"
		property="perms.userHasBbTypeFunctionalPermission(${interfaceNumerus})" escapeXml="false" />
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
			<tiles:insertTemplate template="/jsp/common/TransactionBar.jsp" >
				<tiles:putAttribute name="showBuildingBlockCopyButton" value="true"/>
				<tiles:putAttribute name="service_class" value="InformationSystemInterfaceService" />
				<tiles:putAttribute name="subscribed_element" value="${memBean.componentModel}" />
				<tiles:putAttribute name="element_id" value="${id}" />
				<tiles:putAttribute name="buildingBlockAffectedByDelete" value="true" />
			</tiles:insertTemplate>
			
			<c:choose>
			  <c:when test="${componentMode == 'READ' || componentMode == 'EDIT'}">
			    <tiles:insertTemplate template="/jsp/Interface/InfoInterface.jsp" flush="true"/>
			  </c:when>
			  <c:otherwise>
			    <tiles:insertTemplate template="/jsp/Interface/InfoNewInterface.jsp" flush="true"/>
			  </c:otherwise>
			</c:choose>
			
            <%-- Top-level attribute block --%>
			<tiles:insertTemplate template="/jsp/common/attributes/AttributesComponentView.jsp">
				<tiles:putAttribute name="path_to_componentModel" value="componentModel.toplevelAttributeModel" />
				<tiles:putAttribute name="showHeaderTable" value="false" />
				<tiles:putAttribute name="showATGHeaderTable" value="false" />
				<tiles:putAttribute name="single_col" value="true" />
			</tiles:insertTemplate>
				
			<%-- Insert the Tabs --%>
			<itera:tabgroup id="ConfigTabs">
			  <itera:tab id="TabAssignments" 	textKey="button.assignments" 	page="/jsp/Interface/tabPages/TabAssignments.jsp" />
			  <itera:tab id="TabAttributes" 	textKey="global.attributes"		page="/jsp/common/TabAttributes.jsp" />
			  <itera:tab id="TabPermissions" 	textKey="global.permissions" 	page="/jsp/common/TabPermissions.jsp" />
			  <c:if test="${componentMode == 'READ' && isHistoryEnabled}">
				<itera:tab id="TabLocalHistory" textKey="global.history"    page="/jsp/Interface/tabPages/TabHistory.jsp"/>
			  </c:if> 
			</itera:tabgroup>
			
						<%-- Insert the second TransactionBar for save & cancel at the bottom --%>
			<c:if test="${componentMode != 'READ'}">
				<tiles:insertTemplate template="/jsp/common/TransactionBar.jsp">
					<tiles:putAttribute name="showBuildingBlockCopyButton" value="true"/>
					<tiles:putAttribute name="service_class" value="InformationSystemInterfaceService" />
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