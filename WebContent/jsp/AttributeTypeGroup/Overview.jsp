<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="functionalPermission" value="${userContext.perms.userHasFuncPermAttributeGroups}" scope="request" />
<c:set var="componentMode" value="${memBean.componentModel.componentMode}" scope="request" />

<%-- set a different class for read and write mode --%>
<c:choose>
	  <c:when test="${(componentMode != 'READ')}">
	    <c:set var="componentModeClass" value="write" />
	  </c:when>
	  <c:otherwise>
	      <c:set var="componentModeClass" value="read" />
	  </c:otherwise>
</c:choose>

<c:if test="${componentMode == 'READ'}">
<%-- As this is an exception from the standard, we need to overwrite the standard TransactionBar-Buttons: --%>
<c:set var="baseURIRelative" value="../show/attributetypegroup" />
<c:set var="baseURIRelativeWithId" value="${baseURIRelative}/${memBean.componentModel.chooseAttributeTypeGroupComponentModel.currentId}" />
<script type="text/javascript">
// <![CDATA[
	function addAttributeTypeGroupButtons() {
        <%-- Modify Form Action before executing original button handler --%>
        $("#transactionEdit").each(function(idx, node) {
        	<%-- save previous handler, to append it later on --%>
            var prevHandler = node.onclick;
            <%-- and connect this function to its onclick handler --%>
            node.onclick = function () {
            	document.forms[0].action = '<c:out value="${baseURIRelativeWithId}"/>'; 
            	prevHandler(); 
            };
     	}); 
  
        <%-- Modify Form Action before executing original button handler --%>
        $("#transactionDelete").each(function(idx, node) {
        	<%-- save previous handler, to append it later on --%>
            var prevHandler = node.onclick;
            <%-- and connect this function to its onclick handler --%>
            node.onclick = function () {
            	document.forms[0].action = '<c:out value="${baseURIRelativeWithId}"/>';
            	prevHandler();
            };
     	});
  
	}
	$(document).ready(function(){
		addAttributeTypeGroupButtons();
	});
// ]]></script>
</c:if>

<div class="${componentModeClass}">
	<c:choose>
		<c:when test="${functionalPermission == true}">
			
			<%-- Insert the TransactionBar --%>
			<tiles:insertTemplate template="/jsp/common/TransactionBar.jsp">
				<tiles:putAttribute name="ignoreWritePermission" value="true" />
				<tiles:putAttribute name="showCloseButton" value="false" />
				<tiles:putAttribute name="showBookmarkAndPrint" value="false" />
			</tiles:insertTemplate>
			
			<tiles:insertTemplate template="/jsp/common/ElementNameComponentView.jsp" flush="true">
			    <tiles:putAttribute name="path_to_componentModel" value="componentModel.nameModel" />
			    <tiles:putAttribute name="validate" value="true" />    
			</tiles:insertTemplate>
			
			<tiles:insertTemplate template="/jsp/common/ElementDescriptionComponentView.jsp">
			    <tiles:putAttribute name="path_to_componentModel" value="componentModel.descriptionModel" />
			</tiles:insertTemplate>
			
			<c:choose>
				<c:when test="${componentMode == 'READ'}">
					<tiles:insertTemplate template="/jsp/common/BooleanComponentCheckboxView.jsp">
						<tiles:putAttribute name="path_to_componentModel" value="componentModel.toplevelATGModel" />
					</tiles:insertTemplate>
				</c:when>
				<c:otherwise>
					<label class="checkbox">
						<form:checkbox path="componentModel.toplevelATGModel.current" id="${html_id}_checkbox" />
						<fmt:message key="${memBean.componentModel.toplevelATGModel.labelKey}" />
					</label>
				</c:otherwise>
			</c:choose>
			  
			<c:if test="${componentMode == 'READ'}">
				<tiles:insertTemplate template="/jsp/AttributeTypeGroup/tiles/AttributeTypeGoupSelectionComponentView.jsp">
					<tiles:putAttribute name="path_to_componentModel" value="componentModel.chooseAttributeTypeGroupComponentModel" />
				</tiles:insertTemplate>
			</c:if>
			
			<%-- Contained attributes --%>
			<tiles:insertTemplate template="/jsp/common/ManyAssociationListComponentView.jsp">
				<tiles:putAttribute name="path_to_componentModel" value="componentModel.containedAttributeTypesModel" />
				<tiles:putAttribute name="available_elements_field_style" value="nameforhierarchy" />
				<tiles:putAttribute name="sort_field" value="componentModel.containedAttributeTypesModel.sortOrder" />
				<tiles:putListAttribute name="connected_elements_field_styles">
					<tiles:addAttribute value="name"/>
					<tiles:addAttribute value="descriptionintable"/>
				</tiles:putListAttribute>
			</tiles:insertTemplate>
		
			<c:choose>
				<c:when test="${componentMode == 'EDIT' || componentMode == 'CREATE'}">
					<%-- Roles with access permission --%>
					<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxView.jsp" flush="true">
						<tiles:putAttribute name="path_to_componentModel" value="componentModel.permissionRolesModel"/>
						<tiles:putAttribute name="header_postfix_edit" value="&nbsp;&nbsp;<sup>1)</sup>"/>
					</tiles:insertTemplate>
					<sup>1)</sup> <fmt:message key="messages.usersNeedToLoginAgain"/>
					<br/>
				</c:when>
				<c:otherwise>
					<%-- Aggregated permissions for attribute type groups --%>
					<tiles:insertTemplate template="/jsp/common/ManyAssociationSetReadOnlyComponentView.jsp" flush="true">
						<tiles:putAttribute name="path_to_componentModel" value="componentModel.aggregatedPermissionAtgModel"/>
					</tiles:insertTemplate>
				</c:otherwise>
			</c:choose>
			
			<%-- Insert the second TransactionBar for save & cancel at the bottom --%>
			<c:if test="${componentMode != 'READ'}">
				<tiles:insertTemplate template="/jsp/common/TransactionBar.jsp">
					<tiles:putAttribute name="ignoreWritePermission" value="true" />
					<tiles:putAttribute name="showCloseButton" value="false" />
					<tiles:putAttribute name="showBookmarkAndPrint" value="false" />
				</tiles:insertTemplate>
			</c:if>
		</c:when>
		
		<c:otherwise>
			<tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
		</c:otherwise>
	</c:choose>
</div>