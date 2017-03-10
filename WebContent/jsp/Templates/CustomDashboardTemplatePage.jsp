<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<!-- configutation page for custom dashboard templates -->

<c:set var="dashboardMemory"
	value="${dialogMemory.customDashboardDialogMemory}" scope="request" />
<c:set var="editMode" value="${dashboardMemory.customDashboardSelectedBBType!=null}" />

<div class="row-fluid module">
	<div class="module-heading"><fmt:message key="customDashboard.template.title" />
	</div>
	<div class="module-body-table">
		<table class="table table-striped table-condensed tableInModule">
			<tr>
				<th><fmt:message key="global.name" /></th>
				<th><fmt:message key="global.bbtype" /></th>
				<th><fmt:message key="global.description" /></th>
				<th class="actionsRow"><fmt:message key="global.manage" /></th>
			</tr>
			<c:forEach items="${dashboardMemory.customDashboardTemplates}" var="template">
				<c:set var="selectTemplateJavascript"
					value="createHiddenField('action','setDashboardTemplateToEdit'); 
					createHiddenField('customDashboardId','${template.id}');
					document.forms[0].submit();" />
				<c:choose>
					<c:when test="${editMode}">
						<c:set var="style" value="opacity:0.3;" />
						<c:if test="${dashboardMemory.customDashboardId==template.id}">
							<c:set var="style" value="opacity:1;" />
						</c:if>
						<tr style="${style}">
							<td><c:out value="${template.name}" /></td>
							<td><fmt:message key="${template.buildingBlockType.name}" /></td>
							<td><c:out value="${template.description}" /></td>
							<c:choose>
								<c:when test="${dashboardMemory.customDashboardId==template.id}">
									<td style="vertical-align: middle; white-space: nowrap;"><i style="opacity:0.3;"
											class="icon-pencil"></i>
									<a title="<fmt:message key="button.delete" />"
										href="javascript:confirmDelete(function() {createHiddenField('action','deleteDashboardTemplate'); 
									createHiddenField('customDashboardId','${template.id}');
									document.forms[0].submit();});">
											<i class="icon-trash"></i>
									</a></td>
								</c:when>
								<c:otherwise>
									<td style="vertical-align: middle; white-space: nowrap;"><i
											class="icon-pencil"></i> <i class="icon-trash"></i></td>
								</c:otherwise>
							</c:choose>
						</tr>
					</c:when>
					<c:otherwise>
						<tr class="link">
							<td onclick="${selectTemplateJavascript}"><a href="javascript:${selectTemplateJavascript}"><c:out
										value="${template.name}" /></a></td>
							<td onclick="${selectTemplateJavascript}"><a href="javascript:${selectTemplateJavascript}"><fmt:message
										key="${template.buildingBlockType.name}" /></a></td>
							<td onclick="${selectTemplateJavascript}"><a href="javascript:${selectTemplateJavascript}"><c:out
										value="${template.description}" /></a></td>
							<td style="vertical-align: middle; white-space: nowrap;">
								<a title="<fmt:message key="button.edit" />" href="javascript:${selectTemplateJavascript}">
									<i class="icon-pencil"></i>
								</a>
								<a title="<fmt:message key="button.delete" />" href="javascript:confirmDelete(function() {createHiddenField('action','deleteDashboardTemplate'); createHiddenField('customDashboardId','${template.id}'); document.forms[0].submit();});">
									<i class="icon-trash"></i>
								</a>
							</td>
						</tr>
					</c:otherwise>
				</c:choose>
			</c:forEach>
			<tr>
				<td colspan="4">
					<form:select disabled="${editMode}" style="width:auto;" path="selectedBBTypeId">
						<option value="-1">&lt;<fmt:message key="customDashboard.template.select.type.popup.title" />&gt;</option>
						<c:forEach var="available" items="${dashboardMemory.bbTAvailableForNewTemplate}">
	 						<form:option value="${available.id}"><fmt:message key="${available.name}" /></form:option>
						</c:forEach>
					</form:select>&nbsp;
					<c:choose>
						<c:when test="${editMode}">
							<input disabled="disabled" type="button" id="createDashboardTemplateButton" class="btn" onclick="if ($('#selectedBBTypeId').val() == null || $('#selectedBBTypeId').val() == -1) {showPopupDialog('<fmt:message key="customDashboard.template.select.type.popup.title" />', '<fmt:message key="customDashboard.template.select.type.popup.content" />');} else {createHiddenField('action', 'createDashboardTemplate'); window.document.forms[0].submit();}" value="<fmt:message key="customDashboard.template.create" />" />
						</c:when>
						<c:otherwise>
							<input type="button" id="createDashboardTemplateButton" class="btn" onclick="if ($('#selectedBBTypeId').val() == null || $('#selectedBBTypeId').val() == -1) {showPopupDialog('<fmt:message key="customDashboard.template.select.type.popup.title" />', '<fmt:message key="customDashboard.template.select.type.popup.content" />');} else {createHiddenField('action', 'createDashboardTemplate'); window.document.forms[0].submit();}" value="<fmt:message key="customDashboard.template.create" />" />
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</table>
	</div>
</div>
<c:if test="${editMode}">
	<div class="row-fluid module">
		
		<div id="saveDashboardContainer" class="modal hide fade" style="display: none; ">
		
			<script language="javascript">
				function doSubmit(){
				   if (!$('#titleDashboardInModal').val()){
				     $('#alert_message').show();
				   } else {
					 createHiddenField('action', 'saveDashboardTemplate');
				     document.forms[0].submit();
				   }
				}
			</script> 
			
			
		
			<div class="modal-header">
				<a class="close" data-dismiss="modal">×</a>
					<h3><fmt:message key="customDashboard.saveTemplate" /></h3>
			</div>
			<div class="modal-body">
			
				<div id="alert_message" class="alert alert-error hide">
					<fmt:message key="customDashboard.templateName.error" />
				</div>
			
		    	<div class="control-group">
			    	<label class="control-label" for="titleDashboardInModal" style="width: 100px;">
						<fmt:message key="customDashboard.templateName" />
					</label>
					<div class="controls">
						<form:input id="titleDashboardInModal" cssClass="labelSaveQuery" path="customDashboardDialogMemory.customDashboardName" /> <form:errors path="customDashboardDialogMemory.customDashboardName" />
					</div>
				</div>

			</div>
			<div class="modal-footer">
				<a href="#" class="btn" data-dismiss="modal"><fmt:message key="button.close" /></a>
				<input type="button" id="saveDashboardButton" class="link btn btn-primary" type="submit" onclick="doSubmit();" value="<fmt:message key="button.save" />" />

			</div>
		</div>
		
		<div id="dashboardTemplateEdit" class="module-heading"><fmt:message key="customDashboard.template.for.type" />
			<fmt:message
				key="${dashboardMemory.customDashboardSelectedBBType.typeOfBuildingBlock.pluralValue}" />
		</div>
		
		<div class="module-body">
			<div class="btn-group pull-right">
					<c:if test="${dashboardMemory.customDashboardName == ''}">
					<a class="btn btn-primary" href="#"
						onclick="$('#saveDashboardContainer').modal('show');" value="<fmt:message key="customDashboard.template.create" />"">
						<fmt:message key="button.save" />
					</a>
				</c:if>
				<c:if test="${dashboardMemory.customDashboardName != ''}" >
					<button class="btn btn-primary" type="submit"
						onclick="createHiddenField('action', 'saveDashboardTemplate');">
						<fmt:message key="button.save" />
					</button>
				</c:if>
				<c:if test=""></c:if>
				<a class="btn" href="#"
					onclick="msgOkCancel(function() {createHiddenField('action', 'rollbackDashboardTemplate'); document.forms[0].submit();});">
					<fmt:message key="button.cancel" />
				</a>
			</div>
			<div id="tab_ConfigTabs">
				<ul class="nav nav-tabs">
					<li id="tab_TabEditor"
						class="tab tab1 <c:if test="${dashboardMemory.selectedTab == 'edit'}"> <c:out value="active"/> </c:if> link"
						classname="tab tab1 active link"><a
						href="javascript:createHiddenField('action','edit');
										saveScrollCoordinates();
										document.forms[0].submit();"><fmt:message
								key="global.editor" /></a></li>
					<li id="tab_TabPreview"
						class="tab tab2 <c:if test="${dashboardMemory.selectedTab == 'preview'}"> <c:out value="active"/> </c:if> link"
						classname="tab tab2 link"><a
						href="javascript:createHiddenField('action','preview');
										saveScrollCoordinates();
										document.forms[0].submit();">
							<fmt:message key="global.preview" />
					</a></li>
					<li id="tab_TabMetadata"
						class="tab tab3 <c:if test="${dashboardMemory.selectedTab == 'metadata'}"> <c:out value="active"/> </c:if> link"
						classname="tab tab3 link"><a
						href="javascript:createHiddenField('action','metadata');
										saveScrollCoordinates();
										document.forms[0].submit();">
							<fmt:message key="global.description" />
					</a></li>
				</ul>
			</div>
			<div id="cont_ConfigTabs">


				<c:if test="${dashboardMemory.selectedTab == 'edit'}">
					<tiles:insertTemplate
						template="/jsp/Templates/tabPages/CustomDashboardTemplateEditor.jsp">
						<tiles:putAttribute name="dashboard_field_path"
							value="customDashboardDialogMemory" />
					</tiles:insertTemplate>
				</c:if>

				<c:if test="${dashboardMemory.selectedTab == 'preview'}">
					<tiles:insertTemplate
						template="/jsp/Templates/tabPages/CustomDashboardTemplatePreview.jsp">
						<tiles:putAttribute name="dashboard_field_path"
							value="customDashboardDialogMemory.customDashboardContent" />
					</tiles:insertTemplate>
				</c:if>

				<c:if
					test="${dialogMemory.customDashboardDialogMemory.selectedTab == 'metadata'}">
					<tiles:insertTemplate
						template="/jsp/Templates/tabPages/CustomDashboardTemplateMetadata.jsp">
						<tiles:putAttribute name="dashboard_field_path"
							value="customDashboardDialogMemory" />
					</tiles:insertTemplate>
				</c:if>

			</div>
		</div>
	</div>
	<script type="text/javascript">
    $(document).ready(function() {
    	getElementByIdSafe('pagePositionYId').value=$('#dashboardTemplateEdit').offset().top;
    	});
	</script>
</c:if>