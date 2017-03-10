<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="path_to_componentModel" />

<itera:define id="attributes" name="memBean" property="${path_to_componentModel}.attributeValues" />
<c:forEach items="${attributes}" var="attributeValue">
	<div class="control-group">
		<c:choose>
			<c:when test="${attributeValue.attributeType.multiassignmenttype}">
				<div class="controls">
					<form:checkbox path="${path_to_componentModel}.massUpdateAttributeItem.selectedAttributeValueStringIds" value="${attributeValue.id}" id="EnumOrResponsibility_checkbox" />
				</div>
				<label class="control-label-right" for="EnumOrResponsibility_checkbox">
					<c:out value="${attributeValue.valueString}" />
				</label>
			</c:when>
			<c:otherwise>
				<div class="controls">
					<form:radiobutton path="${path_to_componentModel}.massUpdateAttributeItem.selectedAttributeValueStringIds" value="${attributeValue.id}" id="EnumOrResponsibility_radio" />
				</div>
				<label class="control-label-right" for="EnumOrResponsibility_radio">
					<c:out value="${attributeValue.valueString}" />
				</label>
			</c:otherwise>
		</c:choose>
	</div>
</c:forEach>