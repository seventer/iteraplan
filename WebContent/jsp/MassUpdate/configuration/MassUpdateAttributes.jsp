<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%--
		@UsedFor 	
		@UsedFrom	jsp\MassUpdate\configuration\MassUpdateConfig.jsp;
		@Note		
 --%>

<tiles:useAttribute name="currentReportFormId" />

<itera:define id="attributes" name="memBean" property="availableAttributesForMassUpdate[${currentReportFormId}]"/>
<c:set var="numberOfAttributes" value="${fn:length(attributes)}" />
<c:set var="attributesPerCol" value="5" />

<%-- if MassUpdating Business Mappings, the attribute selection dialogue is not displayed
	as mass-updating attributes of business mappings is not possible --%>
	
<c:set var="cols" value="2"/>
<c:set var="spanNumber" value="span6"/>
<c:if test="${numberOfAttributes > attributesPerCol}">
	<c:set var="cols" value="3"/>
	<c:set var="spanNumber" value="span4"/>
</c:if>
	
<b><fmt:message key="global.attributes" /></b>
<br/>
<itera:define id="attribute_array" name="memBean" property="queryResult.queryForms[${currentReportFormId}].availableUserDefinedAttributes"/>
<div class="row-fluid">
	<c:forEach items="${attribute_array}" var="p" varStatus="attributeStatus">
		<c:set var="attributeId" value="${attributeStatus.index}" />
		<%-- Only attributes for which the user has write permissions can be selected for mass update--%>
		<c:if test="${p.hasWritePermissions and not p.timeseries}">
			<c:set var="propString" value="${p.name}" />
			<c:set var="idString" value="${p.stringId}" />
			
			<div class="<c:out value="${spanNumber}"/>">
				<div class="control-group">
					<div class="controls">
						<form:checkbox id="queryResult.queryForms${currentReportFormId}.queryUserInput.massUpdateData.selectedAttributesList.checkbox_${idString}"
							path="queryResult.queryForms[${currentReportFormId}].queryUserInput.massUpdateData.selectedAttributesList"
							cssClass="checkbox_${idString}" value="${idString}" />
					</div>
					<label class="control-label-right link" for="queryResult.queryForms${currentReportFormId}.queryUserInput.massUpdateData.selectedAttributesList.checkbox_${idString}">
			  			<c:out value="${propString}"/>
			  		</label>
				</div>
			</div>
	
			<c:if test="${ attributeId%cols==0 }">
				</div>
				<div class="row-fluid">
			</c:if>
			
		</c:if>
	</c:forEach>
</div>