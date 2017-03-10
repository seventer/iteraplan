<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<tiles:useAttribute name="path_to_componentModel" />
<tiles:useAttribute name="showNewConnectionLink" ignore="true" />
<tiles:useAttribute name="showExistingConnectionLinks" ignore="true" />

<script type="text/javascript">
   addJumpToInterfaceShortcut();
</script>

<itera:define id="release" name="memBean" property="${path_to_componentModel}.current" />
<itera:define id="connections" name="memBean" property="${path_to_componentModel}.informationSystemInterfaces" />
<itera:define id="mode" name="memBean" property="${path_to_componentModel}.componentMode" />
<itera:define id="htmlID" name="memBean" property="${path_to_componentModel}.htmlId" />

<c:if test="${empty connections && mode == 'READ'}">
  <c:set var="emptyStyle" value="empty"/>
</c:if>

<c:if test="${empty showNewConnectionLink}">
  <c:set var="showNewConnectionLink" value="false" />
</c:if>

<c:set var="currentReleaseId" value="${release.id}"/>

<div id="<c:out value="${htmlID}"/>Modul" class="row-fluid module">
	<div class="module-heading">
		<fmt:message key="businessObject.to.informationSystemInterfaces" />
	</div>
	<div class="row-fluid">
		<div class="module-body-table">
			<div class="row-fluid">
				<table id="<c:out value="${htmlID}"/>" class="table table-striped table-condensed tableInModule">
				  	<colgroup>
						<col class="col-name" />
						<col class="col-desc" />
						<col class="col-name" />
					</colgroup>
					<c:if test="${componentMode == 'CREATE'}">
						<tr>
							<td>
								<div class="alert alert-error">
								<a data-dismiss="alert" class="close" onclick="clearErrors();">×</a> 
								<span><fmt:message key="businessObject.choose.interface" />
								</span></div>
							</td>
						</tr>
					</c:if>
					<thead>
						<c:choose>
							<c:when test="${empty connections}">
								<tr>
									<th colspan="5">&nbsp;</th>
								</tr>
							</c:when>
							<c:otherwise>
								<tr>
									<th><fmt:message key="informationSystemRelease.interfaceTo" />&nbsp;</th>
									<th><fmt:message key="global.description" />&nbsp;</th>
									<th><fmt:message key="informationSystemRelease.interfaceTo" />&nbsp;</th>
								</tr>
							</c:otherwise>
						</c:choose>
					</thead>
					<tbody>
						<c:forEach items="${connections}" var="connection" varStatus="status">
							<tr id="<c:out value="${htmlID}_${status.index}" />">
								<c:set var="linkJavascript"><itera:linkToElement name="connection" type="js" isrOnLeftHandSide="${release.id}"/></c:set>
								<c:set var="HtmlLink"><itera:linkToElement name="connection" type="html" isrOnLeftHandSide="${release.id}"/></c:set>
								<td class="top link" onclick="<c:out value="${linkJavascript}" />">
									<itera:htmlLinkToElement link="${HtmlLink}" isLinked="true">
										<c:out value="${connection.informationSystemReleaseA.hierarchicalName}" />
									</itera:htmlLinkToElement>
								</td>
								<td class="descriptionintablelong top link" onclick="<c:out value="${linkJavascript}" />">
									<itera:write name="connection" property="description" breaksAndSpaces="true" wikiText="true" escapeXml="false" />
								</td>
								<td class="top link" onclick="<c:out value="${linkJavascript}" />" nowrap="nowrap">
									<itera:htmlLinkToElement link="${HtmlLink}" isLinked="true">
										<c:out value="${connection.informationSystemReleaseB.hierarchicalName}" />
									</itera:htmlLinkToElement>
								</td>
							</tr>
						</c:forEach>
						<c:if test="${userContext.perms.userAllowedToCreateConnections && showNewConnectionLink}">
							<tr id="<c:out value="${htmlID}_new" />">
								<td colspan="5" align="left" class="borderTop">
									<a href="#" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key="jumpToInterface.tooltip"/>">
										<input type="button" value="<fmt:message key="button.chooseConn" />" 
											onclick="changeLocation('<c:url value="/interface/init.do" />');" class="transactionButton link btn" id="jumpToInterface"/>
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