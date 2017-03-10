<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<tiles:useAttribute name="atPartPath" />
<tiles:useAttribute name="extended_html_id" />

<itera:define id="atPart" name="memBean" property="${atPartPath}"/>

<c:choose>
   	<c:when test="${atPart.attributeType.multiline == true}">
		<form:textarea path="${atPartPath}.attributeValueAsString" cssClass="multilinetextav" id="${extended_html_id}_text"/>
	</c:when>
	<c:otherwise>
		<form:input path="${atPartPath}.attributeValueAsString" cssClass="input-large" id="${extended_html_id}_text" />  
	</c:otherwise>
</c:choose>
&nbsp;
<form:errors path="${atPartPath}.attributeValueAsString" cssClass="errorMsg" htmlEscape="false"/>