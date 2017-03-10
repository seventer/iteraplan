<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<tiles:useAttribute name="permissionSaveReports" />

<c:if test="${permissionSaveReports == true}">

	<script type="text/javascript">
	<%-- adds the shortcut keys to quickly perform actions such as going back, sending a query or generating a graphical report  --%>
		addMiscShortcuts();		
	</script>

	<tiles:useAttribute name="xmlSaveAsQueryName" />
	<tiles:useAttribute name="xmlSaveAsQueryDescription" />
	<tiles:useAttribute name="xmlQueryName" />

	<div id="saveQueryContainer" class="modal hide fade" style="display: none; ">
	    <div class="modal-header">
			<a class="close" data-dismiss="modal">×</a>
			<h3><fmt:message key="graphicalReport.saveQuery.dialog.header" /></h3>
	    </div>
	    <div class="modal-body">
	    	<div class="control-group">
		    	<label class="control-label" for="titleSaveQueryInModal" style="width: 100px;">
					<fmt:message key="graphicalReport.saveQueryName" />
				</label>
				<div class="controls">
					<form:input id="titleSaveQueryInModal" cssClass="labelSaveQuery" path="xmlSaveAsQueryName" /> <form:errors path="xmlSaveAsQueryName" />
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="descriptionSaveQueryInModal" style="width: 100px;">
					<fmt:message key="graphicalReport.saveQueryDescription" />
				</label>
				<div class="controls">
					<form:textarea id="descriptionSaveQueryInModal" cols="60" rows="4" cssClass="labelSaveQuery" path="xmlSaveAsQueryDescription" /> <form:errors path="xmlSaveAsQueryDescription" />
				</div>
			</div>
		</div>
		<div class="modal-footer">
			<a href="#" class="btn" data-dismiss="modal"><fmt:message key="button.close" /></a>
			<input type="button" id="saveQueryButton" class="link btn btn-primary" onclick="flowAction('saveQueryAs');" value="<fmt:message key="graphicalReport.saveQuery" />" />
		</div>
	</div>
	
	<c:choose>
   		<c:when test="${not empty memBean.xmlQueryName}">
     		<a href="#" rel="tooltip" data-original-title="Hotkey: <img/> <fmt:message key="button.saveQuery.tooltip"/>">
				<input type="button" id="saveQueryButton" class="link btn btn-default button-listener" onclick="flowAction('saveQuery');" value="<fmt:message key="graphicalReport.saveQuery" />" />
			</a>
   		</c:when>
   		<c:otherwise>
    	 <a class="link btn" disabled="disabled"><fmt:message key="graphicalReport.saveQuery" /></a>
   		</c:otherwise>
	</c:choose>
		
	
	<input type="button" id="saveQueryAsButton" data-toggle="modal" data-target="#saveQueryContainer" class="link btn btn-default button-listener" value="<fmt:message key="graphicalReport.saveQueryAs" />" />

</c:if>