<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr"%>

<%--
		@UsedFor 	Shows all saved Diagrams (querys) in a table.
		@UsedFrom	jsp\Templates\ModalDialog\DiagramModalDialog.jsp
		@Note		A single (scrollable) Table.
 --%>

<c:set var="filterTooltip">
	<fmt:message key="global.filter.tooltip" />
</c:set>

<c:set var="savedQueries" value="${dialogMemory.customDashboardDialogMemory.savedQueries}" />
<c:set var="queriesSize" value="${fn:length(savedQueries)}" />

<div id="ShowSafedQueryContainer" class="row-fluid module" >
	<div id="savedQueryHeading" class="module-heading" style="height: 22px">
		<fmt:message key="graphicalReport.savedQueries" />

		<tiles:insertTemplate
			template="/jsp/commonReporting/InstantFilter.jsp">
			<tiles:putAttribute name="filterInputId" value="savedQueryFilter" />
			<tiles:putAttribute name="tableToFilterId"
				value="ShowSavedQueryTable" />
			<tiles:putAttribute name="searchFormIdToUse"
				value="savedQuerySearchForm" />
		</tiles:insertTemplate>
	</div>
	<c:choose>
		<c:when test="${queriesSize > 0}">
			<div class="row-fluid">
				<div class="module-body-table">
					<div id="savedQueriesScrollBox" class="row-fluid overflowBox">
						<table id="ShowSavedQueryTable"
							class="table table-striped table-condensed tableInModule">
							<colgroup>
								<col class="col-desc" />
								<col class="col-name" />
								<col class="col-desc" />
								<col class="col-desc" />
							</colgroup>
							<thead>
								<tr>
									<th class="col-desc"><fmt:message key="global.execute" /></th>
									<th class="col-name"><fmt:message key="global.name" /></th>
									<th class="col-name"><fmt:message key="global.description" /></th>
									<th class="col-desc"><fmt:message key="global.visualisationType" /></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${savedQueries}" var="savedQuery">
									<c:set var="query_name">${itera:escapeJavaScript(savedQuery.name)}</c:set>

									<c:set var="includeDiagramIdOnclickValue">
								    	insertDiagramID(<c:out value="${savedQuery.id}" />);
									</c:set>

									<tr class="${rowClassString}" style="${rowStyle}">
										<td onclick="${includeDiagramIdOnclickValue}">
											<a href="javascript:<c:out value="${includeDiagramIdOnclickValue}"/>" rel="tooltip" data-original-title="<fmt:message key='graphicalReport.insertSavedQuery.tooltip'/>">
											 	<i class="icon-play"></i>
											</a>
										</td>
										<td onclick="${includeDiagramIdOnclickValue}">
											<a href="javascript:<c:out value="${includeDiagramIdOnclickValue}"/>" rel="tooltip" data-original-title="<fmt:message key='graphicalReport.insertSavedQuery.tooltip'/>">
												<c:out value="${savedQuery.name}" escapeXml="true" />
											</a>
										</td>
										<td onclick="${includeDiagramIdOnclickValue}">
											<a href="javascript:<c:out value="${includeDiagramIdOnclickValue}"/>" rel="tooltip" data-original-title="<fmt:message key='graphicalReport.insertSavedQuery.tooltip'/>">
												<c:out value="${savedQuery.description}" />
											</a>
										</td>

										<td onclick="${includeDiagramIdOnclickValue}">
						        			<a href="javascript:<c:out value="${includeDiagramIdOnclickValue}"/>" rel="tooltip" data-original-title="<fmt:message key='graphicalReport.insertSavedQuery.tooltip'/>">
						        				<fmt:message key="${savedQuery.type.titleProperty}" />
											</a>
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</c:when>
		<c:otherwise>
			<%-- Show a message if there are no saved queries --%>
			<div class="module-body">
				<fmt:message key="graphicalReport.noSavedQueries" />
			</div>
		</c:otherwise>
	</c:choose>
</div>
