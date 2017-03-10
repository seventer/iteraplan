<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<tiles:useAttribute name="colorOptions" />
<tiles:useAttribute name="colorOptionsPath" />
<tiles:useAttribute name="tableClass" ignore="true" />
<tiles:useAttribute name="showOnlyTheseValues" ignore="true" />
<tiles:useAttribute name="showUseValueRange" ignore="true" />
<tiles:useAttribute name="refresh_report_event" ignore="true" />
<%-- If set to true: this jsp is displaying a minimal version without the table markup --%>
<tiles:useAttribute name="minimal" ignore="true" />

<c:set var="refreshReportJavaScript" value="" />
<c:if test="${not empty refresh_report_event}">
  <c:set var="refreshReportJavaScript"
        value="flowAction('${refresh_report_event}');" />
</c:if>

<c:set var="attributeValues" value="${colorOptions.attributeValues}" />
<c:if test="${empty showOnlyTheseValues}">
  <c:set var="showOnlyTheseValues" value="${attributeValues}" />
</c:if>
<c:set var="valueToColorMap" value="${colorOptions.valueToColorMap}" />
<c:set var="availableColors" value="${colorOptions.availableColors}" />

	<c:forEach var="mapping" items="${valueToColorMap}" varStatus="loopStatus">
		<c:if test="${fn:contains(showOnlyTheseValues, mapping.key)}">
			<div class="control-group">
				<c:if test="${empty minimal}" >
					<label class="control-label">&nbsp;</label>
				</c:if>
				<div class="controls">
				<c:choose>
					<c:when test="${not colorOptions.useColorRange}">
						<c:set var="color" value="${mapping.value.color}" />
						<c:if test="${empty color}">
							<c:set var="color" value="${availableColors[0]}" />
						</c:if>
						<form:errors path="${colorOptionsPath}.valueToColorMap['${mapping.key}'].color" cssClass="errorMsg" htmlEscape="false"/>
						<form:select path="${colorOptionsPath}.valueToColorMap['${mapping.key}'].color" onchange="changeColor(this)" cssStyle="width:60px;background-color:#${color}">
							<c:forEach var="color" items="${availableColors}">
								<form:option value="${color}" cssStyle="background-color:#${color}">&nbsp;</form:option>
					 			</c:forEach>
						</form:select>
					</c:when>
					<c:otherwise>
					    <tiles:insertTemplate template="/jsp/common/ColorPicker.jsp">
					      <tiles:putAttribute name="id" value="${colorOptionsPath}_${mapping.value.color}"/>
					      <tiles:putAttribute name="path" value="${colorOptionsPath}.valueToColorMap['${mapping.key}'].color"/>
					    </tiles:insertTemplate>
					</c:otherwise>
				</c:choose>
				<span class="help-inline">
					<c:choose>
						<c:when test="${mapping.value.name == 'graphicalReport.unspecified' || mapping.value.name == 'graphicalReport.specified' || 
										mapping.value.name == colorOptions.lowerBoundMessageKey || mapping.value.name == colorOptions.upperBoundMessageKey}">
							<fmt:message key="${mapping.value.name}" />
						</c:when>
						<c:otherwise>
							<c:out value="${mapping.value.name}" />
				    	</c:otherwise>
					</c:choose>
				</span>
				</div>
			</div>
		</c:if>
	</c:forEach>
	  
	<c:if test="${not empty showUseValueRange and showUseValueRange and colorOptions.colorRangeAvailable}">
	  <div class="control-group">
	  	<c:if test="${empty minimal}" >
		  <label class="control-label">&nbsp;</label>
		</c:if>
		  <div class="controls">
			<form:checkbox id="${colorOptionsPath}.useColorRange" path="${colorOptionsPath}.useColorRange" onclick="${refreshReportJavaScript}" />
			<fmt:message key="graphicalExport.configuration.useColorRange"/>
		  </div>
	  </div>
	</c:if>
