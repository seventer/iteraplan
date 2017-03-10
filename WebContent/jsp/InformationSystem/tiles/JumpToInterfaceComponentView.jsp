<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<tiles:useAttribute name="path_to_componentModel" />
<tiles:useAttribute name="showNewConnectionLink" ignore="true" />
<tiles:useAttribute name="showExistingConnectionLinks" ignore="true" />

<script type="text/javascript">
   addJumpToInterfaceShortcut();
</script>

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

<div id="JumpToInterfaceComponentViewModul" class="row-fluid module">
	<div class="module-heading">
		<fmt:message key="informationSystemRelease.to.interfaces" />
	</div>
	<div class="row-fluid">
		<div class="module-body-table">
			<div class="row-fluid">
				<table class="table table-striped table-condensed tableInModule">
					<colgroup>
						<col class="col-name" />
					</colgroup>
					<c:if test="${componentMode == 'CREATE'}">
						<tr>
							<td>
								<div class="alert alert-error">
								<a data-dismiss="alert" class="close" onclick="clearErrors();">×</a> 
								<span><fmt:message key="informationSystemRelease.new.interface" />
								</span></div>
							</td>
						</tr>		
					</c:if>
					<thead>
						<c:choose>
							<c:when test="${empty connections}">
								<tr>
									<th colspan="6">&nbsp;</th>
								</tr>
							</c:when>
							<c:otherwise>
								<tr>
									<th>
										<fmt:message key="global.name" />
									</th>
									<th>
										<fmt:message key="global.direction" />
									</th>
									<th>
										<fmt:message key="informationSystemRelease.interfaceTo" />
									</th>
									<th>
										<fmt:message key="interface.transport.businessObjects" />
									</th>
									<th>
										<fmt:message key="technicalRealisation" />
									</th>
									<th>&nbsp;</th>
								</tr>
							</c:otherwise>
						</c:choose>
					</thead>
					<tbody>
						<c:forEach items="${connections}" var="connection" varStatus="status">
							<tr id="<c:out value="${htmlID}_${status.index}" />">
								<c:set var="linkJavascript"><itera:linkToElement name="connection" isrOnLeftHandSide="${currentReleaseId}" type="js"/></c:set>
								<c:set var="link"><itera:linkToElement name="connection" isrOnLeftHandSide="${currentReleaseId}" type="html"/></c:set>
								<td class="top link" onclick="<c:out value="${linkJavascript}" />">
									<itera:htmlLinkToElement link="${link}" isLinked="true" >
										<itera:write name="connection" property="name" breaksAndSpaces="true" wikiText="false" escapeXml="true" />
									</itera:htmlLinkToElement>	
								</td>
								<td class="top link" onclick="<c:out value="${linkJavascript}" />">
									<c:choose>
										<c:when test="${connection.informationSystemReleaseB.id == currentReleaseId}">
											<itera:htmlLinkToElement link="${link}" isLinked="true">
												<tiles:insertTemplate template="/jsp/common/DirectionIcon.jsp">
													<tiles:putAttribute name="directionKey" value="${connection.isiKeyBA['isiDirectionKeyBA']}" />
												</tiles:insertTemplate>
											</itera:htmlLinkToElement>
										</c:when>
										<c:otherwise>
											<itera:htmlLinkToElement link="${link}" isLinked="true">
												<tiles:insertTemplate template="/jsp/common/DirectionIcon.jsp">
													<tiles:putAttribute name="directionKey" value="${connection.isiKey['isiDirectionKey']}" />
												</tiles:insertTemplate>
											</itera:htmlLinkToElement>
										</c:otherwise>
									</c:choose>
								</td>
								<td class="top link" onclick="<c:out value="${linkJavascript}" />" nowrap="nowrap">
								<c:choose>
									<c:when test="${connection.informationSystemReleaseB.id == currentReleaseId}">
										<itera:write name="connection" property="informationSystemReleaseA.hierarchicalName" escapeXml="true" />
									</c:when>
									<c:otherwise>
										<itera:write name="connection" property="informationSystemReleaseB.hierarchicalName" escapeXml="true" />
									</c:otherwise>
								</c:choose>
								</td>
								<td class="top link" onclick="<c:out value="${linkJavascript}" />" nowrap="nowrap">
									<itera:define id="transports" name="connection" property="transportInformation" />
									<c:set var="size" value="${fn:length(transports)}" />
									<c:forEach items="${transports}" var="transport" varStatus="countStatus">
										<tiles:insertTemplate template="/jsp/common/DirectionIcon.jsp">
											<tiles:putAttribute name="directionKey" value="${transport.misc['transportkey']}" />
										</tiles:insertTemplate>
										<itera:write name="transport" property="name" escapeXml="true" />
										<c:if test="${countStatus.index < (size-1)}">&nbsp;<br/></c:if>
									</c:forEach>
								</td>
								<td class="top link" onclick="<c:out value="${linkJavascript}" />" nowrap="nowrap">
									<itera:define id="technicalComponentReleases" name="connection" property="technicalComponentReleasesSorted" />
									<c:set var="size" value="${fn:length(technicalComponentReleases)}" />
									<c:forEach items="${technicalComponentReleases}" var="release" varStatus="countStatus">
										<itera:write name="release" property="releaseName" escapeXml="true" />
										<c:if test="${countStatus.index < (size-1)}">
											&nbsp;<br />
										</c:if>
									</c:forEach>
								</td>
								<td class="top link" onclick="<c:out value="${linkJavascript}" />">
									&nbsp;
								</td>		
							</tr> 
						</c:forEach>
						<c:if test="${userContext.perms.userAllowedToCreateConnections && showNewConnectionLink}">
							<tr id="<c:out value="${html_id}_new" />">
								<td colspan="5" align="left" class="borderTop">
									<a href="#" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key="jumpToInterface.tooltip"/>">
										<input type="button" class="link btn" value="<fmt:message key="button.newConn" />" 
										   	onclick="createHiddenField('isrAid', '<c:out value="${currentReleaseId}" />'); 
										   		flowActionRedirect('<c:url value="/show/interface" />', 'create');" 
										   	class="transactionButton link" id="jumpToInterface"/>
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