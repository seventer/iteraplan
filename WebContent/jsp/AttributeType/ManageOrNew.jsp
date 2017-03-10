<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<c:set var="componentMode" value="${memBean.componentModel.componentMode}" scope="request"/>  
<c:set var="functionalPermission" value="${userContext.perms.userHasFuncPermAttributes}" scope="request" />

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
			  <tiles:putAttribute name="require_attribute_type_delete_confirmation" value="true" />
			  <tiles:putAttribute name="ignoreWritePermission" value="true" />
			</tiles:insertTemplate>
			
			<tiles:insertTemplate template="/jsp/common/ElementNameComponentView.jsp" flush="true">
			    <tiles:putAttribute name="path_to_componentModel" value="componentModel.nameModel" />
			    <tiles:putAttribute name="validate" value="false" />
			</tiles:insertTemplate>
			
			<tiles:insertTemplate template="/jsp/common/ElementDescriptionComponentView.jsp">
			    <tiles:putAttribute name="path_to_componentModel" value="componentModel.descriptionModel" />
			</tiles:insertTemplate>
			
			<%-- Insert the Properties --%>
			<tiles:insertTemplate template="/jsp/common/EnumComponentComboboxView.jsp">
				<tiles:putAttribute name="path_to_componentModel" value="componentModel.typeOfAttributeModel" />
			</tiles:insertTemplate>
			
			<tiles:insertTemplate template="/jsp/common/OneAssociationComponentComboboxView.jsp">
				<tiles:putAttribute name="path_to_componentModel" value="componentModel.attributeTypeGroupModel" />
				<tiles:putAttribute name="available_elements_label" value="name" />
				<tiles:putAttribute name="select_box_style" value="name" />
			</tiles:insertTemplate>
			
  			<tiles:insertTemplate template="/jsp/common/BooleanComponentCheckboxView.jsp">
    			<tiles:putAttribute name="path_to_componentModel" value="componentModel.mandatoryModel" />
  			</tiles:insertTemplate>
		
			<c:if test="${memBean.componentModel.managedTypeOfAttribute.name == 'attribute.type.enum'}">
				<tiles:insertTemplate template="/jsp/common/BooleanComponentCheckboxView.jsp">
					<tiles:putAttribute name="path_to_componentModel" value="componentModel.multivalueModel" />
				</tiles:insertTemplate>
				<c:if test="${guiContext.timeseriesEnabled == 'true'}">
					<tiles:insertTemplate template="/jsp/common/BooleanComponentCheckboxView.jsp">
						<tiles:putAttribute name="path_to_componentModel" value="componentModel.timeseriesModel" />
					</tiles:insertTemplate>
				</c:if>
	  	</c:if>
		  
			<c:if test="${memBean.componentModel.managedTypeOfAttribute.name == 'attribute.type.number'}">
				<tiles:insertTemplate template="/jsp/common/BigDecimalComponentInputView.jsp">
					<tiles:putAttribute name="path_to_componentModel" value="componentModel.lowerBoundModel" />
				</tiles:insertTemplate>
				<tiles:insertTemplate template="/jsp/common/BigDecimalComponentInputView.jsp">
					<tiles:putAttribute name="path_to_componentModel" value="componentModel.upperBoundModel" />
				</tiles:insertTemplate>
				<tiles:insertTemplate template="/jsp/common/StringComponentInputView.jsp">
					<tiles:putAttribute name="path_to_componentModel" value="componentModel.unitModel" />
				</tiles:insertTemplate>
				<tiles:insertTemplate template="/jsp/AttributeType/RangeSetting.jsp">
					<tiles:putAttribute name="path_to_componentModel" value="componentModel.rangesModel" />
				</tiles:insertTemplate>
				<c:if test="${guiContext.timeseriesEnabled == 'true'}">
					<tiles:insertTemplate template="/jsp/common/BooleanComponentCheckboxView.jsp">
						<tiles:putAttribute name="path_to_componentModel" value="componentModel.timeseriesModel" />
					</tiles:insertTemplate>
				</c:if>
			</c:if>
		  
			<c:if test="${memBean.componentModel.managedTypeOfAttribute.name == 'attribute.type.text'}">
		    	<tiles:insertTemplate template="/jsp/common/BooleanComponentCheckboxView.jsp">
		      		<tiles:putAttribute name="path_to_componentModel" value="componentModel.multiLineModel" />
			    </tiles:insertTemplate>
		  	</c:if>
		  
			<c:if test="${memBean.componentModel.managedTypeOfAttribute.name == 'attribute.type.responsibility'}">
				<tiles:insertTemplate template="/jsp/common/BooleanComponentCheckboxView.jsp">
					<tiles:putAttribute name="path_to_componentModel" value="componentModel.multiAssignmentTypeModel" />
				</tiles:insertTemplate>
			</c:if>
	
			<c:if test="${memBean.componentModel.managedTypeOfAttribute.name == 'attribute.type.enum'}" >
				<tiles:insertTemplate template="/jsp/AttributeType/tiles/EnumAttributeValuesComponentListView.jsp">
			    	<tiles:putAttribute name="path_to_componentModel" value="componentModel.enumAttributeValuesModel" />
			    	<tiles:putAttribute name="sort_event" value="sort" />
			    	<tiles:putAttribute name="sort_field" value="componentModel.enumAttributeValuesModel.sortOrder" />
				</tiles:insertTemplate>
			</c:if>
			
			<c:if test="${memBean.componentModel.managedTypeOfAttribute.name == 'attribute.type.responsibility'}" >
				<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxView.jsp" flush="true">
					<tiles:putAttribute name="path_to_componentModel" value="componentModel.responsibilityAttributeValuesModel" />
			    	<tiles:putAttribute name="askBeforeDelete" value="true"/>
			    	<tiles:putAttribute name="confirmDeleteAttributeValueAssignmentMessageKey" value= "global.confirmDeletionOfAttributeValueAssignmentsUe" />
			  	</tiles:insertTemplate>
			</c:if>
			
			<%-- Insert the Tabs --%>
			<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxView.jsp" flush="true">
				<tiles:putAttribute name="path_to_componentModel" value="componentModel.buildingBlockTypeModel"/>
				<tiles:putAttribute name="isLinked" value="false"/>
				<tiles:putAttribute name="askBeforeDelete" value="true"/>
				<tiles:putAttribute name="confirmDeleteAttributeValueAssignmentMessageKey" value="global.confirmDeletionOfAttributeValueAssignments" />
				<tiles:putAttribute name="addColumColspan" value="2" />
			</tiles:insertTemplate>
			
			<%-- Insert the second TransactionBar for save & cancel at the bottom --%>
			<c:if test="${componentMode != 'READ'}">
				<tiles:insertTemplate template="/jsp/common/TransactionBar.jsp">
				  <tiles:putAttribute name="showBuildingBlockCopyButton" value="true"/>
				  <tiles:putAttribute name="require_attribute_type_delete_confirmation" value="true" />
				  <tiles:putAttribute name="ignoreWritePermission" value="true" />
				</tiles:insertTemplate>
			</c:if>
		</c:when>
		<c:otherwise>
			<tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
		</c:otherwise>
	</c:choose>
</div>