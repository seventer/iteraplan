<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
 

<div class="accordion" id="lineAndColorSettingsContainer">
   <div class="accordion-group">
      <div class="accordion-heading">
         <a class="accordion-toggle" data-toggle="collapse" data-parent="#lineAndColorSettingsContainer" href="#lineAndColorSettings"
				onclick="toggleIcon('lineAndColorIcon', 'icon-resize-full', 'icon-resize-small');" >
			<i id="lineAndColorIcon" class="icon-resize-small"></i>
            <fmt:message key="graphicalExport.helpColorLabel" />&nbsp;<fmt:message key="global.and" />&nbsp;<fmt:message key="graphicalExport.helpLineTypeLabel" />
         </a>
      </div>
   <div id="lineAndColorSettings" class="accordion-body in collapse">
      <div class="accordion-inner">
      	<div class="row-fluid">
      		<div class="span6">
      			<fmt:message key="graphicalExport.informationflow.helpColor">
					<fmt:param>${chosenContentType}</fmt:param>
				</fmt:message>
				<br/>
				<ul>
					<li>
						<fmt:message key="graphicalExport.helpDimensionAttributes">
							<fmt:param><fmt:message key="reports.color" /></fmt:param>
						</fmt:message>
					</li>
				</ul>
				<br/>
				<tiles:insertTemplate template="/jsp/GraphicalReporting/DimensionOptionsSimple.jsp">
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
      		<div class="span6">
      			<fmt:message key="graphicalExport.informationflow.helpLineType">
					<fmt:param>${chosenContentType}</fmt:param>
				</fmt:message>
				<br/>
				<ul>
					<li>
						<fmt:message key="graphicalExport.helpDimensionAttributesSingleValue">
							<fmt:param><fmt:message key="reports.lineType" /></fmt:param>
						</fmt:message>
					</li>
				</ul>
				<br/>
				<tiles:insertTemplate template="/jsp/GraphicalReporting/DimensionOptionsSimple.jsp">
					<tiles:putAttribute name="dimension_key" value="reports.lineType" />
					<tiles:putAttribute name="available_attributes_field" value="graphicalOptions.singleValueIsiAttributes" />
					<tiles:putAttribute name="selected_id_field" value="graphicalOptions.lineOptionsBean.dimensionAttributeId" />
					<tiles:putAttribute name="refresh_report_event" value="refreshReport" />
					<tiles:putAttribute name="show_enum" value="true" />
					<tiles:putAttribute name="show_number" value="false" />
					<tiles:putAttribute name="show_text" value="true" />
					<tiles:putAttribute name="show_date" value="true" />
					<tiles:putAttribute name="show_responsibility" value="true" />
				</tiles:insertTemplate>
					<tiles:insertTemplate template="/jsp/GraphicalReporting/DimensionOptionLine.jsp">
					<tiles:putAttribute name="lineOptions" value="${memBean.graphicalOptions.lineOptionsBean}" />
					<tiles:putAttribute name="valueToLineTypeMapPath" value="graphicalOptions.lineOptionsBean.valueToLineTypeMap" />
				</tiles:insertTemplate>
      		</div>
      	</div>
		<br />
      </div>
     </div>
    </div>
   </div>    

<div class="accordion" id="lineCaptionSettingsContainer">
   <div class="accordion-group">
      <div class="accordion-heading">
         <a class="accordion-toggle" data-toggle="collapse" data-parent="#lineCaptionSettingsContainer" href="#lineCaptionSettings"
				onclick="toggleIcon('lineCaptionSettingsIcon', 'icon-resize-full', 'icon-resize-small');" >
		   <i id="lineCaptionSettingsIcon" class="icon-resize-small"></i>
           <fmt:message key="graphicalExport.helpLineCaptionLabel" />
         </a>
      </div>
      <div id="lineCaptionSettings" class="accordion-body in collapse">
         <div class="accordion-inner">
           <p>
	     	 <fmt:message key="graphicalExport.informationflow.helpLineCaption">
				<fmt:param>${chosenContentType}</fmt:param>
			 </fmt:message>
		   </p>
		  
		  <div class="form-horizontal">
		    <fieldset>
			  <div class="control-group">
	            <label class="control-label" for="optionsCheckboxList"><fmt:message key="reports.lineCaption" />:</label>
	            <div class="controls">
	            
	              <%-- transported business object (default) --%>
	              <label class="checkbox">
	                <form:checkbox path="graphicalOptions.selectionType" value="1"
								onclick="flowAction('refreshReport');"/>
	                <fmt:message key="businessObject.plural"/>
	              </label>
	              
	              <%-- technical component behind interface --%>
	              <label class="checkbox">
	                <form:checkbox path="graphicalOptions.selectionType" value="2"
								onclick="flowAction('refreshReport');" />
	                <fmt:message key="technicalComponent.plural"/>
	              </label>
	              
	              <label class="checkbox">
	              	<form:checkbox path="graphicalOptions.selectionType" value="4" 
								onclick="flowAction('refreshReport');" />
					<fmt:message key="global.name"/>
	              </label>
	              
	              <%-- description of the interface --%>
	              <label class="checkbox">
	              	<form:checkbox path="graphicalOptions.selectionType" value="3" 
								onclick="flowAction('refreshReport');" />
					<fmt:message key="global.description"/>
	              </label>
	              
	              <%-- attributes of interface with dropdown for selecting attribute --%>
	              <label class="checkbox">
	              	<form:checkbox path="graphicalOptions.selectionType" value="0" 
								onclick="flowAction('refreshReport');" />
					<fmt:message key="global.attributes"/>
	              </label>
	            </div>
	          </div>
			  
			  <div class="control-group">
	            <label class="control-label" for="appendedInput"></label>
	            <div class="controls">
	              <div class="input-append">
	                <tiles:insertTemplate template="/jsp/GraphicalReporting/DimensionOptionsSimple.jsp">
						<tiles:putAttribute name="dimension_key" value="" />
						<tiles:putAttribute name="available_attributes_field" value="graphicalOptions.isiAttributes" />
						<tiles:putAttribute name="selected_id_field" value="graphicalOptions.lineCaptionSelectedAttributeId" />
						<tiles:putAttribute name="refresh_report_event" value="refreshReport" />
						<tiles:putAttribute name="show_enum" value="true" />
						<tiles:putAttribute name="show_number" value="true" />
						<tiles:putAttribute name="show_text" value="true" />
						<tiles:putAttribute name="show_date" value="true" />
						<tiles:putAttribute name="show_responsibility" value="true" />
						<tiles:putAttribute name="disabledAttribute" value="${not memBean.graphicalOptions.attributeLineCaption}" />
					</tiles:insertTemplate>
	              </div>
	            </div>
	          </div>
		    </fieldset>
		  </div>
		  
       </div>
     </div>
   </div>
</div>  

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
        
			<div class="controls"  style="float: none;">
				<label class="checkbox">
					<form:checkbox path="graphicalOptions.showIsBusinessObjects" />
					<fmt:message key="graphicalExport.informationflow.helpShowIsBusinessObjects" />
				</label>
				<label class="checkbox">
					<form:checkbox path="graphicalOptions.showIsBaseComponents" />
					<fmt:message key="graphicalExport.informationflow.helpShowIsBaseComponents" />
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
			<br></br>
			<tiles:insertTemplate template="/jsp/GraphicalReporting/InformationFlow/ShowSelectLayout.jsp"/>
        </div>
     </div>
   </div>
</div>