<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<tiles:useAttribute name="valueComponentModelPath" />

<itera:define id="valueModel" name="memBean" property="${valueComponentModelPath}" />

<c:choose>
	<c:when test="${valueModel.componentMode == 'CREATE'}">
		<!-- Value should be editable only in CREATE mode in this case -->
		<div class="control-group">
			<div class="controls">
				<select name="${valueComponentModelPath}.attributeValueAsString" id="${valueModel.htmlId}" class="combobox">
					<c:forEach items="${valueModel.availableValuesAsStrings}" var="value">
							<c:set var="selected" value="" />
						<c:if test="${value == valueModel.attributeValueAsString}">
							<c:set var="selected" value=" selected" />
						</c:if>
						<option value="${value}"<c:out value="${selected}" />>${value}</option>
					</c:forEach>
				</select>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<span>${valueModel.attributeValueAsString}</span>
	</c:otherwise>
</c:choose>
