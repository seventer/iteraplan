<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<c:set var="functionalPermission" value="${userContext.perms.userHasFuncPermAttributes}" scope="request" />

<tiles:useAttribute name="addManageShortcuts" ignore="true" />

<tiles:putAttribute name="entityNamePluralKey" value="global.dateintervals"/>

<%-- The message key for the plural form of the current entity type. Type: String --%>
<tiles:useAttribute name="entityNamePluralKey" />

<%-- A list that specifies how search results columns shall be rendered. Type: List<ColumnDefinitions> --%>
<tiles:useAttribute name="resultColumnDefinitions" ignore="true" />

<c:set var="createNewJavascript" value="flowActionRedirect(getRestURIRelativeToMVC(), 'create');"/>


<c:choose>
<c:when test="${functionalPermission == true}">
<div class="row">
	<div id="addDateIntervalDiv" class="btn-toolbar pull-right" style="padding: right;">
		<a href="#" onclick="${createNewJavascript}" class="btn btn-primary"><i class="icon-plus icon-white"></i> <fmt:message key="button.create" /></a>
	</div>
</div>

<div id="ResultTableModule" class="row-fluid module">
	<div class="module-heading-nopadding">
		<div style="padding: 8px 0px; float: left;">
			<fmt:message key="global.dateintervals" />
		</div>
	</div>
	<div class="row-fluid">
		<div class="module-body-table">
			<div class="row-fluid">

			<table class="table table-striped table-condensed tableInModule">
				
				<thead>
					<tr>                  
						<th nowrap="nowrap"><fmt:message key="global.name" /></th>
						<th nowrap="nowrap"><fmt:message key="global.startfordateinterval" /></th>
						<th nowrap="nowrap"><fmt:message key="global.endfordateinterval" /></th>
						<th nowrap="nowrap"><fmt:message key="reports.color" /></th>
						<th nowrap="nowrap"><fmt:message key="global.edit" /></th>
					</tr>
				</thead>
				<tbody>
				
					<c:forEach var="row" items="${dialogMemory.intervals}">
						<tr>
							<td class="link" onclick="window.location.href=getRestURIRelativeToMVC()+'<c:out value="${row.id}" />';">
								<c:out value="${row.name}" />
							</td>
							<td class="link" onclick="window.location.href=getRestURIRelativeToMVC()+'<c:out value="${row.id}" />';">
								<c:out value="${row.startDate}" />
							</td>
							<td class="link" onclick="window.location.href=getRestURIRelativeToMVC()+'<c:out value="${row.id}" />';">
								<c:out value="${row.endDate}" />
							</td>
							<td class="link" onclick="window.location.href=getRestURIRelativeToMVC()+'<c:out value="${row.id}" />';">
								<div style="background-color:<c:out value="${row.defaultColorHex}"/>; width:50px;height:20px; border-style:solid; border-width:1px" />
							</td>
							<td>
								<a href="javascript:changeLocation(getRestURIRelativeToMVC()+'<c:out value="${row.id}" />?_eventId=edit');" title="<fmt:message key='button.edit'/>">
								<i class="icon-pencil"></i>
								</a>
								<a href="javascript:confirmDelete(function(){changeLocation(getRestURIRelativeToMVC()+'<c:out value="${row.id}" />?_eventId=delete')});" title="<fmt:message key='button.delete'/>">
								<i class="icon-trash"></i>
								</a>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			</div>
		</div>
	</div>

</div>

</c:when>
<c:otherwise>
  <tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
</c:otherwise>
</c:choose>