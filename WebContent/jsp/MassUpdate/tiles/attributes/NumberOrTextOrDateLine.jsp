<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="path_to_componentModel" />
<tiles:useAttribute name="type" />
<tiles:useAttribute name="line" />
<tiles:useAttribute name="isAttribute" ignore="true"/>

<c:if test="${empty isAttribute}" >
  <c:set var="isAttribute" value="false" />
</c:if>

<c:set var="html_id" value="${itera:replaceNoIdChars(path_to_componentModel)}" />

<itera:define id="multiline" name="memBean" property="${path_to_componentModel}.multiline" />
<div class="control-group">
	<div class="controls">
<c:choose>
	<c:when test="${multiline}">
		<form:textarea path="${path_to_componentModel}.massUpdateAttributeItem.newAttributeValue" cssClass="name" id="${html_id}_newAttributeValue_${line}_${type}" />
	</c:when>
	<c:otherwise>
		<c:if test="${type == 'userdefDate'}">
			<c:set var="cssStyle" value="small datepicker" />
		</c:if>
		<c:if test="${type == 'userdefNumber'}">
			<script type="text/javascript"> 
			 	$(document).ready(function() {
		    		// validateNumber
  					$("#${html_id}_newAttributeValue_${line}_${type}").validate({
  						<%-- TODO: checkRegEx --%>
  						expression: "if (VAL.match(/^[^#:]{1,255}$/) || VAL == '') return true; else return false;",
  						<%-- TODO: checkErrorMessage --%>
  						message: "<fmt:message key="errors.invalidValue"/>"
  					});
			  	});
			</script>
		</c:if>
		<form:input path="${path_to_componentModel}.massUpdateAttributeItem.newAttributeValue" cssClass="${cssStyle}" id="${html_id}_newAttributeValue_${line}_${type}" />
	</c:otherwise>
</c:choose>
	</div>
</div>
