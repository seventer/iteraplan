<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="currentReportFormId" />
 
<itera:define id="massUpdateType" name="memBean" property="massUpdateType"/>

<c:forEach items="${memBean.lines}" var="line" varStatus="muiCountStatus">
	<c:set var="muiCount" value="${muiCountStatus.index}" />

	<c:set var="numberOfBusinessMappings" value="${fn:length(line.componentModel.businessMappingComponentModels)}" />

	<c:if test="${numberOfBusinessMappings > 0}">
		<itera:define id="result" name="line" property="massUpdateResult"/>
		<c:if test="${result != null}">
			<c:if test="${result.wasExecuted}">
			<tr>
				<td colspan="<c:out value="${(memBean.totalNumberOfColumns * 2) + 2}"/>">
					<c:choose>
						<c:when test="${result.exception != null}">
							<div style="background-color: red; color: white">
							<itera:define id="exception" name="result" property="exception.localizedMessage"/>
							<c:out value="${exception}" escapeXml="false"/>
							</div>
						</c:when>
						<c:otherwise>
						<div class="alert alert-success">
							<a class="close" data-dismiss="alert">×</a>
	            			<fmt:message key="massUpdates.updateWasSucessful" />
	            		</div>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			</c:if>
		</c:if>
	</c:if>
 
	<c:set var="numberOfBusinessMappings" value="${fn:length(line.componentModel.businessMappingComponentModels)}" />
	
	<c:forEach items="${line.componentModel.businessMappingComponentModels}" var="componentModel" varStatus="componentModelModelId">
		<c:set var="componentModelModelId" value="${componentModelModelId.index}" />

		<tr>
			<c:if test="${componentModelModelId == 0}">
			
				<%-- Add the name of the BuildingBlock in the first column --%>
				<td class="borderBottom borderRight" rowspan="<c:out value="${numberOfBusinessMappings}"/>">
					<form:checkbox path="lines[${muiCount}].selectedForMassUpdate" id="lines[${muiCount}].selectedForMassUpdate" onclick="unCheckCheckBox('checkAllBox');"/>
				</td>
				
				<td class="borderBottom borderRight" nowrap="nowrap" rowspan="<c:out value="${numberOfBusinessMappings}"/>">
					<c:out value="${line.buildingBlockToUpdate.identityString}" />
				</td>
			</c:if>
			
			<%-- When updating business mappings, the part of the business mapping that is not part of the update is displayed "read-only" --%>
			<c:set var="numberNotSelected" value="${fn:length(line.componentModel.componentModelPathToNotSelectedElements)}" />
			<td>
				<c:forEach items="${line.componentModel.componentModelPathToNotSelectedElements}" var="path" varStatus="pathIdStatus">
					<c:set var="pathId" value="${pathIdStatus.index}" />
					<itera:define id="selectedIdentityString" name="componentModel" property="selected${path}.identityString"/>
					<c:out value="${selectedIdentityString}" />
					<c:if test="${numberNotSelected > pathId+1}">
						#
					</c:if>
				</c:forEach>
			</td>
			
			<c:set var="pathToComponentModel" value="lines[${muiCount}].componentModel.businessMappingComponentModels[${componentModelModelId}]"/>
			
			<c:forEach items="${line.componentModel.componentModelPathToSelectedElements}" var="path" varStatus="pathIdStatus">
			<c:set var="pathId" value="${pathIdStatus.index}" />
				<td valign="top">
					<itera:define id="availableItems" name="memBean" property="${pathToComponentModel}.available${path}" />
					<form:select path="${pathToComponentModel}.selected${path}Id" items="${availableItems}" itemLabel="identityString"
						itemValue="id" cssClass="nameforhierarchy"	id="${pathToComponentModel}_${path}_select" />
				</td>
			</c:forEach>
			
			<%-- Add the attributes selected for update - currently not used --%>
		</tr>
	</c:forEach>
</c:forEach>
