<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<c:set var="functionalPermission" value="${userContext.perms.userHasFuncPermAttributes}" scope="request" />
<c:set var="componentMode" value="${memBean.componentModel.componentMode}" scope="request" />
<tiles:insertTemplate template="/jsp/common/TransactionBar.jsp">
  <tiles:putAttribute name="showBuildingBlockCopyButton" value="true"/>
  <tiles:putAttribute name="ignoreWritePermission" value="true" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/common/ElementNameComponentView.jsp" flush="true">
  <tiles:putAttribute name="path_to_componentModel" value="componentModel.nameModel" />
  <tiles:putAttribute name="validate" value="false" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/common/ElementDescriptionComponentView.jsp">
  <tiles:putAttribute name="path_to_componentModel" value="componentModel.descriptionModel" />
</tiles:insertTemplate>

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

<tiles:insertTemplate template="/jsp/AttributeType/ListConnectedElementsTakeOver.jsp" />
			
<%-- Insert the second TransactionBar for save & cancel at the bottom --%>
<c:if test="${componentMode != 'READ'}">
	<tiles:insertTemplate template="/jsp/common/TransactionBar.jsp">
	  <tiles:putAttribute name="showBuildingBlockCopyButton" value="true"/>
	  <tiles:putAttribute name="ignoreWritePermission" value="true" />
	</tiles:insertTemplate>
</c:if>
				
