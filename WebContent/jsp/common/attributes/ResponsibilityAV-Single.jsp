<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<tiles:useAttribute name="atPartPath" />
<tiles:useAttribute name="writePermissionATG" />
<tiles:useAttribute name="extended_html_id" />
<tiles:useAttribute name="overviewMode" ignore="true" />
<tiles:useAttribute name="valueOnly" ignore="true" />

<itera:define id="atPart" name="memBean" property="${atPartPath}" />

<c:if test="${atPart.attributeType.mandatory == true}">
	<c:set var="mandatoryClass" value=' mandatoryAttribute' scope="page" />
</c:if>

<c:if test="${overviewMode == null}">
	<c:set var="overviewMode" value="false" />
</c:if>

<c:if test="${empty valueOnly}">
	<div class="control-group">
		<label class="control-label <c:out value="${mandatoryClass}" default="" escapeXml="false"/>" for="${extended_html_id}_select">
			<tiles:insertTemplate template="/jsp/common/attributes/tiles/BasicAtData.jsp" flush="true">
				<tiles:putAttribute name="atPartPath" value="${atPartPath}" />
				<tiles:putAttribute name="extended_html_id" value="${extended_html_id}" />
			</tiles:insertTemplate>
		</label>
		<div class="controls">
</c:if>
		<c:choose>
			<c:when test="${atPart.componentMode != 'READ' && writePermissionATG && overviewMode == false}">
				<tiles:insertTemplate template="/jsp/common/attributes/tiles/AddRemoveResponsibilityAVs-Single.jsp" flush="true">
					<tiles:putAttribute name="atPartPath" value="${atPartPath}" />
					<tiles:putAttribute name="extended_html_id" value="${extended_html_id}" />
				</tiles:insertTemplate>
			</c:when>
			<c:otherwise>
				<tiles:insertTemplate template="/jsp/common/attributes/tiles/ConnectedResponsibilityAVs-Single.jsp" flush="true">
					<tiles:putAttribute name="atPartPath" value="${atPartPath}" />
					<tiles:putAttribute name="extended_html_id" value="${extended_html_id}" />
				</tiles:insertTemplate>
			</c:otherwise>
		</c:choose>
<c:if test="${empty valueOnly}">
		</div>
	</div>
</c:if>