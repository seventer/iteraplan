<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<tiles:useAttribute name="atPartPath" />
<tiles:useAttribute name="extended_html_id" />

<itera:define id="atPart" name="memBean" property="${atPartPath}" />

<%-- If this attribute type is marked as mandatory, warn the user by displaying the value in red. --%>
<c:set var="warnMandatory" value="" />
<c:if test="${atPart.attributeType.mandatory == true}">
	<c:set var="warnMandatory" value="warning" />
</c:if>

<c:choose>
  	<c:when test="${empty atPart.attributeValue.id}">
		<span class="<c:out value="${warnMandatory}" />">
			<fmt:message key="attribute.novalue" />
		</span>
  	</c:when>
  	<c:otherwise>	   
		<c:out value="${atPart.attributeValue.name}" />
  	</c:otherwise>
</c:choose>