<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="chosenContentType" scope="request">
	<fmt:message>
		<itera:write name="memBean" property="graphicalOptions.level0Options.selectedBbType" escapeXml="false" />
	</fmt:message>
</c:set>

<c:if test="${not empty memBean.graphicalOptions.level1Options}">
	<c:set var="chosenContentType_1" scope="request">
		<fmt:message>
			<itera:write name="memBean" property="graphicalOptions.level1Options.selectedBbType" escapeXml="false" />
		</fmt:message>
	</c:set>
</c:if>

<c:if test="${not empty memBean.graphicalOptions.level2Options}">
	<c:set var="chosenContentType_2" scope="request">
		<fmt:message>
			<itera:write name="memBean" property="graphicalOptions.level2Options.selectedBbType" escapeXml="false" />
		</fmt:message>
	</c:set>
</c:if>

<c:if test="${memBean.reportUpdated}">
	<div class="alert alert-error">
		<a data-dismiss="alert" class="close" onclick="clearErrors();">×</a> 
		<span><fmt:message key="message.report.attributeNotFound" /></span>
	</div>
</c:if>

<tiles:insertTemplate template="/jsp/GraphicalReporting/ShowSelectedElements.jsp">
	<tiles:putAttribute name="messageKey" value="graphicalExport.masterplan.showChosenElements" />
	<tiles:putAttribute name="messageKeyArg" value="${chosenContentType}" />
	<tiles:putAttribute name="collection" value="queryResult.selectedResults" />
	<tiles:putAttribute name="field" value="identityString" />
</tiles:insertTemplate>


<div class="accordion" id="level1Container">
    <div class="accordion-group">
        <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#level1Container" href="#level1Settings"
					onclick="toggleIcon('level1SettingsIcon', 'icon-resize-full', 'icon-resize-small');" >
			   <i id="level1SettingsIcon" class="icon-resize-small"></i>
               <fmt:message key="graphicalExport.level.1" />
            </a>
        </div>
	    <div id="level1Settings" class="accordion-body in collapse">
			<div class="accordion-inner">
				<p>
					<fmt:message key="graphicalExport.masterplan.helpColor" />
				</p>
				
				<div class="control-group">
					<div class="controls">
						<form:radiobutton path="graphicalOptions.level0Options.useDefaultColoring" value="false" onclick="flowAction('refreshReport')" id="level0.radio.attrs" />
					</div>
					<label class="control-label-right" for="level0.radio.attrs">
				  		<fmt:message key="graphicalExport.masterplan.helpColor.attrs" />
				  	</label>
				</div>
				
				<c:if test="${not memBean.graphicalOptions.level0Options.useDefaultColoring}">
					<div style="padding-left: 3em">
						<p>
							<fmt:message key="graphicalExport.helpDimensionAttributes">
								<fmt:param><fmt:message key="reports.color" /></fmt:param>
							</fmt:message>
						</p>
					
						<tiles:insertTemplate template="/jsp/GraphicalReporting/DimensionOptionsSimple.jsp">
					    	<tiles:putAttribute name="dimension_key" value="reports.color" />
						    <tiles:putAttribute name="available_attributes_field" value="graphicalOptions.level0Options.availableColorAttributes" />
						    <tiles:putAttribute name="selected_id_field" value="graphicalOptions.level0Options.colorOptions.dimensionAttributeId" />
						    <tiles:putAttribute name="refresh_report_event" value="refreshReport" />
						    <tiles:putAttribute name="show_enum" value="true" />
						    <tiles:putAttribute name="show_number" value="false" />
						    <tiles:putAttribute name="show_text" value="true" />
						    <tiles:putAttribute name="show_date" value="true" />
						    <tiles:putAttribute name="show_responsibility" value="true" />
						</tiles:insertTemplate>
						
						<tiles:insertTemplate template="/jsp/GraphicalReporting/DimensionOptionColor.jsp">
							<tiles:putAttribute name="colorOptions" value="${memBean.graphicalOptions.level0Options.colorOptions}" />
							<tiles:putAttribute name="colorOptionsPath" value="graphicalOptions.level0Options.colorOptions" />
							<tiles:putAttribute name="refresh_report_event" value="refreshReport" />
							<tiles:putAttribute name="showUseValueRange" value="true" />
						</tiles:insertTemplate>
					</div>
				</c:if>
				
				<div class="control-group">
					<div class="controls">
						<form:radiobutton path="graphicalOptions.level0Options.useDefaultColoring" value="true" onclick="flowAction('refreshReport')" id="level0.radio.defaults" />
					</div>
					<label class="control-label-right" for="level0.radio.defaults">
				  		<fmt:message key="graphicalExport.masterplan.helpColor.default" />
				  	</label>
				</div>
				
				<br/>
				
				<p><fmt:message key="graphicalExport.masterplan.helpStep2.timespan"/></p>
				
				<fmt:message key="global.from"/>
				&nbsp;
				<form:input class="small datepicker" type="text" path="graphicalOptions.startDateString" id="graphicalOptions.startDateString" />
				&nbsp;&nbsp;
				<fmt:message key="global.to"/>
				&nbsp;
				<form:input class="small datepicker" type="text" path="graphicalOptions.endDateString" id="graphicalOptions.endDateString" />
				&nbsp;&nbsp;
				<br/>
				
				<c:if test="${not empty memBean.graphicalOptions.level0Options.availableTimeLines}">
					<div style="padding-top: 1em">
						<p><fmt:message key="graphicalExport.masterplan.helpStep2.chooseDateIntervals"/></p>
						<form:select path="graphicalOptions.level0Options.currentDateInterval" cssStyle="width:200px;">
				   	    	<itera:define id="customDatInt_array" name="memBean" property="graphicalOptions.level0Options.availableTimeLines"/>
				   	    	<c:forEach var="availableTimeLine" items="${customDatInt_array}">
				   	     		<form:option value="${availableTimeLine.position}">
					            	<c:out value="${availableTimeLine.name}" />
					          	</form:option>
				      	 	</c:forEach>
			     	 	</form:select>
			     	 	
			     	 	<a class="link" name="addDateInterval" href="#" title="<fmt:message key='tooltip.add'/>" id="addSelectedDateInterval0" onclick="flowAction('addDateInterval0');" >
							<i class="icon-plus"></i>
						</a>
					</div>
				</c:if>
	     	 	
	     	 	<c:if test="${not empty memBean.graphicalOptions.level0Options.timelineFeatures}">
	     	 		<div style="padding-top: 1em">
			     	 	<p><fmt:message key="graphicalExport.masterplan.helpStep2.selectedDateIntervals"/></p>
			     	 	<itera:define id="customDatInt_array" name="memBean" property="graphicalOptions.level0Options.timelineFeatures"/>
						<c:forEach var="di" items="${customDatInt_array}">
							<a class="link" href="#" onclick="createHiddenField('graphicalOptions.dateIntervalToRemove','${di.position}');flowAction('removeDateInterval0');" >
					 			<i class="icon-remove"></i>
					 		</a>
				            	<c:out value="${di.name}" />
				            <br/>
					 	</c:forEach>
				 	</div>
				</c:if>
				
				<div style="padding-top: 1em">
					<p>
						<fmt:message key="graphicalExport.masterplan.helpStep2.sorting">
							<fmt:param>${chosenContentType}</fmt:param>
						</fmt:message>
					</p>
					
					<label class="checkbox">
						<form:checkbox path="graphicalOptions.level0Options.hierarchicalSort" id="level0Options.hierarchicalSort" />
						<fmt:message key="graphicalExport.masterplan.helpStep2.sorting.option.hierarchical"/>
					</label>
				</div>
				
				<c:if test="${memBean.graphicalOptions.level0Options.additionalCustomColumnsAllowed && not empty memBean.graphicalOptions.level0Options.availableCustomColumns}" >
					<div style="padding-top: 1em">
						<p><fmt:message key="graphicalExport.masterplan.helpStep2.chooseCustomColumns"/></p>
						
			     		<form:select path="graphicalOptions.level0Options.currentCustomColumn" cssStyle="width:200px;">
				   	    	<itera:define id="customCols_array" name="memBean" property="graphicalOptions.level0Options.availableCustomColumns"/>
				   	    	<c:forEach var="availableColumn" items="${customCols_array}">
				   	     		<form:option value="${availableColumn.head}">
						        	<c:choose>
							        	<c:when test="${availableColumn.type != 'attribute'}">
											<fmt:message key="${availableColumn.head}" />
							            </c:when>
							            <c:otherwise>
							            	<c:out value="${availableColumn.head}" />
							            </c:otherwise>
					                </c:choose>
					          	</form:option>
				      	 	</c:forEach>
			     	 	</form:select>
			     	 	
						<a class="link" name="addColumn" href="#" title="<fmt:message key='tooltip.add'/>" id="addSelectColumn" onclick="flowAction('addCustomColumn0');" >
							<i class="icon-plus"></i>
						</a>
					</div>
				</c:if>
				
				<c:if test="${not empty memBean.graphicalOptions.level0Options.selectedCustomColumns}">
					<div style="padding-top: 1em">
						<p><fmt:message key="graphicalExport.masterplan.helpStep2.selectedCustomColumns"/></p>
						
						<itera:define id="customCols_array" name="memBean" property="graphicalOptions.level0Options.selectedCustomColumns"/>
						<c:forEach var="col" items="${customCols_array}">
							<a class="link" href="#" onclick="createHiddenField('graphicalOptions.level0Options.columnToRemove','${col.head}');flowAction('removeCustomColumn0');" >
					 			<i class="icon-remove"></i>
					 		</a>
					 		<c:choose>
				        		<c:when test="${col.type != 'attribute'}">
									<fmt:message key="${col.head}" />
				            	</c:when>
				            	<c:otherwise>
				            		<c:out value="${col.head}" />
				            	</c:otherwise>
				            </c:choose>
				            <br/>
					 	</c:forEach>
					</div>
				</c:if>
				
			</div>
	     </div>
    </div>
</div>


<div class="accordion" id="level2Container">
	<div class="accordion-group">
		<div class="accordion-heading">
			<a class="accordion-toggle" data-toggle="collapse" data-parent="#level2Container" href="#level2Settings"
					onclick="toggleIcon('level2Icon', 'icon-resize-full', 'icon-resize-small');">
				<i id="level2Icon" class="icon-resize-small"></i>
        		<fmt:message key="graphicalExport.level.2" />
      		</a>
		</div>
		<div id="level2Settings" class="accordion-body collapse in">
			<div class="accordion-inner">
        		<p><fmt:message key="graphicalExport.masterplan.helpStep2.relatedTypes"/></p>
				
				<form:select path="graphicalOptions.selectedLevel1Relation" cssStyle="wide" onchange="flowAction('selectLevel1Type');">
					<itera:define id="relatedTypes_array" name="memBean" property="graphicalOptions.level0Options.availableRelatedTypes"/>
					<c:forEach items="${relatedTypes_array}" var="type" varStatus="countStatus">
						<form:option value="${type}">
							<fmt:message key="${type}" />
						</form:option>
					</c:forEach>
				</form:select>
				
				<c:if test="${not empty memBean.graphicalOptions.level1Options}">
				
					<c:if test="${memBean.graphicalOptions.level1Options.canBuildClosure}">
						<div style="padding-top: 1em">
						<label class="checkbox">
							<form:checkbox path="graphicalOptions.level1Options.buildClosure" id="buildClosure1" />
							<fmt:message key="graphicalExport.masterplan.helpStep2.relatedTypes.chaining" />
						</label>
						</div>
					</c:if>
				
					<div style="padding-top: 1em">
						<p><fmt:message key="graphicalExport.masterplan.helpColor" /></p>
						
						<div class="control-group">
							<div class="controls">
								<form:radiobutton path="graphicalOptions.level1Options.useDefaultColoring" value="false" onclick="flowAction('refreshReport')" id="level1.radio.attrs" />
							</div>
							<label class="control-label-right" for="level1.radio.attrs">
						  		<fmt:message key="graphicalExport.masterplan.helpColor.attrs" />
						  	</label>
						</div>
						
						<c:if test="${not memBean.graphicalOptions.level1Options.useDefaultColoring}">
							<div style="padding-left: 3em">
								<p>
									<fmt:message key="graphicalExport.helpDimensionAttributes">
										<fmt:param><fmt:message key="reports.color" /></fmt:param>
									</fmt:message>
								</p>
								
								<tiles:insertTemplate template="/jsp/GraphicalReporting/DimensionOptionsSimple.jsp">
								    <tiles:putAttribute name="dimension_key" value="reports.color" />
								    <tiles:putAttribute name="available_attributes_field" value="graphicalOptions.level1Options.availableColorAttributes" />
								    <tiles:putAttribute name="selected_id_field" value="graphicalOptions.level1Options.colorOptions.dimensionAttributeId" />
								    <tiles:putAttribute name="refresh_report_event" value="refreshReport" />
								    <tiles:putAttribute name="show_enum" value="true" />
								    <tiles:putAttribute name="show_number" value="false" />
								    <tiles:putAttribute name="show_text" value="true" />
								    <tiles:putAttribute name="show_date" value="true" />
								    <tiles:putAttribute name="show_responsibility" value="true" />
								</tiles:insertTemplate>
								
								<tiles:insertTemplate template="/jsp/GraphicalReporting/DimensionOptionColor.jsp">
									<tiles:putAttribute name="colorOptions" value="${memBean.graphicalOptions.level1Options.colorOptions}" />
									<tiles:putAttribute name="colorOptionsPath" value="graphicalOptions.level1Options.colorOptions" />
									<tiles:putAttribute name="refresh_report_event" value="refreshReport" />
									<tiles:putAttribute name="showUseValueRange" value="true" />
								</tiles:insertTemplate>
							</div>
						</c:if>
						
						<div class="control-group">
							<div class="controls">
								<form:radiobutton path="graphicalOptions.level1Options.useDefaultColoring" value="true" onclick="flowAction('refreshReport')" id="level1.radio.defaults" />
							</div>
							<label class="control-label-right" for="level1.radio.defaults">
						  		<fmt:message key="graphicalExport.masterplan.helpColor.default" />
						  	</label>
						</div>
					</div>
				
					<c:if test="${not empty memBean.graphicalOptions.level1Options.availableTimeLines}">
						<div style="padding-top: 1em">
							<p><fmt:message key="graphicalExport.masterplan.helpStep2.chooseDateIntervals"/></p>
							<form:select path="graphicalOptions.level1Options.currentDateInterval" cssStyle="width:200px;">
					   	    	<itera:define id="customDatInt_array" name="memBean" property="graphicalOptions.level1Options.availableTimeLines"/>
					   	    	<c:forEach var="availableTimeLine" items="${customDatInt_array}">
					   	     		<form:option value="${availableTimeLine.position}">
						            	<c:out value="${availableTimeLine.name}" />
						          	</form:option>
					      	 	</c:forEach>
				     	 	</form:select>
				     	 	
				     	 	<a class="link" name="addDateInterval" href="#" title="<fmt:message key='tooltip.add'/>" id="addSelectedDateInterval1" onclick="flowAction('addDateInterval1');" >
								<i class="icon-plus"></i>
							</a>
						</div>
					</c:if>
		     	 	
		     	 	<c:if test="${not empty memBean.graphicalOptions.level1Options.timelineFeatures}">
		     	 		<div style="padding-top: 1em">
				     	 	<p><fmt:message key="graphicalExport.masterplan.helpStep2.selectedDateIntervals"/></p>
				     	 	<itera:define id="customDatInt_array" name="memBean" property="graphicalOptions.level1Options.timelineFeatures"/>
							<c:forEach var="di" items="${customDatInt_array}">
								<a class="link" href="#" onclick="createHiddenField('graphicalOptions.dateIntervalToRemove','${di.position}');flowAction('removeDateInterval1');" >
						 			<i class="icon-remove"></i>
						 		</a>
					            	<c:out value="${di.name}" />
					            <br/>
						 	</c:forEach>
					 	</div>
					</c:if>
					
					<div style="padding-top: 1em">
						<p>
							<fmt:message key="graphicalExport.masterplan.helpStep2.sorting">
								<fmt:param>${chosenContentType_1}</fmt:param>
							</fmt:message>
						</p>
						
						<label class="checkbox">
							<form:checkbox path="graphicalOptions.level1Options.hierarchicalSort" id="level1Options.hierarchicalSort" />
							<fmt:message key="graphicalExport.masterplan.helpStep2.sorting.option.hierarchical"/>
						</label>
					</div>
				
					<c:if test="${memBean.graphicalOptions.level1Options.additionalCustomColumnsAllowed && not empty memBean.graphicalOptions.level1Options.availableCustomColumns}">
						<div style="padding-top: 1em">
							<p><fmt:message key="graphicalExport.masterplan.helpStep2.chooseCustomColumns"/></p>
							
				     		<form:select path="graphicalOptions.level1Options.currentCustomColumn" cssStyle="width:200px;">
					   	    	<itera:define id="customCols_array" name="memBean" property="graphicalOptions.level1Options.availableCustomColumns"/>
					   	    	<c:forEach var="availableColumn" items="${customCols_array}">
					   	     		<form:option value="${availableColumn.head}">
							        	<c:choose>
								        	<c:when test="${availableColumn.type != 'attribute'}">
												<fmt:message key="${availableColumn.head}" />
								            </c:when>
								            <c:otherwise>
								            	<c:out value="${availableColumn.head}" />
								            </c:otherwise>
						                </c:choose>
						          	</form:option>
					      	 	</c:forEach>
				     	 	</form:select>
				     	 	
							<a class="link" name="addColumn" href="#" title="<fmt:message key='tooltip.add'/>" id="addSelectColumn" onclick="flowAction('addCustomColumn1');" >
								<i class="icon-plus"></i>
							</a>
						</div>
					</c:if>
				
					<c:if test="${not empty memBean.graphicalOptions.level1Options.selectedCustomColumns}">
						<div style="padding-top: 1em">
							<p><fmt:message key="graphicalExport.masterplan.helpStep2.selectedCustomColumns"/></p>
							
							<itera:define id="customCols_array" name="memBean" property="graphicalOptions.level1Options.selectedCustomColumns"/>
							<c:forEach var="col" items="${customCols_array}">
								<a class="link" href="#" onclick="createHiddenField('graphicalOptions.level1Options.columnToRemove','${col.head}');flowAction('removeCustomColumn1');" >
						 			<i class="icon-remove"></i>
						 		</a>
						 		<c:choose>
					        		<c:when test="${col.type != 'attribute'}">
										<fmt:message key="${col.head}" />
					            	</c:when>
					            	<c:otherwise>
					            		<c:out value="${col.head}" />
					            	</c:otherwise>
					            </c:choose>
					            <br/>
						 	</c:forEach>
						</div>
					</c:if>
				
				</c:if>
      		</div>
		</div>
	</div>
</div>

<c:if test="${not empty memBean.graphicalOptions.level1Options}">
<div class="accordion" id="level3Container">
	<div class="accordion-group">
		<div class="accordion-heading">
			<a class="accordion-toggle" data-toggle="collapse" data-parent="#level3Container" href="#level3Settings"
					onclick="toggleIcon('level3Icon', 'icon-resize-full', 'icon-resize-small');">
				<i id="level3Icon" class="icon-resize-small"></i>
        		<fmt:message key="graphicalExport.level.3" />
      		</a>
		</div>
		<div id="level3Settings" class="accordion-body collapse in">
			<div class="accordion-inner">
        		<p><fmt:message key="graphicalExport.masterplan.helpStep2.relatedTypes"/></p>
        		
        		<form:select path="graphicalOptions.selectedLevel2Relation" cssStyle="wide" onchange="flowAction('selectLevel2Type');">
					<itera:define id="relatedTypes_array" name="memBean" property="graphicalOptions.level1Options.availableRelatedTypes"/>
					<c:forEach items="${relatedTypes_array}" var="type" varStatus="countStatus">
						<form:option value="${type}">
							<fmt:message key="${type}" />
						</form:option>
					</c:forEach>
				</form:select>
				
				<c:if test="${not empty memBean.graphicalOptions.level2Options}">
				
					<c:if test="${memBean.graphicalOptions.level2Options.canBuildClosure}">
						<div style="padding-top: 1em">
						<label class="checkbox">
							<form:checkbox path="graphicalOptions.level2Options.buildClosure" id="buildClosure2" />
							<fmt:message key="graphicalExport.masterplan.helpStep2.relatedTypes.chaining" />
						</label>
						</div>
					</c:if>
				
					<div style="padding-top: 1em">
						<p><fmt:message key="graphicalExport.masterplan.helpColor" /></p>

						<div class="control-group">
							<div class="controls">
								<form:radiobutton path="graphicalOptions.level2Options.useDefaultColoring" value="false" onclick="flowAction('refreshReport')" id="level2.radio.attrs" />
							</div>
							<label class="control-label-right" for="level2.radio.attrs">
						  		<fmt:message key="graphicalExport.masterplan.helpColor.attrs" />
						  	</label>
						</div>
						
						<c:if test="${not memBean.graphicalOptions.level2Options.useDefaultColoring}">
							<div style="padding-left: 3em">
								<p>
									<fmt:message key="graphicalExport.helpDimensionAttributes">
										<fmt:param><fmt:message key="reports.color" /></fmt:param>
									</fmt:message>
								</p>
								
								<tiles:insertTemplate template="/jsp/GraphicalReporting/DimensionOptionsSimple.jsp">
								    <tiles:putAttribute name="dimension_key" value="reports.color" />
								    <tiles:putAttribute name="available_attributes_field" value="graphicalOptions.level2Options.availableColorAttributes" />
								    <tiles:putAttribute name="selected_id_field" value="graphicalOptions.level2Options.colorOptions.dimensionAttributeId" />
								    <tiles:putAttribute name="refresh_report_event" value="refreshReport" />
								    <tiles:putAttribute name="show_enum" value="true" />
								    <tiles:putAttribute name="show_number" value="false" />
								    <tiles:putAttribute name="show_text" value="true" />
								    <tiles:putAttribute name="show_date" value="true" />
								    <tiles:putAttribute name="show_responsibility" value="true" />
								</tiles:insertTemplate>
								
								<tiles:insertTemplate template="/jsp/GraphicalReporting/DimensionOptionColor.jsp">
									<tiles:putAttribute name="colorOptions" value="${memBean.graphicalOptions.level2Options.colorOptions}" />
									<tiles:putAttribute name="colorOptionsPath" value="graphicalOptions.level2Options.colorOptions" />
									<tiles:putAttribute name="refresh_report_event" value="refreshReport" />
									<tiles:putAttribute name="showUseValueRange" value="true" />
								</tiles:insertTemplate>
							</div>
						</c:if>
						
						<div class="control-group">
							<div class="controls">
								<form:radiobutton path="graphicalOptions.level2Options.useDefaultColoring" value="true" onclick="flowAction('refreshReport')" id="level2.radio.defaults" />
							</div>
							<label class="control-label-right" for="level2.radio.defaults">
						  		<fmt:message key="graphicalExport.masterplan.helpColor.default" />
						  	</label>
						</div>
					</div>
				
					<c:if test="${not empty memBean.graphicalOptions.level2Options.availableTimeLines}">
						<div style="padding-top: 1em">
							<p><fmt:message key="graphicalExport.masterplan.helpStep2.chooseDateIntervals"/></p>
							<form:select path="graphicalOptions.level2Options.currentDateInterval" cssStyle="width:200px;">
					   	    	<itera:define id="customDatInt_array" name="memBean" property="graphicalOptions.level2Options.availableTimeLines"/>
					   	    	<c:forEach var="availableTimeLine" items="${customDatInt_array}">
					   	     		<form:option value="${availableTimeLine.position}">
						            	<c:out value="${availableTimeLine.name}" />
						          	</form:option>
					      	 	</c:forEach>
				     	 	</form:select>
				     	 	
				     	 	<a class="link" name="addDateInterval" href="#" title="<fmt:message key='tooltip.add'/>" id="addSelectedDateInterval2" onclick="flowAction('addDateInterval2');" >
								<i class="icon-plus"></i>
							</a>
						</div>
					</c:if>
		     	 	
		     	 	<c:if test="${not empty memBean.graphicalOptions.level2Options.timelineFeatures}">
			     	 	<div style="padding-top: 1em">
				     	 	<p><fmt:message key="graphicalExport.masterplan.helpStep2.selectedDateIntervals"/></p>
				     	 	<itera:define id="customDatInt_array" name="memBean" property="graphicalOptions.level2Options.timelineFeatures"/>
							<c:forEach var="di" items="${customDatInt_array}">
								<a class="link" href="#" onclick="createHiddenField('graphicalOptions.dateIntervalToRemove','${di.position}');flowAction('removeDateInterval2');" >
						 			<i class="icon-remove"></i>
						 		</a>
					            	<c:out value="${di.name}" />
					            <br/>
						 	</c:forEach>
					 	</div>
					</c:if>
					
					<div style="padding-top: 1em">
						<p>
							<fmt:message key="graphicalExport.masterplan.helpStep2.sorting">
								<fmt:param>${chosenContentType_2}</fmt:param>
							</fmt:message>
						</p>
						
						<label class="checkbox">
							<form:checkbox path="graphicalOptions.level2Options.hierarchicalSort" id="level2Options.hierarchicalSort" />
							<fmt:message key="graphicalExport.masterplan.helpStep2.sorting.option.hierarchical"/>
						</label>
					</div>
				
					<c:if test="${memBean.graphicalOptions.level2Options.additionalCustomColumnsAllowed && not empty memBean.graphicalOptions.level2Options.availableCustomColumns}">
						<div style="padding-top: 1em">
							<p><fmt:message key="graphicalExport.masterplan.helpStep2.chooseCustomColumns"/></p>
							
				     		<form:select path="graphicalOptions.level2Options.currentCustomColumn" cssStyle="width:200px;">
					   	    	<itera:define id="customCols_array" name="memBean" property="graphicalOptions.level2Options.availableCustomColumns"/>
					   	    	<c:forEach var="availableColumn" items="${customCols_array}">
					   	     		<form:option value="${availableColumn.head}">
							        	<c:choose>
								        	<c:when test="${availableColumn.type != 'attribute'}">
												<fmt:message key="${availableColumn.head}" />
								            </c:when>
								            <c:otherwise>
								            	<c:out value="${availableColumn.head}" />
								            </c:otherwise>
						                </c:choose>
						          	</form:option>
					      	 	</c:forEach>
				     	 	</form:select>
				     	 	
							<a class="link" name="addColumn" href="#" title="<fmt:message key='tooltip.add'/>" id="addSelectColumn" onclick="flowAction('addCustomColumn2');" >
								<i class="icon-plus"></i>
							</a>
						</div>
					</c:if>
				
					<c:if test="${not empty memBean.graphicalOptions.level2Options.selectedCustomColumns}">
						<div style="padding-top: 1em">
							<p><fmt:message key="graphicalExport.masterplan.helpStep2.selectedCustomColumns"/></p>
							
							<itera:define id="customCols_array" name="memBean" property="graphicalOptions.level2Options.selectedCustomColumns"/>
							<c:forEach var="col" items="${customCols_array}">
								<a class="link" href="#" onclick="createHiddenField('graphicalOptions.level2Options.columnToRemove','${col.head}');flowAction('removeCustomColumn2');" >
						 			<i class="icon-remove"></i>
						 		</a>
						 		<c:choose>
					        		<c:when test="${col.type != 'attribute'}">
										<fmt:message key="${col.head}" />
					            	</c:when>
					            	<c:otherwise>
					            		<c:out value="${col.head}" />
					            	</c:otherwise>
					            </c:choose>
					            <br/>
						 	</c:forEach>
						</div>
					</c:if>
				
				</c:if>
      		</div>
		</div>
	</div>
</div>
</c:if>


<label class="checkbox">
	<form:checkbox path="graphicalOptions.useNamesLegend" id="useNamesLegend" />
	<fmt:message key="graphicalExport.helpUseNamesLegend" />
</label>
<label class="checkbox">
	<form:checkbox path="graphicalOptions.showSavedQueryInfo" id="showSavedQueryInfo"/>
	<fmt:message key="graphicalExport.showQueryInfo" />
</label>



<tiles:insertTemplate template="/jsp/GraphicalReporting/RequestButtons.jsp">
	<tiles:putAttribute name="permissionSaveReports" value="${permissionSaveReports}" />
	<tiles:putAttribute name="availableGraphicFormats" value="${memBean.graphicalOptions.availableGraphicFormats}" />
	<tiles:putAttribute name="exportFormatPath" value="graphicalOptions.selectedGraphicFormat" />
</tiles:insertTemplate>