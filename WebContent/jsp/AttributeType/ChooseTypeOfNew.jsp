<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<div id="ChooseTypeOfNewModul" class="row-fluid module">
	<div class="module-heading">
		<fmt:message key="manageAttributes.chooseNewTypeHeader" />
	</div>
	<div class="row-fluid">
		<div class="module-body">
			<div class="row-fluid">
				<fmt:message key="manageAttributes.chooseNewTypeInfo" />.
				<br/>
				<div class="control-group">
					<div class="controls">
						<form:radiobutton path="attributeTypeToCreate" value="attribute.type.enum" id="attributeTypeEnum_radio" />
					</div>
					<label class="control-label-right" for="attributeTypeEnum_radio">
				  		<fmt:message key="attribute.type.enum" />
				  	</label>
				</div>
				<div class="control-group">
					<div class="controls">
						<form:radiobutton path="attributeTypeToCreate" value="attribute.type.number" id="attributeTypeNumber_radio" />
					</div>
					<label class="control-label-right" for="attributeTypeNumber_radio">
				  		<fmt:message key="attribute.type.number" />
				  	</label>
				</div>
				<div class="control-group">
					<div class="controls">
						<form:radiobutton path="attributeTypeToCreate" value="attribute.type.text" id="attributeTypeText_radio" />
					</div>
					<label class="control-label-right" for="attributeTypeText_radio">
				  		<fmt:message key="attribute.type.text" />
				  	</label>
				</div>
				<div class="control-group">
					<div class="controls">
						<form:radiobutton path="attributeTypeToCreate" value="attribute.type.date" id="attributeTypeDate_radio" />
					</div>
					<label class="control-label-right" for="attributeTypeDate_radio">
				  		<fmt:message key="attribute.type.date" />
				  	</label>
				</div>
				<div class="control-group">
					<div class="controls">
						<form:radiobutton path="attributeTypeToCreate" value="attribute.type.responsibility" id="attributeTypeResponsibility_radio" />
					</div>
					<label class="control-label-right" for="attributeTypeResponsibility_radio">
				  		<fmt:message key="attribute.type.responsibility" />
				  	</label>
				</div>
				<div class="button-group">
					<input type="button" value="<fmt:message key="global.back" />" onclick="flowAction('cancel');" class="btn" />
	        		<input type="button" value="<fmt:message key="global.forward" />" onclick="flowAction('create');" class="btn" />
				</div>
			</div>
		</div>
	</div>
</div>