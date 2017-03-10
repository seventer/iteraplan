<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<br />
<br />
<br />
<br />

<c:choose>
  <%-- Determine if there are any domains which contain consistency checks with results. --%>
  <c:when test="${not empty dialogMemory.domainsResult}">
    
    <%-- Iterate over the list of domains that contain a consistency check with results. --%>
    <c:forEach items="${dialogMemory.domainsResult}" var="domain" varStatus="domainStatus">
    	<div class="row-fluid module">
			<div class="module-heading">
				<fmt:message key="reports.results" /> - <fmt:message key="manageConsistencyReport.title" /> - <fmt:message key="${domain.value}" />
			</div>
			<div class="row-fluid">
				<div class="module-body-table">
					<div class="row-fluid">
						<table class="searchResultView table table-striped table-condensed tableInModule noHighlighting">
							<colgroup>
								<col class="col-ico" />
								<col class="col-consist" />
							</colgroup>
							<thead>
								<tr>
									<th class="col-ico"><fmt:message key="global.number" /></th>
									<th><fmt:message key="check.name" /></th>
								</tr>
							</thead>
							<tbody>
								<%-- Iterate over the result objects of each domain. --%>
								<c:forEach items="${dialogMemory.results[domain]}" var="result" varStatus="resultStatus">
								
									<%-- To display the correct number of the executed consistency check, 
									     it has to be determined if just one or all checks have been executed. 
									     This is done by checking the size of the collection of results. 
									     If one check has been executed, display the number that was transmitted 
									     in the preceding request and stored in the result object. 
									     Otherwise just loop through the results and display the loop index. --%>
									<c:choose>
										<c:when test="${resultStatus.last}">
											<c:choose>
												<c:when test="${resultStatus.count == 1}">
													<c:set var="index" value="${result.number}" />
												</c:when>
												<c:otherwise>
													<c:set var="index" value="${resultStatus.index}" />
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<c:set var="index" value="${resultStatus.index}" />
										</c:otherwise>
									</c:choose>
									<tr>
										<td class="col-ico">
											<c:out value="${index + 1}" />
										</td>
										<td>
											<fmt:message key="${result.name}" var="name" /> 
											<%-- Tokenize the name of the consistency check to find the location of each parameter. --%>
											<c:forTokens items="${name}" delims="$" var="token" varStatus="tokenStatus">
												<c:set var="parameter" value="${result.parameters[token]}" />
												<c:choose>
													<c:when test="${parameter != null}">
														<c:choose>
															<c:when test="${parameter.localized}">
																<b><fmt:message key="${parameter.value}" /></b>
															</c:when>
															<c:otherwise>
																<b><itera:write name="parameter" property="value" escapeXml="true" /></b>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<%-- Write the tokens. --%>
														<c:out value="${token}" />
													</c:otherwise>
												</c:choose>
											</c:forTokens> 
											<br />
											<br />
											<c:set var="columnCount" value="${fn:length(result.columns)}" />
											<c:set var="rowCount" value="${fn:length(result.rows)}" />
											<c:choose>              
												<%-- The consistency check yielded no results. --%>
												<c:when test="${rowCount <= 0}">
												<fmt:message key="manageConsistencyReport.noInconsistencies" />
												</c:when>
												<c:otherwise>
													<table class="searchResultView table table-bordered table-striped table-condensed">
														<thead>
															<tr>                  
																<%-- Iterate over the columns of the consistency check and print them. --%>  
																<c:forEach items="${result.columns}" var="column">             
																	<th nowrap="nowrap">
																		<fmt:message key="${column.name}" />&nbsp;&nbsp;
																	</th>
																</c:forEach>
															</tr>
														</thead>
														<tbody>
															<%-- Iterate over the resulting rows of the consistency check and print them. --%>
															<c:forEach var="row" items="${result.rows}">
																<tr>
																	<%-- Iterate over the objects in each row. --%>
																	<c:forEach items="${row.elements}" var="element" varStatus="elementStatus" >
																		<c:set var="elementIndex" value="${elementStatus.index}" />
																		<%-- Get the corresponding column to read its meta information. --%>
																		<itera:define id="column" name="result" property="columns[${elementIndex}]" />
																		
																		<c:set var="linkScript" value="" />
																		<c:set var="link" value="" />
																		<c:set var="linkStyle" value="" />
																		
																		<%-- Determine if the object shall be linked. --%>
																		<c:if test="${column.type == 'OBJECT' && column.linked == true}">
																			<c:set var="linkScript"><itera:linkToElement name="element" type="js"/></c:set>
																			<c:set var="link"><itera:linkToElement name="element" type="html"/></c:set>
																			<c:set var="linkStyle" value="link" />
																		</c:if>
																		 
																		<c:choose>                          
																			<%-- The object is of type LIST. --%>
																			<c:when test="${column.type == 'OBJECTLIST'}">
																			
																				<%-- Get the accessor for the object and print a string representation. --%>
																				<c:set var="accessor" value="${column.accessor}" />
																				  
																				<td>
																					<table width="100%">
																						<tbody>
																							<c:forEach var="listElement" items="${element}" >
																								<%-- Objects in lists are always linked. --%>
																								<c:set var="linkScript"><itera:linkToElement name="listElement" /></c:set>
																								<c:set var="link"><itera:linkToElement name="listElement" type="html"/></c:set>
																								<tr>
																									<td class="link" onclick="<c:out value="${linkScript}" />" nowrap="nowrap">
																										<itera:htmlLinkToElement link="${link}" isLinked="true">
																											<itera:write name="listElement" property="${accessor}" escapeXml="true" />
																										</itera:htmlLinkToElement>
																									</td>
																								</tr>
																							</c:forEach>
																						</tbody>
																					</table>
																				</td> 
																			</c:when>
																			<c:otherwise>
																				<td class="<c:out value="${linkStyle}"/>" onclick="<c:out value="${linkScript}" />" nowrap="nowrap">
																					<c:choose>
																						<c:when test="${column.type == 'OBJECT'}">
																						           
																							<%-- Get the accessor for the object and print a string representation. --%>
																							<c:set var="accessor" value="${column.accessor}" />                                
																							
																							<c:choose>
																								<c:when test="${column.localized == true}">
																									<itera:define id="elementKey" name="element" property="${accessor}" />                           
																									<fmt:message key="${elementKey}" />    
																								</c:when>
																								<c:otherwise>
																									<itera:htmlLinkToElement link="${link}" isLinked="true">
																										<itera:write name="element" property="${accessor}" escapeXml="true" />
																									</itera:htmlLinkToElement>
																								</c:otherwise>                                
																							</c:choose>
																						</c:when>
																						<%-- The object is of type STRING or DATE. --%>
																						<c:otherwise>
																							<c:out value="${element}" />
																						</c:otherwise>
																					</c:choose>
																				</td>    
																			</c:otherwise>
																		</c:choose>
																	</c:forEach>
																</tr>
															</c:forEach>
														</tbody>
													</table>
												</c:otherwise>
											</c:choose>
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
      <c:if test="${not domainStatus.last}">
        <br />
        <br />
      </c:if>
    </c:forEach>
  </c:when>
  <c:otherwise>
    <fmt:message key="reports.results.resultListIsEmtpy" />
  </c:otherwise>
</c:choose>