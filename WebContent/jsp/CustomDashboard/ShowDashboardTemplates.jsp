<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<div class="row-fluid module">
	<div class="module-heading">
		<fmt:message key="customDashboard.template.title" />
	</div>
	
	<div class="module-body-table">
		<table class="table table-striped table-condensed tableInModule">
			<tr>
				<th><fmt:message key="global.name" /></th>
				<th><fmt:message key="global.bbtype" /></th>
				<th><fmt:message key="global.description" /></th>
			</tr>
			
			<c:forEach items="${customDashboardDialogMemory.templates}" var="template">
				<c:set var="selectTemplateJavascript"
					value="createHiddenField('templateId','${template.id}');
					createHiddenField('action', 'loadTemplate');
					document.forms[0].submit();" />
				<c:choose>
					<c:when test="${not empty customDashboardDialogMemory.templateId}">
						<c:set var="style" value="opacity:0.3;" />
						<c:if test="${customDashboardDialogMemory.templateId==template.id}">
							<c:set var="style" value="opacity:1;" />
						</c:if>
						<tr style="${style}">
							<td>
							<c:out value="${template.name}" />
							</td>
							<td><fmt:message key="${template.buildingBlockType.name}" /></td>
							<td><c:out value="${template.description}" /></td>
						</tr>
					</c:when>
					<c:otherwise>
						<tr class="link">
							<td onclick="${selectTemplateJavascript}"><a href="javascript:${selectTemplateJavascript}"><c:out
									value="${template.name}" /></a></td>
							<td onclick="${selectTemplateJavascript}"><a href="javascript:${selectTemplateJavascript}"><fmt:message
									key="${template.buildingBlockType.name}" /></a></td>
							<td onclick="${selectTemplateJavascript}">
								<a href="javascript:${selectTemplateJavascript}">
									<c:out value="${template.description}" />
								</a>
							</td>
						</tr>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</table>
	</div>
</div>

<script type="text/javascript">
    $(document).ready(function() {
    	getElementByIdSafe('pagePositionYId').value=$('#dashboardTemplateEdit').offset().top;
    });
</script>