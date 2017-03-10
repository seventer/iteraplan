<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<script type="text/javascript">
   addJumpToInterfaceShortcut();
</script>

<tiles:useAttribute name="path_to_componentModel" />
<tiles:useAttribute name="showNewConnectionLink" ignore="true" />
<tiles:useAttribute name="showExistingConnectionLinks" ignore="true" />

<itera:define id="release" name="memBean"
	property="${path_to_componentModel}.current" />
<itera:define id="connections" name="memBean"
	property="${path_to_componentModel}.informationSystemInterfaces" />
<itera:define id="mode" name="memBean"
	property="${path_to_componentModel}.componentMode" />
<itera:define id="htmlID" name="memBean"
	property="${path_to_componentModel}.htmlId" />

<c:if test="${empty connections && mode == 'READ'}">
	<c:set var="emptyStyle" value="empty" />
</c:if>

<c:if test="${empty showNewConnectionLink}">
	<c:set var="showNewConnectionLink" value="false" />
</c:if>

<c:set var="currentReleaseId" value="${release.id}" />

<div id="JumpToInterfaceComponentViewModule" class="row-fluid module">
	<div class="module-heading">
		<fmt:message key="technicalComponentRelease.to.informationSystemInterfaces" />
	</div>
	<div class="row-fluid">
		<div class="module-body-table">
			<div class="row-fluid">
				<table class="table table-striped table-condensed tableInModule">
					<colgroup>
						<col class="col-name" />
						<col class="col-name" />
						<col class="col-name" />
						<col class="col-name" />
					</colgroup>
					<c:if test="${componentMode == 'CREATE'}">
						<tr>
							<td colspan="4" class="errorHeader"><fmt:message key="technicalComponentRelease.choose.interface" /></td>
						</tr>
					</c:if>
					<thead>
						<c:choose>
							<c:when test="${empty connections}">
								<tr>
									<th colspan="4">&nbsp;</th>
								</tr>
							</c:when>
							<c:otherwise>
								<tr>
								    <th><fmt:message key="global.name" />&nbsp;</th>
									<th><fmt:message key="informationSystemRelease.interfaceTo" />&nbsp;</th>
									<th><fmt:message key="global.direction" />&nbsp;</th>
									<th><fmt:message key="informationSystemRelease.interfaceTo" />&nbsp;</th>
								</tr>
							</c:otherwise>
						</c:choose>
					</thead>
					<tbody>
						<c:forEach items="${connections}" var="connection" varStatus="status">
							<tr id="<c:out value="${htmlID}_${status.index}" />">
								<c:set var="linkJavascript"><itera:linkToElement name="connection" isrOnLeftHandSide="${release.id}" type="js"/></c:set>
								<c:set var="link"><itera:linkToElement name="connection" isrOnLeftHandSide="${release.id}" type="html"/></c:set>
								<td class="nameintable top link"onclick="<c:out value="${linkJavascript}" />">
									<itera:htmlLinkToElement link="${link}" isLinked="true" >
										<itera:write name="connection" property="name" breaksAndSpaces="true" wikiText="false" escapeXml="true" />
									</itera:htmlLinkToElement>	
								</td>
								<td onclick="<c:out value="${linkJavascript}" />">
									<itera:htmlLinkToElement link="${link}" isLinked="true">
										<c:out value="${connection.informationSystemReleaseA.hierarchicalName}" />
									</itera:htmlLinkToElement>
								</td>
								<td class="top link"onclick="<c:out value="${linkJavascript}" />">
									<itera:htmlLinkToElement link="${link}" isLinked="true" >
										<tiles:insertTemplate template="/jsp/common/DirectionIcon.jsp">
											<tiles:putAttribute name="directionKey" value="${connection.isiKey['isiDirectionKey']}" />
										</tiles:insertTemplate>
									</itera:htmlLinkToElement>	
								</td>
								<td onclick="<c:out value="${linkJavascript}" />" nowrap="nowrap">
									<itera:htmlLinkToElement link="${link}" isLinked="true">
										<c:out value="${connection.informationSystemReleaseB.hierarchicalName}" />
									</itera:htmlLinkToElement>
								</td>
							</tr>
						</c:forEach>
						<c:if test="${userContext.perms.userAllowedToCreateConnections && showNewConnectionLink}">
							<tr id="<c:out value="${html_id}_new" />">
								<td colspan="4">
									<a href="#" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key="jumpToInterface.tooltip"/>">
										<input type="button" class="link btn" value="<fmt:message key="button.chooseConn" />" 
										   	onclick="changeLocation('<c:url value="/interface/init.do" />');" class="transactionButton link" id="jumpToInterface"/>
									</a>
								</td>
							</tr>
						</c:if>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>