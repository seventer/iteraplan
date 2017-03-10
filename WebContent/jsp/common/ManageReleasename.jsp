<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<tiles:useAttribute name="overviewMode" />
<tiles:useAttribute name="element_html_id" ignore="true" />
<tiles:useAttribute name="release_html_id" ignore="true" />
<tiles:useAttribute name="doCopyAll" ignore="true" />
<tiles:useAttribute name="icon_suffix" ignore="true" />

<c:if test="${componentMode != 'READ'}">
	<c:set var="disabled" value="true" />
</c:if>

<input type="hidden" name="setFocusOnTextfield" id="setFocusOnTextfield" value="${setFocusOnTextfield}" />

<c:choose>
	<c:when test="${(componentMode == 'EDIT' || componentMode == 'CREATE') && overviewMode}">
		<script type="text/javascript">
			$(document).ready(function() {
				// validateName
				$("#${element_html_id}").validate({
					<%-- TODO: checkRegEx --%>
					expression: "if (VAL.match(/^[^#:]{0,255}$/)) return true; else return false;",
					<%-- TODO: checkErrorMessage --%>
					message: "<fmt:message key="errors.invalidValue"/>"
				});
				$('[id="${element_html_id}"]').attr("autocomplete","on");
				$('[id="${release_html_id}"]').attr("autocomplete","on");
				
				if($('[id="${element_html_id}"]').is(':disabled') != true && (getElementByIdSafe('setFocusOnTextfield').value == "true")){
					$('[id="${element_html_id}"]').focus();
				}
			});
		</script>
		<div class="control-group">
			<c:if test="${not empty icon_suffix}">
				<label class="control-label" for="${element_html_id}" style="width: 40px;">
	    			<span class="headerIcon icon_${icon_suffix }" style="margin-top: 5px;"></span>
	    		</label>
	    	</c:if>
	    	<div class="controls">
				<span rel="tooltip" title="<fmt:message key="global.release_name" />">
					<form:input path="componentModel.releaseNameModel.elementName.current" disabled="${doCopyAll}" id="${element_html_id}" />
				</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="${release_html_id}" style="width:60px;">
				<fmt:message key="global.release" />:
			</label>
			<div class="controls">
				<form:input path="componentModel.releaseNameModel.releaseName.current" id="${release_html_id}" />
			</div>
		</div>
		<form:errors path="componentModel.releaseNameModel.releaseName.current" cssClass="errorMsg" htmlEscape="false"/>
	</c:when>
	<c:otherwise>
		<c:if test="${not empty icon_suffix}">
    		<span class="headerIcon icon_${icon_suffix }" style="margin-top: 10px;"></span>
    	</c:if>
		<h1><c:out value="${memBean.componentModel.releaseNameModel.name}" />&nbsp;</h1>
	</c:otherwise>
</c:choose>