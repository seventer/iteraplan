<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<%--
		@UsedFor 	
		@UsedFrom	jsp\commonReporting\ManageReportOrMassupdate.jsp;
		@Note		
 --%>
 
<tiles:useAttribute name="permissionCreateReports" ignore="true" />
<c:choose>
	<c:when test="${not empty permissionCreateReports}">
	  <c:set var="showQueryForm" value="${permissionCreateReports}" />
	</c:when>
	<c:otherwise>
	  <c:set var="showQueryForm" value="true" />
	</c:otherwise>
</c:choose>

<%-- adds the shortcut keys to quickly perform actions such as going back, sending a query or generating a graphical report  --%>
<script type="text/javascript">
   addMiscShortcuts();
</script>

<c:if test="${showQueryForm == 'true'}" >
  <tiles:insertTemplate template="/jsp/commonReporting/StandardQueryForm.jsp" flush="true" />
</c:if>

<c:if test="${massUpdateMode != true}"> 
	<c:if test="${showQueryForm == 'true'}" >
	  <tiles:insertTemplate template="/jsp/TabularReporting/ResultFormatSelection.jsp" flush="true"/>
	</c:if> 
	<tiles:insertTemplate template="/jsp/TabularReporting/ShowAddColumn.jsp" flush="true"/> 
</c:if> 

<c:if test="${showQueryForm == 'true'}" >
	<div class="ReportingRequestButtons">
		<a href="#" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key="button.sendQuery.tooltip"/>">
		
		
		<%--  switch between a file download (csv, xls, etc) and displaying the list in the browser, because different flow actions have to be  triggered --%>
		<c:choose>
      		<c:when test="${memBean.tabularOptions.resultFormat eq 'reports_exportHTML' or empty memBean.tabularOptions.resultFormat}">
				<!--  download the file directly -->
    			<input id="sendQueryButton" class="link btn btn-primary" onclick="flowAction('requestReport')" type="button" value="<fmt:message key="button.sendQuery" />"/>
      		</c:when>
   			 <c:otherwise>
   			 <!-- show the downloadlabel first and then start download -->
    			<input id="sendQueryButton" class="link btn btn-primary" onclick="flowAction('triggerDownloadEvent')" type="button" value="<fmt:message key="button.sendQuery" />"/>
    		</c:otherwise>
		</c:choose>



		</a>
		<a href="#" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key="button.resetReport.tooltip"/>">
			<input id="buttonResetReport" class="link btn" onclick="flowAction('resetReport')" type="button" value="<fmt:message key="button.reset" />"/>
		</a>
	</div>
</c:if>
