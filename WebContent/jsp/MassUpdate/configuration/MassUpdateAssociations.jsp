<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="currentReportFormId" />

<itera:define id="massUpdateType" name="memBean" property="queryResult.queryForms[${currentReportFormId}].massUpdateType"/>

<c:choose>
	<c:when test="${massUpdateType.typeNamePresentationKey ==  'businessMapping.singular'}">
		<b><fmt:message key="businessMapping.singular" /></b>
	</c:when>
	<c:otherwise>
		<b><fmt:message key="massUpdates.associations" /></b>
	</c:otherwise>
</c:choose>
<br/>
<itera:define id="association_array" name="memBean" property="queryResult.queryForms[${currentReportFormId}].massUpdateType.massUpdateAssociations"/>
<c:forEach items="${association_array}" var="p">
	<c:set var="propString"><c:out value="${p.namePresentationKey}" /></c:set>
	<c:set var="idString"><c:out value="${p.name}" /></c:set>
	<div class="control-group">
		<div class="controls">
			<form:checkbox id="queryResult.queryForms${currentReportFormId}.queryUserInput.massUpdateData.selectedAssociationsList.checkbox_${idString}"
				path="queryResult.queryForms[${currentReportFormId}].queryUserInput.massUpdateData.selectedAssociationsList"
				cssClass="checkbox_${idString}" value="${idString}" />
		</div>
		<label class="control-label-right link" for="queryResult.queryForms${currentReportFormId}.queryUserInput.massUpdateData.selectedAssociationsList.checkbox_${idString}">
  			<fmt:message key="${propString}" />
  		</label>
	</div>
</c:forEach>