<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<div id="ListConnectedElementsTakeOverModul" class="row-fluid module">
	<div class="module-heading">
		<fmt:message key="attribute.copy.predecessor" />
	</div>
	<div class="row-fluid">
		<div class="module-body">
			<div class="row-fluid">
				<div class="control-group">
					<div class="controls">
						<form:checkbox path="componentModel.copyAttributeValues" id="copyAttributeValues_checkbox" />
					</div>
					<label class="control-label-right" for="copyAttributeValues_checkbox">
				  		<fmt:message key="attribute.copy.values" />
				  	</label>
				</div>
				<div class="control-group">
					<div class="controls">
						<form:checkbox path="componentModel.copyAssociatedBuildingBlockTypes" id="copyAssocLabel_checkbox" />
					</div>
					<label class="control-label-right" for="copyAssocLabel_checkbox">
				  		<fmt:message key="attribute.copy.bbt" />
				  	</label>
				</div>
			</div>
		</div>
	</div>
</div>

<fmt:message key="attribute.copy.checkBoxes" />!