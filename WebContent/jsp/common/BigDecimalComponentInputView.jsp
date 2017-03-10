<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="path_to_componentModel" />

<c:set var="currentLocale" value="${userContext.locale}" />

<itera:define id="message_key" name="memBean" property="${path_to_componentModel}.labelKey" />
<c:set var="text_field" value="${path_to_componentModel}.currentAsString" />
<itera:define id="component_mode" name="memBean" property="${path_to_componentModel}.componentMode" />
<itera:define id="html_id" name="memBean" property="${path_to_componentModel}.htmlId" />

<c:if test="${component_mode == 'READ'}">
  <c:set var="tdstyle" value="top margin" />
</c:if>

<div class="control-group">
	<label class="control-label" for="${html_id}_text">
    	<fmt:message key="${message_key}" />:
	</label>
	<div class="controls">
		<c:choose>
			<c:when test="${component_mode != 'READ'}">
				<script type="text/javascript">
					$(function(){
						// validateNumber
						$("#${html_id}_text").validate({
							<%-- TODO: checkRegEx --%>
							expression: "if (VAL.match(/^[^#:]{1,255}$/) || VAL == '') return true; else return false;",
							<%-- TODO: checkErrorMessage --%>
							message: "<fmt:message key="errors.invalidValue"/>"
						});
					});
				</script>
				<form:input path="${text_field}" cssClass="input-large" id="${html_id}_text"/>
      			<form:errors path="${text_field}" cssClass="errorMsg" htmlEscape="false"/>
    		</c:when>
    		<c:otherwise>
      			<c:set var="numberString">
        			<itera:write name="memBean" property="${text_field}" escapeXml="false" />
      			</c:set>
       
            	<!-- <c:out value="${currentLocale}" /> -->
            	<!--  en/US -->
            
      			<fmt:parseNumber value="${numberString}" var="parsedNumber" parseLocale="${currentLocale}" />
      			<fmt:formatNumber value="${parsedNumber}" minFractionDigits="2"/>
    		</c:otherwise>
  		</c:choose>
	</div>
</div>