<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="path_to_componentModel"/>
<tiles:useAttribute name="name_field_path" ignore="true"/>
<tiles:useAttribute name="icon_suffix" ignore="true"/>
<tiles:useAttribute name="overviewMode" ignore="true" />
<tiles:useAttribute name="html_id" ignore="true" />
<tiles:useAttribute name="componentMode" ignore="true" />
<tiles:useAttribute name="nameFieldIsMandatory" ignore="true" />
<tiles:useAttribute name="validate" ignore="true" />
<tiles:useAttribute name="prefixKey" ignore="true" />

<%-- When this attribute is true it means that the virtual element is selected. this is used 
to disable certain fields in case the component is in the EDIT or CREATE mode. --%>
<tiles:useAttribute name="virtualElementSelected" ignore="true" />

<c:if test="${empty html_id}">
  <itera:define id="html_id" name="memBean" property="${path_to_componentModel}.htmlId" />
</c:if>

<c:if test="${name_field_label == null}">
  <c:set var="name_field_label" value="global.name"/>
</c:if>

<c:if test="${empty name_field_path}">
  <c:set var="name_field_path" value="${path_to_componentModel}.name"/>
</c:if>

<c:if test="${empty virtualElementSelected}">
  <c:set var="virtualElementSelected" value="${path_to_componentModel}.virtualElementSelected" />
</c:if>

<c:if test="${empty defaultAttributeGroupSelected && path_to_componentModel ne ''}">
  <itera:define id="defaultAttributeGroupSelected" name="memBean" property="${path_to_componentModel}.defaultAttributeGroupSelected" />
</c:if>

<c:if test="${componentMode == 'READ'}">
  <c:set var="tdstyle" value="top margin" />
</c:if>

<c:if test="${componentMode == 'EDIT' || componentMode == 'CREATE'}">
  <c:set var="disabled" value="true" />
</c:if>

<c:if test="${overviewMode == null}">
  <c:set var="overviewMode" value="true" />
</c:if>


<c:choose>
  <%-- for all other building-blocks, name field is always mandatory --%>
  <c:when test="${empty nameFieldIsMandatory || nameFieldIsMandatory == true}">
 	<c:set var="asterisk" value="* " />
  </c:when>
  <%-- for "interfaces"-building-block, its name field is not supposed to be mandatory --%>
  <c:when test="${not nameFieldIsMandatory}">
	<c:set var="asterisk" value="" />
  </c:when>
</c:choose>

<input type="hidden" name="setFocusOnTextfield" id="setFocusOnTextfield" value="${setFocusOnTextfield}" />

<c:choose>
  <c:when test="${(componentMode == 'EDIT' || componentMode == 'CREATE') && overviewMode }">
    <script type="text/javascript">
    
    	$(document).ready(function() {
    	// validateBBName
			$("#nameField").validate({
				<%-- TODO: checkRegEx --%>
				expression: "if (VAL.match(/^[^#:]{0,255}$/)) return true; else return false;",
				<%-- TODO: checkErrorMessage --%>
				message: "<fmt:message key="errors.invalidValue"/>"
			});
		
			if(getElementByIdSafe('setFocusOnTextfield').value == "true"){
				$('[id="nameField"]').focus();
			}
        	
			$('[id="nameField"]').attr("autocomplete","on");
        });
    </script>
    
    <c:if test="${not empty icon_suffix}">
    <span class="headerIcon icon_${icon_suffix }" style="margin-top: 5px;"></span>
    </c:if>
   	<c:if test="${not empty prefixKey}"><fmt:message key="${prefixKey}"/>: </c:if>
	<div class="control-group">
		<div class="controls">
			<span rel="tooltip" title="<fmt:message key="${name_field_label}" />">
				<form:input id="nameField" path="${name_field_path}" cssClass="${input_style}" disabled="${virtualElementSelected || defaultAttributeGroupSelected}" />
 			</span>
 		</div>
	</div>
    <form:errors path="${name_field_path}" cssClass="errorMsg" htmlEscape="false"/>
    
  </c:when>
  <c:otherwise>
  	<c:if test="${not empty icon_suffix}">
    	<span class="headerIcon icon_${icon_suffix }" style="margin-top: 10px;"></span>
    </c:if>
    <h1><c:if test="${not empty prefixKey}"><fmt:message key="${prefixKey}"/>: </c:if><itera:write name="memBean" property="${name_field_path}" escapeXml="true" />&nbsp;</h1>
  </c:otherwise>
</c:choose>
