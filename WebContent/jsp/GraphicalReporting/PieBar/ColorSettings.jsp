<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:choose>
<c:when test="${memBean.graphicalOptions.diagramKeyType == 'ATTRIBUTE_TYPES'}">
	<c:set var="helpValuesType" value="graphicalExport.pieBar.bar.attributes.helpValuesType"/>
</c:when>
<c:otherwise>
	<c:choose>
	<c:when test="${memBean.graphicalOptions.valuesSource == 'ASSOCIATION'}">
		<c:set var="helpValuesType" value="graphicalExport.pieBar.helpValuesType.association"/>
	</c:when>
	<c:otherwise>
    	<c:set var="helpValuesType" value="graphicalExport.pieBar.helpValuesType.attribute"/>
	</c:otherwise>
	</c:choose>
</c:otherwise>
</c:choose>

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
				<c:choose>
				<c:when test="${memBean.graphicalOptions.diagramType == 'BAR' && memBean.graphicalOptions.diagramKeyType == 'ATTRIBUTE_TYPES'}">
					<%-- bar chart over attribute types --%>
					<p class="aligned"><fmt:message key="${helpValuesType}" /></p>
					<form:select path="graphicalOptions.diagramValuesType" onchange="flowAction('selectValueType');">
						<c:forEach var="availableValue" items="${memBean.graphicalOptions.availableDiagramValuesTypes}">
							<form:option value="${availableValue}"><fmt:message key="${availableValue.value}"/></form:option>
						</c:forEach>
			    	</form:select>
			    	
			    	<br/>
			    	<br/>
			    	
					<c:if test="${memBean.graphicalOptions.diagramValuesType == 'MAINTAINED'}">
					<%-- show only one color selection form for specified/not specified values --%>
						<tiles:insertTemplate template="/jsp/GraphicalReporting/DimensionOptionColor.jsp">
					      <tiles:putAttribute name="colorOptions" value="${memBean.graphicalOptions.colorOptionsBean}" />
								<tiles:putAttribute name="colorOptionsPath" value="graphicalOptions.colorOptionsBean" />
								<tiles:putAttribute name="minimal" value="true" />
						</tiles:insertTemplate>
						
						<br />
						<br />
						
					</c:if>
					<%-- bar chart over attribute types: if bars contain attribute values, show the corresponding color selection table --%>
					<div id="globalAttributesModule" class="row-fluid inner-module">
						<div class="inner-module-heading">
							<fmt:message key="global.attributes" />
						</div>
						<div class="row-fluid">
							<div class="inner-module-body-table">
								<div class="row-fluid">
									<table class="searchResultView table table-striped table-condensed" id="additionalSearchResultView">
										<colgroup>
											<col class="col-ico" />
											<col class="col-namebe" />
											<c:if test="${memBean.graphicalOptions.diagramValuesType == 'VALUES'}">
												<col class="col-val" />
											</c:if>
										</colgroup>
										<thead>
											<tr>
												<th>
													<form:checkbox path="graphicalOptions.checkAllBoxPie" id="checkAllBox2" value="" onclick="checkUnCheckAllByPattern('graphicalOptions.singleBars[', '].selected', this);flowAction('refreshPage');" />
													<br />
													<fmt:message key="reports.selectAll" />
												</th>						  
												<th><fmt:message key="global.name" /></th>
												<c:if test="${memBean.graphicalOptions.diagramValuesType == 'VALUES'}">
													<th><fmt:message key="reports.color" /></th>
												</c:if>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="entry" items="${memBean.graphicalOptions.singleBars}" varStatus="loopStatus">
												<tr>
													<c:set var="pieAttributeIndex" value="${loopStatus.index}"/>
													<td>
														<form:checkbox path="graphicalOptions.singleBars[${loopStatus.index}].selected" id="selectPieAttributeBox${pieAttributeIndex}"
															onclick="updateCheckAllBoxByNamePattern('graphicalOptions.singleBars[', '].selected', 'checkAllBox2');flowAction('refreshPage');" title="${checkAllBoxTitle}"/>
													</td>
													<td>${entry.label}</td>
													<c:if test="${memBean.graphicalOptions.diagramValuesType == 'VALUES'}">
														<td>
															<c:if test="${entry.selected == true}">
																<tiles:insertTemplate template="/jsp/GraphicalReporting/DimensionOptionColor.jsp">
																	<tiles:putAttribute name="colorOptions" value="${entry.colorOptions}" />
																	<tiles:putAttribute name="colorOptionsPath" value="graphicalOptions.singleBars[${loopStatus.index}].colorOptions" />
																	<tiles:putAttribute name="showUseValueRange" value="true" />
																	<tiles:putAttribute name="refresh_report_event" value="selectValueType" />
																	<tiles:putAttribute name="tableClass" value="attributeView" />
																	<tiles:putAttribute name="minimal" value="true" />
																</tiles:insertTemplate>
															</c:if>
														</td>
													</c:if>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</c:when>
				<c:otherwise>
					<%-- no bar chart over attribute types --%>
					<p class="aligned"><fmt:message key="graphicalExport.pieBar.helpColorType" /></p>
					<form:select path="graphicalOptions.valuesSource" onchange="flowAction('selectValuesSource');">
				  	  <c:forEach var="valuesSource" items="${memBean.graphicalOptions.valuesSource.values}">
					    	<form:option value="${valuesSource}"><fmt:message key="${valuesSource.value}" /></form:option>
				  	  </c:forEach>
					</form:select>
					
					<c:choose>
					  <c:when test="${memBean.graphicalOptions.valuesSource == 'ASSOCIATION'}">
					  <%-- show association selection --%>
						  <p class="aligned"><fmt:message key="graphicalExport.pieBar.helpColorAssociation" /></p>
						  <form:select path="graphicalOptions.selectedAssociation" onchange="flowAction('selectAssociation');">
								<form:option value=""><fmt:message key="graphicalExport.pieBar.selectAssociation"/></form:option>
								<c:forEach var="association" items="${memBean.graphicalOptions.availableAssociations}">
									<form:option value="${association}"><fmt:message key="${association}"/></form:option>
								</c:forEach>
						  </form:select>
					  </c:when>
					  <c:otherwise>
					  <%-- show attribute selection --%>
					  	<p class="aligned"><fmt:message key="graphicalExport.pieBar.helpColorAttribute" /></p>
						  <tiles:insertTemplate template="AttributeSelection.jsp">
						  	<tiles:putAttribute name="path" value="graphicalOptions.colorOptionsBean.dimensionAttributeId" />
						  	<tiles:putAttribute name="availableAttributes" value="${memBean.graphicalOptions.availableAttributeTypes}" />
						  	<tiles:putAttribute name="flowAction" value="selectAttribute" />
						  </tiles:insertTemplate>
					  </c:otherwise>
					</c:choose>
					
					<%-- values type selection --%>
					<c:if test="${memBean.graphicalOptions.colorAttributeAssociationSelected}">
						<p class="aligned"><fmt:message key="${helpValuesType}" /></p>
						
						<form:select path="graphicalOptions.diagramValuesType" onchange="flowAction('selectValueType');">
							<c:forEach var="availableValue" items="${memBean.graphicalOptions.availableDiagramValuesTypes}">
						  	<form:option value="${availableValue}"><fmt:message key="${availableValue.value}"/></form:option>
							</c:forEach>
						</form:select>
						
						<br/>
						<br/>
						
						<tiles:insertTemplate template="/jsp/GraphicalReporting/DimensionOptionColor.jsp">
					      <tiles:putAttribute name="colorOptions" value="${memBean.graphicalOptions.colorOptionsBean}" />
						  <tiles:putAttribute name="colorOptionsPath" value="graphicalOptions.colorOptionsBean" />
						  <tiles:putAttribute name="showUseValueRange" value="true" />
						  <tiles:putAttribute name="refresh_report_event" value="selectValueType" />
						  <tiles:putAttribute name="minimal" value="true" />
						</tiles:insertTemplate>
					</c:if>
				</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</div>