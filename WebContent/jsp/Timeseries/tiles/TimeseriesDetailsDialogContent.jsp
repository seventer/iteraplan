<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<script>
	$(document).ready(function() {
		$('#timeseriesDialog').on('hidden', function() {
			closeTimeseriesDialogAndUpdate('${memBean.currentTimeseriesComponentModel.componentMode}');
		});
	});
</script>
		
<div class="modal-header">
	<h4>${memBean.currentTimeseriesComponentModel.attributeName}</h4>
</div>
<div class="modal-body">
	<c:if test="${memBean.currentTimeseriesAttributeId != null}">
	
	<c:if test="${not empty memBean.currentTimeseriesComponentModel.errorMessages}">
		<div class="alert alert-error">
			<ul>
			<c:forEach items="${memBean.currentTimeseriesComponentModel.errorMessages}" var="message">
				<li><c:out value="${message}" escapeXml="false" /></li>
			</c:forEach>
			</ul>
		</div>
	</c:if>

	<table class="table table-condensed table-striped">
		<tr>
			<th></th>
			<th>
				<fmt:message key="global.date"/>
			</th>
			<th>
				<fmt:message key="global.attributevalue"/>
			</th>
		</tr>
		<c:if test="${memBean.currentTimeseriesComponentModel.componentMode != 'READ'}">
			<tiles:insertTemplate template="TimeseriesEntryRow.jsp">
				<tiles:putAttribute name="timeseriesEntryCMPath" value="currentTimeseriesComponentModel.newEntryComponentModel"/>
				<tiles:putAttribute name="rowIndex" value=""/>
			</tiles:insertTemplate>
		</c:if>
		<c:forEach items="${memBean.currentTimeseriesComponentModel.entryComponentModels}" var="entryCM" varStatus="status">
			<tiles:insertTemplate template="TimeseriesEntryRow.jsp">
				<tiles:putAttribute name="timeseriesEntryCMPath" value="currentTimeseriesComponentModel.entryComponentModels[${status.index}]"/>
				<tiles:putAttribute name="rowIndex" value="${status.index}"/>
			</tiles:insertTemplate>
		</c:forEach>
		<c:if test="${empty memBean.currentTimeseriesComponentModel.entryComponentModels && memBean.currentTimeseriesComponentModel.componentMode == 'READ'}">
			<tr>
				<td></td>
				<td><fmt:message key="attribute.novalue"/></td>
				<td><fmt:message key="attribute.novalue"/></td>
			</tr>
		</c:if>
	</table>
	
	</c:if>
</div>
<div class="modal-footer">
	<a class="btn btn-primary" data-dismiss="modal">OK</a>
</div>