<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<tiles:useAttribute name="path_to_componentModel" />
<tiles:useAttribute name="cluster_index" />
<tiles:useAttribute name="cluster_html_id" />
<tiles:useAttribute name="force_component_mode" ignore="true" />
<tiles:useAttribute name="updatePermission" />
<tiles:useAttribute name="deletePermission" />

<c:set var="path_to_cluster_part" value="${path_to_componentModel}.clusterParts[${cluster_index}]" />
<itera:define id="model" name="memBean" property="${path_to_cluster_part}" />
<itera:define id="componentMode" name="memBean" property="${path_to_cluster_part}.componentMode" />
<itera:define id="clusterBbTypeKey" name="memBean" property="${path_to_componentModel}.elementDisplayOrder[0].elementTypeKey"/>

<div id="<c:out value="${cluster_html_id}" />_Modul" class="row-fluid inner-module">
	<div class="inner-module-heading">
		<%-- Take the first descriptor from elementDisplayOrder to print type and name for the clustering element, e.g. Business Process XY --%>
		<fmt:message key="${clusterBbTypeKey}" />&nbsp;
		<a href="<itera:linkToElement name="model" property="clusteredByBuildingBlock" type="html"/>"> 
			<c:out value="${model.clusteredByBuildingBlock.hierarchicalName}" />
		</a>
	</div>
	<div class="row-fluid">
		<div class="inner-module-body-table">
			<div class="row-fluid">
				<table class="table table-striped table-condensed tableInModule">
					<tbody>
						<c:forEach items="${model.mappingParts}" var="mappingPart" varStatus="loopStatus">
							<tiles:insertTemplate template="/jsp/common/businessmapping/MappingPart.jsp" flush="true">
								<tiles:putAttribute name="path_to_componentModel" value="${path_to_componentModel}" />
								<tiles:putAttribute name="cluster_index" value="${cluster_index}" />
								<tiles:putAttribute name="mapping_index" value="${loopStatus.index}" />
								<tiles:putAttribute name="mapping_html_id" value="${cluster_html_id}_${loopStatus.index}" />
								<tiles:putAttribute name="force_component_mode" value="${force_component_mode}" />
								<tiles:putAttribute name="updatePermission" value="${updatePermission}" />
								<tiles:putAttribute name="deletePermission" value="${deletePermission}" />
							</tiles:insertTemplate>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>