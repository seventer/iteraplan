<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%--
		@UsedFor 	Shows a query Form for selecting a date span
		@UsedFrom	jsp\commonReporting\DynamicForm.jsp
		@Note		
 --%>

<tiles:useAttribute name="currentReportFormId" />

<fmt:message key="${memBean.queryResult.queryForms[currentReportFormId].timeSpanDescription}"/>:
&nbsp;
<fmt:message key="global.from"/>
&nbsp;
<form:input class="small datepicker" type="text" path="queryResult.queryForms[${currentReportFormId}].queryUserInput.timespanQueryData.startDateAsString" id="startDateAsString_${currentReportFormId}" />
<a id="<c:out value="dateRemoveAnchor1_${currentReportFormId}"/>" class="link"
	title="<fmt:message key="global.date.remove"/>"
	onclick="$('#<c:out value="startDateAsString_${currentReportFormId}"/>').val('');" >
	<i class="icon-remove"></i>
</a>
&nbsp;
<fmt:message key="global.to"/>
&nbsp;
<form:input class="small datepicker" type="text" path="queryResult.queryForms[${currentReportFormId}].queryUserInput.timespanQueryData.endDateAsString" id="endDateAsString_${currentReportFormId}" />
&nbsp;
<a id="<c:out value="dateRemoveAnchor2_${currentReportFormId}"/>" class="link"
	title="<fmt:message key="global.date.remove"/>"
	onclick="$('#<c:out value="endDateAsString_${currentReportFormId}"/>').val('');" >
   	<i class="icon-remove"></i>
</a>