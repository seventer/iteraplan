<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<c:set var="functionalPermissionImport"
	value="${userContext.perms.userHasFuncPermExcelImport}" scope="request" />

<h1><fmt:message key="global.excel_data_import" /></h1>

<c:choose>
	<c:when test="${functionalPermissionImport == true}">
		<c:set var="buttonMessage">
			<fmt:message key="global.back" />
		</c:set>
		<div id="timeseriesUploadModule" class="row-fluid module">
			<div class="module-heading">${memBean.fileName}</div>
			<div class="module-body">
				<a id="backButton" href="javascript:flowAction('done');" class="link btn" style="width: 100px">
					<i class="icon-chevron-left"></i>
					${buttonMessage}
				</a>
			</div>
		</div>
		
		<tiles:insertTemplate template="AllMessagesTile.jsp" />
	</c:when>
	<c:otherwise>
		<tiles:insertTemplate template="/jsp/common/AccessDenied.jsp" />
	</c:otherwise>
</c:choose>