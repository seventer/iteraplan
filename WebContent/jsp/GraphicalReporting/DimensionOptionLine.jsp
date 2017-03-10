<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%-- If set to true: this jsp is displaying a minimal version without the table markup --%>
<tiles:useAttribute name="minimal" ignore="true" />

<tiles:useAttribute name="lineOptions" />
<tiles:useAttribute name="valueToLineTypeMapPath" />

<c:set var="attributeValues" value="${lineOptions.attributeValues}" />
<c:set var="availableLineTypes" value="${lineOptions.availableLineTypes}" />
<c:set var="lineTypeIds" value="${lineOptions.availableLineTypeIds}"/>

<c:if test="${not empty attributeValues}" >
	<c:forEach var="mapping" items="${lineOptions.valueToLineTypeMap}" varStatus="loopStatus">
		<div class="control-group">
		<c:if test="${empty minimal}" >
			<label class="control-label">&nbsp;</label>
		</c:if>
			<div class="controls">
			<form:errors path="${valueToLineTypeMapPath}['${mapping.key}']" cssClass="errorMsg" htmlEscape="false"/>
			<form:select path="${valueToLineTypeMapPath}['${mapping.key}']" cssStyle="width:60px;">
				<c:forEach var="lineTypeId" items="${lineTypeIds}">
					<form:option value="${lineTypeId}">
						<c:out value="${availableLineTypes[lineTypeId]}" escapeXml="false" />
					</form:option>
			 	</c:forEach>
			</form:select>
			<c:choose>
				<c:when test="${mapping.key == 'graphicalReport.unspecified'}">
					<fmt:message key="graphicalReport.unspecified" />
				</c:when>
				<c:otherwise>
					<c:out value="${mapping.key}" />
		    	</c:otherwise>
		    </c:choose>
		    </div>
		</div>
	</c:forEach>
</c:if>
