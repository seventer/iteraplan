<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="atPartPath" />
<tiles:useAttribute name="extended_html_id" />

<itera:define id="atPart" name="memBean" property="${atPartPath}"/>

<c:forEach items="${atPart.connectedAVs}" var="connectedAv" varStatus="avStatus">
	<a id="<c:out value="${extended_html_id}" />_<c:out value="${connectedAv.valueForHtmlId}" />_remove" class="link" href="#"
		title="<fmt:message key="tooltip.remove"/>" 
		onclick="createHiddenField('<c:out value="${atPartPath}" />.avIdToRemove','<c:out value="${connectedAv.id}"/>');flowAction('update');" >
		<i class="icon-remove"></i>
	</a>
	&nbsp;
	<c:out value="${connectedAv.name}" />
	<br/>
	<c:if test="${not empty connectedAv.description}">
		<c:out value="${connectedAv.description}"/>
		<br/>
	</c:if>
</c:forEach>
<span class="dontwrap">
	<a id="<c:out value="${extended_html_id}_add" />" class="link" href="#"
		title="<fmt:message key="tooltip.add"/>" 
		onclick="flowAction('update');" >
		<i class="icon-plus"></i>
	</a>
	&nbsp;
	<form:select path="${atPartPath}.avIdToAdd" cssClass="name combobox" onchange="displayDescription(${atPart.attributeType.id}, this.value, '${extended_html_id}');" 
		onkeyup="displayDescription(${atPart.attributeType.id}, this.value, '${extended_html_id}');" id="${extended_html_id}_select">
		<form:options items="${atPart.availableAVsForPresentation}" itemLabel="name" itemValue="id" />
	</form:select>
	&nbsp;
	<form:errors path="${atPartPath}.avIdToAdd" cssClass="errorMsg" htmlEscape="false"/>
</span>
<br/>
<c:set var="avsborderstyle" value="border"/>
<c:if test="${empty atPart.avIdToAdd}">
	<c:set var="avsborderstyle" value=""/>
</c:if>
    
<script type="text/javascript">
	addEvent(window, 'load', displayDescHelperEnumAVMulti, false);
	function displayDescHelperEnumAVMulti() {
		var selectBox = $('#<c:out value="${extended_html_id}_select" />');
		var index = selectBox.get('value');

		displayDescription(<c:out value="${atPart.attributeType.id}" />, index, '<c:out value="${extended_html_id}" />');
	}
</script>

<div id="<c:out value="${extended_html_id}_descriptionOutput${atPart.attributeType.id}"/>" class="<c:out value="${avsborderstyle}"/> nameintable">
	<c:forEach items="${atPart.availableAVsForPresentation}" var="avs">
		<c:choose>
			<c:when test="${atPart.avIdToAdd == avs.id}">
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