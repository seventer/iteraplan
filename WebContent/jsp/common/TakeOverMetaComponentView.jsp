<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="path_to_componentModel" />
<tiles:useAttribute name="master_checkbox_id" ignore="true" />

<itera:define id="message_key" name="memBean" property="${path_to_componentModel}.labelKey" />
<c:set var="boolean_field" value="${path_to_componentModel}.value" />
<itera:define id="component_mode" name="memBean" property="${path_to_componentModel}.componentMode" />
<itera:define id="html_id" name="memBean" property="${path_to_componentModel}.htmlId" />

<%-- start content --%>
<tr id="<c:out value="${html_id}" />">
	<td class="col-ico">
		<form:checkbox path="${boolean_field}" cssClass="checkUnCheckAll" id="${html_id}_checkbox" onclick="unCheckCheckBox('${master_checkbox_id}');" />
	</td>
	<td><fmt:message key="${message_key}" /></td>
</tr>