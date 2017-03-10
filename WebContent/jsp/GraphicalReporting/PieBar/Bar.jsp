<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="resultPostSelection" value="true" scope="request" />
<c:set var="permissionSaveReports" value="${userContext.perms.userHasFuncPermGraphReportingFull}" scope="request" />

<h1><fmt:message key="graphicalExport.barDiagram" /></h1>

<br/>

<!-- Selected Elements -->
<fmt:message var="chosenContentType" scope="request" key="${memBean.graphicalOptions.selectedBbType}" />
<tiles:insertTemplate template="/jsp/GraphicalReporting/ShowSelectedElements.jsp">
	<tiles:putAttribute name="messageKey" value="graphicalExport.pieBar.showChosenElements" />
	<tiles:putAttribute name="messageKeyArg" value="${chosenContentType}" />
	<tiles:putAttribute name="collection" value="queryResult.selectedResults" />
	<tiles:putAttribute name="field" value="identityString" />
</tiles:insertTemplate>

<!-- Select Diagram-Type and Attribute/Association -->
<div class="accordion" id="diagramTypeSettingsContainer">
	<div class="accordion-group">
		<div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#diagramTypeSettingsContainer" href="#diagramTypeSettings"
					onclick="toggleIcon('diagramTypeSettingsIcon', 'icon-resize-full', 'icon-resize-small');" >
			   <i id="diagramTypeSettingsIcon" class="icon-resize-small"></i>
               <fmt:message key="graphicalExport.pieBar.fieldsetDiagramType" />
            </a>
        </div>
	    <div id="diagramTypeSettings" class="accordion-body in collapse">
			<div class="accordion-inner">
				<p class="aligned"><fmt:message key="graphicalExport.pieBar.bar.helpDiagramKeyType" /></p>
				<!--  diagram key type selection -->
				<form:select path="graphicalOptions.diagramKeyType" onchange="flowAction('selectDiagramKeyType');">
			 		<c:forEach var="keyType" items="${memBean.graphicalOptions.availableDiagramKeyTypes}">
			  			<form:option value="${keyType}"><fmt:message key="${keyType.value}" /></form:option>
			 		</c:forEach>
				</form:select>
				
				<br/>
				
				<c:choose>
				<%-- attribute type selection --%>
					<c:when test="${memBean.graphicalOptions.diagramKeyType.value == 'global.attributevalues'}">
						<p class="aligned"><fmt:message key="graphicalExport.pieBar.bar.attributeValues.helpKeyAttribute" /></p>
						<tiles:insertTemplate template="AttributeSelection.jsp">
							<tiles:putAttribute name="path" value="graphicalOptions.selectedKeyAttributeTypeId" />
							<tiles:putAttribute name="availableAttributes" value="${memBean.graphicalOptions.availableKeyAttributeTypes}" />
							<tiles:putAttribute name="flowAction" value="selectKeyAttribute" />
						</tiles:insertTemplate>

						<br/>
					</c:when>
					<c:when test="${memBean.graphicalOptions.diagramKeyType.value == 'graphicalExport.pieBar.assignmentCountAttribute'}">
						<p class="aligned"><fmt:message key="graphicalExport.pieBar.bar.attributeCount.helpKeyAttribute" /></p>
						<tiles:insertTemplate template="AttributeSelection.jsp">
							<tiles:putAttribute name="path" value="graphicalOptions.selectedKeyAttributeTypeId" />
							<tiles:putAttribute name="availableAttributes" value="${memBean.graphicalOptions.availableMultiValueAttributeTypes}" />
							<tiles:putAttribute name="flowAction" value="selectKeyAttribute" />
						</tiles:insertTemplate>
						
						<br/>
					</c:when>
					<c:when test="${memBean.graphicalOptions.diagramKeyType.value == 'global.attributes'}"></c:when>
					<c:otherwise>
					<%-- association selection --%>
						<c:if test="${memBean.graphicalOptions.diagramKeyType.value == 'global.associations'}">
							<p class="aligned"><fmt:message key="graphicalExport.pieBar.bar.association.helpAssociation" /></p>
						</c:if>
						<c:if test="${memBean.graphicalOptions.diagramKeyType.value == 'graphicalExport.pieBar.assignmentCountAssociation'}">
							<p class="aligned"><fmt:message key="graphicalExport.pieBar.bar.associationCount.helpAssociation" /></p>
						</c:if>
						<form:select path="graphicalOptions.selectedKeyAssociation" onchange="flowAction('selectKeyAssociation');">
							<form:option value=""><fmt:message key="graphicalExport.pieBar.selectAssociation"/></form:option>
							<c:forEach var="association" items="${memBean.graphicalOptions.availableKeyAssociations}">
								<form:option value="${association}"><fmt:message key="${association}"/></form:option>
							</c:forEach>
						</form:select>
						
						<br/>
						
						<c:if test="${memBean.graphicalOptions.availableTopLevel != memBean.graphicalOptions.availableBottomLevel && memBean.graphicalOptions.diagramKeyType == 'ASSOCIATION_NAMES'}">
							<br />
							<tiles:insertTemplate template="/jsp/GraphicalReporting/SelectLevels.jsp">
								<tiles:putAttribute name="selectedLevelRangePath" value="graphicalOptions.selectedLevelRange"/>
								<tiles:putAttribute name="selectedLevelRangeField" value="${memBean.graphicalOptions.selectedLevelRange}"/>
								<tiles:putAttribute name="minLevel" value="${memBean.graphicalOptions.availableTopLevel}"/>
								<tiles:putAttribute name="maxLevel" value="${memBean.graphicalOptions.availableBottomLevel}"/>
							</tiles:insertTemplate>
							<br />
						</c:if>
					</c:otherwise>
				</c:choose>
				
				<br/>
				
			</div>
		</div>
	</div>
</div>

<!-- Color Settings -->
<tiles:insertTemplate template="ColorSettings.jsp" />

<!-- Advanced Settings -->
<div class="accordion" id="advancedSettingsContainer">
    <div class="accordion-group">
        <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#advancedSettingsContainer" href="#advancedSettings"
					onclick="toggleIcon('advancedSettingsIcon', 'icon-resize-full', 'icon-resize-small');" >
			 	<i id="advancedSettingsIcon" class="icon-resize-full"></i>
                <fmt:message key="graphicalExport.helpAdvancedLabel" />
            </a>
        </div>
	    <div id="advancedSettings" class="accordion-body collapse">
			<div class="accordion-inner">
			  <div class="controls" style="float:none;">
				<label class="checkbox">
			  		<form:checkbox path="graphicalOptions.showEmptyBars" id="showEmptyBars" />
					<fmt:message key="graphicalExport.pieBar.bar.helpShowEmptyBars" />
				</label>
				<label class="checkbox">
			  		<form:checkbox path="graphicalOptions.showBarSizeLabels" id="showBarSizeLabels" />
					<fmt:message key="graphicalExport.pieBar.helpShowBarSizeLabels" />
				</label>
				<label class="checkbox">
		  			<form:checkbox path="graphicalOptions.showSegmentLabels" id="showSegmentLabels" />
					<fmt:message key="graphicalExport.pieBar.helpShowSegmentLabels" />
				</label>
				<p>
					<fmt:message key="graphicalExport.pieBar.barsOrderMethod" />
				</p>
		  		<form:select path="graphicalOptions.barsOrderMethod" id="barsOrderMethod">
			  		<c:forEach var="orderMethod" items="${memBean.graphicalOptions.availableBarsOrderMethods}">
			  			<form:option value="${orderMethod}"><fmt:message key="${orderMethod.value}"/></form:option>
			  		</c:forEach>
		  		</form:select>
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