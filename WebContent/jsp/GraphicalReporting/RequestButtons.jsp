<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<tiles:useAttribute name="permissionSaveReports" />
<tiles:useAttribute name="availableGraphicFormats" />
<tiles:useAttribute name="exportFormatPath" />

<div class="ReportingRequestButtons">
	<tiles:insertTemplate template="/jsp/GraphicalReporting/ShowSelectExportFormatForm.jsp">
		<tiles:putAttribute name="availableGraphicFormats" value="${availableGraphicFormats}" />
		<tiles:putAttribute name="exportFormatPath" value="${exportFormatPath}" />
	</tiles:insertTemplate>
	&nbsp;
	<a id="generateExport" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key='button.generate.tooltip'/>" class="link btn btn-primary button-listener" href="#" onclick="flowAction('triggerDownloadEvent');">
		<i class="icon-download-alt icon-white"></i>
		<fmt:message key="graphicalExport.generate" />
	</a>
	&nbsp;
	<tiles:insertTemplate template="/jsp/commonReporting/ShowSaveQuery.jsp">
		<tiles:putAttribute name="xmlSaveAsQueryName" value="xmlSaveAsQueryName"/>
		<tiles:putAttribute name="xmlSaveAsQueryDescription" value="xmlSaveAsQueryDescription"/>
		<tiles:putAttribute name="xmlQueryName" value="xmlQueryName" />
		<tiles:putAttribute name="permissionSaveReports" value="${permissionSaveReports}" />
	</tiles:insertTemplate>
</div>