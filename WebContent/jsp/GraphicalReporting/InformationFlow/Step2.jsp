<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<c:set var="chosenContentType" scope="request">
  <fmt:message>
     <itera:write name="memBean" property="graphicalOptions.selectedBbType" escapeXml="false" />
  </fmt:message>
</c:set>

<c:if test="${memBean.reportUpdated}">
	<div class="alert alert-error">
		<a data-dismiss="alert" class="close" onclick="clearErrors();">×</a> 
		<span><fmt:message key="message.report.attributeNotFound" /></span>
	</div>
</c:if>

<tiles:insertTemplate template="/jsp/GraphicalReporting/ShowSelectedElements.jsp">
	<tiles:putAttribute name="messageKey" value="graphicalExport.informationflow.showChosenElements" />
	<tiles:putAttribute name="messageKeyArg" value="${chosenContentType}" />
	<tiles:putAttribute name="collection" value="queryResult.selectedResults" />
	<tiles:putAttribute name="field" value="identityString" />
</tiles:insertTemplate>

<div class="accordion" id="relevantInterfacesContainer">
	<div class="accordion-group">
		<div class="accordion-heading">
			<a class="accordion-toggle" data-toggle="collapse" data-parent="#relevantInterfacesContainer" href="#relevantInterfaces"
					onclick="toggleIcon('relevantInterfacesIcon', 'icon-resize-full', 'icon-resize-small');" >
				<i id="relevantInterfacesIcon" class="icon-resize-full"></i>
				<fmt:message key="graphicalExport.informationflow.relevantInterfaces" />
			</a>
		</div>
	    <div id="relevantInterfaces" class="accordion-body collapse">
			<div class="accordion-inner">
				<input type="button" class="link btn" onclick="flowAction('filterInterfaces');" value="<fmt:message key="button.filterInterfaces"/>" />
				<c:set var="collection" value="${memBean.graphicalOptions.relevantInterfaces}"/>
				<c:if test="${collection != null}">
					<input type="button" class="link btn" onclick="flowAction('resetInterfacesFilter');" value="<fmt:message key="button.reset"/>" />
					<br />
					<br />
					<tiles:insertTemplate template="/jsp/GraphicalReporting/ShowSelectedElements.jsp">
					  <tiles:putAttribute name="messageKey" value="graphicalExport.filterInterfaces.relevant" />
					  <tiles:putAttribute name="collection" value="graphicalOptions.relevantInterfaces" />
					  <tiles:putAttribute name="field" value="identityString" />
					  <tiles:putAttribute name="extendedOptionalField" value="name" />
					  <tiles:putAttribute name="simpleList" value="true" />
					</tiles:insertTemplate>
				</c:if>
			</div>
		</div>
	</div>
</div>
 
 <div class="accordion" id="relevantBusinessObjectsContainer">
    <div class="accordion-group">
        <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#relevantBusinessObjectsContainer" href="#relevantBusinessObjects"
					onclick="toggleIcon('relevantBusinessObjectsIcon', 'icon-resize-full', 'icon-resize-small');" >
				<i id="relevantBusinessObjectsIcon" class="icon-resize-full"></i>
            	<fmt:message key="graphicalExport.informationflow.relevantBusinessObjects" />
            </a>
		</div>
		<div id="relevantBusinessObjects" class="accordion-body collapse">
			<div class="accordion-inner">
				<input type="button" class="link btn" onclick="flowAction('filterBusinessObjects');" value="<fmt:message key="button.filterBusinessObjects"/>" />
				<c:set var="collection" value="${memBean.graphicalOptions.relevantBusinessObjects}"/>
				<c:if test="${collection != null}">
					<input type="button" class="link btn" onclick="flowAction('resetBusinessObjectsFilter');" value="<fmt:message key="button.reset"/>" />
					<br />
					<br />
					<tiles:insertTemplate template="/jsp/GraphicalReporting/ShowSelectedElements.jsp">
					  <tiles:putAttribute name="messageKey" value="graphicalExport.filterBusinessObjects.relevant" />
					  <tiles:putAttribute name="collection" value="graphicalOptions.relevantBusinessObjects" />
					  <tiles:putAttribute name="field" value="identityString" />
					  <tiles:putAttribute name="extendedOptionalField" value="name" />
					  <tiles:putAttribute name="simpleList" value="true" />
					</tiles:insertTemplate>
				</c:if>
			</div>
		</div>
	</div>
</div>

<tiles:insertTemplate template="/jsp/GraphicalReporting/InformationFlow/InformationFlowOptions.jsp"/>

<tiles:insertTemplate template="/jsp/GraphicalReporting/RequestButtons.jsp">
	<tiles:putAttribute name="permissionSaveReports" value="${permissionSaveReports}" />
	<tiles:putAttribute name="availableGraphicFormats" value="${memBean.graphicalOptions.availableGraphicFormats}" />
	<tiles:putAttribute name="exportFormatPath" value="graphicalOptions.selectedGraphicFormat" />
</tiles:insertTemplate>