<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%--
		@UsedFor 	
		@UsedFrom	jsp\commonReporting\GeneralQueryForm.jsp
		@Note		
 --%>


<%-- If this bean is set to any value, the header for the query table will not be displayed.
Currently used for the object related permission page. --%>
<tiles:useAttribute name="hideHeader" ignore="true" />

<form:input path="reportExtensionToRemove" cssStyle="display: none;" />
<form:input path="formModification.affectedQueryFormId" cssStyle="display: none;" />
<form:input path="formModification.affectedSecondLevelQueryFormId" cssStyle="display: none;" />
<form:input path="formModification.firstLevelIdToExpand" cssStyle="display: none;" />
<form:input path="formModification.firstLevelIdToShrink" cssStyle="display: none;" />
<form:input path="formModification.secondLevelIdToShrink" cssStyle="display: none;" />

<div id="PropertiesFormContainer" class="row-fluid module">
	<c:if test="${empty hideHeader}">
		<div class="module-heading">
		    <fmt:message key="reports.resultAttributes" /> 
		    <fmt:message key="${memBean.reportResultType.typeNamePluralPresentationKey}" />
		    <c:if test="${massUpdateMode == true}">
		    	<c:out value=" "/>
		        <fmt:message key="global.for" />
		        <c:out value=" "/>
		        <fmt:message key="global.mass_updates" />
		    </c:if>
		</div>
	</c:if>
	<div class="row-fluid">
		<div class="module-body">
			<div class="row-fluid">
				<tiles:insertTemplate template="/jsp/commonReporting/DynamicForm.jsp" flush="false">
		            <tiles:putAttribute name="currentReportFormId" value="0" />
		            <tiles:putAttribute name="dynamicQueryFormDataField" value="queryResult.queryForms[0]" />
		            <tiles:putAttribute name="hideNoAssociationFormPart" value="true" />
	           </tiles:insertTemplate>
			</div>
		   	<c:forEach items="${memBean.queryResult.queryForms}" var="extForm" begin="1" varStatus="qfStatus">
			   	<div class="row-fluid">
		   			<tiles:insertTemplate template="/jsp/commonReporting/ReportExtension.jsp" flush="false">
		            	<tiles:putAttribute name="extensionDescriptionParam" type="string" value="${extForm.extension.nameKeyForPresentation}" />
		                <tiles:putAttribute name="currentReportFormIdParam" value="${qfStatus.index}" />
		            </tiles:insertTemplate>
			   	</div>
			</c:forEach>
		</div>
	</div>
</div>

<%-- Drop down list for available extensions --%>
<div id="SelectedReportExtensionModule" class="row-fluid module">
	<div class="row-fluid">
		<div class="module-body">
			<div class="row-fluid">
				<form:select path="selectedReportExtension" onchange="flowAction('addReportExtension');" cssStyle="width: auto;">
			  		<option selected="selected" value="">
				    	<fmt:message key="reports.extension.select" />
				    </option>
			      	<c:forEach items="${memBean.queryResult.availableReportExtensionsSorted}" var="avExtForm">
			      		<option value="${avExtForm.name}">
			      			<fmt:message key="${avExtForm.nameKeyForPresentation}" />
			      		</option>
			      	</c:forEach>
			    </form:select>
			</div>
		</div>
	</div>
</div>

<c:if test="${memBean.queryResult.timeseriesQueryAvailable}">
<div class="row-fluid module">
	<div class="row-fluid">
		<div class="module-heading">
				<fmt:message key="reports.timeseries.heading" />
		</div>
		<div class="module-body">
			<div class="row-fluid">
				<div class="row-fluid">
					<c:forEach var="quantor" items="${memBean.queryResult.timeseriesQuery.quantors}">
						<label class="radio inline"><form:radiobutton path="queryResult.timeseriesQuery.quantor" name="timeseries_quantor" value="${quantor}" /><fmt:message key="${quantor.name}" /></label>
					</c:forEach>
				<fmt:message key="reports.timeseries.value_s"/>&nbsp;<fmt:message key="global.between"/>
				&nbsp;
				<form:input class="small datepicker" type="text" path="queryResult.timeseriesQuery.timespan.startDateAsString" id="startDateAsString_ts" />
				<a id="<c:out value="dateRemoveAnchor1_ts"/>" class="link"
					title="<fmt:message key="global.date.remove"/>"
					onclick="$('#<c:out value="startDateAsString_ts"/>').val('');" >
					<i class="icon-remove"></i>
				</a>
				&nbsp;
				<fmt:message key="global.and"/>
				&nbsp;
				<form:input class="small datepicker" type="text" path="queryResult.timeseriesQuery.timespan.endDateAsString" id="endDateAsString_ts" />
				&nbsp;
				<a id="<c:out value="dateRemoveAnchor2_ts"/>" class="link"
					title="<fmt:message key="global.date.remove"/>"
					onclick="$('#<c:out value="endDateAsString_ts"/>').val('');" >
				   	<i class="icon-remove"></i>
				</a> <fmt:message key="reports.timeseries.must_meet_criterion"/>
				</div>
				<div class="row-fluid"><hr />
				</div>
				<table>
					<tr>
						<tiles:insertTemplate template="/jsp/commonReporting/QPartForm.jsp" flush="true">
							<tiles:putAttribute name="pathToQPart" value="queryResult.timeseriesQuery.part" />
							<tiles:putAttribute name="idSuffix" value="ts" />
							<tiles:putAttribute name="availableAttributes" value="${memBean.queryResult.timeseriesQuery.availableAttributes}" />
							<tiles:putAttribute name="availableAttributeValuesResolver" value="queryResult.timeseriesQuery" />
						</tiles:insertTemplate>
					</tr>
				</table>
			</div>
		</div>
	</div>
</div>
</c:if>

<tiles:insertTemplate template="/jsp/commonReporting/AdvancedFunctions.jsp" />