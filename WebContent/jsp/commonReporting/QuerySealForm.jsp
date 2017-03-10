<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%--
		@UsedFor 	Shows a query Form for selecting a IS seal state.
		@UsedFrom	jsp\commonReporting\DynamicForm.jsp
		@Note		
 --%>

<tiles:useAttribute name="currentReportFormId" />

<fmt:message key="seal" />:
&nbsp;
<c:forEach var="status" items="${memBean.queryResult.queryForms[currentReportFormId].queryUserInput.sealQueryData.statusMap}">
	<c:set var="statusString" value="${status.key}" />
	<c:set var="id_checkbox" value="checkbox_statusQuery_${currentReportFormId}_${statusString}" />
	<c:set var="id_field" value="field_statusQuery_${currentReportFormId}_${statusString}" />
	   
	<label class="checkbox inline">
		<form:checkbox id="${id_checkbox}" path="queryResult.queryForms[${currentReportFormId}].queryUserInput.sealQueryData.selectedStatus" 
			value="${statusString}" />
		<fmt:message key="${statusString.value}" />
	</label>
     
</c:forEach>