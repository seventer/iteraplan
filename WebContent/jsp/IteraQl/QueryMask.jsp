<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:if test="${!(dialogMemory.errors == null)}">
	<div class="alert alert-error">
		<a class="close" data-dismiss="alert">×</a>
		<span class="errorHeader"><fmt:message key="errors.header" /><br />
			<c:forEach var="error" items="${dialogMemory.errors}" >
				<c:out value="${error}" /><br />
			</c:forEach>
		</span>
	</div>       			
</c:if>

<div class="row-fluid">
	<div id="QueryConsoleContainer" class="row-fluid module">
		<div class="module-heading">
		    <fmt:message key="global.iteraql" /> 
		</div>
		<div class="row-fluid">
			<div class="module-body">
				<div class="row-fluid">
					<fmt:message key="iteraql.query.message"/>
					<br/>
					<textarea name="query" cols="120" rows="3" ><c:out value="${dialogMemory.query}"/></textarea>
					<br/>
					<input id="sendQuery" type="button" class="link btn btn-primary" value="<fmt:message key="button.sendQuery" />" onclick="submitForm('sendQuery.do');" />
					<input id="clear" type="button" class="link btn" value="<fmt:message key="iteraql.clear.trigger" />" onclick="submitForm('clear.do');"/>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="accordion" id="examplesContainer">
    <div class="accordion-group">
        <div class="accordion-heading">
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#examplesContainer" href="#examples"
					onclick="toggleIcon('examplesIcon', 'icon-resize-full', 'icon-resize-small');" >
			  <i id="examplesIcon" class="icon-resize-full"></i>
              <fmt:message key="iteraql.helpLabel" />
            </a>
        </div>
	    <div id="examples" class="accordion-body collapse">
			<div class="accordion-inner">
				<fmt:message key="iteraql.examples" />
				<br/><br/>
				<a href="http://www.iteraplan.de/wiki/display/iteraplan/Query+Console+%26+iteraQL" target="_blank" class="iteratecLink"><u><fmt:message key="iteraql.userManual" /></u></a>
			</div>
	    </div>
	</div>
</div>

<c:choose>
	<c:when test="${not empty memBean}">
	<fmt:message key="interchange.help.choose.destination"/>
		<br/>
  	  <form:select path="selectedDestination" style="width: 320px" >
			<c:forEach items="${dialogMemory.availableDestinations}" var="destination">
				<form:option value="${destination}" >
					<fmt:message key="${destination}" />
				</form:option>
			</c:forEach>
		</form:select>
	
		<input id="doInterchange" type="button" class="link btn btn-primary" value="<fmt:message key="iteraql.interchange.trigger" />" onclick="createHiddenField('selectedBuildingBlock','${memBean.selectedBuildingBlock}');submitForm('interchange.do');" />
		<br/><br/>
		<tiles:insertTemplate template="/jsp/commonReporting/resultPages/GeneralResultPage.jsp" flush="true" />
	</c:when>
	<c:otherwise>
		<c:if test="${iteraQlBean ne null}">
			<div class="row-fluid">
				<div id="QueryResultsContainer" class="row-fluid module">
					<div class="module-heading">
						<fmt:message key="iteraQl.results" />
					</div>
					<div class="row-fluid">
						<div class="module-body-table">
							<div class="row-fluid">
								<table class="searchResultView table table-striped table-condensed tableInModule" id="iteraQlResultsView">
									<c:choose>
										<c:when test="${iteraQlBean.bindingSetResult}">
											<colgroup>
												<col width="3%"  />
												<col width="15%"  />
												<col width="32%"  />
												<col width="3%"  />
												<col width="15%"  />
												<col width="32%"  />
											</colgroup>
											<thead>
												<tr>
													<th colspan="6">
														<fmt:message key="iteraQl.queryExecTime" />
														<c:out value="${iteraQlBean.execTime}" />
													</th>
												</tr>
												<tr>
													<th colspan="3">
														<fmt:message key="iteraQl.resultType" />
														<c:out value="${iteraQlBean.type1Name}" />
													</th>
													<th colspan="3">
														<fmt:message key="iteraQl.resultType" />
														<c:out value="${iteraQlBean.type2Name}" />
													</th>
												</tr>
												<tr>
													<th colspan="3">
														<fmt:message key="iteraQl.resultCount" />
														<c:out value="${iteraQlBean.type1Size}" />
													</th>
													<th colspan="3">
														<fmt:message key="iteraQl.resultCount" />
														<c:out value="${iteraQlBean.type2Size}" />
													</th>
												</tr>
												<tr>
													<th scope="col">
														<b><fmt:message key="global.id" /></b>
													</th>
													<th scope="col">
														<b><fmt:message key="global.name" /></b>
													</th>
													<th scope="col">
														<b><fmt:message key="global.description" /></b>
													</th>
													<th scope="col">
														<b><fmt:message key="global.id" /></b>
													</th>
													<th scope="col">
														<b><fmt:message key="global.name" /></b>
													</th>
													<th scope="col">
														<b><fmt:message key="global.description" /></b>
													</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach var="resultEntry" items="${iteraQlBean.resultEntries}" varStatus="loopStatus">
													<tr>
														<c:set var="entryIndex" value="${loopStatus.index}" />
														<td>
															<c:out value="${iteraQlBean.resultEntries[entryIndex].id1}" />
														</td>
														<td>
															<c:out value="${iteraQlBean.resultEntries[entryIndex].name1}" />
														</td>
														<td>
															<itera:write name="resultEntry" property="description1" plainText="true" truncateText="true" escapeXml="false" />
														</td>
														<td>
															<c:out value="${iteraQlBean.resultEntries[entryIndex].id2}" />
														</td>
														<td>
															<c:out value="${iteraQlBean.resultEntries[entryIndex].name2}" />
														</td>
														<td>
															<itera:write name="resultEntry" property="description2" plainText="true" truncateText="true" escapeXml="false" />
														</td>
													</tr>
												</c:forEach>
											</tbody>
										</c:when>
										<c:otherwise>
											<colgroup>
												<col width="6%"  />
												<col width="30%"  />
												<col width="64%"  />
											</colgroup>
											<thead>
												<tr>
													<th colspan="3"><fmt:message
															key="iteraQl.queryExecTime" />
														<c:out value="${iteraQlBean.execTime}" />
													</th>
												</tr>
												<tr>
													<th colspan="3"><fmt:message key="iteraQl.resultType" />
														<c:out value="${iteraQlBean.type1Name}" />
													</th>
												</tr>
												<tr>
													<th colspan="3"><fmt:message key="iteraQl.resultCount" />
														<c:out value="${iteraQlBean.type1Size}" />
													</th>
												</tr>
												<tr>
													<th scope="col">
														<b><fmt:message key="global.id" /></b>
													</th>
													<th scope="col">
														<b><fmt:message key="global.name" /></b>
													</th>
													<th scope="col">
														<b><fmt:message key="global.description" /></b>
													</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach var="resultEntry" items="${iteraQlBean.resultEntries}" varStatus="loopStatus">
													<tr>
														<c:set var="entryIndex" value="${loopStatus.index}" />
														<td>
															<c:out value="${iteraQlBean.resultEntries[entryIndex].id1}" />
														</td>
														<td>
															<c:out value="${iteraQlBean.resultEntries[entryIndex].name1}" />
														</td>
														<td>
															<itera:write name="resultEntry" property="description1" plainText="true" truncateText="true" escapeXml="false" />
														</td>
													</tr>
												</c:forEach>
											</tbody>
										</c:otherwise>
									</c:choose>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
		</c:if>
	</c:otherwise>
</c:choose>			
			

			<%--	<fmt:message key="interchange.help.choose.destination"/>
 		<br>
	    <form:select path="selectedDestination" cssStyle="wide" >
			<c:forEach items="${dialogMemory.availableDestinations}" var="destination">
				<form:option value="${destination}" >
					<fmt:message key="${destination}" />
				</form:option>
			</c:forEach>
		</form:select>
		
		<input id="doInterchange" type="button" class="link btn" value="<fmt:message key="iteraql.interchange.trigger" />" onclick="createHiddenField('selectedBuildingBlock','${memBean.selectedBuildingBlock}');submitForm('interchange.do');" />

	<br><br>
	 <tiles:insertTemplate template="/jsp/commonReporting/resultPages/GeneralResultPage.jsp" flush="true" /> --%>
		