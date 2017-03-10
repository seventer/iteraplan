<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<c:set var="showLinkedProjects" value="${false}" />

<c:choose>
  <%-- query on ISRs was executed --%>
  <c:when test="${dialogMemory.isrSuccessorDTO != null && !dialogMemory.isrSuccessorDTO.noQueryExecuted}">
    <c:set var ="queryResultDto" value="${dialogMemory.isrSuccessorDTO}" />
    <c:set var="showLinkedProjects" value="${true}" />
  </c:when>
  <%-- query on TCRs was executed --%>
  <c:when test="${dialogMemory.tcrSuccessorDTO != null && !dialogMemory.tcrSuccessorDTO.noQueryExecuted}">
    <c:set var ="queryResultDto" value="${dialogMemory.tcrSuccessorDTO}" />
  </c:when>
  <%-- no query was executed --%>
  <c:when test="${dialogMemory.isrSuccessorDTO != null && dialogMemory.isrSuccessorDTO.noQueryExecuted
               && dialogMemory.tcrSuccessorDTO != null && dialogMemory.tcrSuccessorDTO.noQueryExecuted}">
    <p><fmt:message key="reports.results.resultListIsEmtpy" /></p>
  </c:when>
  <%-- no releases available for querying --%>
  <c:when test="${(dialogMemory.selectedIsrId == null) && (dialogMemory.selectedTcrId == null)}">
    <p><fmt:message key="message.report.successor.noReleaseAvaliable" /></p>
  </c:when>  
</c:choose>

<c:if test="${queryResultDto != null && queryResultDto.succession[0] != null && 
	            queryResultDto.selectedResultFormat != 'reports_export_Excel2003' && 
	            queryResultDto.selectedResultFormat != 'reports_export_Excel2007'}">

	<div id="SuccessorReportResultsModule" class="row-fluid module">
		<div class="module-heading">
			<fmt:message key="reports.results" />
		</div>
		<div class="row-fluid">
			<div class="module-body-table">
				<div class="row-fluid">
					<table class="table table-striped table-condensed tableInModule">
						<thead>
							<tr>
								<th><fmt:message key="global.nameversion" /></th>
								<th><fmt:message key="global.from" /></th>
								<th><fmt:message key="global.to" /></th>
								<th><fmt:message key="global.type_of_status" /></th>
								<c:if test="${showLinkedProjects}">
            						<th><fmt:message key="project.singular" /></th>
          						</c:if>
							</tr>
						</thead>
						<tbody>
							<c:set var="linkScript" value="" />
						    <c:set var="link" value="" />
						    <c:set var="linkStyle" value="link" />
						      
						    <c:forEach var="container" items="${queryResultDto.succession}">
						        
						    	<c:set var="release" value="${container.release}" />
						        <c:set var="linkScript">
						            <itera:linkToElement name="release" type="js"/>
						        </c:set>
						        <c:set var="link">
						            <itera:linkToElement name="release" type="html"/>
								</c:set>                
						        
						        <tr class="<c:out value="${linkStyle}" />" onclick="${linkScript}" >
						        	<td style="white-space: nowrap;">
							            <c:set var="hLevel" value="${container.level}" />
							            <c:if test="${hLevel > 1}">
							            	<c:forEach begin="2" end="${hLevel}">
							                	<img height="7" alt="" vspace="4" hspace="7" src="<c:url value="/images/blank.gif" />" width="10" align="left" border="0" />
							              	</c:forEach>
							            </c:if> 
							            <c:if test="${hLevel > 0}">
							              	<img height="7" alt="" vspace="4" hspace="7" src="<c:url value="/images/Angle2.gif" />" width="10" align="left" border="0" />
							            </c:if>
							            <itera:htmlLinkToElement link="${link}" isLinked="true"> 
											<c:out value="${release.releaseName}" />
										</itera:htmlLinkToElement>
							        </td>
						          	<td><fmt:formatDate value="${release.runtimePeriod.start}" dateStyle="short" /></td>
						          	<td><fmt:formatDate value="${release.runtimePeriod.end}" dateStyle="short" /></td>
						          	<td class="statusintable"><fmt:message key="${release.typeOfStatusAsString}" /></td>
						          	<c:if test="${showLinkedProjects}">
						            	<td>
						              		<c:if test="${not empty release.projects}">
						              			<c:forEach var="project" items="${release.projects}" varStatus="loopStatus">
						                			<c:if test="${loopStatus.index > 1}">
						                  				<%-- If there is more than one project, 
						                       				place a semicolon in front of each subsequent value --%>
						                  				<c:out value="; " />
						                			</c:if> 
						                			<c:out value="${project.name}" />
						              			</c:forEach>
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
	</div>
</c:if>
