<%@page import="de.iteratec.iteraplan.common.UserContext"%>
<%@page import="de.iteratec.iteraplan.common.util.BigDecimalConverter"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<tiles:useAttribute name="atPartPath"/>
<tiles:useAttribute name="extended_html_id" />

<itera:define id="atPart" name="memBean" property="${atPartPath}"/>

<script type="text/javascript">
	$(document).ready(function() {
		// validateNumber
			separator = "<%out.print(BigDecimalConverter.getLocalizedSeparator(UserContext.getCurrentLocale()));%>";
			if (separator == ".") {
				separator = "\\"+separator;
			}
		regexStr = "/^([+-]?((\\d+)|(\\d+" + separator + "\\d+)))$/.test(VAL)";
					
		$("#${extended_html_id}_number").validate({

			<c:choose>
			<c:when test="${(not empty atPart.attributeType.maxValue) && (not empty atPart.attributeType.minValue)}" >
				expression: "if (("+regexStr+" && parseFloat(VAL.replace(',', '.')) >= ${atPart.attributeType.minValue} && parseFloat(VAL.replace(',', '.')) <= ${atPart.attributeType.maxValue} ) || VAL == '') return true; else return false;",
			</c:when>
			<c:when test="${(empty atPart.attributeType.maxValue) && (not empty atPart.attributeType.minValue)}" >
				expression: "if (("+regexStr+" && parseFloat(VAL.replace(',', '.')) >= ${atPart.attributeType.minValue}) || VAL == '') return true; else return false;",
			</c:when>
			<c:when test="${(not empty atPart.attributeType.maxValue) && (empty atPart.attributeType.minValue)}" >
				expression: "if (("+regexStr+" && parseFloat(VAL.replace(',', '.')) <= ${atPart.attributeType.maxValue} ) || VAL == '') return true; else return false;",
			</c:when>
			<c:otherwise>
				expression: "if ("+regexStr+" || VAL == '') return true; else return false;",
			</c:otherwise>
			</c:choose>
			<%-- TODO: checkErrorMessage --%>
			message: "<fmt:message key="errors.invalidValue"/>"
		});
	});
	
</script>

<div class="control-group">
	<div class="controls">
		<form:input path="${atPartPath}.attributeValueAsString" id="${extended_html_id}_number" cssClass="input-large" />&nbsp;<itera:write name="atPart" property="attributeType.unit" escapeXml="true" />
	</div>
</div>
&nbsp;
<form:errors path="${atPartPath}.attributeValueAsString" cssClass="errorMsg" htmlEscape="false"/>