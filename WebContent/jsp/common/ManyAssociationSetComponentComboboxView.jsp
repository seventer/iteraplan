<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%-- The path to the component model that provides the values for this page. --%>
<tiles:useAttribute name="path_to_componentModel" />

<%-- An alternative path to the connected elements. --%>
<tiles:useAttribute name="path_to_connected_elements" ignore="true" />

<%-- If set to true, the component model is attributable --%>
<tiles:useAttribute name="isAttributable" ignore="true" />

<%-- 
  If set to true, the table rows are linked to the corresponding element's 
  management page. Defaults to true. 
--%>
<tiles:useAttribute name="isLinked" ignore="true" />

<%-- If set to true, you have to confirm any tries to delete an Object --%>
<tiles:useAttribute name="askBeforeDelete" ignore="true" />

<%-- Message Key to be passed on deletion of elements with Attribute Value Assignments --%>
<tiles:useAttribute name="confirmDeleteAttributeValueAssignmentMessageKey" ignore="true" />
<%-- Set Default value --%>
<c:if test="${empty confirmDeleteAttributeValueAssignmentMessageKey}">
	<c:set var="confirmDeleteAttributeValueAssignmentMessageKey"
		value="global.confirmDeletionOfAttributeValueAssignments" />
</c:if>

<%-- 
  Optional list of CSS styles to be used for the fields of the connected
  elements. If not specified, default values are assumed. 
--%>
<tiles:useAttribute name="connected_elements_field_styles" ignore="true" />

<%-- 
  This attribute contains a list of strings that act as the label and value 
  (internationalized) for a select box. The select box is displayed next to 
  the combobox of available elements. Furthermore the last entry for each 
  connected_elements_fields is internationalized because it is assumed that 
  it is the value that was set by the combobox when the element was added. 
--%>
<tiles:useAttribute name="combobox_label_values" ignore="true" />

<%-- 
  The field in which the selected value of the combobox is stored. 
  See combobox_label_values attribute. 
--%>
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

<%--
  If set, the minimal Version of the output will be generated. This
  means no headers, labels etc. This is used for Massupdates for example.
--%>
<tiles:useAttribute name="minimal" ignore="true" />

<tiles:useAttribute name="addColumColspan" ignore="true" />

<tiles:useAttribute name="dynamically_loaded" ignore="true" />

<%-- Set the variable for the HTML ID of this component. --%>
<c:set var="html_id" value="${path_to_componentModel}.htmlId" />

<%-- Set the variables for the hidden fields into which the IDs to remove/add are to be placed. --%>
<c:set var="add_id_field" value="${path_to_componentModel}.elementIdToAdd" />
<c:set var="remove_id_field" value="${path_to_componentModel}.elementIdToRemove" />

<%-- Set the variables for the table header. --%>
<itera:define id="table_header_key" name="memBean" property="${path_to_componentModel}.tableHeaderKey" />

<%-- Set the variables for the column headers of this component. --%>
<itera:define id="column_header_keys" name="memBean" property="${path_to_componentModel}.columnHeaderKeys" />
<c:set var="column_header_keys_size" value="${fn:length(column_header_keys)}" />

<%-- Set the variables for the column headers of this component. --%>
<c:choose>
	<c:when test="${not empty isAttributable && isAttributable}">
		<itera:define id="attribute_types" name="memBean" property="${path_to_componentModel}.attributeTypes" />
		<c:set var="attribute_types_size" value="${fn:length(attribute_types)}" />
	</c:when>
	<c:otherwise>
		<c:set var="attribute_types_size" value="0" />
	</c:otherwise>
</c:choose>

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
<itera:define id="lookup_lables_mode" name="memBean" property="${path_to_componentModel}.lookupLablesMode" />
<itera:define id="lookup_available_lables_mode" name="memBean" property="${path_to_componentModel}.lookupAvailableLablesMode" />

<%-- Set the variables for the additional select box. Ignore JspException if property not present --%>
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

<%-- Determine the view mode of this component. --%>
<c:set var="viewMode" value="${componentMode}" />
<c:if test="${force_component_mode != null}">
	<c:set var="viewMode" value="${force_component_mode}" />
</c:if>

<c:if test="${connected_elements_size == 0 && viewMode == 'READ'}">
	<c:set var="emptyStyle" value="empty" />
</c:if>

<c:set var="postfix" value="" />
<c:if test="${viewMode == 'READ' && not empty header_postfix_read}">
	<c:set var="postfix" value="${header_postfix_read}" />
</c:if>
<c:if test="${viewMode != 'READ' && not empty header_postfix_edit}">
	<c:set var="postfix" value="${header_postfix_edit}" />
</c:if>

<c:choose>
	<c:when test="${addColumColspan != null}">
		<c:set var="addCol" value="${addColumColspan}" />
	</c:when>
	<c:when test="${viewMode != 'READ'}">
		<c:set var="addCol" value="${addCol + 1}" />
	</c:when>
	<c:otherwise>
		<c:set var="addCol" value="0" />
	</c:otherwise>
</c:choose>

<%-- used for correct formatting of icons column widths --%>
<c:set var="additionalCols" value="${fn:length(connected_elements_fields)}" />
<c:if test="${not empty isAttributable && isAttributable}">
	<c:set var="additionalCols" value="${additionalCols + fn:length(attribute_types)}" />
</c:if>

<c:if test="${empty minimal}">
<div id="${html_id}_modul" class="row-fluid module">
		<div class="module-heading">
			<fmt:message key="${table_header_key}" /> <c:out value="${postfix}" escapeXml="false" />
		</div>
	
	<div class="row-fluid">
		<div class="module-body-table">
			<div class="row-fluid">
</c:if>
				<table class="table table-striped table-condensed tableInModule" id="<c:out value="${html_id}_table" />">
					<c:choose>
						<c:when test="${viewMode != 'READ'}">
							<colgroup>
								<col class="col-ico" />
								<col class="col-name" />
								<col class="col-desc" />
							</colgroup>
						</c:when>
						<c:otherwise>
							<colgroup>
								<col class="col-name" />
								<col class="col-desc" />
							</colgroup>
						</c:otherwise>
					</c:choose>

					<%-- Shows the info-message if possible --%>
					<c:if test="${info_message_key != null}">
						<tr>
							<td colspan="<c:out value="${column_header_keys_size + attribute_types_size + addCol + 1}"/>">
								<div class="helpText">
									<c:out value="${info_message_key}" />
								</div>
							</td>
						</tr>
					</c:if>
			
					<thead>
						<%-- Shows the table head if some connected elements exists (for explanation) --%>
						<c:if test="${connected_elements_size > 0 && empty minimal}">
							<tr>
								<c:if test="${viewMode != 'READ'}">
									<th></th>
								</c:if>
								<c:forEach items="${column_header_keys}" var="column_header_key">
									<th><fmt:message key="${column_header_key}" /></th>
								</c:forEach>
								<c:if test="${not empty isAttributable && isAttributable}">
									<c:forEach items="${attribute_types}" var="attribute_type" varStatus="atCount">
										<th>
											<itera:define id="atPart" name="memBean" property="${path_to_componentModel}.rowsWithAssociationAttributes[0].atParts[${atCount.index}]" />
											<tiles:insertTemplate template="/jsp/common/attributes/AttributeDescription.jsp" flush="true">
												<tiles:putAttribute name="atPart" value="${atPart}" />
												<tiles:putAttribute name="showDetailsInEditMode" value="${viewMode != 'READ'}" />
												<tiles:putAttribute name="popoverOptions" value="{ placement : 'left' }" />
											</tiles:insertTemplate>
											&nbsp;<c:out value="${attribute_type.name}" />
										</th>
										<c:if test="${componentMode != 'READ'}">
											<itera:checkAttrTypeGroupPermission result="writePermissionATG" userContext="userContext" name="attribute_type" property="attributeTypeGroup" permissionType="write" />
											<c:if test="${!writePermissionATG}">
												<div class="helpText">
													<fmt:message key="messages.noAtGroupWritePermission.association">
														<fmt:param value="${attribute_type.name}" />
													</fmt:message>
												</div>
											</c:if>
										</c:if>
									</c:forEach>
								</c:if>
								
								<c:if test="${additionalCols le 1}"> <th></th> </c:if>
							</tr>
						</c:if>
				
						<%-- Shows an empty table head row if no connected elements exist --%>
						<c:if test="${connected_elements_size <= 0 && viewMode == 'READ'}">
							<tr>
								<th colspan="<c:out value="${column_header_keys_size + attribute_types_size + addCol}"/>" class="<c:out value="${emptyStyle}"/> ">
									<c:choose>
										<c:when test="${info_message_key != null}">
											<c:out value="${info_message_key_empty}" />
										</c:when>
										<c:otherwise>
						            		&nbsp;
						          		</c:otherwise>
									</c:choose>
								</th>
								
								<c:if test="${additionalCols le 1}"> <th></th> </c:if>
							</tr>
						</c:if>
					</thead>
					<tbody>
						<itera:define id="connected_elements_array" name="memBean" property="${connected_elements}" />
						<c:forEach items="${connected_elements_array}" var="connected_element" varStatus="elCount">
							<%-- Store the onClick()-Handler if the elements should be linked --%>
							<c:set var="linkScript" value="" />
							<c:set var="link" value="" />
							<c:if test="${linkShow}">
								<c:set var="linkScript"><itera:linkToElement name="connected_element" type="js" /></c:set>
								<c:set var="link"><itera:linkToElement name="connected_element" type="html" /></c:set>
							</c:if>
				
							<%-- Display each connected element on a table row --%>
							<tr>
								<c:if test="${viewMode != 'READ'}">
									<td>
										<a id="<c:out value="${html_id}_${elCount.index}_remove" />"
											title="<fmt:message key="tooltip.remove"/>"
											onclick="confirmDeleteAttributeValueAssignment('<c:out value="${remove_id_field}"/>','<c:out value="${connected_element.id}"/>', function() {flowAction('update');}, '<c:out value="${askBeforeDelete}"/>','<fmt:message key="global.confirmDelete"/>','<fmt:message key="${confirmDeleteAttributeValueAssignmentMessageKey}"/>');" >
											<i class="icon-remove"></i>
										</a>
									</td>
								</c:if>
				
								<%-- Display the fields of each connected elements --%>
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
											<c:set var="style" value="" />
										</c:when>
										<c:otherwise>
											<c:set var="style" value="" />
										</c:otherwise>
									</c:choose>
				
									<c:choose>
										<%-- The last field is the value of a combobox that has to be internationalised. --%>
										<c:when test="${not empty combobox_label_values && count == (connected_elements_fields_size - 1)}">
											<c:set var="output">
												<fmt:message>
													<itera:write name="connected_element" property="${connected_element_field}" escapeXml="true" />
												</fmt:message>
											</c:set>
										</c:when>
										<%-- Field has to be internationalized --%>
										<c:when test="${lookup_lables_mode != null && lookup_lables_mode[count]}">
											<c:set var="output">
												<fmt:message>
													<itera:write name="connected_element" property="${connected_element_field}" escapeXml="true" />
												</fmt:message>
											</c:set>
										</c:when>
										<%-- Field is a date --%>
										<c:when test="${column_header_keys[count] == 'global.start' || column_header_keys[count] == 'global.finish'}">
											<c:set var="output">
												<fmt:formatDate value="${connected_element[connected_element_field]}" dateStyle="short" />
											</c:set>
										</c:when>
										<%-- Field is a color representation --%>
										<c:when test="${column_header_keys[count] == 'manageAttributes.defaultcolor'}">
											<itera:define id="colorMap" name="memBean" property="${path_to_componentModel}.valueToColorMap" />
											<itera:define id="availableColors" name="memBean" property="${path_to_componentModel}.availableColors" />
											<c:set var="output">
												<c:choose>
													<c:when test="${viewMode != 'READ'}">
														<%-- dropbox to choose the color --%>
														<c:set var="color" value="${colorMap[connected_element.identityString]}" />
														<!-- setting the color values of the map as path -->
														<form:select path="${path_to_componentModel}.valueToColorMap['${connected_element.identityString}']"
															id="${html_id}_colorsToAdd_${elCount.index}"
															cssClass="colorBox" onchange="changeColor(this)"
															cssStyle="width:60px;background-color:#${color}">
															<c:forEach var="color" items="${availableColors}">
																<form:option value="${color}" cssStyle="background-color:#${color}">&nbsp;</form:option>
															</c:forEach>
														</form:select>
				
													</c:when>
													<c:otherwise>
														<%-- if in view mode then plain display --%>
														<div style="background-color:#<c:out value="${colorMap[connected_element.identityString]}" />;width:60px;">&nbsp;</div>
													</c:otherwise>
												</c:choose>
											</c:set>
										</c:when>
										<%-- Field is a normal string --%>
										<c:otherwise>
											<c:set var="output">
												<%-- Sets a plain text filter for WikiMarkup in description fields--%>
												<c:choose>
													<c:when test="${column_header_keys != null && column_header_keys[count] == 'global.description'}">
														<itera:write name="connected_element" property="${connected_element_field}" breaksAndSpaces="true" plainText="true" truncateText="true" escapeXml="true" />
													</c:when>
													<c:otherwise>
														<itera:write name="connected_element" property="${connected_element_field}" breaksAndSpaces="true" escapeXml="true" />
													</c:otherwise>
												</c:choose>
											</c:set>
										</c:otherwise>
									</c:choose>
				
									<%-- disable linking for the color selection --%>
									<c:if test="${column_header_keys[count] == 'manageAttributes.defaultcolor'}">
										<c:set var="linkShow" value="false" />
										<c:set var="linkStyle" value="" />
										<c:set var="linkScript" value="" />
									</c:if>
									<%-- Only show an a-Link if linkShow is true --%>
									<td class="<c:out value="${style} ${linkStyle}"/> top" onclick="<c:out value="${linkScript}" />"
										<c:out value="${nowrap}" escapeXml="false"/>>
										<itera:htmlLinkToElement link="${link}" isLinked="${linkShow}">
											<c:out value="${output}" escapeXml="false" />
										</itera:htmlLinkToElement>
									</td>
								</c:forEach>
								<c:if test="${not empty isAttributable && isAttributable}">
									<c:forEach items="${attribute_types}" var="attribute_type" varStatus="atCount">
										<itera:checkAttrTypeGroupPermission result="writePermissionATG"
											userContext="userContext" name="attribute_type"
											property="attributeTypeGroup" permissionType="write" />
										<td class="<c:out value="${linkStyle}"/>"><tiles:insertTemplate template="/jsp/common/attributes/ManageAVA.jsp" flush="true">
												<tiles:putAttribute name="atPartPath" value="${path_to_componentModel}.rowsWithAssociationAttributes[${elCount.index}].atParts[${atCount.index}]" />
												<tiles:putAttribute name="atPartPathAnchor" value="${elCount.index}_${atCount.index}" />
												<tiles:putAttribute name="writePermissionATG" value="${writePermissionATG}" />
												<tiles:putAttribute name="extended_html_id" value="${connected_element}" />
												<tiles:putAttribute name="valueOnly" value="true" />
											</tiles:insertTemplate>
										</td>
									</c:forEach>
								</c:if>
								
								<c:if test="${additionalCols le 1}"> <td></td> </c:if>
							</tr>
						</c:forEach>

						<c:if test="${viewMode != 'READ'}">
							<tr>
								<td><input type="hidden" name="<c:out value="${remove_id_field}"/>" value="" />
									<a id="<c:out value="${html_id}_add"/>" class="link <c:out value="${html_id}_add" />"  title="<fmt:message key="tooltip.add"/>" onclick="flowAction('update');" >
										<i class="icon-plus"></i>
									</a>
								</td>
								<td class="dontwrap">
								
<%--
										Creating/Rendering the select list including content:
											- empty aside from the possibility of a pre-selected item if dynamically loaded
											- available elements if not dynamically loaded
										This and similar decisions made here by JSP c:choose and c:if blocks should be implemented in an own JSP-tag
										for better testability. The reason it hasn't been done yet is due to time constraints.
--%>
								
									<c:choose>
										<%-- Field is internationalized --%>
										<c:when test="${lookup_available_lables_mode != null && lookup_available_lables_mode}">
											<%-- define helper variable to iterate over --%>
											<form:select path="${add_id_field}" id="${html_id}_select" cssClass="name">
												<itera:define id="available_elements_presentation_obj" name="memBean" property="${available_elements_presentation}" />
												<c:forEach items="${available_elements_presentation_obj}" var="available_element">
													<itera:define id="available_key" name="available_element" property="${available_elements_label}" />
													<form:option value="${available_element.id}">
														<c:if test="${available_key != ''}">
															<fmt:message key="${available_key}" />
														</c:if>
													</form:option>
												</c:forEach>
											</form:select>
										</c:when>
										<%-- Field is grouped --%>
										<c:when test="${available_elements_presentation_group_keys != null}">
											<form:select path="${add_id_field}" id="${html_id}_select" required="false" cssClass="name combobox">
												<option label="" value="" />
												<%-- iterate over option groups --%>
												<itera:define id="available_elements_presentation_obj" name="memBean" property="${available_elements_presentation}" />
												<c:forEach items="${available_elements_presentation_obj}" var="available_group" varStatus="indexStatus">
													<option value="">- <fmt:message	key="${available_elements_presentation_group_keys[indexStatus.index]}" /> -</option>
													<%-- iterate over options in the current group --%>
													<c:forEach items="${available_group}" var="available_group_el">
														<form:option value="${available_group_el.id}">
															<c:out value="${available_group_el[available_elements_label]}" />
														</form:option>
													</c:forEach>
												</c:forEach>
											</form:select>
										</c:when>
				
										<%-- Field is a normal string --%>
										<c:otherwise>
											<c:if test="${dynamically_loaded == true}">
												<itera:define id="bbtype" name="memBean" property="${path_to_componentModel}.typeOfBuildingBlock" />
											</c:if>
											<form:select path="${add_id_field}" id="${html_id}_select" cssClass="name combobox" dynLoad="${dynamically_loaded}" bbtype="${fn:toLowerCase(bbtype)}">
												<itera:define id="available_elements_presentation_obj" name="memBean" property="${available_elements_presentation}" />
												<c:if test="${not dynamically_loaded}">
													<form:options items="${available_elements_presentation_obj}" itemLabel="${available_elements_label}" itemValue="id" />
												</c:if>
											</form:select>
										</c:otherwise>
									</c:choose>
									&nbsp;
									<form:errors path="${add_id_field}" cssClass="errorMsg" htmlEscape="false" />
									<c:if test="${not empty combobox_label_values}">
										&nbsp;
										<form:select path="${combobox_field}" id="${html_id}_select2">
											<%-- Options must be localized, so we can't use the items attribute :( --%>
											<c:forEach items="${combobox_label_values}" var="label_value">
												<form:option value="${label_value}">
													<fmt:message key="${label_value}" />
												</form:option>
											</c:forEach>
										</form:select>
									</c:if>
								</td>
								
								<c:choose>
									<c:when test="${path_to_componentModel == 'componentModel.responsibilityAttributeValuesModel'}">
										<td>&nbsp;</td>
										<td class="color">
											<%-- dropbox to choose the color  --%> 
											<itera:define id="availableColors_create" name="memBean" property="${path_to_componentModel}.availableColors" /> 
											<itera:define id="colorIndex" name="memBean" property="${path_to_componentModel}.availableColorIndex" /> 
											<c:set var="colorDisplay" value="${availableColors_create[colorIndex]}" /> 
											<form:select path="${path_to_componentModel}.colorToAdd"
												id="${html_id}_colorToAdd" cssClass="colorBox"
												onchange="changeColor(this)"
												cssStyle="width:60px;background-color:#${colorDisplay}">
												<%-- add the selected-tag for the right entry --%>
												<c:forEach var="color" items="${availableColors_create}">
													<c:choose>
														<c:when test="${colorDisplay == color}">
															<form:option selected="selected" value="${color}" cssStyle="background-color:#${color}">&nbsp;</form:option>
														</c:when>
														<c:otherwise>
															<form:option value="${color}" cssStyle="background-color:#${color}">&nbsp;</form:option>
														</c:otherwise>
													</c:choose>
												</c:forEach>
											</form:select>
										</td>
										<td></td>
									</c:when>
									<c:otherwise>
										<c:forEach var="count" begin="1" end="${additionalCols-1}">
											<%-- fill last row with empty cells columns --%> 
											<td></td>
										</c:forEach>
										
										<c:if test="${additionalCols le 1}"> <td></td> </c:if>
									</c:otherwise>
								</c:choose>
							</tr>
						</c:if>
					</tbody>
				</table>
<c:if test="${empty minimal}">
			</div>
		</div>
	</div>
</div>
</c:if>