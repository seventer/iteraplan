<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>


<tiles:useAttribute name="addManageShortcuts" ignore="true" />

<tiles:useAttribute name="entityNamePluralKey" />

<tiles:useAttribute name="resultColumnDefinitions" ignore="true" />




<c:set var="result_column_count"
	value="${fn:length(resultColumnDefinitions)}" />

<c:choose>
	<%-- case 1: no search results --%>
	<c:when
		test="${empty resultList || (dialogMemory.pageSize == null) || (dialogMemory.pageSize == 0)}">
		<div>
			<fmt:message key="search.noResults" />
		</div>
	</c:when>
	<%-- case 2: at least one search result; first, compute all relevant numbers --%>
	<c:when
		test="${not empty resultList && not (dialogMemory.pageSize == null) && not (dialogMemory.pageSize == 0) }">
		<%-- rounding is ok here, because there'll be no fractional part --%>
		<c:set var="currentPageNum"
			value="${(dialogMemory.pageStart div dialogMemory.pageSize) + 1}" />
		<c:set var="allPagesNum"
			value="${((resultList.nrOfElements - (resultList.nrOfElements  mod dialogMemory.pageSize)) div dialogMemory.pageSize) + 1}" />
		<c:if
			test="${(resultList.nrOfElements mod dialogMemory.pageSize) eq 0}">
			<c:set var="allPagesNum" value="${allPagesNum - 1}" />
		</c:if>
		<c:if test="${currentPageNum lt 1 || allPagesNum lt 1}">
			<c:set var="currentPageNum" value="1" />
			<c:set var="allPagesNum" value="1" />
		</c:if>

		<c:set var="updatePermissionType">
			<itera:write name="userContext"
				property="perms.userHasBbTypeUpdatePermission(${bbt.typeOfBuildingBlock.value})" escapeXml="false" />
		</c:set>
		<c:set var="createPermissionType">
			<itera:write name="userContext"
				property="perms.userHasBbTypeCreatePermission(${bbt.typeOfBuildingBlock.value})" escapeXml="false" />
		</c:set>
		<c:set var="deletePermissionType">
			<itera:write name="userContext"
				property="perms.userHasBbTypeDeletePermission(${bbt.typeOfBuildingBlock.value})" escapeXml="false" />
		</c:set>
		<c:set var="hasPermissionForActions"
			value="${createPermissionType || updatePermissionType || deletePermissionType}" />

		<div id="ResultTableModule" class="row-fluid module">
			<div class="module-heading-nopadding">
				<div style="padding: 8px 0px; float: left;">
					<c:if test="${addManageShortcuts == true}">
						<c:if test="${hasPermissionForActions}">
							<c:set var="result_column_count" value="${result_column_count+1}" />
						</c:if>
					</c:if>
					<c:set var="found">
						<fmt:message key="search.found.header" />
					</c:set>
					<c:out value=" ${resultList.nrOfElements} " />
					<fmt:message key="${entityNamePluralKey}" />
					<c:out value=" ${fn:toLowerCase(found)} " />
				</div>
				<div style="float: right; margin-right: 8px; font-weight: normal;">
					
					<form:select id="pageSize"
						style="margin-top: 4px; margin-right: 8px;" path="pageSize"
						onchange="setHiddenField('pageStart', 0); self.document.forms[0].submit();">
						<spring:eval var="resultCount1"
						expression="@applicationProperties.getProperty('searchresults.option.1')" />
						<spring:eval var="resultCount2"
						expression="@applicationProperties.getProperty('searchresults.option.2')" />
						<spring:eval var="resultCount3"
						expression="@applicationProperties.getProperty('searchresults.option.3')" />
						<spring:eval var="defaultResultCount"
						expression="@applicationProperties.getProperty('searchresults.default.count')" />
						<fmt:message key="search.perPage" var="perPage" />
						<form:option label="${resultCount1} ${perPage}"
							value="${resultCount1}" />
						<form:option label="${resultCount2} ${perPage}"
							value="${resultCount2}" />
						<form:option label="${resultCount3} ${perPage}"
							value="${resultCount3}" />

						<fmt:message key="search.option.allresults" var="allLabel" />
						<form:option label="${allLabel}" value="-1" />
					</form:select>
                    <c:if test="${(not empty bbt) and (not empty resultList.source) and (fn:length(resultList.source) gt 0)}">
				    <div class="btn-group pull-right">
                      <div class="btn dropdown-toggle" data-toggle="dropdown" style="margin-left: 7px; margin-top: 4px;">
                        <i class="icon-download-alt"></i>
                        <span><fmt:message key="reports.nettoexport.button"></fmt:message></span>
                        <span class="caret"></span>
                      </div>
                      <ul class="dropdown-menu dropdown-right">
                        <li>
                          <a class="link" onclick="triggerNettoDownload('xlsx');">Excel (XLSX)</a>
                        </li>                      
                        <li>
                          <a class="link" onclick="triggerNettoDownload('xls');">Excel 2003 (XLS)</a>
                        </li>
                         <li>
              				<a class="link" onclick="triggerNettoDownloadFlow('csv');">CSV</a>
           				</li>                      
                      </ul>
                    </div>
                    </c:if>
					<c:if test="${dialogMemory.hierarchical}">
						<div class="pull-right">
							<button type="button" class="btn"
								onclick="createHiddenField('showTreeView', 'true'); self.document.forms[0].submit();"
								style="margin-left: 7px; margin-top: 4px;">
								<i class="icon-eye-open"></i>
								<fmt:message key="reports.tree" />
							</button>
						</div>
					</c:if>
					<c:if
						test="${fn:length(dialogMemory.tableState.availableColumnDefinitions) gt 0}">
						<div class="pull-right">
							<button data-toggle="modal" href="#addColumnContainer"
								class="link btn" style="margin-left: 7px; margin-top: 4px;">
								<i class="icon-plus" style="margin-right: 3px;"></i>
								<fmt:message key="reports.addColumn.button" />
							</button>
						</div>
					</c:if>
					<div class="pagination" style="float: right;">
						<ul>
							<c:if test="${dialogMemory.pageStart gt 0}">
								<li><a href="#"
									onclick="setHiddenField('pageStart', <c:out value="${dialogMemory.pageStart - dialogMemory.pageSize}" />);setHiddenField('currentPageNumber', '<fmt:formatNumber value="${currentPageNum - 1.0}" maxFractionDigits="0"/>');setHiddenField('nextPageToShow', true);setHiddenField('previousPage', 'previous');setHiddenField('nextPage', '');document.forms[0].submit();">«</a>
								</li>
							</c:if>
							<li class="active"><a href="#"><fmt:formatNumber
										value="${currentPageNum}" maxFractionDigits="0" /></a></li>
							<c:if test="${currentPageNum lt allPagesNum}">
								<li><a href="#"
									onclick="setHiddenField('pageStart', <c:out value="${dialogMemory.pageStart + dialogMemory.pageSize}" />); setHiddenField('nextPage', 'next'); setHiddenField('nextPageToShow', true); setHiddenField('currentPageNumber', '<fmt:formatNumber value="${currentPageNum - 1.0}" maxFractionDigits="0"/>');setHiddenField('previousPage', '');document.forms[0].submit();">»</a>
								</li>
							</c:if>
						</ul>
					</div>
				</div>

			</div>
			<div class="row-fluid">
				<div class="module-body-table">
					<div class="row-fluid">
						<table id="resultTable"
							class="table table-striped table-condensed tableInModule">
							<thead>
								<tr>
									<c:forEach items="${resultColumnDefinitions}" var="columnProps"
										varStatus="s">
										<th class="link sortable"
											onclick="sortByColumn('${s.count-1}')"><span
											style="white-space: nowrap;"> <c:choose>
													<c:when test="${columnProps.attributeType ne null}">
														<c:out value="${columnProps.tableHeaderKey}" />
													</c:when>
													<c:otherwise>
														<fmt:message key="${columnProps.tableHeaderKey}" />
													</c:otherwise>
												</c:choose> <c:choose>
													<c:when
														test="${resultList.sort.ascending == true && resultList.sort.property == columnProps.beanPropertyPath}">
                            &nbsp;<i class="icon-chevron-up"></i>
													</c:when>
													<c:when
														test="${resultList.sort.ascending == false && resultList.sort.property == columnProps.beanPropertyPath}">
                            &nbsp;<i class="icon-chevron-down"></i>
													</c:when>
												</c:choose>
										</span> <span class="dontwrap" style="margin-left: 7px;"> <c:if
													test="${s.count gt 1}">
													<img src="<c:url value="/images/SortArrowLeft.gif"/>"
														onclick="createHiddenField('colMoveIndex', '${s.count-1}'); createHiddenField('colMoveDirection', 'left'); self.document.forms[0].submit();"
														class="link" alt="<fmt:message key='tooltip.moveLeft'/>"
														title="<fmt:message key='tooltip.moveLeft'/>" />
												</c:if> <a class="link" href="#"
												title="<fmt:message key='tooltip.remove'/>"
												onclick="createHiddenField('colRemoveIndex', '${s.count-1}'); self.document.forms[0].submit();">
													<i class="icon-remove"></i>
											</a> <c:if
													test="${s.count lt fn:length(resultColumnDefinitions)}">
													<img src="<c:url value="/images/SortArrowRight.gif"/>"
														onclick="createHiddenField('colMoveIndex', '${s.count-1}'); createHiddenField('colMoveDirection', 'right'); self.document.forms[0].submit();"
														class="link" alt="<fmt:message key='tooltip.moveRight'/>"
														title="<fmt:message key='tooltip.moveRight'/>" />
												</c:if>
										</span></th>
									</c:forEach>
									<c:if test="${addManageShortcuts == true}">
										<c:if test="${hasPermissionForActions}">
											<th class="actionsRow"><fmt:message key="global.manage" />
											</th>
										</c:if>
									</c:if>
								</tr>
							</thead>

							<tbody>
								<c:forEach var="resultItem" items="${resultList.pageList}">

									<tr>
										<%-- Stores the destination if the elements should be linked --%>
										<c:set var="link" value="" />
										<%-- iterate over result item columns to fetch their properties --%>
										<c:forEach var="columnProps"
											items="${resultColumnDefinitions}">
											<c:choose>
												<c:when
													test="${columnProps.pathToLinkedElement != null && columnProps.pathToLinkedElement != ''}">
													<%-- the link points to a property of the element --%>
													<c:set var="linkScript">
														<itera:linkToElement name="resultItem"
															property="${columnProps.pathToLinkedElement}" type="js" />
													</c:set>
													<c:set var="linkStyle" value="link" />
													<c:set var="link">
														<itera:linkToElement name="resultItem"
															property="${columnProps.pathToLinkedElement}" type="html" />
													</c:set>
												</c:when>
												<c:when
													test="${columnProps.pathToLinkedElement != null && columnProps.pathToLinkedElement == ''}">
													<%-- the link points to the element itself --%>
													<c:set var="linkScript">
														<itera:linkToElement name="resultItem" type="js" />
													</c:set>
													<c:set var="linkStyle" value="link" />
													<c:set var="link">
														<itera:linkToElement name="resultItem" type="html" />
													</c:set>
												</c:when>
												<c:otherwise>
													<c:set var="linkScript" value="" />
													<c:set var="linkStyle" value="" />
													<c:set var="link" value="" />
												</c:otherwise>
											</c:choose>
											<%-- End onclick()-Handler creation. --%>


											<td class="${linkStyle}" onclick="${linkScript}"><c:set
													var="linklength" value="${fn:length(link)}" /> <c:choose>
													<c:when test="${columnProps.attributeType ne null}">
														<c:set var="output">
															<itera:define id="attributeValues" name="resultItem"
																property="${columnProps.modelPath}" />

															<c:if
																test="${attributeValues ne null && fn:length(attributeValues) gt 0}">
																<c:set var="attributeValuesCounter" value="${1}" />
																<c:forEach items="${attributeValues}"
																	var="attributeValue">
																	<c:if test="${(attributeValuesCounter > 1)}">
																		<c:out value="; " />
																	</c:if>
																	<c:choose>
																		<c:when
																			test="${columnProps.attributeType.typeOfAttribute.name eq 'attribute.type.date'}">
																			<fmt:message var="dateFormat"
																				key="calendar.dateFormat" />
																			<fmt:formatDate value="${attributeValue.value}"
																				pattern="${dateFormat}" />
																		</c:when>
																		<c:when
																			test="${columnProps.attributeType.typeOfAttribute.name == 'attribute.type.text'}">
																			<itera:write name="attributeValue" property="value"
																				plainText="true" truncateText="true" escapeXml="false" />
																		</c:when>
																		<c:otherwise>
																			<c:out value="${attributeValue.value}"
																				escapeXml="true"  />
																		</c:otherwise>
																	</c:choose>
																	<c:set var="attributeValuesCounter"
																		value="${attributeValuesCounter + 1}" />
																</c:forEach>
															</c:if>
														</c:set>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${columnProps.modelPath == 'direction'}">
																<tiles:insertTemplate
																	template="/jsp/common/DirectionIcon.jsp">
																	<tiles:putAttribute name="directionString"
																		value="${resultItem.direction}" />
																</tiles:insertTemplate>
																<c:set var="output" value="" />
															</c:when>
															<c:when test="${columnProps.internationalized}">
																<c:set var="output">
																	<fmt:message>
																		<itera:write name="resultItem"
																			property="${columnProps.modelPath}" escapeXml="false" />
																	</fmt:message>
																</c:set>
															</c:when>
															<c:when
																test="${columnProps.tableHeaderKey != null && columnProps.tableHeaderKey == 'global.description'}">
																<c:set var="output">
																	<itera:write name="resultItem" plainText="true"
																		truncateText="true"
																		property="${columnProps.modelPath}" escapeXml="false" />
																</c:set>
															</c:when>
															<c:otherwise>
																<c:set var="output">
																	<itera:write name="resultItem"
																		property="${columnProps.modelPath}" escapeXml="false" />
																</c:set>
															</c:otherwise>
														</c:choose>
													</c:otherwise>
												</c:choose> <span
												class="<c:if test='${columnProps.rightAligned}'>pull-right</c:if>">
													<itera:htmlLinkToElement link="${link}"
														isLinked="${linklength != 0}">
														<c:out value="${output}" />
													</itera:htmlLinkToElement>
											</span></td>
										</c:forEach>
										<c:if test="${addManageShortcuts == true}">
											<c:if test="${hasPermissionForActions}">
												<td style="vertical-align: middle; white-space: nowrap;">
													<tiles:insertTemplate template="BuildingBlockManageActions.jsp">
														<tiles:putAttribute name="bb" value="${resultItem}"/>
														<tiles:putAttribute name="updatePermissionType" value="${updatePermissionType}" />
														<tiles:putAttribute name="createPermissionType" value="${createPermissionType}" />
														<tiles:putAttribute name="deletePermissionType" value="${deletePermissionType}" />
													</tiles:insertTemplate>
												</td>
											</c:if>
										</c:if>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</c:when>
</c:choose>

<div class="errorMsg">
	<form:errors path="*" />
</div>

<%-- (initially hidden) form for column adding --%>
<tiles:insertTemplate template="/jsp/common/ShowAddColumn.jsp">
	<tiles:putAttribute name="availableColumnDefinitions"
		value="${dialogMemory.tableState.availableColumnDefinitions}" />
</tiles:insertTemplate>

<div id="stickyTableHeaderBar" class="unvis">
	<c:forEach items="${resultColumnDefinitions}" var="columnProps"
		varStatus="s">
		<div id="stickyTableHeaderBar_${s.count}">
			<span style="white-space: nowrap;"> <c:choose>
					<c:when test="${columnProps.attributeType ne null}">
						<c:out value="${columnProps.tableHeaderKey}" />
					</c:when>
					<c:otherwise>
						<fmt:message key="${columnProps.tableHeaderKey}" />
					</c:otherwise>
				</c:choose>
			</span>
		</div>
	</c:forEach>
	<c:if test="${addManageShortcuts == true}">
		<c:if test="${hasPermissionForActions}">
			<div
				id="stickyTableHeaderBar_${fn:length(resultColumnDefinitions)+1}">
				<fmt:message key="global.manage" />
			</div>
		</c:if>
	</c:if>
</div>