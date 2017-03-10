<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

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

<div id="InfoNewInterfaceModul" class="row-fluid module">
	<div class="row-fluid">
		<div class="module-body-table">
			<div class="row-fluid">
				<table class="table table-striped table-condensed tableInModule">
					<thead>
						<%-- Field labels for information systems and flow direction --%>
						<tr> 
							<th class="col-interface-name" nowrap="nowrap"><fmt:message key="interface.releaseA" />:</th>
							<th class="col-interface-dir" style="text-align: center;">
								<tiles:insertTemplate template="/jsp/common/TransportInfoHeader.jsp">
									<tiles:putAttribute name="path_to_componentModel" value="componentModel.transportInfoModel" />
								</tiles:insertTemplate>
							</th>
							<th class="col-interface-name" nowrap="nowrap"><fmt:message key="interface.releaseB" />:</th>
						</tr>
					</thead>
					<tbody>
						<%-- Connection --%>
						<tr>
							<td>
								<form:select path="componentModel.selectNewModel.releaseAId" id="select_new_ipurelease1" cssClass="nameforhierarchy combobox" onchange="flowAction('update')">
									<form:options items="${memBean.componentModel.selectNewModel.releasesA}" itemLabel="hierarchicalName" itemValue="id"/>
								</form:select>  
							</td>
							
							<tiles:insertTemplate template="/jsp/common/TransportInfoComponentView.jsp">
								<tiles:putAttribute name="path_to_componentModel" value="componentModel.transportInfoModel" />
							</tiles:insertTemplate>
							
							<td valign="top" align="left">
								<form:select path="componentModel.selectNewModel.releaseBId" cssClass="nameforhierarchy combobox" id="select_new_ipurelease2" onchange="flowAction('update');">
									<form:options items="${memBean.componentModel.selectNewModel.releasesB}" itemLabel="hierarchicalName" itemValue="id"/>
								</form:select>
							</td>
						</tr>
					
						<%-- Descriptions for the selected information systems. --%>
						<tr>
							<td class="top margin borderBottom">
								<div style="margin-bottom: 1em;">
									<fmt:message key="global.description" />:<br />
									<div class="border">
										<c:if test="${not empty memBean.componentModel.selectNewModel.releaseA}">
											<itera:write name="memBean" property="componentModel.selectNewModel.releaseAFromId.description" wikiText="true" breaksAndSpaces="true" escapeXml="false" />
										</c:if>
										&nbsp;
									</div>
								</div>
							</td>
							<td class="borderBottom">&nbsp;</td>
							<td class="top margin borderBottom">
								<div style="margin-bottom: 1em;">
									<fmt:message key="global.description" />:<br />
									<div class="border">
										<c:if test="${not empty memBean.componentModel.selectNewModel.releaseB}">
											<itera:write name="memBean" property="componentModel.selectNewModel.releaseB.description" wikiText="true" breaksAndSpaces="true" escapeXml="false" />
										</c:if>
										&nbsp;
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