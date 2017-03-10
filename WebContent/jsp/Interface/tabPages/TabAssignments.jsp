<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%-- ********************* --%>
<%-- Associated Transports --%>
<%-- ********************* --%>

<%-- Define variables --%>
<c:catch>
	<itera:define id="connected_elements" name="memBean" property="componentModel.transportModel.connectedElements" />
</c:catch>

<c:catch>
	<itera:define id="available_elements_presentation" name="memBean" property="componentModel.transportModel.availableElementsPresentation" />
</c:catch>

<c:set var="connected_transports_empty" value="${empty memBean.componentModel.transportModel.connectedElements}" />

<c:set var="linkScript" value="" />
<c:set var="linkStyle" value="link" />

<c:set var="emptyStyle" value="" />
<c:if test="${connected_transports_empty && componentMode == 'READ'}">
	<c:set var="emptyStyle" value="empty" />
</c:if>

<div id="InterfaceToBusinessObjectsModule" class="row-fluid module">
	<div class="module-heading">
		<fmt:message key="interface.to.businessObjects" />
	</div>
	<div class="row-fluid">
		<div class="module-body-table">
			<div class="row-fluid">
				<table class="table table-striped table-condensed tableInModule">
					<colgroup>
						<col class="col-ico" />
						<col class="col-ico" />
						<col class="col-ico" />
						<col class="col-desc" />
					</colgroup>
					<c:choose>
						<c:when test="${componentMode == 'READ'}">
							<c:choose>
								<%-- If no element is connected, display an empty table row --%>
								<c:when test="${connected_transports_empty}">
									<tbody>
										<tr>
											<td colspan="5">&nbsp;</td>
										</tr>
								</c:when>
								<%-- If an element is connected, display its details --%>
								<c:otherwise>
									<thead>
										<tr>
											<th><fmt:message key="global.direction" /></th>
											<th><fmt:message key="global.name" /></th>
											<th><fmt:message key="global.description" /></th>
											<th>&nbsp;</th>
											<th>&nbsp;</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach items="${connected_elements}" var="transport">
											<c:set var="businessObject" value="${transport.businessObject}" />
											<%-- Create the javascript function --%>
											<c:set var="linkScript"><itera:linkToElement name="businessObject" type="js" /></c:set>
											<c:set var="link"><itera:linkToElement name="businessObject" type="html" /></c:set>
				
											<tr class="<c:out value="${linkStyle}" />" onclick="<c:out value="${linkScript}" />">
												<td class="col-ico">
													<tiles:insertTemplate template="/jsp/common/DirectionIcon.jsp">
														<tiles:putAttribute name="directionKey" value="${transport.transportKey}" />
													</tiles:insertTemplate>
												</td>
												<td>
													<itera:htmlLinkToElement link="${link}" isLinked="true">
														<itera:write name="transport" property="businessObject.name" escapeXml="true" />
													</itera:htmlLinkToElement>
												</td>
												<td>
													<itera:write name="transport" property="businessObject.description" breaksAndSpaces="true" wikiText="true" escapeXml="false" />
												</td>
												<td>&nbsp;</td>
												<td>&nbsp;</td>
											</tr>
										</c:forEach>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<c:if test="${not connected_transports_empty}">
								<thead>
									<tr>
										<th><input type="hidden" name="componentModel.transportModel.elementIdToRemove" /></th>
										<th><fmt:message key="global.direction" /></th>
										<th><fmt:message key="global.name" /></th>
										<th><fmt:message key="global.description" /></th>
										<th></th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${connected_elements}" var="transport" varStatus="status">
										<c:set var="businessObject" value="${transport.businessObject}" />
										<%-- Create the javascript function --%>
										<c:set var="linkScript">
											<itera:linkToElement name="businessObject" type="js" />
										</c:set>
										<c:set var="link">
											<itera:linkToElement name="businessObject" type="html" />
										</c:set>
				
										<tr class="<c:out value="${linkStyle}"/>">
											<td>
												<a class="link" title="<fmt:message key="tooltip.remove"/>" onclick="setHiddenField('componentModel.transportModel.elementIdToRemove','<c:out value="${transport.id}"/>');flowAction('update');" >
													<i class="icon-remove"></i>
												</a>
											</td>
											<td>
												<form:select path="componentModel.transportModel.connectedElements[${status.index}].transportDirection" id="${memBean.componentModel.transportModel.htmlId}_${status.index}" cssStyle="width: 6em;">
												<form:options items="${memBean.componentModel.transportModel.availableTransportDirections}" itemLabel="name" itemValue="description" />
												</form:select>
											</td>
											<td onclick="<c:out value="${linkScript}" />">
												<c:out value="${transport.businessObject.name}" />
											</td>
											<td onclick="<c:out value="${linkScript}" /> ">
												<itera:write name="transport" property="businessObject.description" breaksAndSpaces="true" wikiText="true" escapeXml="false" />
											</td>
											<td onclick="<c:out value="${linkScript}" /> ">&nbsp;</td>
										</tr>
									</c:forEach>
							</c:if>
							<c:if test="${connected_transports_empty}">
								<tbody>
							</c:if>
						</c:otherwise>
					</c:choose>
					<c:if test="${componentMode != 'READ'}">
						<tr>
							<td>
								<a href="#" title="<fmt:message key="tooltip.add"/>" onclick="flowAction('update');" class="link" >
									<i class="icon-plus"></i>
								</a>
							</td>
							<td>
								<form:select path="componentModel.transportModel.transportDirectionToAdd" id="${memBean.componentModel.transportModel.htmlId}_selectDirection" cssStyle="width: 6em;">
									<form:options items="${memBean.componentModel.transportModel.availableTransportDirections}" itemLabel="name" itemValue="description" />
								</form:select>
							</td>
							<td>
								<form:select path="componentModel.transportModel.elementIdToAdd" cssClass="name combobox" id="${memBean.componentModel.transportModel.htmlId}_select">
									<c:forEach items="${available_elements_presentation}" var="transport">
										<c:set var="businessObject" value="${transport.businessObject}" />
										<c:set var="linkScript">
											<itera:linkToElement name="businessObject" type="js" />
										</c:set>
										<c:set var="link">
											<itera:linkToElement name="businessObject" type="html" />
										</c:set>
										<form:option label="${transport.businessObject.hierarchicalName}" value="${transport.businessObject.id}" />
									</c:forEach>
								</form:select>
							</td>
							<td>&nbsp;</td>
							<td></td>
						</tr>
					</c:if>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>

<%-- ******************************* --%>
<%-- Connected Catalog Item Releases --%>
<%-- ******************************* --%>

<tiles:insertTemplate template="/jsp/common/ManyAssociationSetComponentComboboxView.jsp" flush="true">
	<tiles:putAttribute name="path_to_componentModel" value="componentModel.technicalComponentReleaseModel" />
	<tiles:putAttribute name="dynamically_loaded" value="true" />
</tiles:insertTemplate>