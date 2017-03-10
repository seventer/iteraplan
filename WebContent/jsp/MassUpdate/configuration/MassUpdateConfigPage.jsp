<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%--
		@UsedFor 	
		@UsedFrom	jsp\commonReporting\ManageReportOrMassupdate.jsp;
		@Note		
 --%>
<c:set var="currentReportFormIdValue" value="0" />
<itera:define id="properties" name="memBean" property="queryResult.queryForms[${currentReportFormIdValue}].massUpdateType.properties"/>
<itera:define id="attributes" name="memBean" property="availableAttributesForMassUpdate[${currentReportFormIdValue}]"/>
<itera:define id="massUpdateType" name="memBean" property="massUpdateType"/>
<c:if test="${massUpdateType.typeNamePresentationKey ==  'businessMapping.singular'}">
	<c:set var="businessMapping" value="true"/>
</c:if>
<itera:define id="associations" name="memBean" property="queryResult.queryForms[${currentReportFormIdValue}].massUpdateType.massUpdateAssociations"/>

<div id="MassUpdateConfigModul" class="row-fluid module">
	<div class="module-heading">
		<fmt:message key="massUpdates.updateAttributesAssociations" />
	</div>
	<div class="row-fluid">
		<div class="module-body">
			<div class="row-fluid">
				<c:if test="${not empty properties}">
					<div class="span4">
			            <tiles:insertTemplate template="/jsp/MassUpdate/configuration/MassUpdateProperties.jsp" flush="true">
			            	<tiles:putAttribute name="currentReportFormId" value="${currentReportFormIdValue}" />
			            </tiles:insertTemplate>
					</div>
				</c:if>
				<c:if test="${not empty associations}">
					<div class="span4">
			            <tiles:insertTemplate template="/jsp/MassUpdate/configuration/MassUpdateAssociations.jsp" flush="true">
			            	<tiles:putAttribute name="currentReportFormId" value="${currentReportFormIdValue}" />
			            </tiles:insertTemplate>
					</div>
				</c:if>
				<c:if test="${(not empty attributes) && !businessMapping}">
					<div class="span4">
			            <tiles:insertTemplate template="/jsp/MassUpdate/configuration/MassUpdateAttributes.jsp" flush="true">
			            	<tiles:putAttribute name="currentReportFormId" value="${currentReportFormIdValue}" />
			            </tiles:insertTemplate>
		        	</div>
		        </c:if>
			</div>
		</div>
	</div>
</div>