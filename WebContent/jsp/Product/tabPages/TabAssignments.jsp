<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxView.jsp" flush="true">
  <tiles:putAttribute name="path_to_componentModel" value="componentModel.businessDomainModel" />
  <tiles:putAttribute name="dynamically_loaded" value="true" />
</tiles:insertTemplate>

<tiles:insertTemplate flush="true" template="/jsp/common/businessmapping/MappingComponentView.jsp">
  <tiles:putAttribute name="path_to_componentModel" value="componentModel.businessMappingModel" />
</tiles:insertTemplate>
