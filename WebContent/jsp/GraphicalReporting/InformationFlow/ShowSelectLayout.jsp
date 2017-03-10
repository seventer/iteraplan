<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<p><fmt:message	key="graphicalExport.informationflow.helpSelectLayout" /></p>
<div class="row-fluid">
	<form:select cssStyle="width:200px;" path="graphicalOptions.selectedNodeLayout" onchange="${refreshReportJavaScript}">
	
		<itera:define id="availableNodeLayouts_array" name="memBean" property="graphicalOptions.availableNodeLayouts"/>
		<c:forEach items="${availableNodeLayouts_array}" var="format" varStatus="countStatus">
			<c:if test="${format.visible}">
			<form:option value="${format.presentationKey}">
				<fmt:message key="${format.presentationKey}" />
			</form:option>
			</c:if>
		</c:forEach>
	</form:select>
</div>
<div class="row-fluid">
	<fmt:message key="reports.template" />
	&nbsp;
	<form:select path="graphicalOptions.selectedTemplateName" cssStyle="width:50em;" >
		<c:forEach var="availableTemplate" items="${memBean.graphicalOptions.availableLayoutTemplates}">
			<c:if test="${availableTemplate.visible}">
				<form:option value="${availableTemplate.presentationKey}"/>
			</c:if>
		</c:forEach>
	</form:select>
</div>