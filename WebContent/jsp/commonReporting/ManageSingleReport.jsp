<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<script type="text/javascript">
	<%-- adds the shortcut keys to quickly perform actions such as going back, sending a query or generating a graphical report  --%>
    addMiscShortcuts();
</script>

<tiles:insertTemplate template="/jsp/GraphicalReporting/DiagramBreadcrumb.jsp" flush="true">
  <tiles:putListAttribute name="state_keys">
    <tiles:addAttribute value="graphicalExport.landscape.breadcrumbs.configuration"/>
    <tiles:addAttribute value="graphicalExport.landscape.breadcrumbs.filter"/>
  </tiles:putListAttribute>
  <tiles:putListAttribute name="state_events">
    <tiles:addAttribute value="backFromFilter"/>
    <tiles:addAttribute value="filterColumnResults"/> <%-- this should be unused, but we set an event to be sure --%>
  </tiles:putListAttribute>
  <tiles:putAttribute name="current_state_index" value="1"/>
</tiles:insertTemplate>

<h1><fmt:message key="reports.filterFor"/>&nbsp;<fmt:message key="${memBean.reportResultType.typeNamePluralPresentationKey}" /></h1>
<br/>
<br/>

<tiles:insertTemplate template="/jsp/commonReporting/StandardQueryForm.jsp" flush="true" />

<div class="ReportingRequestButtons">
	<a href="#" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key='button.sendQuery.tooltip'/>">
		<input onclick="flowAction('requestReport');" type="button" id="sendQueryButton" class="link btn" value="<fmt:message key="button.sendQuery" />"/>
	</a>
	<a href="#" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key='button.resetReport.tooltip'/>">
		<input onclick="flowAction('resetReport');" type="button" id="buttonResetReport" class="link btn" value="<fmt:message key="button.reset" />"/>
	</a>
	<input type="button" class="link btn btn-primary" id="buttonSendAndConfirm" value="<fmt:message key="global.requestAndConfirmSelection" />" onclick="flowAction('requestAndConfirmReport');" />
	<c:if test="${not empty memBean.results}">
	    &nbsp;
	    <a href="#" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key='button.confirmSelection.tooltip'/>">
			<input onclick="flowAction('resumeFromFilter');" type="button" id="buttonConfirmSelection" class="link btn" value="<fmt:message key="global.confirmSelection" />"/>
		</a>
	</c:if>
</div>

<tiles:insertTemplate template="/jsp/commonReporting/resultPages/GeneralResultPage.jsp" flush="true" />
