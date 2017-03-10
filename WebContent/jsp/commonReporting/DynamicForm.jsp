<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%> 
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%--
		@UsedFor 	
		@UsedFrom	jsp\commonReporting\StandardQueryForm.jsp;
		@Note		
 --%>

<%-- currentReportFormId identifies the "first level" form that should be displayed, if secondLevelReportFormId is not set. --%>
<tiles:useAttribute name="currentReportFormId" />

<%-- if both currentReportFormId and secondLevelReportFormId are set, it is assumed that a "second level" form is the base of this JSP. This attribute should not be set for "first level" forms. --%>
<tiles:useAttribute name="secondLevelReportFormId" ignore="true" />

<%-- string that contains the path to the respective first or second level form field within the memBean. --%>
<tiles:useAttribute name="dynamicQueryFormDataField" />

<%-- if set to true, the "no association" part of the form is invisible. --%>
<tiles:useAttribute name="hideNoAssociationFormPart" ignore="true" />

<tiles:useAttribute name="noAssociationKey" ignore="true" />

<%-- ######################### --%>

<itera:define id="ANDcountHelper" name="memBean" property="${dynamicQueryFormDataField}.queryUserInput.queryFirstLevels" />
<c:set var="ANDcount" value="${fn:length(ANDcountHelper)}" />

<c:set var="criticalExtension" >
  <itera:write name="memBean" property="${dynamicQueryFormDataField}.criticalExtension" escapeXml="false" />
</c:set>

<c:set var="statusQueryData">
  <itera:write name="memBean" property="${dynamicQueryFormDataField}.queryUserInput.statusQueryData" escapeXml="false" />
</c:set>

<c:set var="sealQueryData">
  <itera:write name="memBean" property="${dynamicQueryFormDataField}.queryUserInput.sealQueryData" escapeXml="false" />
</c:set>

<c:set var="timespanQueryData">
  <itera:write name="memBean" property="${dynamicQueryFormDataField}.queryUserInput.timespanQueryData" escapeXml="false" />
</c:set>

<c:set var="affectedQueryFormIdJavaScript" value="setHiddenField('formModification.affectedQueryFormId','${currentReportFormId}');" />

<c:set var="affectedSecondLevelQueryFormIdJavaScript" value="" />
<c:choose>
  <c:when test="${secondLevelReportFormId != null}">
    <c:set var="affectedSecondLevelQueryFormIdJavaScript"
      value="setHiddenField('formModification.affectedSecondLevelQueryFormId','${secondLevelReportFormId}');" />
  </c:when>
  <c:otherwise>
    <c:set var="secondLevelReportFormId" value="-1" />
  </c:otherwise>
</c:choose>

<c:if test="${hideNoAssociationFormPart == null}">
  <c:set var="hideNoAssociationFormPart" value="false" />
</c:if>

<c:if test="${noAssociationKey == null}">
  <c:set var="noAssociationKey" value="reports.noAssignements" />
</c:if>

<c:set var="noAssignmentsSelected">
  <itera:write name="memBean" property="${dynamicQueryFormDataField}.queryUserInput.noAssignements" escapeXml="false" />
</c:set>

<c:set var="hideOrShowInput" value="visible" />
<c:if test="${not hideNoAssociationFormPart && noAssignmentsSelected}">
  <c:set var="hideOrShowInput" value="hidden" />
</c:if>

<c:if test="${not hideNoAssociationFormPart}">
	<div class="control-group">
		<div class="controls">
			<form:checkbox id="reportExtension_${currentReportFormId}_${secondLevelReportFormId}.queryUserInput.noAssignements"
				path="${dynamicQueryFormDataField}.queryUserInput.noAssignements"
				onclick="toggleDivLayer('reportExtension_${currentReportFormId}_${secondLevelReportFormId}');" />
		</div>
		<label class="control-label-right" for="reportExtension_${currentReportFormId}_${secondLevelReportFormId}.queryUserInput.noAssignements">
			<fmt:message key="${noAssociationKey}" />
		</label>
	</div>
</c:if>

<c:set var="annotatedWithAttributesOrProperties">
  <itera:write name="memBean" property="${dynamicQueryFormDataField}.type.annotatedWithAttributesOrProperties" escapeXml="false" />
</c:set>

<div class="<c:out value="${hideOrShowInput}"/>" id="reportExtension_<c:out value="${currentReportFormId}"/>_<c:out value="${secondLevelReportFormId}"/>">
	<c:if test="${annotatedWithAttributesOrProperties}" >
		<div class="row-fluid">
			<c:if test="${!(empty statusQueryData)}">
				<div class="row-fluid">
					<tiles:insertTemplate template="/jsp/commonReporting/QueryStatusForm.jsp" flush="true">
		        		<tiles:putAttribute name="currentReportFormId" value="${currentReportFormId}" />
		      	  	</tiles:insertTemplate>
				</div>
			</c:if>
			<c:if test="${!(empty sealQueryData)}">
				<div class="row-fluid">
					<tiles:insertTemplate template="/jsp/commonReporting/QuerySealForm.jsp" flush="true">
						<tiles:putAttribute name="currentReportFormId" value="${currentReportFormId}" />
					</tiles:insertTemplate>
				</div>
			</c:if>
			<c:if test="${!(empty timespanQueryData)}">
				<div class="row-fluid">
					<tiles:insertTemplate template="/jsp/commonReporting/QueryTimespanForm.jsp" flush="true">
						<tiles:putAttribute name="currentReportFormId" value="${currentReportFormId}" />
					</tiles:insertTemplate>
				</div>
			</c:if>
			<c:if test="${!(empty statusQueryData) || !(empty timespanQueryData) || !(empty sealQueryData)}">
				<div class="row-fluid">
					<hr />
				</div>
			</c:if>
			<div class="row-fluid">
				<%@ include file="/jsp/commonReporting/QueryAttributeForm.jsp" %>
			</div>
		</div>
	</c:if>
</div>