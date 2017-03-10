<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<tiles:useAttribute name="path_to_componentModel" />
<tiles:useAttribute name="showNewConnectionLink" ignore="true" />
<tiles:useAttribute name="showExistingConnectionLinks" ignore="true" />

<itera:define id="release" name="memBean"
	property="${path_to_componentModel}.current" />
<itera:define id="businessObjects" name="memBean"
	property="${path_to_componentModel}.businessObjects" />
<itera:define id="mode" name="memBean"
	property="${path_to_componentModel}.componentMode" />
<itera:define id="htmlID" name="memBean"
	property="${path_to_componentModel}.htmlId" />


<c:if test="${empty businessObjects && mode == 'READ'}">
	<c:set var="emptyStyle" value="empty" />
</c:if>

<c:if test="${empty showNewConnectionLink}">
	<c:set var="showNewConnectionLink" value="false" />
</c:if>

<div id="JumpToBusinessObjectComponentViewModul" class="row-fluid module">
	<div class="module-heading">
		<fmt:message key="informationSystemRelease.to.BusinessObjectsOfIsi" />
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
								<span><fmt:message key="businessObject.choose.interface.businessObject" />
								</span></div>
							</td>
						</tr>
					</c:if>
					<thead>
						<c:choose>
							<c:when test="${empty businessObjects}">
								<tr>
									<th colspan="2">&nbsp;</th>
								</tr>
							</c:when>
							<c:otherwise>
								<tr>
									<th>
										<fmt:message key="global.name" />
									</th>
									<th>
										<fmt:message key="global.description" />
									</th>
								</tr>
							</c:otherwise>
						</c:choose>
					</thead>
					<tbody>
						<c:forEach items="${businessObjects}" var="bo" varStatus="status">
							<tr id="<c:out value="${htmlID}_${status.index}" />">
								<c:set var="linkJavascript"><itera:linkToElement name="bo" isrOnLeftHandSide="${bo.id}" type="js"/></c:set>
								<c:set var="link"><itera:linkToElement name="bo" isrOnLeftHandSide="${bo.id}" type="html"/></c:set>
								<td class="link top" onclick="<c:out value="${linkJavascript}" />">
									<itera:htmlLinkToElement link="${link}" isLinked="true">
										<itera:write name="bo" property="hierarchicalName" escapeXml="true" />
									</itera:htmlLinkToElement>
								</td>
								<td class="link top" onclick="<c:out value="${linkJavascript}" />">
									<itera:write name="bo" property="description" breaksAndSpaces="true" wikiText="true" escapeXml="false" />
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>