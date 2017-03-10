<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%-- The path to the component model that provides the values for this page. --%>
<tiles:useAttribute name="path_to_componentModel" />

<%-- An alternative path to the connected elements. --%>
<tiles:useAttribute name="path_to_connected_elements" ignore="true" />

<%-- If set to true, the table rows are linked to the corresponding element's management page. Defaults to true. --%>
<tiles:useAttribute name="isLinked" ignore="true" />

<%-- If set to true, the table rows are linked to the corresponding element's management page. Defaults to true. --%>
<tiles:useAttribute name="askBeforeDelete" ignore="true" />

<%-- Message Key to be passed on deletion of elements with Attribute Value Assignments --%>
<tiles:useAttribute name="confirmDeleteAttributeValueAssignmentMessageKey" ignore="true" />
<%--Set Default value --%>
<c:if test="${empty confirmDeleteAttributeValueAssignmentMessageKey}">
	<c:set var="confirmDeleteAttributeValueAssignmentMessageKey" value="global.confirmDeletionOfAttributeValueAssignments" />
</c:if>

<%-- Optional list of CSS styles to be used for the fields of the connected elements. If not specified, default values are assumed. --%>
<tiles:useAttribute name="connected_elements_field_styles" ignore="true" />

<%-- 
  This attribute contains a list of strings that act as the label and value 
  (internationalized) for a select box. The select box is displayed next to 
  the combobox of available elements. Furthermore the last entry for each 
  connected_elements_fields is internationalized because it is assumed that 
  it is the value that was set by the combobox when the element was added. 
--%>
<tiles:useAttribute name="combobox_label_values" ignore="true" />

<%-- The field in which the selected value of the combobox is stored. See combobox_label_values attribute. --%>
<tiles:useAttribute name="combobox_field" ignore="true" />

<%-- String appended to the header of the table when in READ mode. --%>
<tiles:useAttribute name="header_postfix_read" ignore="true" />

<%-- String appended to the header of the table when in EDIT mode. --%>
<tiles:useAttribute name="header_postfix_edit" ignore="true" />

<%-- 
  If set, this attribute contains a message key for an info message 
  to show after the header row. This can be used e.g. to inform the 
  user about missing permissions. 
--%>
<tiles:useAttribute name="info_message_key" ignore="true" />

<%-- 
  If set, this attribute contains a message key for an info message 
  to show if there are no connected elements to display in READ mode. 
  This can be used e.g. to inform the user about missing permissions. 
--%>
<tiles:useAttribute name="info_message_key_empty" ignore="true" />

<%-- 
  If set, this attribute contains the component mode that is to be 
  used for the rendering of this view. If not set, the currently set 
  component mode is used as a default. This can be handy to force a 
  certain rendering. 
--%>
<tiles:useAttribute name="force_component_mode" ignore="true" />

<%-- Set the variable for the HTML ID of this component. --%>
<itera:define id="html_id" name="memBean" property="${path_to_componentModel}.htmlId" />

<%-- Set the variables for the hidden fields into which the IDs to remove/add are to be placed. --%>
<c:set var="add_id_field" value="${path_to_componentModel}.elementIdsToAdd" />
<c:set var="remove_id_field" value="${path_to_componentModel}.elementIdsToRemove" />
<c:set var="remove_id_field_array" value="${path_to_componentModel}.elementIdToRemoveArray" />

<c:set var="available_elements" value="${path_to_componentModel}.availableElements" />

<%-- Set the variables for the table header. --%>
<itera:define id="table_header_key" name="memBean" property="${path_to_componentModel}.tableHeaderKey" />

<%-- Set the variables for the column headers of this component. --%>
<itera:define id="column_header_keys" name="memBean" property="${path_to_componentModel}.columnHeaderKeys" />
<c:set var="column_header_keys_size" value="${fn:length(column_header_keys)}" />

<%-- Set the variables for the available elements. --%>
<itera:define id="available_elements_label" name="memBean" property="${path_to_componentModel}.availableElementsLabel" />
<%-- 
  If this attribute is not null, the list 'available_elements_presentation' is assumed to be an 
  array of lists which groups the available elements in some fashion. This attribute is a list of 
  keys that mark these groups in the select box. Note that when this attribute is set, the attribute
  'lookup_available_lables_mode attribute' is ignored. 
--%>
<itera:define id="available_elements_presentation_group_keys" name="memBean" property="${path_to_componentModel}.availableElementsPresentationGroupKeys" />
<c:set var="available_elements_presentation" value="${path_to_componentModel}.availableElementsPresentation" />

<itera:define id="available_elements_presentationGroupKeys" name="memBean" property="${path_to_componentModel}.availableElementsPresentationGroupKeys" />
<c:if test="${not empty available_elements_presentationGroupKeys}">
	<c:set var="available_elements_presentation" value="${path_to_componentModel}.availableElementsPresentationGrouped" />
</c:if>

<%-- Set the variables for the connected elements. --%>
<c:set var="connected_elements" value="${path_to_componentModel}.connectedElements" />
<itera:define id="connected_elements_fields" name="memBean" property="${path_to_componentModel}.connectedElementsFields" />
<itera:define id="connected_elements_list" name="memBean" property="${connected_elements}" />
<c:set var="connected_elements_size" value="${fn:length(connected_elements_list)}" />
<c:if test="${path_to_connected_elements != null}">
	<c:set var="connected_elements" value="${path_to_connected_elements}" />
</c:if>

<%-- Set the variables for the look up of internationalized values. --%>
<%-- A list of boolean values that specify which of the connected element fields should be internationalised.--%>
<itera:define id="lookup_lables_mode" name="memBean"
	property="${path_to_componentModel}.lookupLablesMode" />
<itera:define id="lookup_available_lables_mode" name="memBean"
	property="${path_to_componentModel}.lookupAvailableLablesMode" />

<%-- Set the variables for the additional select box. --%>
<c:catch>
	<itera:define id="combobox_label_values" name="memBean" property="${path_to_componentModel}.comboboxLabelValues" />
</c:catch>
<c:if test="${not empty combobox_label_values}">
	<c:set var="combobox_field" value="${path_to_componentModel}.comboboxField" />
</c:if>

<%-- Determine, if table rows should be linked. --%>
<c:set var="linkShow" value="true" />
<c:set var="linkStyle" value="link" />
<c:if test="${not empty isLinked && !isLinked}">
	<c:set var="linkShow" value="false" />
	<c:set var="linkStyle" value="" />
</c:if>

<%-- Determine, if the user provided styles for the fields of the connected elements. --%>
<c:set var="user_styles" value="false" />
<c:if test="${not empty connected_elements_field_styles}">
	<c:set var="user_styles" value="true" />
</c:if>

<%-- Determine the effective component mode of this component. --%>
<c:set var="initial_component_mode" value="${componentMode}" />
<c:set var="component_mode" value="${initial_component_mode}" />
<c:if test="${force_component_mode != null}">
	<c:set var="component_mode" value="${force_component_mode}" />
</c:if>

<c:if test="${connected_elements_size == 0 && component_mode == 'READ'}">
	<c:set var="emptyStyle" value="empty" />
</c:if>

<c:set var="postfix" value="" />
<c:if test="${component_mode == 'READ' && not empty header_postfix_read}">
	<c:set var="postfix" value="${header_postfix_read}" />
</c:if>
<c:if test="${component_mode != 'READ' && not empty header_postfix_edit}">
	<c:set var="postfix" value="${header_postfix_edit}" />
</c:if>

<c:set var="addCol" value="0" />
<c:if test="${component_mode != 'READ'}">
	<c:set var="addCol" value="2" />
</c:if>


<%-- ************************* --%>
<%-- Start ManyAssociationSetView --%>
<%-- ************************* --%>

<div id="ManyAssociationSetViewModul" class="row-fluid module">
	<div class="module-heading">
		<fmt:message key="${table_header_key}" />
	</div>
	<div class="row-fluid">
		<div class="module-body-table">
			<div class="row-fluid">
				<table class="table table-striped table-condensed tableInModule">
					<!--  <c:out value="ComponentMode = ${component_mode}"/>  -->
					<c:choose>
						<c:when test="${component_mode != 'READ'}">
							<colgroup>
								<col class="col-att" />
								<col class="col-ico" />
								<col class="col-att" />
							</colgroup>
						</c:when>
						<c:otherwise>
							<colgroup>
								<col class="col-name" />
								<%-- <col class="col-desc" /> --%>
							</colgroup>
						</c:otherwise>
					</c:choose>
					<itera:define id="connected_elements_array" name="memBean" property="${connected_elements}" />
					<c:forEach items="${connected_elements_array}" var="connected_element" varStatus="countStatus">
						<%-- Store the onClick()-Handler if the elements should be linked --%>
						<c:set var="linkScript" value="" />
						<c:if test="${linkShow}">
							<c:set var="linkScript"><itera:linkToElement name="connected_element" type="js" /></c:set>
						</c:if>
						<%-- Display each connected element on a table row--%>
					</c:forEach>
			
					<%-- Shows the info-message if possible --%>
					<c:if test="${info_message_key != null}">
						<tr>
							<td colspan="<c:out value="${column_header_keys_size + addCol + 1}"/>">
								<div class="helpText"><c:out value="${info_message_key}" /></div>
							</td>
						</tr>
					</c:if>
					<thead>
						<%-- Shows the table head if some connected elements exists (for explanation) --%>
						<c:if test="${connected_elements_size > 0 && component_mode == 'READ'}">
							<tr>
								<c:forEach items="${column_header_keys}" var="column_header_key" varStatus="countStatus">
									<th><fmt:message key="${column_header_key}" /></th>
								</c:forEach>
							</tr>
						</c:if>
				
						<%-- Shows a empty Table-Row if no connected elements exist  --%>
						<c:if test="${connected_elements_size <= 0 && component_mode == 'READ'}">
							<tr>
								<th colspan="<c:out value="${column_header_keys_size + addCol}" />">
									<c:choose>
										<c:when test="${info_message_key != null}">
											<c:out value="${info_message_key_empty}" />
										</c:when>
										<c:otherwise>
						            		&nbsp;
						          		</c:otherwise>
									</c:choose>
								</th>
							</tr>
						</c:if>
						<c:if test="${component_mode == 'READ'}">
							</thead>
							</tbody>
							<itera:define id="connected_elements_array" name="memBean" property="${connected_elements}" />
							<c:forEach items="${connected_elements_array}" var="connected_element" varStatus="countStatus">
								<c:set var="linkScript" value="" />
								<c:if test="${linkShow}">
									<c:set var="linkScript"><itera:linkToElement name="connected_element" type="js"/></c:set>
									<c:set var="link"><itera:linkToElement name="connected_element" type="html"/></c:set>
								</c:if>
								<tr>
				
									<c:set var="nowrap" value='nowrap="nowrap"' />
									<c:set var="connected_elements_fields_size" value="${fn:length(connected_elements_fields)}" />
									<c:forEach items="${connected_elements_fields}" var="connected_element_field" varStatus="cntStatus">
										<c:set var="count" value="${cntStatus.index}" />
										<c:if test="${count > 0}">
											<c:set var="nowrap" value="" />
										</c:if>
										<c:choose>
											<c:when test="${user_styles}">
												<c:set var="style"
													value="${connected_elements_field_styles[count]}" />
											</c:when>
											<c:when
												test="${column_header_keys[count] == 'global.description'}">
												<c:set var="style" value="descriptionintablelong" />
											</c:when>
											<c:otherwise>
												<c:set var="style" value="" />
											</c:otherwise>
										</c:choose>
				
										<c:set var="outputText">
											<c:choose>
												<c:when test="${not empty combobox_label_values && count == (connected_elements_fields_size - 1)}">
													<fmt:message>
														<itera:write name="connected_element" property="${connected_element_field}" escapeXml="true" />
													</fmt:message>
												</c:when>
				
												<c:when test="${lookup_lables_mode != null && lookup_lables_mode[count]}">
													<fmt:message>
														<itera:write name="connected_element" property="${connected_element_field}" escapeXml="true" />
													</fmt:message>
												</c:when>
				
												<c:when test="${column_header_keys[count] == 'global.start' || column_header_keys[count] == 'global.finish'}">
													<fmt:formatDate value="${connected_element[connected_element_field]}" dateStyle="short" />
												</c:when>
				
												<c:otherwise>
													<%-- Sets a plain text filter for WikiMarkup in description fields--%>
													<c:choose>
										              	<c:when test="${column_header_keys != null && column_header_keys[count] == 'global.description'}">
										              		<itera:write name="connected_element" property="${connected_element_field}" breaksAndSpaces="true" plainText="true" truncateText="true" escapeXml="false" />
										              	</c:when>
										              	<c:otherwise>
										           			<itera:write name="connected_element" property="${connected_element_field}" breaksAndSpaces="true" escapeXml="true" />
										              	</c:otherwise>
										              </c:choose>
												</c:otherwise>
											</c:choose>
										</c:set>
				
										<td class="<c:out value="${style} ${linkStyle}"/>" onclick="<c:out value="${linkScript}" />"
											<c:out value="${nowrap}" escapeXml="false"/>>
											<itera:htmlLinkToElement link="${link}" isLinked="${linkShow}">
												<c:out value="${outputText}" escapeXml="false" />
											</itera:htmlLinkToElement>
				                        </td>
									</c:forEach>
									<%-- <td class="<c:out value="${linkStyle}"/>" onclick="<c:out value="${linkScript}" />">&nbsp;</td> --%>
								</tr>
							</c:forEach>
						</c:if>
				
						<c:if test="${component_mode != 'READ'}">
							<tr>
								<th>
									<fmt:message key="global.selectedElements" />
				        
				                    <%-- Set the Javascript to call when pressing a button. This Javascript will select the Mutliselectbox-entrys to persist the changes made in them. --%>
				                    <c:set var="jsMethodLeftToRight">moveSelection2('${html_id}_connected', '${html_id}_available');</c:set>
				                    <c:set var="jsMethodRightToLeft">moveSelection2('${html_id}_available', '${html_id}_connected');</c:set>
				          
				                    <%-- Add decorations to all items with onclick handler, so that the multi-select boxes are selected before submission --%>
				                    <script type="text/javascript">// <![CDATA[
					                    $(window).load(function() {
					                    	<%-- get all DOM nodes with an onclick handler
					                          -- ideally, we can even use the selector [onclick*="flowAction"] to catch only those handlers 
					                             which contain the string "flowAction"; however, it breaks in IE... --%>
					                       	var nodes = null;
					                       	if ($.browser.msie && $.browser.version.substr(0,1) < 8) {
					                        	nodes = $('[onclick]');
					                       	} else {
					                        	nodes = $('[onclick*="flowAction"], [onclickHandlerHasBeenReplaced=true]');
					                       	}
					                       	nodes.each(function(idx, node) {
					                        	<%-- for IE, hacky: skip all those nodes where the ID starts with element_add or element_remove (the two arrow buttons).
					                        		All other browsers already filtered correctly with jQuery above...
					                              	These two should not be connected with the 'mark-all' handler --%>  
					                         	if ((node.id.search(/^element_add_.+$/) != -1) || (node.id.search(/^element_remove_.+$/) != -1)) {
					                           		return;
					                         	}
					                         	<%-- save previous handler, to append it later on; mark all nodes with a property, so that even IE8 can later find these nodes again when this tile is included the second time --%>
					                         	var prevHandler = node.onclick;
					                         	node["onclickHandlerHasBeenReplaced"] = true;
					                         	<%-- and connect this function to their onclick handler --%>
					                         	node.onclick = function() { 
					                        		selectOptionsAll('${html_id}_connected', '${html_id}_available');
					                             	if (prevHandler != null) {
					                                	<%-- wait one second so that other event listeners can execute before we call the initial handler --%>
					                                	setTimeout(prevHandler, 1000);
					                             	}
					                         	};
					                       	});
					                     });
				
				                    // ]]> --</script>
				
				                </th>
								<th>&nbsp;</th>
								<th><fmt:message key="global.availableElements" /></th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td>
									<c:set var="connected_elements_fields_size" value="${fn:length(connected_elements_fields)}" /> 
				                  	<form:select path="${path_to_componentModel}.elementIdsToAdd" multiple="true" cssClass="nameforSelect" id="${html_id}_connected" size="10" ondblclick="${jsMethodLeftToRight}">
										<itera:define id="connected_elements_array" name="memBean" property="${connected_elements}" />
										<c:forEach items="${connected_elements_array}" var="connected_element">
											<c:set var="nowrap" value='nowrap="nowrap"' />
											<c:set var="connected_elements_fields_size" value="${fn:length(connected_elements_fields)}" />
					
											<c:forEach items="${connected_elements_fields}" var="connected_element_field" varStatus="countStatus">
												<c:set var="count" value="${countStatus.index}" />
												<c:if test="${count > 0}">
													<c:set var="nowrap" value="" />
												</c:if>
												<c:choose>
													<c:when test="${user_styles}">
														<c:set var="style" value="${connected_elements_field_styles[count]}" />
													</c:when>
													<c:when test="${column_header_keys[count] == 'global.description'}">
														<c:set var="style" value="descriptionintablelong" />
													</c:when>
													<c:otherwise>
														<c:set var="style" value="" />
													</c:otherwise>
												</c:choose>
												<form:option value="${connected_element.id}">
													<fmt:message>
														<itera:write name="connected_element" property="${connected_element_field}" escapeXml="true" />
													</fmt:message>
												</form:option>
											</c:forEach>
										</c:forEach>
									</form:select> <form:errors path="${path_to_componentModel}.elementIdsToAdd" cssClass="errorMsg" htmlEscape="false" />
								</td>
								<td>
								  	<a onclick="<c:out value="${jsMethodRightToLeft}" />">
						                <i class="icon-arrow-left" title="<fmt:message key="tooltip.add"/>" ></i>
								  	</a>
								  	<a onclick="<c:out value="${jsMethodLeftToRight}" />">
										<i class="icon-arrow-right" title="<fmt:message key="tooltip.remove"/>" ></i>
								  	</a>
				                </td>
								<td>
									<form:select path="${path_to_componentModel}.elementIdsToRemove"
										cssClass="nameforSelect" multiple="true" size="10"
										id="${html_id}_available" cssStyle="${html_id}_available"
										ondblclick="${jsMethodRightToLeft}">
										<c:choose>
											<%-- Field is internationalised --%>
											<c:when test="${lookup_available_lables_mode != null && lookup_available_lables_mode}">
												<itera:define id="av_el_pr_array" name="memBean" property="${available_elements_presentation}" />
												<c:forEach items="${av_el_pr_array}" var="available_element" varStatus="countStatus">
													<c:set var="available_key">
														<itera:write name="available_element" property="${available_elements_label}" escapeXml="false" />
													</c:set>
													<c:if test="${available_element.id != null}">
														<form:option value="${available_element.id}">
															<fmt:message key="${available_key}" />
														</form:option>
													</c:if>
												</c:forEach>
											</c:when>
					
											<%-- Field is grouped --%>
											<c:when test="${available_elements_presentation_group_keys != null}">
												<option label="" value="" /><itera:define id="av_el_pr_array" name="memBean" property="${available_elements_presentation}" />
												<c:forEach items="${av_el_pr_array}" var="available_group" varStatus="countStatus">
													<option value="">&lt; <fmt:message key="${available_elements_presentation_group_keys[countStatus.index]}" />
													&gt;</option>
													<c:forEach items="${available_group}" var="available_group_el">
														<form:option value="${available_group_el.id}">
						                  					&nbsp;&nbsp;&nbsp;
						                  					<c:out value="${available_group_el[available_elements_label]}" />
														</form:option>
													</c:forEach>
												</c:forEach>
											</c:when>
					
											<%-- Field is a normal string --%>
											<c:otherwise>
												<form:options items="${available_elements_presentation}" itemLabel="${available_elements_label}" itemValue="id" />
											</c:otherwise>
					
										</c:choose>
									</form:select> 
									<form:errors path="${path_to_componentModel}.elementIdsToRemove" cssClass="errorMsg" htmlEscape="false" />
				                </td>
							</tr>
						</c:if>					
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>