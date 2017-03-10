<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<tiles:useAttribute name="path_to_componentModel" />
<tiles:useAttribute name="showNewConnectionLink" ignore="true" />
<tiles:useAttribute name="showExistingConnectionLinks" ignore="true" />

<itera:define id="release" name="memBean" property="${path_to_componentModel}.current" />
<itera:define id="isrs" name="memBean" property="${path_to_componentModel}.informationSystemReleasesOfIsi" />
<itera:define id="mode" name="memBean" property="${path_to_componentModel}.componentMode" />
<itera:define id="htmlID" name="memBean" property="${path_to_componentModel}.htmlId" />

<c:if test="${empty showNewConnectionLink}">
  <c:set var="showNewConnectionLink" value="false" />
</c:if>

<c:set var="currentReleaseId" value="${release.id}"/>

<div id="<c:out value="${htmlID}"/>Modul" class="row-fluid module">
	<div class="module-heading">
		<fmt:message key="businessObject.to.informationSystemReleasesOfInterface" />
	</div>
	<div class="row-fluid">
		<div class="module-body-table">
			<div class="row-fluid">
				<table class="table table-striped table-condensed tableInModule">
					<colgroup>
						<col class="col-name" />
						<col class="col-desc" />
					</colgroup>
					<c:if test="${componentMode == 'CREATE'}">
						<tr>
							<td>
		  						<div class="alert alert-error">
								<a data-dismiss="alert" class="close" onclick="clearErrors();">×</a> 
								<span><fmt:message key="businessObject.choose.interface.informationSystem" />
		  						</span></div>
							</td>
						</tr>
						<br />
  					</c:if>
					<thead>
					<c:choose>
						<c:when test="${empty isrs}">
							<tr>
								<th colspan="2">&nbsp;</th>
							</tr>
						</c:when>
						<c:otherwise>
							<tr>
								<th><fmt:message key="global.name" />&nbsp;</th>
								<th><fmt:message key="global.description" />&nbsp;</th>
							</tr>
						</c:otherwise>
					</c:choose>
					</thead>
					<tbody>
						<c:forEach items="${isrs}" var="isr" varStatus="status">
							<tr id="<c:out value="${htmlID}_${status.index}" />">
								<c:set var="linkJavascript"><itera:linkToElement name="isr" type="js" /></c:set>
								<c:set var="HtmlLink"><itera:linkToElement name="isr" type="html"/></c:set>
								<td class="link top" onclick="<c:out value="${linkJavascript}" />">
									<itera:htmlLinkToElement link="${HtmlLink}" isLinked="true">
										<c:out value="${isr.hierarchicalName}" />
									</itera:htmlLinkToElement>
								</td>
								<td class="descriptionintablelong top link" onclick="<c:out value="${linkJavascript}" />">
									<itera:htmlLinkToElement link="${HtmlLink}" isLinked="true">
										<itera:write name="isr" property="description" breaksAndSpaces="true" wikiText="true" escapeXml="false" />
									</itera:htmlLinkToElement>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>