<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="availableGraphicFormats" />
<tiles:useAttribute name="exportFormatPath" />

<fmt:message key="graphicalExport.resultFormat" />: 
<form:select path="${exportFormatPath}" cssStyle="width:200px;" onchange="${refreshReportJavaScript}">
	<c:forEach var="format" items="${availableGraphicFormats}">
		<c:if test="${format.visible}">
			<form:option value="${format.presentationKey}">
				<fmt:message key="${format.presentationKey}" />
			</form:option>
		</c:if>
	</c:forEach>
</form:select>