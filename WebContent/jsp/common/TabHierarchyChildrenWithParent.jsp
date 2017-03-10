<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<div id="TabHierarchyChildrenWithParentModul" class="row-fluid module">
	<div class="row-fluid">
		<div class="module-body">
			<div class="row-fluid">
				<tiles:insertTemplate template="/jsp/common/OneAssociationComponentComboboxView.jsp">
					<tiles:putAttribute name="path_to_componentModel" value="componentModel.parentModel" />
					<tiles:putAttribute name="dynamically_loaded" value="true" />
					<tiles:putAttribute name="required" value="true" />
				</tiles:insertTemplate>
			</div>
		</div>
	</div>
</div>

<tiles:insertTemplate template="/jsp/common/ManyAssociationListComponentView.jsp">
	<tiles:putAttribute name="path_to_componentModel" value="componentModel.childrenModel" />
	<tiles:putAttribute name="available_elements_field_style" value="nameforhierarchy" />
	<tiles:putAttribute name="sort_field" value="componentModel.childrenModel.sortOrder" />
	<tiles:putAttribute name="disallowElementDeletion" value="${virtualElementSelected}" /> 
	<tiles:putListAttribute name="connected_elements_field_styles">
		<tiles:addAttribute value="name"/>
		<tiles:addAttribute value="descriptionintable"/>
	</tiles:putListAttribute>
	<tiles:putAttribute name="dynamically_loaded" value="true" />
</tiles:insertTemplate>