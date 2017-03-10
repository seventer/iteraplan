<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="currentMassUpdateAttributeConfig" />

<c:set var="attributeValueCount" value="${fn:length(memBean.massUpdateAttributeConfig)}" />

<c:if test="${attributeValueCount > 0}">
	<itera:define id="attributeValues" name="memBean" property="massUpdateAttributeConfig[${currentMassUpdateAttributeConfig}].attributeValues" />
	<c:forEach items="${attributeValues}" var="value">
		<div class="control-group">
			<c:choose>  	
				<c:when test="${value.attributeType.multiassignmenttype}">
					<div class="controls">
						<form:checkbox path="massUpdateAttributeConfig[${currentMassUpdateAttributeConfig}].selectedStandardAtributeValueStringIds" value="${value.id}" id="StandardValuesEnumOrResponsibilityAV_${value.id}_checkbox"/>
					</div>
					<label class="control-label-right" for="StandardValuesEnumOrResponsibilityAV_${value.id}_checkbox">
						<c:out value="${value.valueString}" />
					</label>
				</c:when>
				<c:otherwise>
					<div class="controls">
						<form:radiobutton path="massUpdateAttributeConfig[${currentMassUpdateAttributeConfig}].selectedStandardAtributeValueStringIds" value="${value.id}" id="StandardValuesEnumOrResponsibilityAV_${value.id}_radio" />
					</div>
					<label class="control-label-right" for="StandardValuesEnumOrResponsibilityAV_${value.id}_radio">
						<c:out value="${value.valueString}" />
					</label>
				</c:otherwise>
			</c:choose>
		</div>
	</c:forEach>
</c:if>