<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%-- If set to true, the table rows are linked to the corresponding element's 
management page. Defaults to true. --%>
<tiles:useAttribute name="isLinked" ignore="true" />

<tiles:useAttribute name="available_elements_label" ignore="true" />
<tiles:useAttribute name="path_to_componentModel" />
<tiles:useAttribute name="read_only" ignore="true" />
<tiles:useAttribute name="select_box_style" ignore="true" />
<tiles:useAttribute name="dynamically_loaded" ignore="true" />
<tiles:useAttribute name="required" ignore="true" />

<c:if test="${available_elements_label == null}">
	<c:set var="available_elements_label" value="identityString" />
</c:if>

<c:if test="${empty select_box_style}">
	<c:set var="select_box_style" value="name" />
</c:if>

<c:set var="available_elements_presentation"
	value="${path_to_componentModel}.availableElementsPresentation" />
<c:set var="connected_element_id"
	value="${path_to_componentModel}.connectedElementId" />

<itera:define id="connected_element" name="memBean"
	property="${path_to_componentModel}.connectedElement" />
<itera:define id="connected_element_key" name="memBean"
	property="${path_to_componentModel}.labelKey" />
<itera:define id="component_mode" name="memBean"
	property="${path_to_componentModel}.componentMode" />

<c:if test="${dynamically_loaded == true}">
	<itera:define id="bbtype" name="memBean"
		property="${path_to_componentModel}.typeOfBuildingBlock" />
</c:if>

<%-- Set the variable for the HTML ID of this component. --%>
<c:set var="html_id"
	value="${path_to_componentModel}.htmlId" />

<c:if test="${read_only == true}">
	<c:set var="component_mode" value="READ" />
</c:if>

<%-- ************************ --%>
<%-- start OneAssociationView --%>
<%-- ************************ --%>
<div class="control-group">
	<c:choose>
	
		<%-- Don't show links, if not in read mode --%>
		<c:when test="${component_mode != 'READ'}">
			<label class="control-label" for="${html_id}_select">
				<fmt:message key="${connected_element_key}" />:
			</label>
			<div class="controls">
<%--
										Creating/Rendering the select list including content:
											- empty aside from the possibility of a pre-selected item if dynamically loaded
											- available elements if not dynamically loaded
										This and similar decisions made here by JSP c:choose and c:if blocks should be implemented in an own JSP-tag
										for better testability. The reason it hasn't been done yet is due to time constraints.
--%>
				<%-- Transfer String into List --%>
				<c:set var="displayed_value" value=""/>
				<itera:define id="av_el_pr_array" name="memBean" property="${available_elements_presentation}" />
				<form:select path="${connected_element_id}" id="${html_id}_select" cssClass="${select_box_style} combobox" dynLoad="${dynamically_loaded}" bbtype="${fn:toLowerCase(bbtype)}" initValue="${displayed_value}" req="${required}">
					<c:choose>
						<c:when test="${dynamically_loaded && not empty connected_element}">
	    				<option value="${connected_element.id}" selected="selected"><c:out value="${connected_element}" /></option>
	    			</c:when>
	    			<c:when test="${not dynamically_loaded}">
							<form:options items="${av_el_pr_array}" itemValue="id" itemLabel="${available_elements_label}" />
	    			</c:when>
					</c:choose>
				</form:select>
				<form:errors path="${connected_element_id}" cssClass="errorMsg" htmlEscape="false"/>
			</div>
		</c:when>
	
		<c:otherwise>
	
			<%-- Determine, if table rows should be linked. --%>
			<%-- Default values --%>
	
			<c:set var="linkShow" value="true" />
			<c:set var="linkStyle" value="link" />
	
			<%-- If no element is connected, disable links --%>
	
			<c:catch>
				<itera:define id="con_el_array" name="memBean"
					property="${path_to_componentModel}.connectedElement" />
			</c:catch>
			<c:if test="${empty con_el_array}">
				<c:set var="linkShow" value="false" />
				<c:set var="linkStyle" value="" />
			</c:if>
	
			<%-- If isLinked is set to false, disable links --%>
			<c:if test="${not empty isLinked}">
				<c:if test="${not isLinked}">
					<c:set var="linkShow" value="false" />
					<c:set var="linkStyle" value="" />
				</c:if>
			</c:if>
	
			<%-- Store the onClick()-Handler if the elements should be linked --%>
			<c:set var="linkScript" value="" />
			<c:if test="${linkShow}">
				<c:set var="linkScript">
					<c:catch>
						<itera:define id="con_el_array" name="memBean" property="${path_to_componentModel}.connectedElement" />
					</c:catch>	
					<c:if test="${not empty con_el_array}">
						<itera:linkToElement name="connected_element" type="js"/>
					</c:if>
				</c:set>
				
				<c:set var="link">
					<c:catch>
						<itera:define id="con_el_array" name="memBean" property="${path_to_componentModel}.connectedElement" />
					</c:catch>
					<c:if test="${not empty con_el_array}">
						<itera:linkToElement name="connected_element" type="html"/>
					</c:if>
				</c:set>
			</c:if>
			
			<label class="control-label" for="${html_id}_select">
				<span class="<c:out value="${linkStyle}"/>" onclick="<c:out value="${linkScript}" />">
	              <fmt:message key="${connected_element_key}" />:
	            </span>
			</label>
			<div class="controls <c:out value="${linkStyle}"/>" onclick="<c:out value="${linkScript}" />">
				<%-- If an element is connected, display its name. --%>
				<c:catch>
					<itera:define id="con_el_array" name="memBean" property="${path_to_componentModel}.connectedElement" />
				</c:catch>
				<c:if test="${not empty con_el_array}">
					<itera:htmlLinkToElement link="${link}" isLinked="${linkShow}">
				    	<itera:write name="memBean" property="${path_to_componentModel}.connectedElement.identityString" escapeXml="true" />
					</itera:htmlLinkToElement>
				</c:if>
			</div>
		</c:otherwise>
	</c:choose>
</div>