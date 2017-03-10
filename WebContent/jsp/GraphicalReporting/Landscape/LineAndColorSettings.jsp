<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<c:set var="isRowRelationSet" value="false"/>
<c:if test="${not empty memBean.graphicalOptions.selectedRowRelation}">
	<c:set var="isRowRelationSet" value="true"/>
</c:if>

<c:set var="isColumnRelationSet" value="false"/>
<c:if test="${not empty memBean.graphicalOptions.selectedColumnRelation}">
	<c:set var="isColumnRelationSet" value="true"/>
</c:if>

<c:set var="contentType" scope="page">
	<fmt:message key="${memBean.graphicalOptions.selectedBbType}"/>
</c:set>

<div class="accordion" id="lineAndColorSettingsContainer">
	<div class="accordion-group">
		<div class="accordion-heading">
			<a class="accordion-toggle" data-toggle="collapse" data-parent="#lineAndColorSettingsContainer" href="#lineAndColorSettings"
					onclick="toggleIcon('lineAndColorSettingsIcon', 'icon-resize-full', 'icon-resize-small');" >
				<i id="lineAndColorSettingsIcon" class="icon-resize-small"></i>
				<fmt:message key="graphicalExport.helpColorLabel" />&nbsp;<fmt:message key="global.and" />&nbsp;<fmt:message key="graphicalExport.helpLineTypeLabel" />
			</a>
		</div>
		<div id="lineAndColorSettings" class="accordion-body in collapse">
           <div class="accordion-inner">
           
	           	<div class="row-fluid">
	           		<div class="form-horizontal span6">
	           			<div class="control-group">
			              	<fmt:message key="graphicalExport.landscape.configuration.content.color">
								<fmt:param value="${contentType}"/>
							</fmt:message>:
	             		</div>
						<div class="control-group">
		             		<tiles:insertTemplate template="/jsp/GraphicalReporting/DimensionOptionsSimple.jsp">
								<tiles:putAttribute name="available_attributes_field" value="queryResults.contentQuery.queryForms[0].dimensionAttributes" />
								<tiles:putAttribute name="selected_id_field" value="graphicalOptions.colorOptionsBean.dimensionAttributeId" />
								<tiles:putAttribute name="refresh_report_event" value="updateColorAttribute" />
								<tiles:putAttribute name="show_enum" value="true" />
								<tiles:putAttribute name="show_number" value="false" />
								<tiles:putAttribute name="show_text" value="false" />
								<tiles:putAttribute name="show_date" value="false" />
								<tiles:putAttribute name="show_responsibility" value="true" />
								<tiles:putAttribute name="minimal" value="true" />
							</tiles:insertTemplate>
						</div>
						<tiles:insertTemplate template="/jsp/GraphicalReporting/DimensionOptionColor.jsp">
							<tiles:putAttribute name="colorOptions" value="${memBean.graphicalOptions.colorOptionsBean}" />
							<tiles:putAttribute name="colorOptionsPath" value="graphicalOptions.colorOptionsBean" />
							<tiles:putAttribute name="refresh_report_event" value="updateColorAttribute" />
							<tiles:putAttribute name="showUseValueRange" value="true" />
							<tiles:putAttribute name="minimal" value="true" />
						</tiles:insertTemplate>
		           	</div>
					<div class="form-horizontal span6">
	          			<div class="control-group">
				           	<fmt:message key="graphicalExport.landscape.configuration.content.line">
								<fmt:param value="${contentType}"/>
							</fmt:message>:
	          			</div>
	          			<div class="control-group">
	           				<tiles:insertTemplate template="/jsp/GraphicalReporting/DimensionOptionsSimple.jsp">
								<tiles:putAttribute name="available_attributes_field" value="queryResults.contentQuery.queryForms[0].singleValueDimensionAttributes" />
								<tiles:putAttribute name="selected_id_field" value="graphicalOptions.lineOptionsBean.dimensionAttributeId" />
								<tiles:putAttribute name="refresh_report_event" value="updateLineTypeAttribute" />
								<tiles:putAttribute name="show_enum" value="true" />
								<tiles:putAttribute name="show_number" value="false" />
								<tiles:putAttribute name="show_text" value="false" />
								<tiles:putAttribute name="show_date" value="false" />
								<tiles:putAttribute name="show_responsibility" value="true" />
								<tiles:putAttribute name="minimal" value="true" />
							</tiles:insertTemplate>
						</div>
						<tiles:insertTemplate template="/jsp/GraphicalReporting/DimensionOptionLine.jsp">
							<tiles:putAttribute name="lineOptions" value="${memBean.graphicalOptions.lineOptionsBean}" />
							<tiles:putAttribute name="valueToLineTypeMapPath" value="graphicalOptions.lineOptionsBean.valueToLineTypeMap" />
							<tiles:putAttribute name="minimal" value="true" />
						</tiles:insertTemplate>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>