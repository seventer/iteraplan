<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="componentMode" value="${memBean.componentMode}" scope="request" />

<c:set var="functionalPermission" value="${userContext.perms.userHasFuncPermInstancePerms}" scope="request" />

<%-- set a different class for read and write mode --%>
<c:choose>
	  <c:when test="${(componentMode != 'READ')}">
	    <c:set var="componentModeClass" value="write" />
	  </c:when>
	  <c:otherwise>
	      <c:set var="componentModeClass" value="read" />
	  </c:otherwise>
</c:choose>

<div class="${componentModeClass}">
	<c:choose>
	<c:when test="${functionalPermission == true}">
		
		<%--Start construction of page --%>
		<tiles:insertTemplate template="/jsp/common/TransactionBar.jsp">
			<tiles:putAttribute name="showDeleteButton" value="false" />
			<tiles:putAttribute name="ignoreWritePermission" value="true" />
		</tiles:insertTemplate>
		
		<tiles:insertTemplate template="/jsp/common/ElementNameComponentView.jsp" flush="true">
		    <tiles:putAttribute name="path_to_componentModel" value="" />
		    <tiles:putAttribute name="html_id" value="name" />
		    <tiles:putAttribute name="name_field_path" value="dto.currentUserEntity.identityString" />
		    <tiles:putAttribute name="componentMode" value="READ" />
		    <tiles:putAttribute name="validate" value="false" />
		</tiles:insertTemplate>
		  
		<tiles:insertTemplate template="/jsp/common/ElementDescriptionComponentView.jsp">
			<tiles:putAttribute name="path_to_componentModel" value="" />
		    <tiles:putAttribute name="html_id" value="description" />
		    <tiles:putAttribute name="description_field_path" value="dto.currentUserEntity.descriptiveString" />
		    <tiles:putAttribute name="componentMode" value="READ" />
		</tiles:insertTemplate>
		
		<tiles:insertTemplate template="/jsp/ObjectRelatedPermission/RemoveBbInstances.jsp" flush="true"/>
		<tiles:insertTemplate template="/jsp/ObjectRelatedPermission/AddBbInstances.jsp" flush="true"/>
	</c:when>
	<c:otherwise>
		<tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
	</c:otherwise>
	</c:choose>
</div>