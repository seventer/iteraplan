<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="resultPostSelection" value="true" scope="request" />
<c:set var="permissionSaveReports" value="${userContext.perms.userHasFuncPermGraphReportingFull}" scope="request" />

<h1><fmt:message key="graphicalExport.pieDiagram" /></h1>

<br/>

<!-- Selected Elements -->
<fmt:message var="chosenContentType" scope="request" key="${memBean.graphicalOptions.selectedBbType}" />
<tiles:insertTemplate template="/jsp/GraphicalReporting/ShowSelectedElements.jsp">
	<tiles:putAttribute name="messageKey" value="graphicalExport.pieBar.showChosenElements" />
	<tiles:putAttribute name="messageKeyArg" value="${chosenContentType}" />
	<tiles:putAttribute name="collection" value="queryResult.selectedResults" />
	<tiles:putAttribute name="field" value="identityString" />
</tiles:insertTemplate>

<!-- Color Settings -->
<tiles:insertTemplate template="ColorSettings.jsp" />

<!-- Advanced Settings -->
<div class="accordion" id="advancedSettingsContainer">
    <div class="accordion-group">
        <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#advancedSettingsContainer" href="#advancedSettings"
					onclick="toggleIcon('collapseIcon', 'icon-resize-full', 'icon-resize-small');" >
			   <i id="collapseIcon" class="icon-resize-full"></i>
               <fmt:message key="graphicalExport.helpAdvancedLabel" />
            </a>
        </div>
	    <div id="advancedSettings" class="accordion-body collapse">
			<div class="accordion-inner">
			  <div class="controls" style="float: none;">
			  	<label class="checkbox">
			  		<form:checkbox path="graphicalOptions.showSegmentLabels" id="showSegmentLabels" />
					<fmt:message key="graphicalExport.pieBar.helpShowSegmentLabels" />
			  	</label>
			  	<label class="checkbox">
					<form:checkbox path="graphicalOptions.showSavedQueryInfo" id="showSavedQueryInfo"/>
					<fmt:message key="graphicalExport.showQueryInfo" />
			  	</label>
			  </div>
			</div>
	     </div>
	</div>
</div>

<tiles:insertTemplate template="/jsp/GraphicalReporting/RequestButtons.jsp">
	<tiles:putAttribute name="permissionSaveReports" value="${permissionSaveReports}" />
	<tiles:putAttribute name="availableGraphicFormats" value="${memBean.graphicalOptions.availableGraphicFormats}" />
	<tiles:putAttribute name="exportFormatPath" value="graphicalOptions.selectedGraphicFormat" />
</tiles:insertTemplate>