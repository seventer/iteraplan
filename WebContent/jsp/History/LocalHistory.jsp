<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>

		
<%-- No Permission? --%>
<c:choose>
	<c:when test="${!isHasViewHistoryPermission}">
		<div class=alert>
			<a class="close" data-dismiss="alert">×</a>
			<fmt:message key="AUTHORISATION_REQUIRED"/>
		</div>
	</c:when>
	<c:otherwise>
		<%-- Page Navigation --%>
		<div>
			<div style="float: left;">
				<%-- Show number of results --%>
				<fmt:message key="search.results"/><c:out value=" ${resultsPage.firstShownResult} - ${resultsPage.lastShownResult} " />
				<fmt:message key="search.results.of"/><c:out value=" ${resultsPage.numberResults}" />
			</div>
			
			<div style="float: right; margin-right: 8px; font-weight: normal;">
				<div id="searchResultBar_field" class="field" style="float: left;">
					<%-- Print dropdown for selection of results per page number, automatically selects correct resultsPerPage --%>
					<form:select cssClass="input" id="resultsPerPage" path="resultsPage.resultsPerPage" onchange="localHistory_changePageSize()">
						<fmt:bundle basename="iteraplan">
				       		<fmt:message key="searchresults.option.1" var="resultCount1"/>
				       		<fmt:message key="searchresults.option.2" var="resultCount2"/>
				       		<fmt:message key="searchresults.option.3" var="resultCount3"/>
				       		<fmt:message key="searchresults.default.count" var="defaultResultCount"/>
				       	</fmt:bundle>
				         
						<form:option label="${resultCount1}" value="${resultCount1}">${resultCount1}&nbsp;<fmt:message key="search.perPage"/></form:option>
				       	<form:option label="${resultCount2}" value="${resultCount2}">${resultCount2}&nbsp;<fmt:message key="search.perPage"/></form:option>
				       	<form:option label="${resultCount3}" value="${resultCount3}">${resultCount3}&nbsp;<fmt:message key="search.perPage"/></form:option>
					        
					    <fmt:message key="search.option.allresults" var="allLabel"/>
					    <form:option label="${allLabel}" value="-1"/>
					</form:select>
					&nbsp;			    
				</div>

				<%-- Show page numbers and page switching buttons --%>
				<div style="float: right;">
					<%-- Use right spacing to achieve that text remains always at the same position, 
			    	 regardless whether page switching icons are shown or not --%>
					<%-- Test if this is the first page --%>
					<div style="float: right;">
						<c:if test="${resultsPage.curPage gt 0}">
							<div style="float: left;">
								<button id="arrow_first_page" class="link navigateSearchResults" onclick="localHistory_load(0); return false;">
						    		<img alt="First Page" src="<c:url value="/images/pfeil_ganzlinks_schwarz.gif" />" title="" />
					        	</button>
				  			</div>
					  	</c:if>
						<c:if test="${resultsPage.curPage gt 0}">			
					  		<div style="float: left;">
								<button id="arrow_prev_page" class="link navigateSearchResults" onclick="localHistory_load(${resultsPage.curPage-1}); return false;">
									<img alt="Previous Page" src="<c:url value="/images/pfeil_einenlinks_schwarz.gif" />" title="" />
								</button>
							</div>
						</c:if>
							
						<div style="float: left;">
							&nbsp;
							<fmt:message key="search.page"/>
							&nbsp;
							<fmt:formatNumber value="${resultsPage.curPage1BasedIndex}" maxFractionDigits="0"/>/<fmt:formatNumber value="${resultsPage.pages}" maxFractionDigits="0"/>
							&nbsp;
						</div>
						
					  	<%-- Test if this is the last page --%>
						<c:if test="${!resultsPage.lastPage}">
						  	<div style="float: left;">
								<button id="arrow_next_page" class="link navigateSearchResults" onclick="localHistory_load(${resultsPage.curPage+1}); return false;">
							    	<img alt="Next Page" src="<c:url value="/images/pfeil_einenrechts_schwarz.gif" />" title="" />
								</button>
							</div> 
						</c:if>
						<c:if test="${!resultsPage.lastPage}">
							<div style="float: left;">
								<button id="arrow_last_page" class="link navigateSearchResults" onclick="localHistory_load(${resultsPage.pages-1}); return false;">
							   		<img alt="Last Page" src="<c:url value="/images/pfeil_ganzrechts_schwarz.gif" />"  title="" />
								</button>
							</div>
						</c:if>
					</div>	
				</div>
			</div>
		</div>

		<div style="clear: both;" />
		
		<%-- Changesets --%>
		<div>
			<c:forEach items="${resultsPage.bbChangesets}" var="bbChangeset">
				<tiles:insertTemplate template="/jsp/History/ChangesetFragment.jsp">
					<tiles:putAttribute name="bbChangeset" value="${bbChangeset}" />
				</tiles:insertTemplate>
			</c:forEach>
			
			<c:if test="${!isHistoryEnabled}">
				<div class="alert alert-info">
					<a class="close" data-dismiss="alert">×</a>
					<fmt:message key="history.disabledwarning"/>
				</div>
			</c:if>
		</div>
	</c:otherwise>
</c:choose>

