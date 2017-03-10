<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxView.jsp" flush="true">
	<tiles:putAttribute name="path_to_componentModel" value="componentModel.businessDomainModel" />
	<tiles:putAttribute name="dynamically_loaded" value="true" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxView.jsp" flush="true">
  <tiles:putAttribute name="path_to_componentModel" value="componentModel.businessFunctionModel" />
  <tiles:putAttribute name="dynamically_loaded" value="true" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxView.jsp" flush="true">
	<tiles:putAttribute name="path_to_componentModel" value="componentModel.informationSystemReleaseModel" />
	<tiles:putAttribute name="isAttributable" value="true" />
	<tiles:putAttribute name="dynamically_loaded" value="true" />
</tiles:insertTemplate>

<tiles:insertTemplate template="/jsp/BusinessObject/tiles/JumpToInterfaceComponentView.jsp" flush="true">
	<tiles:putAttribute name="path_to_componentModel" value="componentModel.informationSystemInterfaceModel" />
	<c:choose>
		<c:when test="${componentMode == 'CREATE'}">
			<tiles:putAttribute name="showNewConnectionLink" value="false" />
		</c:when>
		<c:otherwise>
			<tiles:putAttribute name="showNewConnectionLink" value="true" />
		</c:otherwise>
	</c:choose>
</tiles:insertTemplate>


<tiles:insertTemplate template="/jsp/BusinessObject/tiles/JumpToInformationSystemReleasesOfIsiComponentView.jsp" flush="true">
	<tiles:putAttribute name="path_to_componentModel" value="componentModel.informationSystemReleasesOfIsiModel" />
	<c:choose>
		<c:when test="${componentMode == 'CREATE'}">
			<tiles:putAttribute name="showNewConnectionLink" value="false" />
		</c:when>
		<c:otherwise>
			<tiles:putAttribute name="showNewConnectionLink" value="true" />
		</c:otherwise>
	</c:choose>
</tiles:insertTemplate>
