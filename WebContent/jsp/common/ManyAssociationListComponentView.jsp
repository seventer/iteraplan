<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%-- Use Attribute sort_field defined in TabHierarchy.jsp of hierarchical building block--%>
<tiles:useAttribute name="sort_field" ignore="true" />

<%-- The path to the component model that provides the values for this page. --%>
<tiles:useAttribute name="path_to_componentModel" />

<%-- If set to true, the table rows are linked to the corresponding element's 
management page. Defaults to true. --%>
<tiles:useAttribute name="isLinked" ignore="true"/>

<%-- If set to true, delete icons will be suppressed on this page. Usefule for virt. elements --%>
<tiles:useAttribute name="disallowElementDeletion" ignore="true"/>

<%-- 
Optional list of css styles to be used for the fields. If this list is 
not specified, the column_header_keys are evaluated to reasonable defaults. 
--%>
<tiles:useAttribute name="connected_elements_field_styles" ignore="true"/>

<%-- 
CSS style to be used for the available elements select box.  
--%>
<tiles:useAttribute name="available_elements_field_style" ignore="true" />

<tiles:useAttribute name="dynamically_loaded" ignore="true" />

<%-- 
  Message Key to be passed on deletion of elements with Attribute Value Assignments

<tiles:useAttribute name="confirmDeleteAttributeValueAssignmentMessageKey" ignore="true"/>
<%--Set Default value 
<c:if test="${empty confirmDeleteAttributeValueAssignmentMessageKey}">
  <c:set var="confirmDeleteAttributeValueAssignmentMessageKey" value="global.confirmDeletionOfAttributeValueAssignments" />
</c:if>--%>

<itera:define id="available_elements_label" name="memBean" property="${path_to_componentModel}.availableElementsLabel"/>
<itera:define id="column_header_keys" name="memBean" property="${path_to_componentModel}.columnHeaderKeys"/>
<itera:define id="component_mode" name="memBean" property="${path_to_componentModel}.componentMode" />
<itera:define id="connected_elements" name="memBean" property="${path_to_componentModel}.connectedElements"/>
<itera:define id="connected_elements_fields" name="memBean" property="${path_to_componentModel}.connectedElementsFields"/>
<itera:define id="model" name="memBean" property="${path_to_componentModel}" />
<itera:define id="table_header_key" name="memBean" property="${path_to_componentModel}.tableHeaderKey"/>

<c:if test="${not empty sort_field}">
	<itera:define id="sortOrder" name="memBean" property="${sort_field}"/>
</c:if>

<%-- Set the variable for the HTML ID of this component. --%>
<c:set var="html_id"
	value="${path_to_componentModel}.htmlId" />

<c:set var="column_header_keys_size" value="${fn:length(column_header_keys)}" />

<c:set var="add_id_field" value="${path_to_componentModel}.elementIdToAdd"/>
<c:set var="available_elements_presentation" value="${path_to_componentModel}.availableElementsPresentation"/>
<c:set var="columns_size" value="${column_header_keys_size + 2}"/>

<%-- Determine, if table rows should be linked. --%>
<c:set var="linkShow" value="true" />
<c:set var="linkStyle" value="link" />
<c:if test="${not empty isLinked && !isLinked}">
	<c:set var="linkShow" value="false" />
	<c:set var="linkStyle" value="" />
</c:if>

<c:if test="${component_mode == 'READ'}">
  <c:set var="tdstyle" value="top margin" />
</c:if>

<c:set var="user_styles" value="false"/>
<c:if test="${not empty connected_elements_field_styles}">
  <c:set var="user_styles" value="true"/>
</c:if>

<c:if test="${empty available_elements_field_style}" >
  <c:set var="available_elements_field_style" value="name" />
</c:if>

<c:if test="${empty connected_elements && component_mode == 'READ'}">
  <c:set var="emptyStyle" value="empty"/>
</c:if>

<%-- ***************************** --%>
<%-- Start ManyAssociationListView --%>
<%-- ***************************** --%>
<input type="hidden" name="<c:out value="${path_to_componentModel}" />.selectedPosition" />
<input type="hidden" name="<c:out value="${path_to_componentModel}" />.action" />

<div id="<c:out value="${html_id}" />Module" class="row-fluid module">
	<div class="module-heading">
		<fmt:message key="${table_header_key}"/>
	</div>
	<div class="row-fluid">
		<div class="module-body-table">
			<div class="row-fluid">
				<table id="<c:out value="${html_id}" />" class="table table-striped table-condensed tableInModule">
					<c:choose>
					    <c:when test="${component_mode != 'READ'}">
						  	<colgroup>
								<col class="col-ico" />
								<col class="col-val" />
								<col class="col-desc" />
							</colgroup>
						</c:when>
						<c:otherwise>
							<colgroup>
								<col class="col-ico" />
								<col class="col-name" />
								<col class="col-desc" />
							</colgroup>
						</c:otherwise>
					</c:choose>
						<c:choose>
	  					
							<%--  	Display connected elements if component is 
						    	 	### not in READ mode ###
						     --%>
							<c:when test="${component_mode != 'READ'}">
						      
								<%-- Display the table-header if there are any connected elements --%>
						      	<c:if test="${not empty connected_elements}">
						      		<thead>
						        		<tr>
						          			<th>&nbsp;</th>
						          			<c:forEach items="${column_header_keys}" var="key">
						            			<th><fmt:message key="${key}" /></th>
						          			</c:forEach>
						          			<th><fmt:message key="global.order" /></th>         
						        		</tr>
						        	</thead>
						      	</c:if>
						      
						      	<tbody>
						      	
							      	<%-- Loop through the connected elements --%>
							      	<c:forEach items="${model.connectedElements}" var="connected" varStatus="status">
							        
							        	<%-- Store the onClick()-Handler if the elements should be linked --%>
							        	<c:set var="linkScript" value="" />
							        	<c:set var="link" value="" />
							        	<c:if test="${linkShow}">
							          		<c:set var="linkScript">
							            		<itera:linkToElement name="connected" type="js"/>
							          		</c:set>
							          		<c:set var="link">
							            		<itera:linkToElement name="connected" type="html"/>
							          		</c:set>
							        	</c:if>
							
							        	<%-- Display each connected element on a table row --%>
						        		<tr>
						          
						          			<%-- Display the 'Remove'-Icon --%>
						          			<td>
						            			<c:if test="${disallowElementDeletion != true}">
						          	  				<a id="<c:out value="${html_id}_${status.index}_remove" />" class="link" href="#"
									          	  		title="<fmt:message key="tooltip.remove"/>"
									                   	onclick="triggerAttributeValueAction(<c:out value="'${path_to_componentModel}', ${status.count}, 'remove'" />)" >
									                	<i class="icon-remove"></i>
						              				</a>
						            			</c:if>
						          			</td>
						          
						          			<%-- Loop through the fields to be displayed of each connected element --%>
						          			<c:forEach items="${connected_elements_fields}" var="field" varStatus="fieldsStatus">
						            			<c:choose>
						              				<c:when test="${user_styles}">
						                				<c:set var="style" value="${connected_elements_field_styles[fieldsStatus.index]}"/>
						              				</c:when>
						              				<c:when test="${column_header_keys[fieldsStatus.index] == 'global.description'}">
						                				<c:set var="style" value="descriptionintablelong"/>
						              				</c:when>
						              				<c:otherwise>
						                				<c:set var="style" value=""/>
						              				</c:otherwise>
						            			</c:choose>
						            			<td class="<c:out value="${style} ${linkStyle} top"/>" onclick="<c:out value="${linkScript}" />" >
						              				<itera:htmlLinkToElement link="${link}" isLinked="${linkShow}">
						                			<%-- Sets a plain text filter for WikiMarkup in description fields--%>
														<c:choose>
											              	<c:when test="${column_header_keys != null && column_header_keys[fieldsStatus.index] == 'global.description'}">
											              		<itera:write name="connected" property="${field}" breaksAndSpaces="true" plainText="true" truncateText="true" escapeXml="true" />
											              	</c:when>
											              	<c:otherwise>
											           			<itera:write name="connected" property="${field}" breaksAndSpaces="true" escapeXml="true" />
											              	</c:otherwise>
											        	</c:choose>
										            </itera:htmlLinkToElement>
						            			</td> 
						          			</c:forEach>
						          
						          			<%-- Display icons to sort the connected elements --%>
						          			<td>
									        	<img src="<c:url value="/images/SortArrowTop.gif" />" 
									              onclick="triggerAttributeValueAction('<c:out value="${path_to_componentModel}" />', <c:out value="${status.count}" />, 'moveTop')"
									              class="link" 
									              title="<fmt:message key="tooltip.moveTop" />"
									              alt="<fmt:message key="tooltip.moveTop" />"
									              id="<c:out value="${html_id}_${status.index}_moveTop" />" />
									            <img src="<c:url value="/images/SortArrowUp.gif"/>" 
									              onclick="triggerAttributeValueAction('<c:out value="${path_to_componentModel}" />', <c:out value="${status.count}" />, 'moveUp')"
									              class="link" 
									              title="<fmt:message key="tooltip.moveUp"/>" 
									              alt="<fmt:message key="tooltip.moveUp"/>" 
									              id="<c:out value="${html_id}_${status.index}_moveUp" />"/>
									            <img src="<c:url value="/images/SortArrowDown.gif"/>" 
									              onclick="triggerAttributeValueAction('<c:out value="${path_to_componentModel}" />', <c:out value="${status.count}" />, 'moveDown')"
									              class="link" 
									              title="<fmt:message key="tooltip.moveDown"/>" 
									              alt="<fmt:message key="tooltip.moveDown"/>" 
									              id="<c:out value="${html_id}_${status.index}_moveDown" />"/>                  
									            <img src="<c:url value="/images/SortArrowBottom.gif"/>" 
									              onclick="triggerAttributeValueAction('<c:out value="${path_to_componentModel}" />', <c:out value="${status.count}" />, 'moveBottom')"
									              class="link" 
									              title="<fmt:message key="tooltip.moveBottom"/>" 
									              alt="<fmt:message key="tooltip.moveBottom"/>" 
									              id="<c:out value="${html_id}_${status.index}_moveBottom" />"/>                  
									        </td>            
										</tr>
						      		</c:forEach>
						    </c:when>
					    
					    	<%-- 	Just display the connected elements if component is 
					    		### in READ mode ### 
					      	--%>
					    	<c:otherwise>
					      		<c:choose>
					        		<c:when test="${empty connected_elements}">
					        			<thead>
					          				<tr>
					            				<th colspan="<c:out value="${columns_size}" />">&nbsp;</th>
					          				</tr>
					          			</thead>
					        		</c:when>
					        		<c:otherwise>
					        			<thead>
						          			<tr>
						            			<th class="col-ico"><fmt:message key="global.number" /></th>
						            			<c:forEach items="${column_header_keys}" var="key">
						              				<th><fmt:message key="${key}" /></th>
						            			</c:forEach>
						            			<th>&nbsp;</th>
						          			</tr>
						          		</thead>
					        		</c:otherwise>
					      		</c:choose>
			            
			      				<tbody>
						    		<%-- Display each connected element on a table row --%>
								    <c:forEach items="${model.connectedElements}" var="connected" varStatus="status">
					        
					        			<%-- Store the onClick()-Handler if the elements should be linked --%>
					        			<c:set var="linkScript" value="" />
					        			<c:set var="link" value="" />
					        			<c:if test="${linkShow}">
					          				<c:set var="linkScript">
					            				<itera:linkToElement name="connected" type="js"/>
					          				</c:set>
					          				<c:set var="link">
					            				<itera:linkToElement name="connected" type="html"/>
					          				</c:set>
					        			</c:if>
					        
					        			<tr>
					          				<td onclick="<c:out value="${linkScript}" />" class="col-ico ${linkStyle}">
					            				<c:out value="${status.count}" />
					          				</td>
					            			<c:forEach items="${connected_elements_fields}" var="field" varStatus="fieldsStatus">
						              			<c:choose>
						                			<c:when test="${user_styles}">
						                  				<c:set var="style" value="${connected_elements_field_styles[fieldsStatus.index]}"/>
						                			</c:when>
						                			<c:when test="${column_header_keys[fieldsStatus.index] == 'global.description'}">
						                  				<c:set var="style" value="descriptionintablelong"/>
						                			</c:when>
						                			<c:otherwise>
						                  				<c:set var="style" value=""/>
						                			</c:otherwise>
						              			</c:choose>             
						              			<td class="<c:out value="${linkStyle}"/>" 
						                  			onclick="<c:out value="${linkScript}" />" >
						                			<itera:htmlLinkToElement link="${link}" isLinked="${linkShow}">
							                  			<%-- Sets a plain text filter for WikiMarkup in description fields--%>
														<c:choose>
									              			<c:when test="${column_header_keys != null && column_header_keys[fieldsStatus.index] == 'global.description'}">
									              				<itera:write name="connected" property="${field}" breaksAndSpaces="true" plainText="true" truncateText="true" escapeXml="true" />
									              			</c:when>
									              			<c:otherwise>
									           					<itera:write name="connected" property="${field}" breaksAndSpaces="true" escapeXml="true" />
									              			</c:otherwise>
									              		</c:choose>
						                			</itera:htmlLinkToElement>            
						              			</td>
					            			</c:forEach>
					          				<td class="<c:out value="${linkStyle}"/>" 
					              				onclick="<c:out value="${linkScript}" />" >&nbsp;</td>
					        			</tr>
					      			</c:forEach>
				    		</c:otherwise>
					  	</c:choose>
				  	
					  <%--
					  				Display the select box of available items and the 'Add'-Icon if the component is not in READ mode 
										Content of the select box as follows:
											- empty aside from the possibility of a pre-selected item if dynamically loaded
											- available elements if not dynamically loaded
										This and similar decisions made here by JSP c:choose and c:if blocks should be implemented in an own JSP-tag
										for better testability. The reason it hasn't been done yet is due to time constraints.
						--%>
						<c:if test="${component_mode != 'READ'}" >
		    				<tr>
		      					<td>
		      						<a id="<c:out value="${html_id}_add" />" class="link" href="#"
							        	title="<fmt:message key="tooltip.add"/>"
							        	onclick="setHiddenField('<c:out value="${path_to_componentModel}" />.action', 'add'); flowAction('update');" >
							        	<i class="icon-plus"></i>
		        					</a>
		      					</td>
		      					<td colspan="2">
		      				<c:if test="${not dynamically_loaded}">
										<itera:define id="selectableCollection" name="memBean" property="${available_elements_presentation}" />
									</c:if>
									<c:if test="${dynamically_loaded == true}">
										<itera:define id="bbtype" name="memBean" property="${path_to_componentModel}.typeOfBuildingBlock" />
									</c:if>
									<form:select path="${add_id_field}" id="${html_id}_select" cssClass="${available_elements_field_style} combobox" dynLoad="${dynamically_loaded}" bbtype="${fn:toLowerCase(bbtype)}" sort="${sortOrder}">
										<form:options items="${selectableCollection}" itemLabel="${available_elements_label}" itemValue="id" />
									</form:select>
		      					</td>
		      					<td></td>        
		    				</tr>    
		    
		    				<tr>
								<td></td>
								   	<td>
								   	  <label class="radio">
								     	<fmt:message key="manageAttributes.enum.sortOrder.ascending" var="sortOrderAscLabel"/>
								     	<form:radiobutton path="${sort_field}" value="asc" label="${sortOrderAscLabel}"/>
						              </label>
								      <label class="radio">
								    	<fmt:message key="manageAttributes.enum.sortOrder.descending" var="sortOrderDescLabel"/>
								    	<form:radiobutton path="${sort_field}" value="desc" label="${sortOrderDescLabel}"/>
								      </label>
									</td>
								 	<td>
								 		<input type="button" id="${html_id}_sortButton" name="sortButton" value="<fmt:message key="button.sort" />" 
								 	         onclick="flowAction('sort');" class="btn"/>		 	  
								   	</td>
								   	<td colspan="<c:out value="${columns_size-3}" />">&nbsp;<form:errors path="${sort_field}" cssClass="errorMsg" htmlEscape="false"/></td>
							  </tr>
						</c:if>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>