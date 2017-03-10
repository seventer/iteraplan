<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<tiles:useAttribute name="path_to_componentModel" />
<tiles:useAttribute name="changeCheckboxAction" ignore="true" />
<tiles:useAttribute name="minimal" ignore="true" />

<%-- If set to true: In edit mode, the checkBox will be shown on the left side --%>
<tiles:useAttribute name="boxLeft" ignore="true" />

<itera:define id="message_key" name="memBean" property="${path_to_componentModel}.labelKey" />
<c:set var="boolean_field" value="${path_to_componentModel}.current" />
<itera:define id="component_mode" name="memBean" property="${path_to_componentModel}.componentMode" />
<itera:define id="html_id" name="memBean" property="${path_to_componentModel}.htmlId" />

<c:if test="${component_mode == 'READ'}">
  <c:set var="tdstyle" value="top margin" />
</c:if>

<c:if test="${empty changeCheckboxAction}">
  <c:set var="changeCheckboxAction" value="" />
</c:if>

<div class="control-group">
	<c:if test="${empty minimal}" >
		<label class="control-label " for="${html_id}_checkbox">
	  		<fmt:message key="${message_key}" />:
	  	</label>
	</c:if>
	<div class="controls">
		<c:choose>
			<c:when test="${component_mode != 'READ'}">
				<c:if test="${not boxLeft}">
					<form:checkbox path="${boolean_field}" id="${html_id}_checkbox"/>
				</c:if>
				<form:errors path="${boolean_field}" cssClass="errorMsg" htmlEscape="false"/>
			</c:when>
			<c:otherwise>
				<c:set var="checkboxBoolean">
					<itera:write name="memBean" property="${boolean_field}" escapeXml="false" />
				</c:set>
				<c:choose>
					<c:when test="${checkboxBoolean}">
						<fmt:message key="global.yes" />
					</c:when>
					<c:otherwise>
						<fmt:message key="global.no" />
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</div>
</div>
