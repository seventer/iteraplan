<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<script type='text/javascript' src="<c:url value="/dwr/interface/InformationSystemInterfaceService.js" />"></script>        
	
<%-- Connection Header  --%>
<tiles:insertTemplate template="/jsp/common/ElementNameComponentView.jsp">
	<tiles:putAttribute name="icon_suffix" value="interface" />
	<tiles:putAttribute name="path_to_componentModel" value="componentModel.nameModel" />
	<tiles:putAttribute name="virtualElementSelected" value="${disable}" />
	<tiles:putAttribute name="nameFieldIsMandatory" value="false" />
	<tiles:putAttribute name="validate" value="true" />
</tiles:insertTemplate>
  
<%-- Description of Connection --%>
<tiles:insertTemplate template="/jsp/common/ElementDescriptionComponentView.jsp">
    <tiles:putAttribute name="path_to_componentModel" value="componentModel.descriptionModel" />
    <tiles:putAttribute name="virtualElementSelected" value="${disable}" />
</tiles:insertTemplate>

<div id="InfoInterfaceModul" class="row-fluid module">
	<div class="row-fluid">
		<div class="module-body-table">
			<div class="row-fluid">
				<table class="table table-striped table-condensed tableInModule">
					<thead>
						<%-- Field labels for information systems and flow direction --%>
						<tr>
							<th class="col-interface-name">
								<fmt:message key="interface.releaseA" />:
							</th>
							<th class="col-interface-dir" style="text-align: center;">
								<tiles:insertTemplate template="/jsp/common/TransportInfoHeader.jsp">
									<tiles:putAttribute name="path_to_componentModel" value="componentModel.transportInfoModel" />
								</tiles:insertTemplate>
							</th>
							<th class="col-interface-name">
								<fmt:message key="interface.releaseB" />:
							</th>
						</tr>
					</thead>
					<tbody>
						<%-- Connection --%>
						<tr>
							<c:set var="linkScriptRelA"><itera:linkToElement name="memBean" property="componentModel.selectModel.releaseA" type="js"/></c:set>
							<c:set var="linkRelA"><itera:linkToElement name="memBean" property="componentModel.selectModel.releaseA" type="html"/></c:set>    
							<c:choose>
								<c:when test="${componentMode != 'READ'}">
									<td valign="top">
										<form:select path="componentModel.selectNewModel.releaseAId" cssClass="nameforSelect combobox" onchange="flowAction('update');" id="select_new_ipurelease1">
											<form:options items="${memBean.componentModel.selectNewModel.releasesA}" itemLabel="hierarchicalName" itemValue="id"/>
										</form:select>
									</td>
								</c:when>
								<c:otherwise>
									<td valign="top" align="left" class="elementName" onclick="${linkScriptRelA}">
										<itera:htmlLinkToElement link="${linkRelA}" isLinked="true">
											<c:out value="${memBean.componentModel.selectModel.releaseA}"/>
										</itera:htmlLinkToElement>
									</td>
								</c:otherwise>
							</c:choose>    
							
							<c:set var="linkScriptRelB"><itera:linkToElement name="memBean" property="componentModel.selectModel.releaseB" type="js"/></c:set>
							<c:set var="linkRelB"><itera:linkToElement name="memBean" property="componentModel.selectModel.releaseB" type="html"/></c:set> 
							
							<tiles:insertTemplate template="/jsp/common/TransportInfoComponentView.jsp">
								<tiles:putAttribute name="path_to_componentModel" value="componentModel.transportInfoModel" />
							</tiles:insertTemplate> 
							
							<c:choose>
								<c:when test="${componentMode != 'READ'}">
									<td>
										<form:select path="componentModel.selectNewModel.releaseBId" id="select_new_ipurelease2" cssClass="nameforSelect combobox" onchange="flowAction('update');">
											<form:options items="${memBean.componentModel.selectNewModel.releasesB}" itemLabel="hierarchicalName" itemValue="id"/>
										</form:select>
									</td>
								</c:when>
								<c:otherwise>
									<td class="elementName" onclick="${linkScriptRelB}">
										<itera:htmlLinkToElement link="${linkRelB}" isLinked="true">
											<c:out value="${memBean.componentModel.selectModel.releaseB}"/>
										</itera:htmlLinkToElement>
									</td>
								</c:otherwise>
							</c:choose>
						</tr>
					
						<%-- Descriptions of connected ISR releases --%>
						<tr>
							<td valign="top">
								<div style="margin-bottom: 1em;">
									<fmt:message key="global.description" />:<br />
									<div class="border">
										<c:choose>
											<c:when test="${componentMode != 'READ'}">
												<c:if test="${not empty memBean.componentModel.selectNewModel.releaseA}">
													<itera:write name="memBean" property="componentModel.selectNewModel.releaseA.description" wikiText="true" breaksAndSpaces="true" escapeXml="false" />
												</c:if>
											</c:when>
											<c:otherwise>
												<itera:write name="memBean" property="componentModel.selectModel.releaseA.description" breaksAndSpaces="true" wikiText="true" escapeXml="false" />&nbsp;
											</c:otherwise>
										</c:choose>
									</div>
								</div>
							</td>  
							<td nowrap="nowrap">&nbsp;</td> 
							<td valign="top">
								<div style="margin-bottom: 1em;">
									<fmt:message key="global.description" />:<br />
									<div class="border">
										<c:choose>
											<c:when test="${componentMode != 'READ'}">
												<c:if test="${not empty memBean.componentModel.selectNewModel.releaseB}">
													<itera:write name="memBean" property="componentModel.selectNewModel.releaseB.description" wikiText="true" escapeXml="false" breaksAndSpaces="true"/>
												</c:if>
											</c:when>
											<c:otherwise>
												<itera:write name="memBean" property="componentModel.selectModel.releaseB.description" breaksAndSpaces="true" wikiText="true" escapeXml="false" />&nbsp;
											</c:otherwise>
										</c:choose>
									</div>
								</div>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>