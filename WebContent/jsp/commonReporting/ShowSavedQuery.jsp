<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>

<%--
		@UsedFor 	Shows all saved tabularReports (querys) in a table.
		@UsedFrom	jsp\commonReporting\ManageReportOrMassupdate.jsp; jsp\CustomDashboard\Manage.jsp
		@Note		A single (scrollable) Table.
 --%>

<tiles:useAttribute name="mvc" ignore="true" />
<tiles:useAttribute name="selectedSavedQueryId" ignore="true" />
<tiles:useAttribute name="isGraphicalReporting" ignore="true" />
<tiles:useAttribute name="permissionCreateReports" ignore="true" />
<tiles:useAttribute name="permissionSaveReports" ignore="true" />
<%-- The composite diagram does not have any building block type, others do. --%>
<tiles:useAttribute name="showBuildingBlockType" ignore="true" />
<tiles:useAttribute name="disableDirectExecution" ignore="true" />
<tiles:useAttribute name="hideLinkColumn" ignore="true" />
<tiles:useAttribute name="skipLoadRequestConfirmation" ignore="true" />
<tiles:useAttribute name="disableRows" ignore="true" />

<c:choose>
	<c:when test="${not empty permissionCreateReports}">
		<c:set var="executeInsteadOfLoad" value="${not permissionCreateReports}" />
	</c:when>
	<c:otherwise>
		<c:set var="executeInsteadOfLoad" value="false" />
	</c:otherwise>
</c:choose>

<c:set var="filterTooltip">
	<fmt:message key="global.filter.tooltip"/>
</c:set>

<c:choose>
	<c:when test="${not empty mvc}">
		<itera:define id="savedQueries" name="${mvc}" property="savedQueries" />
		<tiles:insertTemplate template="/jsp/commonReporting/SavedQueryMVC.js.jsp">
			<tiles:putAttribute name="skipLoadRequestConfirmation" value="${skipLoadRequestConfirmation}" />
		</tiles:insertTemplate>
	</c:when>
	<c:otherwise>
		<c:set var="savedQueries" value="${memBean.savedQueries}" />

		<%-- 
		submit has to be implemented here explicitly as the normal submitLink(..) method does not work
		for diagram generation
		--%>
		<script type="text/javascript">
		/* <![CDATA[ */
			function loadSavedQuery(queryName, queryId, functionName) {
				showConfirmDialog("<fmt:message key='graphicalReport.loadSavedQuery.tooltip'/>", 
						"<fmt:message key='graphicalReport.executeSavedQuery'><fmt:param>" + queryName +"</fmt:param></fmt:message>",
						function(){
							createHiddenField('savedQueryId', queryId);
							flowAction(functionName);
						});
			}
		
			function deleteSavedQuery(queryName, queryId) {
				showConfirmDialog("<fmt:message key='global.confirmDelete'/>", 
						"<fmt:message key='graphicalReport.executeRemoveQuery'><fmt:param>" + queryName +"</fmt:param></fmt:message>",
						function(){
							createHiddenField('deleteQueryId', queryId);
							flowAction('deleteSavedQuery');
						});
			}
			
			<c:if test="${massUpdateMode == true}">
			function runSavedQuery(queryId, functionName) {
				if (true) {
					createHiddenField('savedQueryId', queryId);
					flowAction(functionName);
				}
		
			}
			</c:if>
		/* ]]> */
		</script>
	</c:otherwise>
</c:choose>


<c:set var="queriesSize" value="${fn:length(savedQueries)}" /> 

<c:set var="save_function" value="loadSavedQuery" />
<%-- Currently there is no need to differentiate between savedQuerys in Tabular/MassUpdate mode
<c:if test="${massUpdateMode == true}"> 
    <c:set var="save_function" value="loadSavedQueryForMassUpdate"/>
</c:if>
--%>

<div id="ShowSafedQueryContainer" class="row-fluid module">
	<div id="savedQueryHeading" class="module-heading" style="height:22px">
		<fmt:message key="graphicalReport.savedQueries" />
		
		<tiles:insertTemplate template="/jsp/commonReporting/InstantFilter.jsp">
			<tiles:putAttribute name="filterInputId" value="savedQueryFilter" />
			<tiles:putAttribute name="tableToFilterId" value="ShowSavedQueryTable" />
			<tiles:putAttribute name="searchFormIdToUse" value="savedQuerySearchForm" />
		</tiles:insertTemplate>
	</div>
	<c:choose>
	<c:when test="${queriesSize > 0}">
		<div class="row-fluid">
			<div class="module-body-table">
				<div id="savedQueriesScrollBox" class="row-fluid overflowBox">
					<table id="ShowSavedQueryTable" class="table table-striped table-condensed tableInModule">
						<colgroup>
							<c:if test="${!disableDirectExecution}">
								<col class="col-desc" />
							</c:if>
							<col class="col-name" />
							<col class="col-desc" />
							<c:if test="${showBuildingBlockType == true}"> 
				            	<col class="col-desc" />
							</c:if>
							<c:if test="${!hideLinkColumn}">
								<col class="col-desc" />
							</c:if>
							<c:if test="${permissionSaveReports}">
								<col class="col-desc" />
							</c:if>
						</colgroup>
						<thead>
							<tr>
								<c:if test="${!disableDirectExecution}">
									<th class="col-desc"><fmt:message key="global.execute"/></th>
								</c:if>
								<th class="col-name"><fmt:message key="global.name"/></th>
					      		<th class="col-name"><fmt:message key="global.description"/></th>
								<c:if test="${showBuildingBlockType == true}"> 
									<th class="col-desc"><fmt:message key="global.bbtype"/></th>
								</c:if>
								<c:if test="${!hideLinkColumn}">
									<th class="col-desc"><fmt:message key="global.link"/></th>
								</c:if>
								<c:if test="${permissionSaveReports == true}">
									<th class="col-desc">
											<fmt:message key="button.delete"/>
									</th>
								</c:if>
							</tr>
						</thead>
						<tbody>
							<c:if test="${disableRows}">
								<c:set var="rowStyle">opacity: 0.3;</c:set>
							</c:if>
							<c:forEach items="${savedQueries}" var="savedQuery">
								<c:set var="query_name">${itera:escapeJavaScript(savedQuery.name)}</c:set>
								
								<c:if test="${massUpdateMode != true}">
									<c:set var="fastExportURL">
								   	<c:url value="/show/fastexport/generateSavedQuery.do?id=${savedQuery.id}&savedQueryType=${savedQuery.type.value}&outputMode=attachment" />
									</c:set>
								</c:if>
								<c:set var="fastExportURLforBookmark">
									<c:if test="${isGraphicalReporting}">../</c:if>
									<c:out value="fastexport/generateSavedQuery.do?id=${savedQuery.id}&savedQueryType=${savedQuery.type.value}&outputMode=attachment" />
								</c:set>
								
								
								<c:if test="${!disableRows}">
									<c:choose>
										<c:when test="${massUpdateMode != 'true'}">
								    		<c:set var="executeQueryOnclickValue">
								        		changeLocation('<c:out value="${fastExportURL}" />'); return false;
								      		</c:set>
								    	</c:when>
								    	<c:otherwise>
								      		<c:set var="executeQueryOnclickValue">
								        		runSavedQuery(<c:out value="${savedQuery.id}"/>, 'runSavedQuery');
								      		</c:set>
								    	</c:otherwise>
								  	</c:choose>
								</c:if>
							
								<c:if test="${!disableRows}">
									<c:choose>
										<c:when test="${executeInsteadOfLoad == 'true'}">
									    	<c:set var="loadQueryOnclickValue" value="${executeQueryOnclickValue}" />
									  	</c:when>
									  	<c:otherwise>
									    	<c:set var="loadQueryOnclickValue">
									      		loadSavedQuery('<c:out value="${query_name}" />', '<c:out value="${savedQuery.id}" />', '<c:out value="${save_function}" />');
									    	</c:set>
									  	</c:otherwise>
									</c:choose>
								</c:if>
								
								<c:if test="${!disableRows}">
									<c:choose>
										<c:when test="${selectedSavedQueryId == savedQuery.id}" >
											<c:set var="rowClassString" value="link highlighted" />
										</c:when>
										<c:otherwise>
											<c:set var="rowClassString">
												link
											</c:set>
										</c:otherwise>
									</c:choose>
								</c:if>
							
					      		<tr class="${rowClassString}" style="${rowStyle}">
									<c:if test="${!disableDirectExecution}">
										<td onclick="${executeQueryOnclickValue}">
											<c:if test="${!disableRows}">
												<a href="javascript:<c:out value="${executeQueryOnclickValue}"/>" rel="tooltip" data-original-title="<fmt:message key='graphicalReport.executeSavedQuery.tooltip'/>">
											</c:if>
											<i class="icon-play"></i>
											<c:if test="${!disableRows}">
												</a>
											</c:if>
										</td>
									</c:if>
									<td onclick="${loadQueryOnclickValue}">
										<c:if test="${!disableRows}">
											<a href="#" rel="tooltip" data-original-title="<fmt:message key='graphicalReport.loadSavedQuery.tooltip'/>">
										</c:if>
										<c:out value="${savedQuery.name}" escapeXml="true"/>
										<c:if test="${!disableRows}">
											</a>
										</c:if>
									</td>
									<td onclick="${loadQueryOnclickValue}">
										<c:if test="${!disableRows}">
											<a href="#" rel="tooltip" data-original-title="<fmt:message key='graphicalReport.loadSavedQuery.tooltip'/>">
										</c:if>
											<c:out value="${savedQuery.description}" />
										<c:if test="${!disableRows}">
											</a>
										</c:if>
									</td>
					        		<c:if test="${showBuildingBlockType == true}"> 
						        		<td onclick="${loadQueryOnclickValue}">
											<c:if test="${!disableRows}">
						        				<a href="#" rel="tooltip" data-original-title="<fmt:message key='graphicalReport.loadSavedQuery.tooltip'/>">
						        			</c:if>
						          				<fmt:message key="${savedQuery.resultBbType.typeOfBuildingBlock.value}" />
											<c:if test="${!disableRows}">
												</a>
											</c:if>
						        		</td>
					        		</c:if>
					                
									<c:if test="${!hideLinkColumn}">
										<td>
											<c:if test="${!disableRows}">
												<a href="#" onclick="showTipLinkDialog('<fmt:message key="global.bookmark" />:', '<fmt:message key="global.bookmark" />', getParentURI()+'<c:out value="${fastExportURLforBookmark}"/>');" >
											</c:if>
								             	<i class="icon-bookmark"></i>
											<c:if test="${!disableRows}">
												</a>
											</c:if>
										</td>
									</c:if>
						        	<c:if test="${permissionSaveReports == true}">
							        	<td>
											<c:if test="${!disableRows}">
								            	<a href="#" onclick="deleteSavedQuery('<c:out value="${query_name}"/>', '<c:out value="${savedQuery.id}"/>');" >
											</c:if>
								            	<i class="icon-remove"></i>
											<c:if test="${!disableRows}">
												</a>
											</c:if>
							        	</td>
						        	</c:if>
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
