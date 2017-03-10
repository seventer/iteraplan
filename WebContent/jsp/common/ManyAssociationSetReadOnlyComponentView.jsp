<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<%-- The path to the component model that provides the values for this page. --%>
<tiles:useAttribute name="path_to_componentModel" />

<%-- If set to true, the table rows are linked to the corresponding element's 
management page. Defaults to true. --%>
<tiles:useAttribute name="isLinked" ignore="true" />

<c:set var="connected_elements"
	value="${path_to_componentModel}.connectedElements" />

<%-- Tiles1
<bean-el:size id="connected_elements_size" name="memBean" property="${connected_elements}"/> --%>
<c:set var="connected_elements_size"
	value="${fn:length(connected_elements)}" />

<itera:define id="table_header_key" name="memBean"
	property="${path_to_componentModel}.tableHeaderKey" />
<itera:define id="column_header_keys" name="memBean"
	property="${path_to_componentModel}.columnHeaderKeys" />
<c:set var="column_header_keys_size"
	value="${fn:length(column_header_keys)}" />
<itera:define id="connected_elements_fields" name="memBean"
	property="${path_to_componentModel}.connectedElementsFields" />
<itera:define id="lookup_lables_mode" name="memBean"
	property="${path_to_componentModel}.lookupLablesMode" />
<itera:define id="html_id" name="memBean"
	property="${path_to_componentModel}.htmlId" />

<%-- Determine, if table rows should be linked. --%>
<c:set var="linkShow" value="true" />
<c:set var="linkStyle" value="link" />
<c:if test="${not empty isLinked}">

	<c:if test="${!isLinked}">
		<c:set var="linkShow" value="false" />
		<c:set var="linkStyle" value="" />
	</c:if>
</c:if>


<c:if test="${connected_elements_size == 0}">
	<c:set var="emptyStyle" value="empty" />
</c:if>

<div id="<c:out value="${html_id}" />Modul" class="row-fluid module">
	<div class="module-heading">
		<fmt:message key="${table_header_key}"/>
	</div>
	<div class="row-fluid">
		<div class="module-body-table">
			<div class="row-fluid">
				<table class="table table-striped table-condensed tableInModule" />
				  	<c:choose>
					    <c:when test="${component_mode != 'READ'}">
					    	<c:set var="columns_size" value="3" />
						  	<colgroup>
								<col class="col-ico" />
								<col class="col-name" />
								<col class="col-desc" />
							</colgroup>
						</c:when>
						<c:otherwise>
							<c:set var="columns_size" value="2" />
							<colgroup>
								<col class="col-name" />
								<col class="col-desc" />
							</colgroup>
						</c:otherwise>
					</c:choose>
					<thead>
						<%-- Display the table head if there are any connected elements --%>
						<c:if test="${connected_elements_size > 0}">
							<tr>
								<c:forEach items="${column_header_keys}" var="column_header_key" varStatus="countStatus">	
									<th><fmt:message key="${column_header_key}" /></th>
								</c:forEach>
								<c:forEach begin="${column_header_keys_size+1}" end="${columns_size}">	
									<th/></th>
								</c:forEach>	
							</tr>
						</c:if>
		
						<%-- Shows a empty Table-Row if no connected elements exist  --%>
						<c:if test="${connected_elements_size <= 0}">
							<tr>
								<th colspan="<c:out value="${column_header_keys_size+1}"/>">&nbsp;</th>
							</tr>
						</c:if>
					</thead>
					<tbody>
						<itera:define id="connected_elements_array" name="memBean" property="${connected_elements}" />
						<c:forEach items="${connected_elements_array}" var="connected_element" varStatus="countStatus">
				
							<%-- Store the onClick()-Handler if the elements should be linked --%>
						    <c:set var="linkScript" value="" />
							<c:if test="${linkShow}">
								<c:set var="linkScript">
							    	<itera:linkToElement name="connected_element" type="js" />
								</c:set>
							</c:if>
				
							<tr>
								<c:set var="nowrap" value='nowrap="nowrap"' />
								<c:set var="connected_elements_fields_size" value="${fn:length(connected_elements_fields)}" />
					
								<c:forEach items="${connected_elements_fields}" var="connected_element_field" varStatus="cntStatus">					
									<c:set var="outputText">
										<c:choose>
											<%-- Field has to be internationalized --%>
											<c:when test="${lookup_lables_mode != null && lookup_lables_mode[cntStatus.index]}">
												<fmt:message>
													<itera:write name="connected_element" property="${connected_element_field}" escapeXml="true" />
												</fmt:message>
											</c:when>
											<%-- Field is a date --%>
											<c:when test="${column_header_keys[cntStatus.index] == 'global.start' || column_header_keys[cntStatus.index] == 'global.finish'}">
												<fmt:formatDate	value="${connected_element[connected_element_field]}" dateStyle="short" />
											</c:when>
											<%-- Field is a normal string --%>
											<c:otherwise>
												<%-- Sets a plain text filter for WikiMarkup in description fields--%>
												<c:choose>
									              	<c:when test="${column_header_keys != null && column_header_keys[cntStatus.index] == 'global.description'}">
									              		<itera:write name="connected_element" property="${connected_element_field}" breaksAndSpaces="true" plainText="true" truncateText="true" escapeXml="true" />
									              	</c:when>
									              	<c:otherwise>
									           			<itera:write name="connected_element" property="${connected_element_field}" breaksAndSpaces="true" escapeXml="true" />
									              	</c:otherwise>
									              </c:choose>
												
											</c:otherwise>
										</c:choose>
									</c:set>
					
									<c:if test="${cntStatus.index > 0}">
										<c:set var="nowrap" value="" />
									</c:if>
									<td class="<c:out value="${linkStyle}"/>" nowrap="nowrap" onclick="<c:out value="${linkScript}" />">
										<itera:htmlLinkToElement name="connected_element" isLinked="${linkShow}">
											<c:out value="${outputText}" escapeXml="false" />
										</itera:htmlLinkToElement>
									</td>
					
								</c:forEach>
								<c:forEach begin="${column_header_keys_size+1}" end="${columns_size}">	
									<td class="<c:out value="${linkStyle}"/>" onclick="<c:out value="${linkScript}" />">&nbsp;</td>
								</c:forEach>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>