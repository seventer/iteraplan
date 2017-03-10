<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<fmt:message var="chosenContentType" scope="request" key="${memBean.graphicalOptions.selectedBbType}" />

<c:if test="${memBean.reportUpdated}">
	<div class="alert alert-error">
		<a data-dismiss="alert" class="close" onclick="clearErrors();">×</a> 
		<span><fmt:message key="message.report.attributeNotFound" /></span>
	</div>
</c:if>

<c:if test="${(memBean.graphicalOptions.selectedClusterMode != 'graphicalExport.cluster.mode.attributes')}">
	<tiles:insertTemplate template="/jsp/GraphicalReporting/ShowSelectedElements.jsp">
		<tiles:putAttribute name="messageKey" value="graphicalExport.cluster.showChosenElements" />
		<tiles:putAttribute name="messageKeyArg" value="${chosenContentType}" />
		<tiles:putAttribute name="collection" value="queryResult.selectedResults" />
		<tiles:putAttribute name="field" value="identityString" />
	</tiles:insertTemplate>
</c:if>

<%-- Color Settings --%>
<c:choose>
<c:when test="${(memBean.graphicalOptions.selectedClusterMode != 'graphicalExport.cluster.mode.attributes')}">
	<div class="accordion" id="colorSettingsContainer">
		<div class="accordion-group">
			<div class="accordion-heading">
				<a class="accordion-toggle" data-toggle="collapse" data-parent="#colorSettingsContainer" href="#colorSettings"
				        onclick="toggleIcon('colorSettingsIcon', 'icon-resize-full', 'icon-resize-small');" >
					<i id="colorSettingsIcon" class="icon-resize-small"></i>
					<fmt:message key="graphicalExport.helpColorLabel" />
				</a>
			</div>
			<div id="colorSettings" class="accordion-body in collapse">
				<div class="accordion-inner">
					<p>
						<fmt:message key="graphicalExport.cluster.helpColor">
							<fmt:param value="${chosenContentType}" />
						</fmt:message>
					</p>
					<p>
						<fmt:message key="graphicalExport.helpDimensionAttributes">
							<fmt:param><fmt:message key="reports.color" /></fmt:param>
						</fmt:message>
					</p>
					<tiles:insertTemplate template="/jsp/GraphicalReporting/DimensionOptionsSimple.jsp" flush="false">
						<tiles:putAttribute name="dimension_key" value="reports.color" />
						<tiles:putAttribute name="available_attributes_field" value="queryResult.queryForms[0].dimensionAttributes" />
						<tiles:putAttribute name="selected_id_field" value="graphicalOptions.colorOptionsBean.dimensionAttributeId" />
						<tiles:putAttribute name="refresh_report_event" value="refreshReport" />
						<tiles:putAttribute name="show_enum" value="true" />
						<tiles:putAttribute name="show_number" value="false" />
						<tiles:putAttribute name="show_text" value="true" />
						<tiles:putAttribute name="show_date" value="true" />
						<tiles:putAttribute name="show_responsibility" value="true" />
					</tiles:insertTemplate>
					<tiles:insertTemplate template="/jsp/GraphicalReporting/DimensionOptionColor.jsp">
						<tiles:putAttribute name="colorOptions" value="${memBean.graphicalOptions.colorOptionsBean}" />
						<tiles:putAttribute name="colorOptionsPath" value="graphicalOptions.colorOptionsBean" />
						<tiles:putAttribute name="refresh_report_event" value="refreshReport" />
						<tiles:putAttribute name="showUseValueRange" value="true" />
					</tiles:insertTemplate>
				</div>
		 	</div>
		</div>
	</div>
</c:when>
<c:otherwise>
	<div class="accordion" id="colorSettingsContainer">
		<div class="accordion-group">
			<div class="accordion-heading">
				<a class="accordion-toggle" data-toggle="collapse" data-parent="#colorSettingsContainer" href="#colorSettings"
					onclick="toggleIcon('colorSettingsIcon', 'icon-resize-full', 'icon-resize-small');" >
					<i id="colorSettingsIcon" class="icon-resize-small"></i>
					<fmt:message key="graphicalExport.helpColorLabel" />
				</a>
			</div>
			<div id="colorSettings" class="accordion-body in collapse">
				<div class="accordion-inner">
					<p>
						<fmt:message key="graphicalExport.cluster.helpColorAttribute" />
					</p>
					<tiles:insertTemplate template="/jsp/GraphicalReporting/DimensionOptionColor.jsp">
						<tiles:putAttribute name="colorOptions" value="${memBean.graphicalOptions.colorOptionsBean}" />
						<tiles:putAttribute name="colorOptionsPath" value="graphicalOptions.colorOptionsBean" />
						<tiles:putAttribute name="showOnlyTheseValues" value="${memBean.graphicalOptions.selectedAttributeValues}" />
						<tiles:putAttribute name="minimal" value="true" />
					</tiles:insertTemplate>
				</div>
			</div>
		</div>
	</div>
</c:otherwise>
</c:choose>

<%-- DimensionSettings --%>
<div class="accordion" id="dimensionSettingsContainer">
	<div class="accordion-group">
		<div class="accordion-heading">
			<a class="accordion-toggle" data-toggle="collapse" data-parent="#dimensionSettingsContainer" href="#dimensionSettings"
			        onclick="toggleIcon('dimensionSettingsIcon', 'icon-resize-full', 'icon-resize-small');" >
				<i id="dimensionSettingsIcon" class="icon-resize-full"></i>
				<fmt:message key="graphicalExport.helpDimensionLabel" />
			</a>
		</div>
		<div id="dimensionSettings" class="accordion-body in collapse">
			<div class="accordion-inner">
				<p><fmt:message key="graphicalExport.cluster.helpColorDimensions" /></p>
				<p>
					<fmt:message key="graphicalExport.helpDimensionAttributes">
						<fmt:param><fmt:message key="reports.color" /></fmt:param>
					</fmt:message>
				</p>
				
				<table class="searchResultView table table-bordered table-striped table-condensed tableInModule">
					<colgroup>
						<col class="col-ico" />
						<col class="col-name" />
						<col class="col-val" />
						<col class="col-val" /> 
						<col class="col-val" />
					</colgroup>
					<thead>
						<tr>
							<th class="col-ico" scope="col">
								<form:checkbox path="graphicalOptions.checkAllBox" id="checkAllBox2" value="" onclick="checkUnCheckAllByPattern('graphicalOptions.secondOrderBeans[', '].selected', this);flowAction('refreshReport');" />
								
								<br />
								
								<fmt:message key="reports.selectAll" />
							</th>
							<th scope="col"><fmt:message key="global.name" /></th>
							<th scope="col"><fmt:message key="graphicalExport.landscape.attribute" /></th>
							<th scope="col"><fmt:message key="graphicalExport.cluster.form.help" /></th>
							<th scope="col"><fmt:message key="reports.color" /></th>
							<th scope="col"><fmt:message key="global.order" /></th>	
						</tr>
					</thead>
					<tbody>
						<c:forEach var="colorDimension" items="${memBean.graphicalOptions.secondOrderBeans}" varStatus="loopStatus">
							<tr>
								<c:set var="dimensionIndex" value="${loopStatus.index}"/>
								<td class="col-ico">
									<form:checkbox path="graphicalOptions.secondOrderBeans[${dimensionIndex}].selected" 
									               id="selectDimensionBox${dimensionIndex}" name="selectDimensionBox"
									               onclick="updateCheckAllBoxByNamePattern('graphicalOptions.secondOrderBeans[', '].selected', 'checkAllBox2');flowAction('refreshReport');"/>
								</td>
								<c:set var="isEmptyCssClass" value="" />
								<c:if test="${!memBean.graphicalOptions.secondOrderBeans[dimensionIndex].selected}">
									<c:set var="isEmptyCssClass" value="empty" />
								</c:if>
								<td class="${isEmptyCssClass}">	
									<c:choose>
										<c:when test="${memBean.graphicalOptions.secondOrderBeans[dimensionIndex].beanType eq 'buildingBlockBean'}">
											<fmt:message key="${memBean.graphicalOptions.secondOrderBeans[dimensionIndex].name}" />
										</c:when>
										<c:otherwise>
											<i><c:out value="${memBean.graphicalOptions.secondOrderBeans[dimensionIndex].name}" /></i>
										</c:otherwise>
									</c:choose>
									<form:errors path="graphicalOptions.secondOrderBeans[${dimensionIndex}].selected" cssClass="errorMsg" htmlEscape="false"/>
								</td>
								
								<c:choose>
									<c:when test="${memBean.graphicalOptions.secondOrderBeans[dimensionIndex].selected}">
										<c:choose>
											<c:when test="${memBean.graphicalOptions.secondOrderBeans[dimensionIndex].beanType eq 'buildingBlockBean'}">
												<td align="center">
													<tiles:insertTemplate template="/jsp/GraphicalReporting/DimensionOptionsSimple.jsp" flush="false">
														<tiles:putAttribute name="dimension_key" value="reports.color" />
														<tiles:putAttribute name="available_attributes_field" value="graphicalOptions.secondOrderBeans[${dimensionIndex}].availableAttributes" />
														<tiles:putAttribute name="selected_id_field" value="graphicalOptions.secondOrderBeans[${dimensionIndex}].colorOptions.dimensionAttributeId" />
														<tiles:putAttribute name="refresh_report_event" value="refreshReport" />
														<tiles:putAttribute name="show_enum" value="true" />
														<tiles:putAttribute name="show_number" value="false" />
														<tiles:putAttribute name="show_text" value="true" />
														<tiles:putAttribute name="show_date" value="true" />
														<tiles:putAttribute name="show_responsibility" value="true" />
														<tiles:putAttribute name="minimal" value="true" />
													</tiles:insertTemplate>
												</td>
											</c:when>
											<c:otherwise>
												<td id="otherwise"/>
											</c:otherwise>
										</c:choose>
											
										<td align="center">
											<form:errors path="graphicalOptions.secondOrderBeans[${dimensionIndex}].selectedBbShape" cssClass="errorMsg" htmlEscape="false"/>
											<form:select path="graphicalOptions.secondOrderBeans[${dimensionIndex}].selectedBbShape">
											 	<c:forEach var="available" items="${memBean.graphicalOptions.secondOrderBeans[dimensionIndex].availableBbShapes}">
											 		<form:option value="${available}">
												  		<fmt:message key="${available}" />
											 		</form:option>
											 	</c:forEach>
											</form:select>
										</td>	
									  	<td>
											<tiles:insertTemplate template="/jsp/GraphicalReporting/DimensionOptionColor.jsp">
												<tiles:putAttribute name="colorOptions" value="${memBean.graphicalOptions.secondOrderBeans[dimensionIndex].colorOptions}" />
												<tiles:putAttribute name="colorOptionsPath" value="graphicalOptions.secondOrderBeans[${dimensionIndex}].colorOptions" />
												<tiles:putAttribute name="tableClass" value="attributeView" />
												<tiles:putAttribute name="refresh_report_event" value="refreshReport" />
												<tiles:putAttribute name="showUseValueRange" value="true" />
												<tiles:putAttribute name="minimal" value="true" />
											</tiles:insertTemplate>
									  	</td>
									  	<td align="center">
											<img src="<c:url value="/images/SortArrowTop.gif"/>" alt="TOP" class="link"
												onclick="setHiddenField('graphicalOptions.movedItem', ${dimensionIndex});setHiddenField('graphicalOptions.move', 1);flowAction('refreshReport');" />
											<img src="<c:url value="/images/SortArrowUp.gif"/>" alt="UP" class="link"
												onclick="setHiddenField('graphicalOptions.movedItem', ${dimensionIndex});setHiddenField('graphicalOptions.move', 2);flowAction('refreshReport');" />
											<img src="<c:url value="/images/SortArrowDown.gif"/>" alt="DOWN" class="link"
												onclick="setHiddenField('graphicalOptions.movedItem', ${dimensionIndex});setHiddenField('graphicalOptions.move', 3);flowAction('refreshReport');" />
											<img src="<c:url value="/images/SortArrowBottom.gif"/>" alt="BOTTOM" class="link"
												onclick="setHiddenField('graphicalOptions.movedItem', ${dimensionIndex});setHiddenField('graphicalOptions.move', 4);flowAction('refreshReport');" />
										</td>
									</c:when>
									<c:otherwise>
										<td></td>
										<td></td>
										<td></td>
										<td></td>
									</c:otherwise>
								</c:choose>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>

<%-- Advanced Settings --%>
<c:choose>
	<c:when test="${(memBean.graphicalOptions.selectedClusterMode != 'graphicalExport.cluster.mode.attributes')}">
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
						<p>
							<fmt:message key="graphicalExport.cluster.sorting.help">
								<fmt:param value="${chosenContentType}" />
							</fmt:message>
						</p>
						
						<form:errors path="graphicalOptions.selectedHierarchicalUpperLevel" cssClass="errorMsg" htmlEscape="false"/>
						<form:select path="graphicalOptions.selectedHierarchicalLevels">
							<c:set var="rangeSplit" value="_" />
							<c:if test="${memBean.graphicalOptions.selectedHierarchicalLowerLevel == 1 && memBean.graphicalOptions.selectedHierarchicalUpperLevel == 1}">
								<c:set var="selected" value='selected="selected"'/>
							</c:if>
							<option value="1<c:out value="${rangeSplit}" escapeXml="false" />1" <c:out value="${selected}" escapeXml="false"/> >
								<fmt:message key="graphicalExport.cluster.sorting.option.nonHierarchical"/>
							</option>
							<c:forEach begin="1" end="${memBean.graphicalOptions.availableHierarchicalLevels}" var="currentUpper">
								<c:forEach begin="1" end="${memBean.graphicalOptions.availableHierarchicalLevels}" var="currentLower">
									<c:set var="selected" value=""/>
									<c:if test="${currentLower == memBean.graphicalOptions.selectedHierarchicalLowerLevel && currentUpper == memBean.graphicalOptions.selectedHierarchicalUpperLevel}">
										<c:set var="selected" value='selected="selected"'/>
									</c:if>
									<c:if test="${currentUpper < currentLower}">
										<option value="${currentUpper}<c:out value="${rangeSplit}" escapeXml="false" />${currentLower}" <c:out value="${selected}" escapeXml="false"/> >
											<fmt:message key="graphicalExport.cluster.sorting.option.level"/> <c:out value="${currentUpper} ... ${currentLower}"/>
										</option>
									</c:if>
								</c:forEach>
							</c:forEach>
						</form:select>
						
						<br />
						<br />
						
						<form:hidden path="graphicalOptions.movedItem" />
						<form:hidden path="graphicalOptions.move"/>
						<div class="controls" style="float: none;">
							<label class="checkbox">
								<form:checkbox path="graphicalOptions.swimlaneContent" />
								<fmt:message key="graphicalExport.cluster.helpSwimLanes" />			
							</label>
							<label class="checkbox">
								<form:checkbox path="graphicalOptions.useNamesLegend" id="useNamesLegend" />
								<fmt:message key="graphicalExport.helpUseNamesLegend" />			
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
	</c:when>
	<c:otherwise>
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
						<form:hidden path="graphicalOptions.movedItem" />
						<form:hidden path="graphicalOptions.move"/>
						<div class="controls" style="float: none;">
							<label class="checkbox">
								<form:checkbox path="graphicalOptions.swimlaneContent" />
								<fmt:message key="graphicalExport.cluster.helpSwimLanes" />			
							</label>
							<label class="checkbox">
								<form:checkbox path="graphicalOptions.useNamesLegend" id="useNamesLegend" />
								<fmt:message key="graphicalExport.helpUseNamesLegend" />
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
	</c:otherwise>
</c:choose>

<tiles:insertTemplate template="/jsp/GraphicalReporting/RequestButtons.jsp">
	<tiles:putAttribute name="permissionSaveReports" value="${permissionSaveReports}" />
	<tiles:putAttribute name="availableGraphicFormats" value="${memBean.graphicalOptions.availableGraphicFormats}" />
	<tiles:putAttribute name="exportFormatPath" value="graphicalOptions.selectedGraphicFormat" />
</tiles:insertTemplate>