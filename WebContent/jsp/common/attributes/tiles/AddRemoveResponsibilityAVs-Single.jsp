<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<tiles:useAttribute name="atPartPath" />
<tiles:useAttribute name="extended_html_id" />

<itera:define id="atPart" name="memBean" property="${atPartPath}"/>

<form:select path="${atPartPath}.avIdToSet" id="${extended_html_id}_select" cssClass="combobox">
	<form:options items="${atPart.availableAVsForPresentation}" itemLabel="name" itemValue="id"/>
</form:select>
&nbsp;
<form:errors path="${atPartPath}.avIdToSet" cssClass="errorMsg" htmlEscape="false"/>