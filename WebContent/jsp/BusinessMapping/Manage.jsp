<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%> 

<c:set var="componentMode" value="${memBean.componentModel.componentMode}" scope="request" />
<c:set var="showTable" value="${not empty memBean.componentModel.table}" scope="request" />

<script type='text/javascript' src="<c:url value="/dwr/interface/BuildingBlockTypeService.js" />"></script>

<c:set var="functionalPermission" scope="request">
	<c:set var="businessMapping" value="businessMapping.singular" />
	<itera:write name="userContext"
		property="perms.userHasBbTypeFunctionalPermission(${businessMapping})" escapeXml="false" />
</c:set>

<c:choose>
<c:when test="${functionalPermission == true}">
	<c:if test="${componentMode == 'READ'}">
		<tiles:insertTemplate template="/jsp/BusinessMapping/BusinessMappingSettings.jsp">
			<tiles:putAttribute name="componentModel" value="${memBean.componentModel.settings}"/>
			<tiles:putAttribute name="path_to_componentModel" value="componentModel.settings"/>
			<tiles:putAttribute name="collapse" value="${showTable}"/>
		</tiles:insertTemplate>
	</c:if>
	<br />
	<c:if test="${showTable}">
	  <tiles:insertTemplate template="/jsp/common/TransactionBar.jsp" >
	  	<tiles:putAttribute name="showDeleteButton" value="false"/>
	  	<tiles:putAttribute name="showBookmarkAndPrint" value="false"/>
	  </tiles:insertTemplate>
	  <br />
	  <br />
	  <tiles:insertTemplate template="/jsp/BusinessMapping/BusinessMappingTable.jsp" > 
	  	<tiles:putAttribute name="componentModel" value="${memBean.componentModel.table}"/>
	  	<tiles:putAttribute name="path_to_componentModel" value="componentModel.table"/>
	  </tiles:insertTemplate>
	</c:if>
</c:when>
<c:otherwise>
  <tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
</c:otherwise>
</c:choose>





