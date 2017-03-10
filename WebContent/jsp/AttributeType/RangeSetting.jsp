<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="changeCheckboxAction" ignore="true" />
<tiles:useAttribute name="range_style" ignore="true" />


<itera:define id="message_key" name="memBean" property="componentModel.rangeUniformyDistributed.labelKey" />
<c:set var="boolean_field" value="componentModel.rangeUniformyDistributed.current" />
<itera:define id="component_mode" name="memBean" property="componentModel.rangeUniformyDistributed.componentMode" />
<itera:define id="html_id" name="memBean" property="componentModel.rangeUniformyDistributed.htmlId" />

<c:if test="${empty changeCheckboxAction}">
    <c:set var="changeCheckboxAction" value="" />
</c:if>

<div class="control-group">
	<label class="control-label" for="${html_id}_checkbox">
    	<fmt:message key="${message_key}" />:
	</label>
	<div class="controls">
		<c:set var="checkboxBoolean">
	        <itera:write name="memBean" property="${boolean_field}" escapeXml="false" />
	    </c:set> 
	    <c:choose>
	        <c:when test="${component_mode != 'READ'}">
	        	<form:checkbox path="${boolean_field}" id="${html_id}_checkbox" onclick="toggleLayer('rangeListId');"/>
	        </c:when>
	        <c:otherwise>
	            <c:choose>
	                <c:when test="${checkboxBoolean}">
	                    <fmt:message key="global.yes" />
	                </c:when>
	                <c:otherwise>
	                    <fmt:message key="global.no" />
	                </c:otherwise>
	            </c:choose>
	        </c:otherwise>
	    </c:choose>
	</div>
</div>

<div class="control-group">
	<c:choose>
	    <c:when test="${component_mode != 'READ'}">
	        <c:if test="${checkboxBoolean}">
	            <c:set var="range_style" value="hiddenRows" />
	        </c:if>
	        <c:if test="${!checkboxBoolean}">
	            <c:set var="range_style" value="visibleRows" />
	        </c:if>
	        <div id="rangeListId" class="<c:out value="${range_style}"/>">
				<tiles:insertTemplate template="/jsp/AttributeType/tiles/RangeValuesComponentListView.jsp" flush="true">
					<tiles:putAttribute name="path_to_componentModel" value="componentModel.rangesModel" />
				</tiles:insertTemplate>
	        </div>
	    </c:when>
	    <c:otherwise>
	        <c:choose>
				<c:when test="${!checkboxBoolean}">
					<label class="control-label" for="${html_id}_checkbox">
				    	<fmt:message key="manageAttributes.numberAT.manualRanges" />
					</label>
					<div class="controls">
						<c:forEach var="object" items="${memBean.componentModel.rangesModel.rangeValuesAsString}" varStatus="status">
	              			<c:out value="${object}" /> 
	              			<c:if test="${!status.last}" >
	              				<span class="blankSpace"><c:out value="|"/></span>
	              			</c:if>
	              		</c:forEach>
					</div>
				</c:when>            
	        </c:choose>
	    </c:otherwise>
	</c:choose>
</div>