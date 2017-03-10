<%@page import="de.iteratec.iteraplan.common.UserContext"%>
<%@page import="de.iteratec.iteraplan.common.util.BigDecimalConverter"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<!-- This is basically a copy of "/jsp/common/attributes/tiles/AddRemoveNumberAV.jsp" without the use of form:input and form:error tags. -->
<!-- This is necessary because in the dynamically reloaded modal dialog content this jsp here is used, those tags don't work due to the -->
<!-- rendering separate from their enclosing form:form tag. -->

<tiles:useAttribute name="valueModelPath"/>

<itera:define id="valueModel" name="memBean" property="${valueModelPath}"/>

<script type="text/javascript">
	$(document).ready(function() {
		// validateNumber
			separator = "<%out.print(BigDecimalConverter.getLocalizedSeparator(UserContext.getCurrentLocale()));%>";
			if (separator == ".") {
				separator = "\\"+separator;
			}
		regexStr = "/^([+-]?((\\d+)|(\\d+" + separator + "\\d+)))$/.test(VAL)";
					
		$("#${valueModel.htmlId}").validate({

			<c:choose>
			<c:when test="${(not empty valueModel.attributeType.maxValue) && (not empty valueModel.attributeType.minValue)}" >
				expression: "if (("+regexStr+" && parseFloat(VAL.replace(',', '.')) >= ${valueModel.attributeType.minValue} && parseFloat(VAL.replace(',', '.')) <= ${valueModel.attributeType.maxValue} ) || VAL == '') return true; else return false;",
			</c:when>
			<c:when test="${(empty valueModel.attributeType.maxValue) && (not empty valueModel.attributeType.minValue)}" >
				expression: "if (("+regexStr+" && parseFloat(VAL.replace(',', '.')) >= ${valueModel.attributeType.minValue}) || VAL == '') return true; else return false;",
			</c:when>
			<c:when test="${(not empty valueModel.attributeType.maxValue) && (empty valueModel.attributeType.minValue)}" >
				expression: "if (("+regexStr+" && parseFloat(VAL.replace(',', '.')) <= ${valueModel.attributeType.maxValue} ) || VAL == '') return true; else return false;",
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
		<input name="${valueModelPath}.attributeValueAsString" id="${valueModel.htmlId}" cssClass="input-large"
					value="<c:out value="${valueModel.attributeValueAsString}" />" />
		&nbsp;<itera:write name="valueModel" property="attributeType.unit" escapeXml="true" />
	</div>
</div>
