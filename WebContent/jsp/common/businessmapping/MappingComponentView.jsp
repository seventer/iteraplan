<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<tiles:useAttribute name="path_to_componentModel" />
<tiles:useAttribute name="force_component_mode" ignore="true"/>

<itera:define id="model" name="memBean" property="${path_to_componentModel}" />
<itera:define id="component_mode" name="memBean" property="${path_to_componentModel}.componentMode" />
<itera:define id="cluster_parts" name="memBean" property="${path_to_componentModel}.clusterParts" />
<itera:define id="html_id" name="memBean" property="${path_to_componentModel}.htmlId" />
<itera:define id="table_header_key" name="memBean" property="${path_to_componentModel}.tableHeaderKey" />

<c:if test="${component_mode == 'READ' && empty cluster_parts}">
  <c:set var="emptyStyle" value="empty" />
</c:if>

<c:set var="businessMapping" value="businessMapping.singular" />
<c:set var="updatePermission">
	<itera:write name="userContext"
		property="perms.userHasBbTypeUpdatePermission(${businessMapping})" escapeXml="false" />
</c:set>
<c:set var="createPermission">
	<itera:write name="userContext"
		property="perms.userHasBbTypeCreatePermission(${businessMapping})" escapeXml="false" />
</c:set>
<c:set var="deletePermission">
	<itera:write name="userContext"
		property="perms.userHasBbTypeDeletePermission(${businessMapping})" escapeXml="false" />
</c:set>

<%-- Start MappingComponentView --%>
<input type="hidden" name="<c:out value="${path_to_componentModel}" />.selectedClusterPosition"/>
<input type="hidden" name="<c:out value="${path_to_componentModel}" />.selectedMappingPosition"/>
<input type="hidden" name="<c:out value="${path_to_componentModel}" />.action"/>

<div id="<c:out value="${html_id}" />_Modul" class="row-fluid module">
	<div class="module-heading">
		<fmt:message key="${table_header_key}" />
	</div>
	<div class="row-fluid">
		<div class="module-body">
			<div class="row-fluid">
				<c:if test="${(component_mode == 'EDIT' || component_mode == 'CREATE') && updatePermission == false}">
					<div class="helpText"><fmt:message key="messages.noBmUpdatePermission" /></div>
				</c:if>
				<c:if test="${(component_mode == 'EDIT' || component_mode == 'CREATE') && createPermission == false}">
					<div class="helpText"><fmt:message key="messages.noBmCreatePermission" /></div>
				</c:if>
				<c:if test="${(component_mode == 'EDIT' || component_mode == 'CREATE') && deletePermission == false}">
					<div class="helpText"><fmt:message key="messages.noBmDeletePermission" /></div>	
				</c:if>
				
				<c:choose>
					<c:when test="${emptyStyle != null}">
						&nbsp;
						<br/>    
					</c:when>
					<c:otherwise>
						<c:forEach items="${cluster_parts}" var="clusterPart" varStatus="clusterStatus">
							<tiles:insertTemplate template="/jsp/common/businessmapping/MappingClusterPart.jsp" flush="true">
								<tiles:putAttribute name="path_to_componentModel" value="${path_to_componentModel}" />
								<tiles:putAttribute name="cluster_index" value="${clusterStatus.index}" />
								<tiles:putAttribute name="cluster_html_id" value="${html_id}_${clusterStatus.index}" />
								<tiles:putAttribute name="force_component_mode" value="${force_component_mode}" />
								<tiles:putAttribute name="updatePermission" value="${updatePermission}" />
								<tiles:putAttribute name="deletePermission" value="${deletePermission}" />
							</tiles:insertTemplate>
						</c:forEach>
					</c:otherwise>
				</c:choose>
				<c:if test="${(component_mode == 'EDIT' || component_mode == 'CREATE') && createPermission}">
					<tiles:insertTemplate template="/jsp/common/businessmapping/NewMappingsPart.jsp" flush="true">
						<tiles:putAttribute name="path_to_componentModel" value="${path_to_componentModel}.newMappingsPart" />
					</tiles:insertTemplate>
				</c:if>
			</div>
		</div>
	</div>
</div>