<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<tiles:useAttribute name="atPartPath" />
<tiles:useAttribute name="extended_html_id" />

<itera:define id="atPart" name="memBean" property="${atPartPath}" />

<form:select path="${atPartPath}.avIdToSet" id="${extended_html_id}_select" cssClass="name combobox" onchange="displayDescription(${atPart.attributeType.id}, this.value, '${extended_html_id}');" 
	onkeyup="displayDescription(${atPart.attributeType.id}, this.value, '${extended_html_id}');">
	<form:options items="${atPart.availableAVsForPresentation}" itemLabel="name" itemValue="id"/>
</form:select>
&nbsp;
<form:errors path="${atPartPath}.avIdToSet" cssClass="errorMsg" htmlEscape="false"/>
<br/>
<c:set var="avsborderstyle" value="border"/>
<c:if test="${empty atPart.avIdToSet}">
	<c:set var="avsborderstyle" value=""/>
</c:if>
    
<script type="text/javascript">
	addEvent(window, 'load', displayDescHelperEnumAVSingle, false);
	function displayDescHelperEnumAVSingle() {
		var selectBox = $('#<c:out value="${extended_html_id}_select" />');
		var index = selectBox.get('value');

		displayDescription(<c:out value="${atPart.attributeType.id}" />, index, '<c:out value="${extended_html_id}" />');
	}
</script>
     
<div id="<c:out value="${extended_html_id}_descriptionOutput${atPart.attributeType.id}"/>" class="<c:out value="${avsborderstyle} nameintable"/>">
	<c:forEach items="${atPart.availableAVsForPresentation}" var="avs">
		<c:choose>
			<c:when test="${atPart.avIdToSet == avs.id}">
				<c:set var="avsstyle" value="visible"/>
			</c:when>
			<c:otherwise>
				<c:set var="avsstyle" value="hidden"/>
			</c:otherwise>
		</c:choose>
		<c:if test="${not empty avs.description}">
			<div id="<c:out value="${extended_html_id}_enumAVdescription${avs.id}"/>" class="<c:out value="${avsstyle}"/> nameintable">
				<itera:write name="avs" property="description" breaksAndSpaces="true" escapeXml="true" />
			</div>
		</c:if>
	</c:forEach>
</div>