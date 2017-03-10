<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="path_to_componentModel" />
<tiles:useAttribute name="cluster_index" />
<tiles:useAttribute name="mapping_index" />
<tiles:useAttribute name="mapping_html_id" />
<tiles:useAttribute name="force_component_mode" ignore="true"/>
<tiles:useAttribute name="updatePermission" />
<tiles:useAttribute name="deletePermission" />

<c:set var="path_to_mapping_part" value="${path_to_componentModel}.clusterParts[${cluster_index}].mappingParts[${mapping_index}]" />
<itera:define id="model" name="memBean" property="${path_to_mapping_part}" />
<itera:define id="componentMode" name="memBean" property="${path_to_mapping_part}.componentMode" />
<c:set var="typeOfBuildingBlock" value="${memBean.componentModel.managedType.typeOfBuildingBlock}" />

<c:if test="${!empty force_component_mode}">
	<c:set var="componentMode" value="${force_component_mode}" />
</c:if>

<tr>
	<c:if test="${(componentMode == 'EDIT' || componentMode == 'CREATE') && deletePermission}">
		<td class="buttonintable top">
			<a id="<c:out value="${mapping_html_id}_remove" />" class="link" href="#"
       			title="<fmt:message key="tooltip.remove"/>"
            		onclick="setHiddenField('<c:out value="${path_to_componentModel}.selectedClusterPosition" />', <c:out value="${cluster_index + 1}" />);
               	setHiddenField('<c:out value="${path_to_componentModel}.selectedMappingPosition" />', <c:out value="${mapping_index + 1}" />);
               	setHiddenField('<c:out value="${path_to_componentModel}.action" />', 'delete');
               	flowAction('update');" >
            		<i class="icon-remove"></i>
       		</a>
     		</td>
   	</c:if>
   	<td>
		<itera:define id="elementDisplayOrder" name="memBean" property="${path_to_componentModel}.elementDisplayOrder" />
		<c:forEach var="displayElementDescriptor" items="${elementDisplayOrder}" varStatus="status">
			<fmt:message key="${displayElementDescriptor.elementTypeKey}" />&nbsp;
			<a class="isBold" href="<itera:linkToElement name="model" property="${displayElementDescriptor.modelPath}" type="html"/>">
				<itera:write name="model" property="${displayElementDescriptor.modelPath}.hierarchicalName" escapeXml="true"/>
			</a>
			<%-- Print separator --%>
			<c:if test="${not status.last}"> / </c:if> 
		</c:forEach>
		<c:if test="${fn:length(model.attributeModel.atgParts) > 0}">
			<!-- business mappings attributes section -->
			<br/>
			<tiles:insertTemplate template="/jsp/common/attributes/AttributesComponentView.jsp">
				<tiles:putAttribute name="path_to_componentModel" value="${path_to_mapping_part}.attributeModel" />
       			<tiles:putAttribute name="showHeaderTable" value="false" />
       			<tiles:putAttribute name="overviewMode" value="${!updatePermission}" />
			</tiles:insertTemplate>
		</c:if>
   	</td>
</tr>