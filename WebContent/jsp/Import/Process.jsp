<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<c:set var="functionalPermissionImport"
	value="${userContext.perms.userHasFuncPermExcelImport}" scope="request" />

<h1>
	<fmt:message key="${memBean.importTypeHeader}" />
</h1>

<c:choose>
	<c:when test="${functionalPermissionImport == true}">

		<c:if test="${memBean.partialImport}">
			<tiles:insertTemplate template="PartialImportInformation.jsp" />
		</c:if>

		<div id="excelUploadModule" class="row-fluid module">
			<div class="module-heading">
				${memBean.fileName}
				<c:if test="${not empty memBean.exportTimestamp}">
					<span style="font-weight: normal"> (<fmt:message
							key="global.excel.import.partial.timestamp">
							<fmt:param value="${memBean.exportTimestamp}"></fmt:param>
						</fmt:message>)
					</span>
				</c:if>
			</div>
			<div class="module-body">
				<table>
					<c:forEach var="checkpoint" items="${memBean.currentCheckList}">
						<tr>
							<td><fmt:message key="${checkpoint.presentationKey}" /></td>
							<td style="color: #AC007C; font-weight: bold; padding-left: 1em;">done</td>
						</tr>
					</c:forEach>
					<c:if test="${memBean.pendingCheckPoint != null}">
						<c:set var="status" value="pending" />
						<c:set var="color" value="#AAAAAA" />
						<c:if test="${not empty memBean.resultMessages.errors}">
							<c:set var="status" value="error" />
							<c:set var="color" value="#FF0000" />
						</c:if>
						<tr>
							<td><fmt:message
									key="${memBean.pendingCheckPoint.presentationKey}" /></td>
							<td style="color:${color};font-weight:bold;padding-left:1em;">${status}</td>
						</tr>
					</c:if>
					<c:forEach var="checkpoint" items="${memBean.currentTodoList}">
						<tr>
							<td><fmt:message key="${checkpoint.presentationKey}" /></td>
							<td style="padding-left: 1em;">-</td>
						</tr>
					</c:forEach>
				</table>
				<br /> <a href="javascript:flowAction('cancel');" class="link btn"
					style="width: 100px"> <i class="icon-chevron-left"></i> <fmt:message
						key="global.back" />
				</a>
				<c:if
					test="${not empty memBean.currentTodoList && empty memBean.resultMessages.errors}">
					<a
						href="javascript:flowAction('${memBean.currentTodoList[0].action}');"
						class="link btn" style="width: 100px"> <fmt:message
							key="global.forward" /> <i class="icon-chevron-right"></i>
					</a>
				</c:if>
			</div>
		</div>

		<tiles:insertTemplate template="AllMessagesTile.jsp" />

	</c:when>

	<c:otherwise>
		<tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
	</c:otherwise>

</c:choose>