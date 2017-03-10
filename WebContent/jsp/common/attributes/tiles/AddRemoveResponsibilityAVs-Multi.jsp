<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="atPartPath"/>
<tiles:useAttribute name="extended_html_id" />

<itera:define id="atPart" name="memBean" property="${atPartPath}"/>

<c:forEach items="${atPart.connectedAVs}" var="connectedAv" varStatus="avStatus">
	<a id="<c:out value="${extended_html_id}_${connectedAv.valueForHtmlId}_remove" />" class="link" href="#"
		title="<fmt:message key="tooltip.remove"/>" 
		onclick="createHiddenField('<c:out value="${atPartPath}.avIdToRemove','${connectedAv.id}"/>');flowAction('update');" >
		<i class="icon-remove"></i>
	</a>
	&nbsp;
	<c:out value="${connectedAv.name}" />
	<br/>
</c:forEach>
<span class="dontwrap">
	<a id="<c:out value="${extended_html_id}_add" />" class="link" href="#"
		title="<fmt:message key="tooltip.add"/>" 
		onclick="flowAction('update');" >
		<i class="icon-plus"></i>
	</a>
	&nbsp;
	<form:select path="${atPartPath}.avIdToAdd" id="${extended_html_id}_select" cssClass="combobox">
		<form:options items="${atPart.availableAVsForPresentation}" itemLabel="name" itemValue="id"/>
	</form:select>
	&nbsp;
	<form:errors path="${atPartPath}.avIdToAdd" cssClass="errorMsg" htmlEscape="false"/>
</span>