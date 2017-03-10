<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="showAddColumnButton" ignore="true" />
<tiles:useAttribute name="show_nettoexport" ignore="true" />

<%--
		@UsedFor 	Shows the report/query result 
		@UsedFrom	jsp\commonReporting\resultPages\GeneralResultPage.jsp
		@Note		
 --%>


<%-- 
Note that the Connection report result page does _not_ use this template!

List of keys of the column headers 
List of the fields of the entities to display. For inherited fields, use the non-inherited
field. The jsp will call <fieldname>Inherited to display the value. 
The type of the field. Must be one of 'name', 'name_inherited', 'description', 'date',
'typeofstatus'
 --%>

<itera:define id="visibleColumns" name="memBean" property="viewConfiguration.visibleColumns" />
<c:set var="result_elements_fields_size" value="${fn:length(visibleColumns)}" />

<c:if test="${resultPostSelection == null}">
	<c:set var="resultPostSelection" value="true"/>
</c:if>

<c:if test="${editColumn == null}">
    <c:set var="editColumn" value="false"/>
</c:if>

<c:choose>
	<c:when test="${resultPostSelection}">
		<c:set var="reportcolspan" value="${result_elements_fields_size+1}" />		
	</c:when>
	<c:otherwise>
		<c:set var="reportcolspan" value="${result_elements_fields_size}" />
	</c:otherwise>
</c:choose>

<c:set var="resultSize" value="${fn:length(memBean.results)}" />
<c:set var="thcount" value="${result_elements_fields_size}" />

<input type="hidden" name="currentColumnAction"/>
<input type="hidden" name="currentColumnName"/>

<div id="ReportingResultTableModule" class="row-fluid module">
	<div class="module-heading">
		<fmt:message key="reports.results" />:&nbsp;<c:out value="${resultSize}" />
        <c:if test="${show_nettoexport and not empty memBean.results and (fn:length(memBean.results) gt 0)}">
        <div class="btn-group pull-right">
          <div class="btn dropdown-toggle" data-toggle="dropdown" style="font-weight: normal; margin:-5px -5px 0 10px;">
            <i class="icon-download-alt"></i>
            <span><fmt:message key="reports.nettoexport.button"></fmt:message></span>
            <span class="caret"></span>
          </div>
          <ul class="dropdown-menu dropdown-right">
            <li>
              <a class="link" onclick="triggerNettoDownloadFlow('xlsx');">Excel (XLSX)</a>
            </li>
            <li>
              <a class="link" onclick="triggerNettoDownloadFlow('xls');">Excel 2003 (XLS)</a>
            </li>
            <li>
              <a class="link" onclick="triggerNettoDownloadFlow('csv');">CSV</a>
            </li>
          </ul>
        </div>
        </c:if>
		<c:if test="${not empty showAddColumnButton && showAddColumnButton eq true}">
  		<div style="float: right;">	
  			<a data-toggle="modal" href="#addColumnContainer" class="link btn" style="font-weight: normal; margin: -5px -5px 0 0;">
  				<i class="icon-plus" style="margin-right: 3px;"></i>
  				<fmt:message key="reports.addColumn.button" />
  			</a>
  		</div>
		</c:if>
	</div>
	<div class="row-fluid">
		<div class="module-body-table">
			<div class="row-fluid">
				<table id="ReportingResultTable" class="table table-striped table-condensed tableInModule">
					<thead>
						<tr>
							<c:if test="${resultPostSelection}">
								<th class="checkboxintable">
									<c:choose>
										<c:when test="${massUpdateMode == true}">
											<form:checkbox path="checkAllBox" id="checkAllBox" value="" onclick="checkUnCheckAll(document.getElementsByName('queryResult.selectedResultIds'), this);" />
										</c:when>
										<c:otherwise>
											<form:checkbox path="checkAllBox" id="checkAllBox" value="" onclick="checkUnCheckAll(document.getElementsByName('queryResult.selectedResultIds'), this);" />						
										</c:otherwise>
									</c:choose>
				                	<fmt:message key="reports.selectAll" />
								</th>
							</c:if>
							<c:forEach items="${visibleColumns}" var="column" varStatus="s">
				                <th class="checkboxintable">
				                	<c:choose>
				                		<c:when test="${column.type eq 'attribute'}">
				                			<c:out value="${column.head}" />
				                		</c:when>
				                		<c:otherwise>
				                			<fmt:message key="${column.head}" />
				                		</c:otherwise>
				                	</c:choose>
				                </th>
				            </c:forEach>
						</tr>
					</thead>
					<tbody>
						<c:if test="${thcount > 1 && editColumn}">
				        	<tr>
				                <c:if test="${resultPostSelection}">
				                    <td class="checkboxintable" id="ResultPageTile_cell_<c:out value='${entityIndex}' />"></td>
				                </c:if>
				                <c:forEach items="${visibleColumns}" var="column" varStatus="s">
				                    <td>
					                    <c:set var="thstyle" value="" />    
					                    <c:if test="${s.count != 1}">
						                	<img src="<c:url value="/images/SortArrowLeft.gif"/>" 
						                    	onclick="setHiddenField('currentColumnAction', 'moveLeft');
						                        	setHiddenField('currentColumnName', '<c:out value="${column.head}" />');
						                        	flowAction('updateColumn');"
						                            class="link" 
						                            alt="<fmt:message key='tooltip.moveLeft'/>"
						                            title="<fmt:message key='tooltip.moveLeft'/>" />
					                    </c:if>
					                    <a class="link" href="#"
					                        title="<fmt:message key='tooltip.remove'/>"
					                        onclick="setHiddenField('currentColumnAction', 'delete');
					                                 setHiddenField('currentColumnName', '<c:out value="${column.head}" />');
					                                 flowAction('removeColumn');" >
					                    	<i class="icon-remove"></i>
					                    </a>
					                    <c:if test="${s.count != thcount}">  
						                    <img src="<c:url value="/images/SortArrowRight.gif"/>" 
					                            onclick="setHiddenField('currentColumnAction', 'moveRight');
					                                     setHiddenField('currentColumnName', '<c:out value="${column.head}" />');
					                                     flowAction('updateColumn');"
					                            class="link" 
					                            alt="<fmt:message key='tooltip.moveRight'/>"
					                            title="<fmt:message key='tooltip.moveRight'/>" />
					                    </c:if>
									</td>
				                </c:forEach>
				            </tr>
						</c:if>
						<c:forEach items="${memBean.results}" var="entity" varStatus="entityIndexStatus">
							<c:set var="entityIndex" value="${entityIndexStatus.index}" />
							<tr>
								<c:if test="${resultPostSelection}">
									<td class="checkboxintable" nowrap="nowrap" align="center" valign="top" id="ResultPageTile_cell_<c:out value='${entityIndex}' />">
					                    <form:checkbox path="queryResult.selectedResultIds" cssClass="ResultPageTile_${entityIndex}" value="${entity.id}"
					                    	onclick="updateCheckAllBox(document.getElementsByName('queryResult.selectedResultIds'), document.getElementById('checkAllBox'));" /> 
					                </td>
								</c:if>
					
								<c:set var="borderstyle" value="borderRight" />
					            <c:set var="linkJavascript">
					            	<itera:linkToElement name="entity" type="js"/>
					            </c:set>
					            <c:set var="linkHtml">
					            	<itera:linkToElement name="entity" type="html"/>
					            </c:set>
					
								<%-- display content. For each type, put a css style in bean 'column_style' and the content
					             to be displayed in bean 'content' --%>
					
					            <c:forEach items="${visibleColumns}" var="column" varStatus="s">
									<c:if test="${s.count == result_elements_fields_size}">
										<c:set var="borderstyle" value="" />
									</c:if>
									<c:set var="nowrap" value='nowrap="nowrap"' />
									<c:set var="type" value="${column.type}" />
									<c:choose>
										<c:when test="${type=='name'}">
											<c:set var="column_style" value="name" />
											<c:set var="content">
												<c:out value="${entity[column.field]}" />
											</c:set>
										</c:when>
										<c:when test="${type=='name_inherited'}">
											<c:set var="column_style" value="completereleaseintable" />
											<c:set var="content">
												<c:out value="${entity[column.field]}" />
											</c:set>
										</c:when>
										<c:when test="${type=='description'}">
											<c:set var="column_style" value="descriptionintable" />
											<c:set var="content">
												<itera:write name="entity" property="${column.field}"
													breaksAndSpaces="true" plainText="true" truncateText="true" escapeXml="true" />
											</c:set>
											<c:set var="nowrap" value="" />
										</c:when>
										<c:when test="${type=='date'}">
											<c:set var="column_style" value="dateintable" />
											<fmt:message var="dateFormat" key="calendar.dateFormat"/>
					                        <itera:define id="date" name="entity" property="${column.field}" />
											<c:set var="content">
												<fmt:formatDate value="${date}" pattern="${dateFormat}"/>
											</c:set>
										</c:when>
										<c:when test="${type=='typeofstatus'}">
										    <c:set var="statusString">
					        					<c:out value="${entity[column.field]}" />
					      					</c:set>
											<c:set var="column_style" value="" />
											<c:set var="content">
												<fmt:message key="${statusString}" />
											</c:set>
										</c:when>
					                    <c:when test="${type=='connection'}">
					                        <c:set var="column_style" value="" />
					                        <c:set var="content">
					                        	<itera:write name="entity" property="${column.field}"
														breaksAndSpaces="true" escapeXml="true" />
					                        </c:set>
					                    </c:when>
					                    <c:when test="${type=='list'}">
					                        <c:set var="column_style" value="" />
					                        <c:set var="notFirst" value="false" />
					                        <c:set var="content">
					                            <c:forEach items="${entity[column.field]}" var="item" >
					                                <c:if test="${notFirst==true}"><c:out value=";<br/>" escapeXml="false" /></c:if>
					                                <c:set var="notFirst" value="true" />
					                                <c:out value="${item}" />
					                            </c:forEach>
					                        </c:set>
					                    </c:when>
					                    <c:when test="${type=='attribute'}">
					                        <c:set var="content">
					                      	<c:set var="assignedValuesPerAttributeCounter" value="${1}"/>
						                        <c:forEach var="attrValAss" items="${entity.attributeValueAssignments}">
						                        	<c:if test="${attrValAss.attributeValue.attributeType.id == column.field}">
							                            <c:if test="${(assignedValuesPerAttributeCounter > 1)}">
							                            	<%-- If there is more than one value assigned to this attribute type, 
							                                   place a semicolon in front of each subsequent value --%>
							                              	<c:out value=";<br/>" escapeXml="false" />
							                            </c:if> 
							                            <c:set var="assignedValuesPerAttributeCounter" value="${assignedValuesPerAttributeCounter + 1}"/>
							                            <c:choose>
															<c:when
																test="${attrValAss.attributeValue.attributeType.typeOfAttribute.name == 'attribute.type.text'}">
																<itera:write name="attrValAss" property="attributeValue.value"
																	plainText="true" truncateText="true" escapeXml="true" />
															</c:when>
															<c:when test="${attrValAss.attributeValue.attributeType.typeOfAttribute.name == 'attribute.type.responsibility'}">
							                             		<c:out value="${attrValAss.attributeValue.name}" />
							                              	</c:when>
							                              	<c:when test="${attrValAss.attributeValue.attributeType.typeOfAttribute.name == 'attribute.type.date'}">
							                              		<fmt:message var="dateFormat" key="calendar.dateFormat"/>
																<fmt:formatDate value="${attrValAss.attributeValue.value}" pattern="${dateFormat}"/>
							                              	</c:when>
							                              	<c:otherwise>
							                                	<c:out value="${attrValAss.attributeValue.value}" />
							                              	</c:otherwise>
							                            </c:choose>
						                          	</c:if>
						                        </c:forEach>
					                      	</c:set>
					                    </c:when>
					                    <c:when test="${type=='seal'}">
					                        <c:set var="column_style" value="" />
					                        <c:set var="content">
					                        	<c:set var="sealState" value="${entity.sealState.value}"/>
												<div id="sealBar">                        	
					                        		<ul>
														<c:choose>
															<c:when test="${sealState == 'seal.valid'}">
																<li id="sealBarValidSeal">
																	<span class="ToolText" onmouseover="this.className='ToolTextHover';" onMouseOut="this.className='ToolText';" >
																		<input type="button"/>
																		<span><fmt:message key="seal"/>: <fmt:message key="seal.valid"/></span>
																	</span>
																</li>
															</c:when>
															<c:when test="${sealState == 'seal.outdated'}">
																<li id="sealBarOutdatedSeal">
																	<span class="ToolText" onmouseover="this.className='ToolTextHover';" onMouseOut="this.className='ToolText';" >
																		<input type="button"/>
																		<span><fmt:message key="seal"/>: <fmt:message key="seal.outdated"/></span>
																	</span>
																</li>
															</c:when>
															<c:when test="${sealState == 'seal.invalid'}">
																<li id="sealBarInvalidSeal">
																	<span class="ToolText" onmouseover="this.className='ToolTextHover';" onMouseOut="this.className='ToolText';" >
																		<input type="button"/>
																		<span><fmt:message key="seal"/>: <fmt:message key="seal.invalid"/></span>
																	</span>
																</li>
															</c:when>
															<c:when test="${sealState == 'seal.notavailable'}">
																<li id="sealBarNotavailableSeal">
																	<span class="ToolText" onmouseover="this.className='ToolTextHover';" onMouseOut="this.className='ToolText';" >
																		<input type="button" />
																		<span><fmt:message key="seal"/>: <fmt:message key="seal.notavailable"/></span>
																	</span>
																</li>
															</c:when>
														</c:choose>
													</ul>
												</div>
					                        </c:set>
					                    </c:when>
					                    <c:when test="${type=='availableForInterfaces'}">
										    <c:set var="availableForInterfaces">
					        					<c:out value="${entity[column.field]}" />
					      					</c:set>
											<c:set var="column_style" value="" />
											<c:set var="content">
												<c:choose>
								        			<c:when test="${availableForInterfaces}">
								          			  <fmt:message key="global.yes" />
								        			</c:when>
											        <c:otherwise>
											          <fmt:message key="global.no" />
											        </c:otherwise>
								      			</c:choose>
											</c:set>
										</c:when>
										<c:otherwise>
											<c:set var="content">
												<c:out value="${entity[column.field]}" />
											</c:set>
										</c:otherwise>
									</c:choose>                
									<td <c:out value="${nowrap}" escapeXml="false"/>
										class="<c:out value="${borderstyle} ${column_style} link"/>"
					                    onclick="<c:out value="${linkJavascript}"/>">
					                    	<itera:htmlLinkToElement isLinked="true" link="${linkHtml}">
					                        	<c:out value="${content}" escapeXml="false" />
					                        </itera:htmlLinkToElement>
					                </td>
								</c:forEach>			            
							</tr>
						</c:forEach>
					</tbody>		
				</table>
			</div>
		</div>
	</div>
</div>

<%-- 
    this hidden field always transfers a value of -1 for selectedResultIds 
	even if no ResultsForMassUpdate is selected 
--%>
<c:if test="${resultPostSelection}">
	<%-- TODO: If this is needed for ResultsForMassUpdate later, it needs to be bound to the model
	     e.g. with form:hidden.! The value might be set with javascript, as it cannot be set directly? --%>
    <input name="test" type="hidden" value="-1" />
</c:if>
