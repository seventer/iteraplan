<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%--
		@UsedFor 	
		@UsedFrom	jsp\MassUpdate\configuration\MassUpdateConfig.jsp;
		@Note		
 --%>

<tiles:useAttribute name="currentReportFormId" />

<itera:define id="massUpdateType" name="memBean" property="queryResult.queryForms[${currentReportFormId}].massUpdateType"/>

<b><fmt:message key="massUpdates.properties" /></b>
<br/>
<itera:define id="properties_array" name="memBean" property="queryResult.queryForms[${currentReportFormId}].massUpdateType.properties"/>
<c:forEach items="${properties_array}" var="p">
	<c:set var="propString"><c:out value="${p.namePresentationKey}" /></c:set>
	<c:set var="idString"><c:out value="${p.nameAsID}" /></c:set>
	<c:set var="idStringforModel" value=" '${p.nameAsID}' " />
	<div class="control-group">
		<div class="controls">
			<form:checkbox id="queryResult.queryForms${currentReportFormId}.queryUserInput.massUpdateData.selectedPropertiesList.checkbox_${idString}"
				path="queryResult.queryForms[${currentReportFormId}].queryUserInput.massUpdateData.selectedPropertiesList"
				cssClass="checkbox_${idString}" value="${idString}" />
		</div>
		<label class="control-label-right link" for="queryResult.queryForms${currentReportFormId}.queryUserInput.massUpdateData.selectedPropertiesList.checkbox_${idString}">
  			<fmt:message key="${propString}" />
  		</label>
	</div>
</c:forEach>