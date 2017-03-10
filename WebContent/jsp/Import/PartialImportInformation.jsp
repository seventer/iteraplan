<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<div class="alert alert-info">
	<h4>
		<fmt:message key="global.excel.import.partial.label" />
	</h4>

	<fmt:message key="global.excel.import.partial.type">
		<fmt:param value="${memBean.filteredTypeName}"></fmt:param>
		<fmt:param value="${memBean.filteredTypePersistentName}"></fmt:param>
	</fmt:message>
	<br />

	<c:if test="${not empty memBean.extendedFilter}">
		<fmt:message key="global.excel.import.partial.filter">
			<fmt:param value="${memBean.extendedFilter}"></fmt:param>
		</fmt:message>
		<br />
	</c:if>
	
	<c:if test="${memBean.importMetamodel && memBean.partialImport}">
		<fmt:message key="global.excel.import.partial.noMMChanges" />
	</c:if>
</div>