<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>

<script type="text/javascript">
	<%-- adds the shortcut keys to quickly perform actions such as going back, sending a query or generating a graphical report  --%>
   addMiscShortcuts();
</script>

<c:set var="permissionSaveReports">
	<itera:write name="userContext" property="perms.userHasFuncPermGraphReportingFull" escapeXml="false" />
</c:set>


<div class="form-horizontal">
	<fieldset>
		<legend>
			<fmt:message key="graphicalExport.landscape.content"/>
		</legend>
		
		<div class="control-group">
			<form:select path="graphicalOptions.selectedBbType" id="contentElementType" onchange="flowAction('selectContentType');" disabled="${memBean.graphicalOptions.dialogStep != 1}">
				<form:option value="">
					<fmt:message key="graphicalExport.landscape.select.content" />
				</form:option>
				<c:forEach var="available" items="${memBean.graphicalOptions.availableBbTypes}">
				    <form:option value="${available}"><fmt:message key="${available}" /></form:option>
				</c:forEach>
			</form:select>

			<c:set var="changeContent" value="disabled='disabled'" />
			<c:if test="${memBean.graphicalOptions.dialogStep > 1}">
				<c:set var="changeContent" />
			</c:if>
			
			<a href="#" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key="button.changeContent.tooltip"/>">
				&nbsp;<input type="button" class="link btn" id="changeContentButton" class="link" <c:out value="${changeContent}" escapeXml="false"/>
					onclick="flowAction('changeContentType');" value="<fmt:message key="button.edit"/>"/>
			</a>
		</div>
		
		<c:if test="${memBean.graphicalOptions.dialogStep > 1}">
			<p></p>
			
			<div class="control-group">
				<c:set var="contentType" scope="page">
					<fmt:message key="${memBean.graphicalOptions.selectedBbType}"/>
				</c:set>
				
				<tiles:insertTemplate template="/jsp/GraphicalReporting/ShowSelectedElements.jsp">
					<tiles:putAttribute name="messageKey" value="graphicalExport.masterplan.showChosenElements" />
					<tiles:putAttribute name="messageKeyArg" value="${contentType}" />
					<tiles:putAttribute name="collection" value='queryResults.contentQuery.selectedResults' />
					<tiles:putAttribute name="field" value="identityString" />
					<tiles:putAttribute name="presentationId" value="contentElements" />
				</tiles:insertTemplate>
			</div>
			
			<div class="control-group">
				<a href="#" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key="button.filterContent.tooltip"/>">
					<input type="button" class="link btn" id="filterContentButton" class="link" value="<fmt:message key="button.filter"/>" onclick="flowAction('filterContentResults')" style="filterContentElements"/>
				</a>
			</div>
		</c:if>
		
		<c:if test="${ memBean.graphicalOptions.levels.contentTopLevel > 1 || memBean.graphicalOptions.levels.contentBottomLevel > 1 }">
			<div class="control-group">
				<b class="help-block">
					<c:set var="contentType" scope="page">
						<fmt:message key="${memBean.graphicalOptions.selectedBbType}"/>
					</c:set>
					
					<fmt:message key="graphicalExport.landscape.configuration.content.levels">
						<fmt:param value="${contentType}"/>
						<fmt:param value="${memBean.graphicalOptions.levels.contentTopLevel}"/>
						<fmt:param value="${memBean.graphicalOptions.levels.contentBottomLevel}"/>
					</fmt:message>
				</b>
				
				<tiles:insertTemplate template="/jsp/GraphicalReporting/SelectLevels.jsp">
					<tiles:putAttribute name="selectedLevelRangePath" value="graphicalOptions.selectedLevelRangeContent"/>
					<tiles:putAttribute name="selectedLevelRangeField" value="${memBean.graphicalOptions.selectedLevelRangeContent}"/>
					<tiles:putAttribute name="minLevel" value="${memBean.graphicalOptions.levels.contentTopLevel}"/>
					<tiles:putAttribute name="maxLevel" value="${memBean.graphicalOptions.levels.contentBottomLevel}"/>
				</tiles:insertTemplate>
			</div>
		</c:if>
	</fieldset>
</div>

<br/>

<c:if test="${memBean.graphicalOptions.dialogStep > 1}">
	<tiles:insertTemplate template="/jsp/GraphicalReporting/Landscape/LineAndColorSettings.jsp"/>
</c:if>