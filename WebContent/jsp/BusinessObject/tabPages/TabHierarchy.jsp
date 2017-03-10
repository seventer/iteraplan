<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insertTemplate template="/jsp/common/TabHierarchyChildrenWithParent.jsp" />

<div id="TabHierarchyModul" class="row-fluid module">
	<div class="row-fluid">
		<div class="module-body">
			<div class="row-fluid">
				<tiles:insertTemplate template="/jsp/common/OneAssociationComponentComboboxView.jsp" flush="true">
					<tiles:putAttribute name="path_to_componentModel" value="componentModel.specialisationModel" />
					<tiles:putAttribute name="dynamically_loaded" value="true" />
				</tiles:insertTemplate>
			</div>
		</div>
	</div>
</div>

<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxView.jsp" flush="true">
	<tiles:putAttribute name="path_to_componentModel" value="componentModel.generalisationModel" />
	<tiles:putAttribute name="dynamically_loaded" value="true" />
</tiles:insertTemplate>
