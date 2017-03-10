<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<c:set var="functionalPermission" value="${userContext.perms.userHasFuncPermSupportingQuery}" scope="request" />

<c:choose>
	<%-- Determine if there are any results to display. --%>
	<c:when test="${not empty dialogMemory.results}">
		<div id="SupportingQueryResultModul" class="row-fluid module">
			<div class="module-heading">
				<fmt:message key="reports.results" /> - <fmt:message key="permission.query.header" />
			</div>
			<div class="row-fluid">
				<div class="module-body-table">
					<div class="row-fluid">
						<table class="table table-striped table-condensed tableInModule">
							<colgroup>
								<col class="col-ico" />
								<col class="col-desc" />
							</colgroup>
							<thead>
								<tr>
									<th class="col-ico"><fmt:message key="global.number" /></th>
									<th><fmt:message key="permission.query.query" /></th>
								</tr>
							</thead>
							<tbody>
								<%-- Iterate over the results. --%>
								<c:forEach items="${dialogMemory.results}" var="result" varStatus="resultStatus">
								
								
								<%-- To display the correct number of the executed supporting query, 
									 display the number that was transmitted in the preceding request 
								     and stored in the result object. --%>
								<c:set var="index" value="${result.number}" />
								
									<tr>
										<td class="col-ico">
											<c:out value="${index + 1}" />
										</td>
										<td>
											<fmt:message key="${result.name}" var="name" /> 
											
											<%-- Tokenize the name of the permission query to find the location of each parameter. --%>
											<c:forTokens items="${name}" delims="$" var="token" varStatus="tokenStatus">
											<c:set var="parameter" value="${result.parameters[token]}" />
												<c:choose>
													<c:when test="${parameter != null}">
														<c:choose>
															<c:when test="${parameter.localized}">
																<b><fmt:message key="${parameter.value}" /></b>
															</c:when>
															<c:otherwise>
																<b><c:out value="${parameter.value}" /></b>
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
													<table class="table table-bordered table-striped table-condensed">
														<tr>
															<%-- Iterate over the columns of the consistency check and print them. --%>
															<c:set var="firstColumnClass" value="col-name" />
															<c:forEach var="column" items="${result.columns}">
																<th class="${firstColumnClass}">
																	<fmt:message key="${column.name}" />&nbsp;&nbsp;
																</th>
																<c:set var="firstColumnClass" value="" />
															</c:forEach>                                           
														</tr>
													
														<%-- Iterate over the resulting rows of the consistency check and print them. --%>
														<c:forEach var="row" items="${result.rows}" varStatus="rowIndexStatus">
															<tr>
																<%-- Iterate over the objects in each row. --%>
																<c:forEach var="element" items="${row.elements}" varStatus="elementIndexStatus">
																
																	<%-- Get the corresponding column to read its meta information. --%>
																	<itera:define id="column" name="result" property="columns[${elementIndexStatus.index}]" />
																	
																	<c:set var="linkScript" value="" />
																	<c:set var="link" value="" />
																	<c:set var="linkStyle" value="" />
																	
																	<%-- Determine if the object shall be linked. --%>
																	<c:if test="${column.type == 'OBJECT' && column.linked == true}">
																		<c:set var="linkScript"><itera:linkToElement name="element" type="js" /></c:set>
																		<c:set var="link"><itera:linkToElement name="element" type="html" /></c:set>
																		<c:set var="linkStyle" value="link" />
																	</c:if>
																	           
																	<c:choose>
																		<%-- The object is of type LIST. --%>
																		<c:when test="${column.type == 'OBJECTLIST'}">
																			<%-- Get the accessor for the object and print a string representation. --%>
																			<c:set var="accessor" value="${column.accessor}" />
																			  
																			<td>
																				<c:forEach var="listElement" items="${element}" varStatus="listElementIndexStatus">
																					<%-- Objects in lists are always linked. --%>
																					<c:set var="linkScript"><itera:linkToElement name="listElement" type="js" /></c:set>
																					<c:set var="link"><itera:linkToElement name="listElement" type="html" /></c:set>
																						<span class="link" onclick="<c:out value="${linkScript}" />">
																							<itera:htmlLinkToElement link="${link}" isLinked="true"> 
																								<itera:write name="listElement" property="${accessor}" escapeXml="true" />
																							</itera:htmlLinkToElement>
																						</span>
																						<br/>
																				</c:forEach>
																			</td> 
																		</c:when>
																		<c:otherwise>
																			<td class="<c:out value="${linkStyle}"/>" onclick="<c:out value="${linkScript}" />">
																				<c:choose>
																					<c:when test="${column.type == 'OBJECT'}">
																					                       
																						<%-- Get the accessor for the object and print a string representation. --%>
																						<c:set var="accessor" value="${column.accessor}" />                                
																						<itera:define id="elementName" name="element" property="${accessor}"/>
																						
																						<c:choose>
																							<c:when test="${column.localized == true}">                                   
																								<fmt:message key="${elementName}"/>
																							</c:when>
																							<c:otherwise>
																								<itera:htmlLinkToElement link="${link}" isLinked="true"> 
																									<c:out value="${elementName}" />
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
	    
	    </c:if>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${functionalPermission == true}">
				<fmt:message key="reports.results.resultListIsEmtpy" />
			</c:when>
			<c:otherwise>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>