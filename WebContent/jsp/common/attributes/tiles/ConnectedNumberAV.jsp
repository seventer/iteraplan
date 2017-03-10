<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<tiles:useAttribute name="atPartPath" />
<tiles:useAttribute name="extended_html_id" />
<tiles:useAttribute name="valueOnly" ignore="true" />

<itera:define id="atPart" name="memBean" property="${atPartPath}" />

<c:set var="warnRange" value="" />
<c:if test="${atPart.outOfRange}">
	<c:set var="warnRange" value="warning" />
</c:if>

<%-- If this attribute type is marked as mandatory, warn the user by displaying the value in red. --%>
<c:set var="warnMandatory" value="" />
<c:if test="${atPart.attributeType.mandatory == true}">
	<c:set var="warnMandatory" value="warning" />
</c:if>

<c:choose>
  	<c:when test="${empty atPart.attributeValueAsString}">
     	<span class="<c:out value="${warnMandatory}" />">
			<fmt:message key="attribute.novalue" />
     	</span>
  	</c:when>
   	<c:otherwise>
	  	<span class="<c:out value="${warnRange}"/> ">
			<c:out value="${atPart.attributeValueAsString}" /> 
			<c:if test="${not empty atPart.attributeType.unit}">
		  		<c:out value=" ${atPart.attributeType.unit}" />
			</c:if>
	  	</span>
   	</c:otherwise>
</c:choose>