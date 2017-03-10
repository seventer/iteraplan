<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="path_to_componentModel" />
<tiles:useAttribute name="select_box_style" ignore="true" />
<%-- used for the MassUpdate View --%>
<tiles:useAttribute name="showDivView" ignore="true" />

<c:set var="text_field"	value="${path_to_componentModel}.currentAsString" />
<c:set var="available_elements"	value="${path_to_componentModel}.availableEnumKeys" />
<c:set var="label_key" value="${path_to_componentModel}.labelKey" />

<itera:define id="component_mode" name="memBean" property="${path_to_componentModel}.componentMode" />
<itera:define id="html_id" name="memBean" property="${path_to_componentModel}.htmlId" />

<c:if test="${component_mode == 'READ'}">
	<c:set var="tdstyle" value="margin" />
</c:if>

<c:if test="${empty select_box_style}">
	<c:set var="select_box_style" value="name" />
</c:if>

<div class="control-group">
	<c:choose>
		<c:when test="${not showDivView}">	
			<label class="control-label <c:out value="${mandatoryClass}" default="" escapeXml="false"/>" for="${extended_html_id}_select">
				<fmt:message>
					<itera:write name="memBean" property="${label_key}" escapeXml="false" />
				</fmt:message>:
			</label>
			<div class="controls">	
				<c:choose>
					<c:when test="${component_mode != 'READ'}">
						<form:select path="${text_field}" id="${html_id}_select" cssStyle="${select_box_style}">
							<itera:define id="elements_array" name="memBean" property="${available_elements}" />
							<c:forEach items="${elements_array}" var="element" varStatus="countStatus">
								<form:option value="${element}">
									<fmt:message>
										<itera:write name="element" escapeXml="false" />
									</fmt:message>
								</form:option>
							</c:forEach>
						</form:select>
						<form:errors path="${text_field}" cssClass="errorMsg" htmlEscape="false"/>
					</c:when>
					<c:otherwise>
						<fmt:message>
							<itera:write name="memBean" property="${text_field}" escapeXml="false" />
						</fmt:message>
					</c:otherwise>
				</c:choose>
			</div>
		</c:when>
		<%-- used for the MassUpdate View --%>
		<c:otherwise>
			<div style="float:left">
				<div class="runtime">
					<fmt:message>
						<itera:write name="memBean" property="${label_key}" escapeXml="false" />
					</fmt:message>:
				</div>
				<div style="float:left;padding-right:1em">
					<c:choose>
						<c:when test="${component_mode != 'READ'}">
							<form:select path="${text_field}" id="${html_id}_select" cssStyle="${select_box_style}">
								<itera:define id="elements_array" name="memBean" property="${available_elements}" />
								<c:forEach items="${elements_array}" var="element" varStatus="countStatus">
									<form:option value="${element}">
										<fmt:message>
											<itera:write name="element" escapeXml="false" />
										</fmt:message>
									</form:option>
								</c:forEach>
							</form:select>
							<form:errors path="${text_field}" cssClass="errorMsg" htmlEscape="false"/>
						</c:when>
						<c:otherwise>
							<fmt:message>
								<itera:write name="memBean" property="${text_field}" escapeXml="false" />
							</fmt:message>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</c:otherwise>
	</c:choose>
</div>