<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="path_to_componentModel" />

<c:set var="text_field" value="${path_to_componentModel}.current" />
<itera:define id="message_key" name="memBean"
	property="${path_to_componentModel}.labelKey" />
<itera:define id="component_mode" name="memBean"
	property="${path_to_componentModel}.componentMode" />
<itera:define id="html_id" name="memBean"
	property="${path_to_componentModel}.htmlId" />

<c:if test="${component_mode == 'READ'}">
	<c:set var="tdstyle" value="top margin" />
</c:if>

<c:set var="isMandatory">
	<itera:write name="memBean"	property="${path_to_componentModel}.mandatory" escapeXml="false" />
</c:set>

<div class="control-group">
	<label class="control-label" for="${html_id}_text">
		<c:if test="${isMandatory && component_mode != 'READ'}">
			* 
		</c:if>
		<fmt:message key="${message_key}" />:
	</label>
	<div class="controls">
		<c:choose>
			<c:when test="${component_mode != 'READ'}">
				<script type="text/javascript">
					$(document).ready(function() {
						
						if("${html_id}_text" != "email_text" && "${html_id}_text" != "unit_text") {
							// validateNumber
							$("#${html_id}_text").validate({
								<%-- TODO: checkRegEx --%>
								expression: "if (VAL.match(/^[^#:]{1,255}$/) || VAL == '') return true; else return false;",
								<%-- TODO: checkErrorMessage --%>
								message: "<fmt:message key="errors.invalidValue"/>"
							});
							$('[id="${html_id}_text"]').attr("autocomplete","on");
							
							if("${html_id}_text" != "firstName_text" && "${html_id}_text" != "lastName_text" ) {
								$('[id="${html_id}_text"]').focus();
							}
						}
						if("${html_id}_text" == "email_text"){
							// validateEmail
							$("#${html_id}_text").validate({
								<%-- TODO: checkRegEx --%>
								<%-- old one used: [ ]*[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}[ ] --%>
								expression: "if (VAL.match(/^[^\\W][a-zA-Z0-9\\_\\-\\.]+([a-zA-Z0-9\\_\\-\\.]+)*\\@[a-zA-Z0-9\\_\\-]+(\\.[a-zA-Z0-9\\_\\-]+)*\\.[a-zA-Z]{2,4}$/) || VAL == '') return true; else return false;",
								<%-- TODO: checkErrorMessage --%>
								message: "<fmt:message key="errors.invalidValue"/>"
							});
						}
			        });
				</script>
				<form:input path="${text_field}" cssClass="input-large" id="${html_id}_text" />
				<form:errors path="${text_field}" cssClass="errorMsg" htmlEscape="false"/>
			</c:when>
			<c:otherwise>
				<itera:write name="memBean" property="${text_field}" escapeXml="true" />
			</c:otherwise>
		</c:choose>
	</div>
</div>