<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<tiles:useAttribute name="atPartPath"/>
<tiles:useAttribute name="extended_html_id" />
<tiles:useAttribute name="showDetailsInEditMode" ignore="true" />

<c:if test="${empty showDetailsInEditMode}">
  <c:set var="showDetailsInEditMode" value="false" />
</c:if>

<itera:define id="atPart" name="memBean" property="${atPartPath}"/>

<c:if test="${showATG == true}">
	<c:set var="atClass" value='' />
</c:if>

<%-- Attribute Type Name --%>
<c:set var="linkScript">
	<itera:linkToElement name="atPart" property="attributeType" type="html" />
</c:set>
<tiles:insertTemplate template="/jsp/common/attributes/AttributeDescription.jsp" flush="true">
	<tiles:putAttribute name="atPart" value="${atPart}" />
	<tiles:putAttribute name="showDetailsInEditMode" value="${showDetailsInEditMode}" />
</tiles:insertTemplate>
<a href="<c:out value="${linkScript}" />">
	<c:out value="${atPart.attributeType.name} ${overviewMode}"/>
</a>
<c:if test="${atPart.attributeType.mandatory == true}">
	<fmt:message key="global.mandatory" />
</c:if>
: