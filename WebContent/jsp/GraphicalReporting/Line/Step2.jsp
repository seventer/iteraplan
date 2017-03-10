<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<jwr:script src="/bundles/vbb.js" />
<jwr:style src="/bundles/vbb.css" />

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
	<tiles:putAttribute name="messageKey" value="graphicalExport.line.showChosenElements" />
	<tiles:putAttribute name="messageKeyArg" value="${chosenContentType}" />
	<tiles:putAttribute name="collection" value="queryResult.selectedResults" />
	<tiles:putAttribute name="field" value="identityString" />
</tiles:insertTemplate>

<div class="accordion" id="attributeContainer">
    <div class="accordion-group">
        <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#attributeContainer" href="#attribute"
					onclick="toggleIcon('attributeIcon', 'icon-resize-full', 'icon-resize-small');" >
			   <i id="attributeIcon" class="icon-resize-small"></i>
               <fmt:message key="graphicalExport.helpAttributeLabel" />
            </a>
        </div>
	    <div id="attribute" class="accordion-body in collapse">
			<div class="accordion-inner">
  			<p>
					<fmt:message key="graphicalExport.line.helpColor" />
				</p>
				<form:select path="graphicalOptions.selectedKeyAttributeTypeId">
					<fmt:message key="graphicalExport.line.selectAttribute"/>
					<c:forEach var="keyAttribute" items="${memBean.graphicalOptions.availableKeyAttributeTypes}">
					<form:option value="${keyAttribute.id}">
	  					<c:choose>
	  						<c:when test="${keyAttribute.id <= 0}">
	  							<fmt:message key="${keyAttribute.name}" />
	  						</c:when>
	  						<c:otherwise>
	  							${keyAttribute.name}
	  						</c:otherwise>
	  					</c:choose>
	  				</form:option>
					</c:forEach>
				</form:select>
			</div>
		</div>
	</div>
</div>

<div class="accordion" id="timespanSettingsContainer">
	<div class="accordion-group">
		<div class="accordion-heading">
			<a class="accordion-toggle" data-toggle="collapse" data-parent="#timespanSettingsContainer" href="#timespanSettings"
					onclick="toggleIcon('timespanSettingsIcon', 'icon-resize-full', 'icon-resize-small');" >
				<i id="timespanSettingsIcon" class="icon-resize-small"></i>
				<fmt:message key="graphicalExport.helpTimespanLabel" />
			</a>
        </div>
	    <div id="timespanSettings" class="accordion-body in collapse">
			<div class="accordion-inner">
			<p><fmt:message key="graphicalExport.line.helpStep2.timespan"/></p>
				<p>
					<fmt:message key="graphicalExport.line.configuration.startDate" />: 
					<form:input class="small datepicker" type="text" path="graphicalOptions.startDateString" id="start" />
				</p>
				<p>
					<fmt:message key="graphicalExport.line.configuration.endDate" />: 
					<form:input class="small datepicker" type="text" path="graphicalOptions.endDateString" id="end" />
				</p>
			</div>
	     </div>
    </div>
</div>

<tiles:insertTemplate template="/jsp/GraphicalReporting/RequestButtons.jsp">
	<tiles:putAttribute name="permissionSaveReports" value="${permissionSaveReports}" />
	<tiles:putAttribute name="availableGraphicFormats" value="${memBean.graphicalOptions.availableGraphicFormats}" />
	<tiles:putAttribute name="exportFormatPath" value="graphicalOptions.selectedGraphicFormat" />
</tiles:insertTemplate>