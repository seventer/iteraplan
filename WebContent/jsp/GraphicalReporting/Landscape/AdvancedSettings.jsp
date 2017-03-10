<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<c:set var="contentType" scope="page">
	<fmt:message key="${memBean.graphicalOptions.selectedBbType}"/>
</c:set>


<div class="accordion" id="advancedSettingsIdContainer">
	<div class="accordion-group">
		<div class="accordion-heading">
			<a class="accordion-toggle" data-toggle="collapse" data-parent="#advancedSettingsIdContainer" href="#advancedSettingsId"
					onclick="toggleIcon('advancedSettingsIcon', 'icon-resize-full', 'icon-resize-small');" >
				<i id="advancedSettingsIcon" class="icon-resize-full"></i>
				<fmt:message key="graphicalExport.helpAdvancedLabel" />
			</a>
		</div>
		<div id="advancedSettingsId" class="accordion-body collapse">
			<div class="accordion-inner">
				<div class="controls" style="float: none;">
					<fmt:message key="graphicalExport.landscape.configuration.content.orientation">
						<fmt:param value="${contentType}"/>
					</fmt:message>: 
					<form:select path="graphicalOptions.columnAxisScalesWithContent">
						<form:option value="true">
							<fmt:message key="graphicalExport.landscape.configuration.content.orientation.vertical"/>
						</form:option>
						<form:option value="false">
							<fmt:message key="graphicalExport.landscape.configuration.content.orientation.horizontal"/>
						</form:option>
					</form:select>
				</div>
				
				<div class="controls" style="float: none;">
					<label class="checkbox">
						<form:checkbox path="graphicalOptions.showUnspecifiedRelations" id="showUnspecifiedRelationsCheckbox" />
						<fmt:message key="graphicalExport.landscape.configuration.content.helpShowUnspecifiedRelations" />
					</label>
					<label class="checkbox">
						<form:checkbox path="graphicalOptions.spanContentBetweenCells" id="spanContentBetweenCellsCheckbox" />
						<fmt:message key="graphicalExport.landscape.configuration.content.helpSpanContentBetweenCells" />
					</label>
					<label class="checkbox">
						<form:checkbox path="graphicalOptions.scaleDownContentElements" id="scaleDownContentElementsCheckbox" />
						<fmt:message key="graphicalExport.landscape.configuration.content.helpScaleDownContentElements" />
					</label>
					<label class="checkbox">
						<form:checkbox path="graphicalOptions.globalScalingEnabled" id="globalScalingEnabledCheckbox" />
						<fmt:message key="graphicalExport.landscape.configuration.content.helpGlobalScalingEnabled" />
					</label>
					<label class="checkbox">
						<form:checkbox path="graphicalOptions.useNamesLegend" id="useNamesLegend" />
						<fmt:message key="graphicalExport.helpUseNamesLegend" />
					</label>
				</div>
				
				<div class="controls" style="float: none;">
					<c:choose>
			 		<c:when test="${memBean.graphicalOptions.businessMappingsBasedDiagram == true}">
						<p class="help-block">
					 		<fmt:message key="graphicalExport.landscape.configuration.content.helpMergeBusinessMappings1" />
						</p>
					 	<label class="checkbox">
							<form:checkbox path="graphicalOptions.strictRelations" id="strictRelationsCheckbox" />
					 		<fmt:message key="graphicalExport.landscape.configuration.content.helpMergeBusinessMappings2" />
						</label>
			 		</c:when>
			 		<c:when test="${memBean.graphicalOptions.selectedBbType == 'informationSystemRelease.plural'}">
			 			<label class="checkbox">
							<form:checkbox path="graphicalOptions.strictRelations" id="strictRelationsCheckbox" />
					 		<fmt:message key="graphicalExport.landscape.configuration.content.helpStrictRelations" />
					 	</label>
			 		</c:when>
		 			</c:choose>
		 		 	<label class="checkbox">
						<form:checkbox path="graphicalOptions.showSavedQueryInfo" id="showSavedQueryInfo"/>
						<fmt:message key="graphicalExport.showQueryInfo" />
					</label>
				</div>
			</div>
		</div>
	</div>
</div>